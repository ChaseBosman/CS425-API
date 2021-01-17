package com.NotJustAWeatherApp.API.RouteWeatherController;

import com.fasterxml.jackson.databind.JsonNode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ResponseBuilder {

    JSONObject response =  new JSONObject();

    void buildResponse(List<JsonNode> route_weather_responses) throws JSONException {
        this.response.put("Status", "Success");

        //route_weather_responses.get(0).at("/properties/maxTemperature/values").toPrettyString();
    }

    String getResponse(){
        return this.response.toString();
    }

}
