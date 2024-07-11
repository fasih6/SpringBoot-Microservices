package com.example.currency_conversion_service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;

@RestController
public class CurrencyConversionController {

    @Autowired
    private CurrencyExchangeProxy proxy;


    @GetMapping("/currency-conversion/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversion calculateCurrencyConversion(
                                        @PathVariable String from,
                                        @PathVariable String to,
                                        @PathVariable BigDecimal quantity
            ){
        // we set response type as CurrencyConversion.class
        // bcz CurrencyConversion structure matches
        // the response of the currency exchange microservice
        // and therefore the values of the currency exchange automatically get mapped
        HashMap<String, String> uriVariables = new HashMap<>();
        uriVariables.put("from",from);
        uriVariables.put("to", to);

        ResponseEntity<CurrencyConversion> responseEntity = new RestTemplate().getForEntity
                ("http://localhost:8000/currency-exchange/from/{from}/to/{to}",
                        CurrencyConversion.class, uriVariables);

        CurrencyConversion currencyConversion = responseEntity.getBody();
        // id, from, to, quantity, conversionMultiple,  totalCalculatedAmount, environment
//        return new CurrencyConversion(10001L, from, to, quantity,
//                BigDecimal.ONE, BigDecimal.ONE, "");
        return new CurrencyConversion(currencyConversion.getId(),
                from, to, quantity,
                currencyConversion.getConversionMultiple(),
                quantity.multiply(currencyConversion.getConversionMultiple()),
                currencyConversion.getEnvironment() + " rest template");
    }

//    http://localhost:8100/currency-conversion-feign/from/USD/to/PKR/quantity/10
    @GetMapping("/currency-conversion-feign/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversion calculateCurrencyConversionFeign(
            @PathVariable String from,
            @PathVariable String to,
            @PathVariable BigDecimal quantity
    ){

        CurrencyConversion currencyConversion =
                proxy.retrieveExchangeValue(from, to);
        // id, from, to, quantity, conversionMultiple,  totalCalculatedAmount, environment
//        return new CurrencyConversion(10001L, from, to, quantity,
//                BigDecimal.ONE, BigDecimal.ONE, "");
        return new CurrencyConversion(currencyConversion.getId(),
                from, to, quantity,
                currencyConversion.getConversionMultiple(),
                quantity.multiply(currencyConversion.getConversionMultiple()),
                currencyConversion.getEnvironment() + " feign");
    }
}
