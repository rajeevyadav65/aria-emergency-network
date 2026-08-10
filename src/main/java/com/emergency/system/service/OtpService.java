package com.emergency.system.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
@Slf4j
public class OtpService {

    // Demo ke liye memory mein save kar rahe hain. Production mein Redis use hota hai.
    private final Map<String, String> otpStorage = new HashMap<>();

    public void generateAndSendOtp(String contact) {
        // Generate 6 digit random OTP
        String otp = String.format("%06d", new Random().nextInt(999999));
        otpStorage.put(contact, otp);

        // 🟢 IMPORTANT: Testing ke liye OTP console mein print hoga!
        log.info("=================================================");
        log.info("🔔 ARIA OTP ALERT 🔔");
        log.info("OTP for {}: {}", contact, otp);
        log.info("=================================================");

        // Future: Yahan Twilio SMS ya JavaMailSender ka code aayega
    }

    public boolean verifyOtp(String contact, String inputOtp) {
        if (otpStorage.containsKey(contact) && otpStorage.get(contact).equals(inputOtp)) {
            otpStorage.remove(contact); // OTP use hone ke baad delete kar dein
            return true;
        }
        return false;
    }
}