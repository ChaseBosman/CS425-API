package com.NotJustAWeatherApp.API.RouteWeatherController;

import com.fasterxml.jackson.databind.JsonNode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ResponseBuilder {

    JSONObject response =  new JSONObject();
    List<JsonNode> route_weather_responses;

    ResponseBuilder(List<JsonNode> route_weather_responses)
    {
        this.route_weather_responses = route_weather_responses;
    }

    void buildResponse() throws JSONException
    {

        List<JSONObject> dayJsonObjects = new ArrayList();

        for(int currentDay = 0; currentDay < 8; ++currentDay)
        {
            dayJsonObjects.add(new JSONObject());
            dayJsonObjects.get(currentDay).put("max",getMaxTempValue(currentDay));
            dayJsonObjects.get(currentDay).put("min",getMinTempValue(currentDay));
            this.response.put("Day" + Integer.toString(currentDay), dayJsonObjects.get(currentDay));
        }

        //this.response.put("Status", "Success");
    }

    float getMaxTempValue(int day)
    {
        float maxVal = -100;

        for(JsonNode route: route_weather_responses)
        {
            String atValue = "/properties/maxTemperature/values/" + Integer.toString(day) + "/value";
            String data = route.at(atValue).toPrettyString();
            float currVal = Float.parseFloat(data);
            if (currVal > maxVal)
                maxVal = currVal;
        }

        return maxVal;
    }

    float getMinTempValue(int day)
    {
        float minVal = 100;

        for(JsonNode route: route_weather_responses)
        {
            String atValue = "/properties/minTemperature/values/" + Integer.toString(day) + "/value";
            String data = route.at(atValue).toPrettyString();
            float currVal = Float.parseFloat(data);
            if (currVal < minVal)
                minVal = currVal;
        }

        return minVal;
    }

    String getResponse(){
        return this.response.toString();
    }

}
