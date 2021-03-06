package com.NotJustAWeatherApp.API.SingleLocationWeatherController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

// mark this class as a com.NotJustAWeatherApp.api.api.controller to handle /demo requests

@RestController
//http://localhost:8080/weather/single?lat=39.7456&lon=-97.0892

@RequestMapping(value = "/weather", produces = "application/json")
public class CoordinatesWeather
{
    @Autowired
    private RestTemplate restTemplate;

    @CrossOrigin
    @GetMapping(value = "/single")
    public String getDemoData(@RequestParam Map<String,String> requestParams) throws JsonProcessingException {
        String lat = requestParams.get("lat");
        String lon = requestParams.get("lon");
        final String pointsURI = "https://api.weather.gov/points/" + lat + ',' + lon;

        JsonNode pointProperties = restTemplate.getForObject(pointsURI, JsonNode.class);
        String gridURI = pointProperties.get("properties").get("forecastGridData").toString();
        gridURI = gridURI.replace("\"", "");
        
        JsonNode gridProperties = restTemplate.getForObject(gridURI, JsonNode.class);
        
        return gridProperties.at("/properties/maxTemperature/values").toPrettyString();
    }
}
