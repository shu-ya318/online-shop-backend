package com.project.demo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.project.demo.dto.payment.PaymentResponseDTO;
import com.project.demo.dto.payment.PaymentSummaryDTO;
import com.project.demo.model.Payment;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PaymentMapper {

    @Mapping(source = "order.uuid", target = "orderUuid")
    @Mapping(target = "redirectUrl", expression = "java(null)")
    PaymentResponseDTO toPaymentResponseDTO(Payment payment);

    PaymentSummaryDTO toPaymentSummaryDTO(Payment payment);
}
