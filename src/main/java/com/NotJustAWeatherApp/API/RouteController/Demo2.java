package com.NotJustAWeatherApp.API.RouteController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
// mark this class as a com.NotJustAWeatherApp.api.api.controller to handle /demo requests

@RestController
@RequestMapping(value = "/weather")
public class Demo2 {    // create GET endpoint to serve demo data at /demo/data
    @GetMapping(value = "/multiple")
    public String getDemoData() {
        return "Its sunny the entire way!";
    }}