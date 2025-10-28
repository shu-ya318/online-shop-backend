package com.project.demo.gateway;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.paypal.api.payments.Amount;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.PaymentExecution;
import com.paypal.api.payments.RedirectUrls;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import com.project.demo.dto.gateway.PaymentGatewayRequestDTO;
import com.project.demo.dto.gateway.PaymentGatewayResponseDTO;
import com.project.demo.enumeration.PaymentMethod;
import com.project.demo.enumeration.PaymentStatus;
import com.project.demo.exception.PaymentGatewayException;
import com.project.demo.model.Payment;

@Component
public class PayPalGateway implements PaymentGateway {

    @Value("${paypal.client-id}")
    private String clientId;

    @Value("${paypal.client-secret}")
    private String clientSecret;

    @Value("${paypal.mode}")
    private String mode;

    @Value("${frontend.url}")
    private String frontendUrl;

    private APIContext getAPIContext() {
        APIContext apiContext = new APIContext(clientId, clientSecret, mode);

        return apiContext;
    }

    // 1.Authorization stage, backend handles request parameters, let frontend carry parameters to redirect to PayPal authorization page
    @Override
    public PaymentGatewayResponseDTO createPayment(Payment payment) {
        try {
            // Parameter: payer
            Payer payer = new Payer();
            payer.setPaymentMethod("paypal");

            // Parameter: redirectUrls
            String orderUuid = payment.getOrder().getUuid().toString();
            RedirectUrls redirectUrls = new RedirectUrls();
            redirectUrls.setReturnUrl(frontendUrl + "/order");
            redirectUrls
                    .setCancelUrl(frontendUrl + "/order?status=" + PaymentStatus.CANCELLED + "&orderUuid=" + orderUuid);

            // Parameter: amount
            Amount amount = new Amount();
            amount.setTotal(payment.getAmount().toPlainString());
            amount.setCurrency(payment.getCurrency());

            // Parameter: transaction
            Transaction transaction = new Transaction();
            transaction.setAmount(amount);
            transaction.setDescription("Payment for order " + payment.getOrder().getUuid());

            List<Transaction> transactions = new ArrayList<>();
            transactions.add(transaction);

            // Assemble complete request parameters: requestPayment
            com.paypal.api.payments.Payment requestPayment = new com.paypal.api.payments.Payment();

            requestPayment.setIntent("sale");
            requestPayment.setPayer(payer);
            requestPayment.setRedirectUrls(redirectUrls);
            requestPayment.setTransactions(transactions);

            // Send request authorization request
            com.paypal.api.payments.Payment createdPayment = requestPayment.create(getAPIContext());

            // Assemble complete response parameters
            String paymentId = createdPayment.getId();

            String approvalUrl = getApprovalUrl(createdPayment)
                    .orElseThrow(() -> new IllegalStateException("Could not find approval URL!"));

            return new PaymentGatewayResponseDTO(paymentId, PaymentStatus.AUTHORIZED, approvalUrl);

        } catch (PayPalRESTException e) {
            throw new PaymentGatewayException("Failed to create PayPal payment", e);
        }
    }

    // (2.Confirmation stage, completed by user and authorization server)
    // 3.Execution stage
    @Override
    public PaymentGatewayResponseDTO capturePayment(PaymentGatewayRequestDTO requestDTO) {
        try {
            // Create execution payment information, additional payerId
            PaymentExecution paymentExecution = new PaymentExecution();
            paymentExecution.setPayerId(requestDTO.payerId());

            // Point to PayPal server authorized transaction
            com.paypal.api.payments.Payment payment = new com.paypal.api.payments.Payment()
                    .setId(requestDTO.paymentId());

            // Send execution payment request
            com.paypal.api.payments.Payment executedPayment = payment.execute(getAPIContext(), paymentExecution);

            // Return different information according to execution result
            if ("approved".equalsIgnoreCase(executedPayment.getState())) {
                String transactionId = executedPayment.getTransactions().get(0).getRelatedResources().get(0).getSale()
                        .getId();
                return new PaymentGatewayResponseDTO(transactionId, PaymentStatus.SUCCESS, "");
            } else {
                return new PaymentGatewayResponseDTO(requestDTO.paymentId(), PaymentStatus.FAILED, "");
            }
        } catch (PayPalRESTException e) {
            throw new PaymentGatewayException("Failed to execute PayPal payment", e);
        }
    }

    @Override
    public PaymentMethod getPaymentMethod() {
        return PaymentMethod.PAYPAL;
    }

	// ----- Private Helper Method -----

    private Optional<String> getApprovalUrl(com.paypal.api.payments.Payment payment) {
        return payment.getLinks().stream()
                .filter(link -> "approval_url".equalsIgnoreCase(link.getRel()))
                .map(Links::getHref)
                .findFirst();
    }
}
