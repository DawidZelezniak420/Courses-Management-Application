package com.zelezniak.project.controller;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import com.zelezniak.project.dto.PaymentInfo;
import com.zelezniak.project.dto.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class PaymentIntentController {

    @PostMapping("/payment/intent")
    @ResponseBody
    public Response createPaymentIntent(@RequestBody PaymentInfo paymentInfo)
            throws StripeException {
        PaymentIntentCreateParams params =
                PaymentIntentCreateParams.builder()
                        .setAmount((long) (paymentInfo.getAmount() * 100L))
                        .putMetadata("productName",
                                paymentInfo.getProductName())
                        .setCurrency("usd")
                        .setAutomaticPaymentMethods(
                                PaymentIntentCreateParams
                                        .AutomaticPaymentMethods
                                        .builder()
                                        .setEnabled(true)
                                        .build()
                        )
                        .build();

        PaymentIntent intent =
                PaymentIntent.create(params);
    log.info("SECREEEET AAAAAAAAAAAAAAAAAAAAAAA                     "+intent.getClientSecret());
        return new Response(intent.getId(),
                intent.getClientSecret());
    }
}