package com.NotJustAWeatherApp.api.api.controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;// mark this class as a com.NotJustAWeatherApp.api.api.controller to handle /demo requests
@RestController
@RequestMapping(value = "/weather")
public class Demo {    // create GET endpoint to serve demo data at /demo/data
    @GetMapping(value = "/single")
    public String getDemoData() {
        return "Its sunny";
    }}
