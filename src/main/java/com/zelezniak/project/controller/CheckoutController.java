package com.zelezniak.project.controller;

import com.zelezniak.project.dto.PaymentInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;


@Controller
@Slf4j
public class CheckoutController {
    @Value("${stripe.api.publicKey}")
    private String publicKey;

    @PostMapping({"/checkout"})
    public String showCard(@ModelAttribute PaymentInfo paymentInfo, Model model) {
        model.addAttribute("publicKey", this.publicKey);
        model.addAttribute("amount", paymentInfo.getAmount());
        model.addAttribute("email", paymentInfo.getEmail());
        model.addAttribute("productName", paymentInfo.getProductName());
        return "checkout-form";
    }


    @GetMapping("/success/payment")
    public ResponseEntity<String> handleSuccessPayment(
            Principal principal,
            @RequestParam String productName) {
        String email = principal.getName();

        return ResponseEntity.ok("Payment success! Email: " + email + ", Product: " + productName);
    }


}
