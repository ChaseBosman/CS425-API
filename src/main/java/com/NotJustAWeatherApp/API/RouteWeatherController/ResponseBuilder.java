package com.NotJustAWeatherApp.API.RouteWeatherController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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
            getWindSpeed(currentDay);
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

    String getWindSpeed(int day)
    {
        String initial_date = getDate((0));
        int curr_day = Integer.parseInt(initial_date.substring(9,11));
        int curr_month = Integer.parseInt(initial_date.substring(6,8));
        int curr_year = Integer.parseInt(initial_date.substring(1,5));
        String searched_date = DateConverter.addDays(curr_day, curr_month, curr_year, day);
        System.out.println(searched_date);

        float maxVal = -100;

        for(JsonNode route: route_weather_responses) {
            String atValue = "/properties/windSpeed/values/";

            String response = " ";
            int accumulator = 0;

            while(!response.equals(""))
            {
                response = route.at(atValue + accumulator+ "/value").toPrettyString();
                accumulator++;
                System.out.println(accumulator + " "+ response);
            }

            /*/String data = route.at(atValue).toPrettyString();
            //System.out.println(data);

            if(!data.isEmpty())
            {
                float currVal = Float.parseFloat(data);
                if (currVal > maxVal)
                    maxVal = currVal;
            }
        }

        return String.valueOf(maxVal);

             */
        }
        return "";
        //find data for searched data
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
