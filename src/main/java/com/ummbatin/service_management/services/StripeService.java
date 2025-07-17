package com.ummbatin.service_management.services;

import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeService {

    @Value("${stripe.api.key}")
    private String apiKey;

    public PaymentIntent createPaymentIntent(Long amount) throws Exception {
        Stripe.apiKey = apiKey;

        PaymentIntentCreateParams params =
                PaymentIntentCreateParams.builder()
                        .setAmount(amount) // amount in cents
                        .setCurrency("usd")
                        .addPaymentMethodType("card")
                        .build();

        return PaymentIntent.create(params);
    }
}
