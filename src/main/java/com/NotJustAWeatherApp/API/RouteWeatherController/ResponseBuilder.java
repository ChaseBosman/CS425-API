package com.NotJustAWeatherApp.API.RouteWeatherController;

import com.fasterxml.jackson.databind.JsonNode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ResponseBuilder {

    private volatile JSONArray response = new JSONArray();
    private volatile List<JsonNode> route_weather_responses;

    ResponseBuilder(List<JsonNode> route_weather_responses)
    {
        this.route_weather_responses = route_weather_responses;
    }

    void buildResponse() throws JSONException
    {

        // List<JSONObject> dayJsonObjects = new ArrayList();
        JSONObject dayForecasts = new JSONObject();

        for(int currentDay = 0; currentDay < 8; ++currentDay)
        {
            JSONObject dayForecast = new JSONObject();
            dayForecast.put("date",getDate(currentDay));
            dayForecast.put("max",getMaxTempValue(currentDay));
            dayForecast.put("min",getMinTempValue(currentDay));
            this.response.put(dayForecast);
        }
    }

    float getMaxTempValue(int day)
    {
        float maxVal = -100;

        for(JsonNode route: route_weather_responses)
        {
            String atValue = "/properties/maxTemperature/values/" + Integer.toString(day) + "/value";
            String data = route.at(atValue).toPrettyString();
            if(!data.isEmpty())
            {
                float currVal = Float.parseFloat(data);
                if (currVal > maxVal)
                    maxVal = currVal;
            }
        }

        return maxVal;
    }

    String getDate(int day)
    {
        for(JsonNode route: route_weather_responses)
        {
            String atValue = "/properties/maxTemperature/values/" + Integer.toString(day) + "/validTime";
            String data = route.at(atValue).toPrettyString();
            if(!data.isEmpty())
            {
                return data;
            }
        }
        return "";
    }

    float getMinTempValue(int day)
    {
        float minVal = 100;

        for(JsonNode route: route_weather_responses)
        {
            String atValue = "/properties/minTemperature/values/" + Integer.toString(day) + "/value";
            String data = route.at(atValue).toPrettyString();
            if(!data.isEmpty())
            {
                float currVal = Float.parseFloat(data);
                if (currVal < minVal)
                    minVal = currVal;
            }
        }

        return minVal;
    }

    String getResponse(){
        return this.response.toString();
    }

}
