package com.NotJustAWeatherApp.API.SingleLocationController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
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
    public String getDemoData() throws JsonProcessingException {
        final String pointsURI = "https://api.weather.gov/points/39.7456,-97.0892";

        JsonNode pointProperties = restTemplate.getForObject(pointsURI, JsonNode.class);
        String gridURI = pointProperties.get("properties").get("forecastGridData").toString();
        gridURI = gridURI.replace("\"", "");
        
        JsonNode gridProperties = restTemplate.getForObject(gridURI, JsonNode.class);
        return gridProperties.at("/properties/maxTemperature/values").toPrettyString();
    }
}
