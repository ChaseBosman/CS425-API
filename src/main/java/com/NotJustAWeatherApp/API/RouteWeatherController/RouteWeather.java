package com.NotJustAWeatherApp.API.RouteWeatherController;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.*;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
//http://localhost:8080/weather/multiple?city1=Brentwood&city2=Antioch&state1=CA&state2=CA

@RequestMapping(value = "/weather")
public class RouteWeather {    // create GET endpoint to serve demo data at /demo/data

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping(value = "/multiple")
    public String getDemoData(@RequestParam Map<String,String> requestParams) throws JSONException {
        String start_city = requestParams.get("city1");
        String end_city = requestParams.get("city2");
        String start_state = requestParams.get("state1");
        String end_state = requestParams.get("state2");

        String start_location = start_city + ',' + start_state;
        String end_location = end_city + ',' + end_state;
        //Define list to get all latlng for the route
        List<LatLng> path = new ArrayList();

        //Execute Directions API request
        //REMOVE THIS API KEY
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

        List<LatLng> weather_coordinates = new ArrayList();
        double dist;
        double lon1, lat1, lon2, lat2;
        LatLng beginning = path.get(0);
        LatLng end = path.get(0);
        weather_coordinates.add(beginning);

        double p = Math.PI / 180;

        for(int position = 1; position < path.size(); position++)
        {
            end = path.get(position);
            lat1 = beginning.lat;
            lon1 = beginning.lng;
            lat2 = end.lat;
            lon2 = end.lng;

            dist = .5 - Math.cos((lat2-lat1)*p)/2 + Math.cos(lat1*p) * Math.cos(lat2*p) * (1-Math.cos((lon2-lon1)*p))/2;
            dist = Math.sqrt(dist);
            dist = Math.asin(dist) * 12742;
            dist = dist * .621371;

            if(dist >= 10){
                weather_coordinates.add(end);
                beginning = end;
            }
        }
        weather_coordinates.add(end);

        List<JsonNode> route_weather_responses = new ArrayList();

        for (LatLng weather_coordinate : weather_coordinates) {
            double lat = weather_coordinate.lat;
            double lon = weather_coordinate.lng;
            final String pointsURI = "https://api.weather.gov/points/" + lat + ',' + lon;

            JsonNode pointProperties = restTemplate.getForObject(pointsURI, JsonNode.class);
            String gridURI = pointProperties.get("properties").get("forecastGridData").toString();
            gridURI = gridURI.replace("\"", "");

            route_weather_responses.add(restTemplate.getForObject(gridURI, JsonNode.class));
        }

        ResponseBuilder response = new ResponseBuilder();
        response.buildResponse(route_weather_responses);

        return response.getResponse();

        //later add predicted weather if outside of window for forecasting
        //allow support for front end drop down menu to select what specific weather statistics?
    }
}
