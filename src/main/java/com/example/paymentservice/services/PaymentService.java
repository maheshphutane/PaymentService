package com.example.paymentservice.services;

import com.razorpay.RazorpayException;
import org.springframework.stereotype.Service;

@Service
public interface PaymentService {
    String createPaymentLink(String orderId) throws RazorpayException;
    String getPaymentStatus();
}
