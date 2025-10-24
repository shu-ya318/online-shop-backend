package com.project.demo.gateway;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.project.demo.enumeration.PaymentMethod;

@Component
public class PaymentGatewayFactory {

    private final Map<PaymentMethod, PaymentGateway> gateways = new EnumMap<>(PaymentMethod.class);

    public PaymentGatewayFactory(List<PaymentGateway> gatewayList) {
        for (PaymentGateway gateway : gatewayList) {
            gateways.put(gateway.getPaymentMethod(), gateway);
        }
    }

    public Optional<PaymentGateway> getGateway(PaymentMethod paymentMethod) {
        return Optional.ofNullable(gateways.get(paymentMethod));
    }
}
