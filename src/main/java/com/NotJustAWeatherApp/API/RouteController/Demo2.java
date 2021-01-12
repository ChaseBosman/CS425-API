package com.NotJustAWeatherApp.API.RouteController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
// mark this class as a com.NotJustAWeatherApp.api.api.controller to handle /demo requests

@RestController
//http://localhost:8080/weather/multiple?city1=Brentwood&city2=Antioch&state1=CA&state2=CA
@RequestMapping(value = "/weather")
public class Demo2 {    // create GET endpoint to serve demo data at /demo/data
    @GetMapping(value = "/multiple")
    public String getDemoData(@RequestParam Map<String,String> requestParams) throws JsonProcessingException {
        String start_city = requestParams.get("city1");
        String end_city = requestParams.get("city2");
        String start_state = requestParams.get("state1");
        String end_state = requestParams.get("state2");

        String start_location = start_city + ',' + start_state;
        String end_location = end_city + ',' + end_state;
        //Define list to get all latlng for the route
        List<LatLng> path = new ArrayList();

        //Execute Directions API request
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey("AIzaSyAsxeRqO3WL308LSQdWPD1eOAUNmEw2_QA")
                .build();

        DirectionsApiRequest req = DirectionsApi.getDirections(context, start_location, end_location);
        try {
            DirectionsResult res = req.await();

            //Loop through legs and steps to get encoded polylines of each step
            if (res.routes != null && res.routes.length > 0) {
                DirectionsRoute route = res.routes[0];

                if (route.legs != null) {
                    for (int i = 0; i < route.legs.length; i++) {
                        DirectionsLeg leg = route.legs[i];
                        if (leg.steps != null) {
                            for (int j = 0; j < leg.steps.length; j++) {
                                DirectionsStep step = leg.steps[j];
                                if (step.steps != null && step.steps.length > 0) {
                                    for (int k = 0; k < step.steps.length; k++) {
                                        DirectionsStep step1 = step.steps[k];
                                        EncodedPolyline points1 = step1.polyline;
                                        if (points1 != null) {
                                            //Decode polyline and add points to list of route coordinates
                                            List<com.google.maps.model.LatLng> coords1 = points1.decodePath();
                                            for (com.google.maps.model.LatLng coord1 : coords1) {
                                                path.add(new LatLng(coord1.lat, coord1.lng));
                                            }
                                        }
                                    }
                                } else {
                                    EncodedPolyline points = step.polyline;
                                    if (points != null) {
                                        //Decode polyline and add points to list of route coordinates
                                        List<com.google.maps.model.LatLng> coords = points.decodePath();
                                        for (com.google.maps.model.LatLng coord : coords) {
                                            path.add(new LatLng(coord.lat, coord.lng));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            return ex.getLocalizedMessage();
        }
        return "Its sunny the entire way!" + path;

        //list by next 7 days
        //min temp max temp
        //10 miles?
        //predicted weather at that time of year
        //drop down menu?
    }
}
