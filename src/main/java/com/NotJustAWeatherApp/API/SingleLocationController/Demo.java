package com.NotJustAWeatherApp.API.SingleLocationController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

// mark this class as a com.NotJustAWeatherApp.api.api.controller to handle /demo requests

@RestController
@RequestMapping(value = "/weather")
public class Demo
{
    @Autowired
    private RestTemplate restTemplate;

    @GetMapping(value = "/single")
    public String getDemoData() {
        final String uri = "https://gturnquist-quoters.cfapps.io/api/random";

        Quote quote = restTemplate.getForObject(uri, Quote.class);

        assert quote != null;
        return quote.getValue().getQuote();
    }
}
