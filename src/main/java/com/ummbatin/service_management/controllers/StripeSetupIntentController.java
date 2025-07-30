package com.ummbatin.service_management.controllers;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.SetupIntent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/stripe")
public class StripeSetupIntentController {

    @Value("${stripe.api.key}")
    private String stripeSecretKey;

    @PostMapping("/setup-intent")
    public Map<String, String> createSetupIntent() throws StripeException {
        Stripe.apiKey = stripeSecretKey;

        SetupIntent setupIntent = SetupIntent.create(new HashMap<>());

        Map<String, String> response = new HashMap<>();
        response.put("clientSecret", setupIntent.getClientSecret());
        return response;
    }
}
