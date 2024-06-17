package com.example.paymentservice.services;

import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class RazorPayPaymentServiceImpl implements PaymentService {
    @Autowired
    private RazorpayClient razorpayClient;
    @Override
    public String createPaymentLink(String orderId) throws RazorpayException {
        JSONObject paymentLinkRequest = new JSONObject();
        paymentLinkRequest.put("amount",1000);
        paymentLinkRequest.put("currency","INR");
        paymentLinkRequest.put("accept_partial",false);
        paymentLinkRequest.put("first_min_partial_amount",100);
        paymentLinkRequest.put("expire_by",System.currentTimeMillis() + 15 * 60 * 1000);
        paymentLinkRequest.put("reference_id",orderId);
        paymentLinkRequest.put("description","Payment for order id "+orderId);
        JSONObject customer = new JSONObject();
        customer.put("name","+919999999999");
        customer.put("contact","Mahesh Phutane");
        customer.put("email","maheshphutane1810@gmail.com");
        paymentLinkRequest.put("customer",customer);
        JSONObject notify = new JSONObject();
        notify.put("sms",true);
        notify.put("email",true);
        paymentLinkRequest.put("reminder_enable",true);
        JSONObject notes = new JSONObject();
        notes.put("policy_name","Jeevan Bima");
        paymentLinkRequest.put("notes",notes);
        paymentLinkRequest.put("callback_url","https://mahesh-phutane-portfoloi.netlify.app/");
        paymentLinkRequest.put("callback_method","get");

        PaymentLink payment = razorpayClient.paymentLink.create(paymentLinkRequest);
        return payment.get("short_url");
    }

    @Override
    public String getPaymentStatus() {
        return "RazorPay Payment Status";
    }
}
