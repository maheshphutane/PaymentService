package com.example.paymentservice.dtos;

import lombok.Getter;
import org.springframework.stereotype.Service;

@Getter
@Service
public class CreatePaymentLinkDTO {
    private String orderId;
}
