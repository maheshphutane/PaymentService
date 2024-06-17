package com.example.paymentservice.controllers;

import com.example.paymentservice.dtos.CreatePaymentLinkDTO;
import com.example.paymentservice.services.PaymentService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    private PaymentService paymentService;

    @Value("${razorpay.keySecret}")
    private String secret;

    @GetMapping("/hello")
    public String hello() {
        return "Hello from Payment Service";
    }

    @PostMapping("/createLink")
    public String createPaymentLink(@RequestBody CreatePaymentLinkDTO request) throws RazorpayException {
        return paymentService.createPaymentLink(request.getOrderId());
    }

    @PostMapping("/webhookevent")
    public Map<String, String> webHookEvent(@RequestBody String payload, @RequestHeader(value="X-Razorpay-Signature",required = false) String signature) {

        logger.info("Received webhook payload: {}", payload);
        logger.info("Received webhook signature: {}", signature);

        if(signature == null) {
            logger.error("X-Razorpay-Signature header is missing");
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "X-Razorpay-Signature header is missing");
            return response;
        }

        boolean isSignatureValid = verifySignature(payload, signature);

        if (isSignatureValid) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                JsonNode jsonNode = objectMapper.readTree(payload);
                // Extract and handle the necessary information from the payload
                String event = jsonNode.get("event").asText();
                logger.info("Received event: {}", event);

                // Handle specific events
                switch (event) {
                    case "payment.captured":
                        logger.info("Payment captured event received");
                        break;
                    case "order.paid":
                        logger.info("Order paid event received");
                        break;
                    // Add more cases as needed
                }
            } catch (Exception e) {
                logger.error("Error processing webhook payload", e);
            }
            // Respond to Razorpay with a success status
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            return response;
        } else {
            // Respond with an error status if the signature is invalid
            logger.warn("Invalid webhook signature");
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            return response;
        }
    }

    private boolean verifySignature(String payload, String signature) {
        try {
            Utils.verifyWebhookSignature(payload, signature, secret);
            return true;
        } catch (Exception e) {
            logger.error("Signature verification failed", e);
            return false;
        }
    }

}
