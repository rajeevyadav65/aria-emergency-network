package com.emergency.system.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    // 🟢 SECURE WAY: Keys are completely hidden. Safe for GitHub!
    private static final String KEY_ID = System.getenv("RAZORPAY_KEY_ID");
    private static final String KEY_SECRET = System.getenv("RAZORPAY_KEY_SECRET");

    public String createOrderWithCommission(Double totalFee, String doctorRazorpayAccountId) throws Exception {

        // 🚨 Safety Check: Agar keys system mein nahi mili toh server cleanly bata dega
        if (KEY_ID == null || KEY_SECRET == null || KEY_ID.isBlank() || KEY_SECRET.isBlank()) {
            throw new RuntimeException("Razorpay API Keys are missing in Environment Variables! Cannot process payment.");
        }

        RazorpayClient razorpay = new RazorpayClient(KEY_ID, KEY_SECRET);

        int amountInPaisa = (int) (totalFee * 100);
        int doctorShare = (int) (amountInPaisa * 0.90); // 90% goes to the Doctor

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amountInPaisa);
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "txn_" + System.currentTimeMillis());

        // RAZORPAY ROUTE: Auto-split payment (10% platform fee)
        JSONArray transfers = new JSONArray();
        JSONObject transfer = new JSONObject();
        transfer.put("account", doctorRazorpayAccountId);
        transfer.put("amount", doctorShare);
        transfer.put("currency", "INR");
        transfer.put("notes", new JSONObject().put("purpose", "Consultation Fee"));
        transfers.put(transfer);

        orderRequest.put("transfers", transfers);

        Order order = razorpay.orders.create(orderRequest);
        return order.get("id");
    }
}