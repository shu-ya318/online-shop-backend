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

    // 用上述基礎設定，作為與 PayPal API 溝通的身分驗證
    private APIContext getAPIContext() {
        APIContext apiContext = new APIContext(clientId, clientSecret, mode);

        return apiContext;
    }

    // 1.授權階段，後端處理請求參數，讓前端夾帶參數 重導向到 PayPal 授權頁
    @Override
    public PaymentGatewayResponseDTO createPayment(Payment payment) {
        try {
            // 建立 付款人資訊
            Payer payer = new Payer();
            payer.setPaymentMethod("paypal");

            // 建立 重定向URL
            String orderUuid = payment.getOrder().getUuid().toString();

            RedirectUrls redirectUrls = new RedirectUrls();
            redirectUrls.setReturnUrl(frontendUrl + "/order");
            redirectUrls
                    .setCancelUrl(frontendUrl + "/order?status=" + PaymentStatus.CANCELLED + "&orderUuid=" + orderUuid);

            // 建立 交易資訊
            Amount amount = new Amount();
            amount.setTotal(payment.getAmount().toPlainString());
            amount.setCurrency(payment.getCurrency());

            Transaction transaction = new Transaction();
            transaction.setAmount(amount);
            transaction.setDescription("Payment for order " + payment.getOrder().getUuid());

            List<Transaction> transactions = new ArrayList<>();
            transactions.add(transaction);

            // 組裝 完整請求資訊
            com.paypal.api.payments.Payment requestPayment = new com.paypal.api.payments.Payment();

            requestPayment.setIntent("sale");
            requestPayment.setPayer(payer);
            requestPayment.setRedirectUrls(redirectUrls);
            requestPayment.setTransactions(transactions);

            // 發送 請求授權請求
            com.paypal.api.payments.Payment createdPayment = requestPayment.create(getAPIContext());

            String approvalUrl = getApprovalUrl(createdPayment)
                    .orElseThrow(() -> new IllegalStateException("Could not find approval URL"));

            String paymentId = createdPayment.getId();

            return new PaymentGatewayResponseDTO(paymentId, PaymentStatus.AUTHORIZED, approvalUrl);

        } catch (PayPalRESTException e) {
            throw new PaymentGatewayException("Failed to create PayPal payment", e);
        }
    }

    // (2.確認階段 由 用戶 和 授權伺服器 共同完成)
    // 3.執行階段
    @Override
    public PaymentGatewayResponseDTO capturePayment(PaymentGatewayRequestDTO requestDTO) {
        try {
            // 建立 執行支付資訊，附加的payerId
            PaymentExecution paymentExecution = new PaymentExecution();
            paymentExecution.setPayerId(requestDTO.payerId());

            // 指向 PayPal伺服器已授權狀態的交易
            com.paypal.api.payments.Payment payment = new com.paypal.api.payments.Payment()
                    .setId(requestDTO.paymentId());

            // 發送 執行支付請求
            com.paypal.api.payments.Payment executedPayment = payment.execute(getAPIContext(), paymentExecution);

            // 依照執行結果，返回不同資訊
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

    // -- Helper Methods --
    private Optional<String> getApprovalUrl(com.paypal.api.payments.Payment payment) {
        return payment.getLinks().stream()
                .filter(link -> "approval_url".equalsIgnoreCase(link.getRel()))
                .map(Links::getHref)
                .findFirst();
    }
}
