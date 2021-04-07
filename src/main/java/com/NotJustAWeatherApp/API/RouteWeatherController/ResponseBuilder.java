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
        for(int currentDay = 0; currentDay < 8; ++currentDay)
        {
            JSONObject dayForecast = new JSONObject();
            dayForecast.put("date",getDate(currentDay));
            dayForecast.put("maxTemp",getMaxTempValue(currentDay));
            dayForecast.put("minTemp",getMinTempValue(currentDay));
            dayForecast.put("maxWindSpeed",getWindSpeed(currentDay));
            dayForecast.put("maxWindGust",getWindGust(currentDay));
            dayForecast.put("maxHumidity",getMaxHumidity(currentDay));
            dayForecast.put("minHumidity",getMinHumidity(currentDay));
            dayForecast.put("maxSnowfallTotal",getMaxSnowfallTotal(currentDay));
            dayForecast.put("maxQuantitativePrecipitation",getMaxQuantitativePrecipitation(currentDay));
            dayForecast.put("maxAvgProbabilityOfPrecipitation",getMaxAvgProbabilityOfPrecipitation(currentDay));
            dayForecast.put("maxProbabilityOfPrecipitation",getMaxProbabilityOfPrecipitation(currentDay));

            double visibility = getMinVisibility(currentDay);
            if(visibility < Float.MAX_VALUE - 1) {
                dayForecast.put("minVisibility", visibility);
            }
            else
            {
                dayForecast.put("minVisibility", "null");
            }
            this.response.put(dayForecast);
        }
    }

    double getMaxTempValue(int day)
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

        return Math.round(maxVal * 100.0) / 100.0;
    }

    double getMaxAvgProbabilityOfPrecipitation(int day)
    {
        String initial_date = getDate((0));
        int curr_day = Integer.parseInt(initial_date.substring(8,10));
        int curr_month = Integer.parseInt(initial_date.substring(5,7));
        int curr_year = Integer.parseInt(initial_date.substring(0,4));
        String searched_date = DateConverter.addDays(curr_day, curr_month, curr_year, day);

        float max_avg = 0;

        for(JsonNode route: route_weather_responses)
        {
            String atValue = "/properties/probabilityOfPrecipitation/values/";
            float sum = 0;
            int count = 0;
            String value_response = " ";
            String date_response = "";
            int accumulator = 0;

            while (!value_response.equals(""))
            {
                value_response = route.at(atValue + accumulator + "/value").toPrettyString();
                date_response = route.at(atValue + accumulator + "/validTime").toPrettyString();
                accumulator++;
                if (!value_response.equals("") && date_response.contains(searched_date))
                {
                    sum += Float.parseFloat(value_response);
                    count++;
                }
            }

            float avg = sum / count;
            if(avg > max_avg)
            {
                max_avg = avg;
            }
        }
        return Math.round(max_avg * 100.0) / 100.0;
    }

    double getMaxProbabilityOfPrecipitation(int day)
    {
        String initial_date = getDate((0));
        int curr_day = Integer.parseInt(initial_date.substring(8,10));
        int curr_month = Integer.parseInt(initial_date.substring(5,7));
        int curr_year = Integer.parseInt(initial_date.substring(0,4));
        String searched_date = DateConverter.addDays(curr_day, curr_month, curr_year, day);

        float max_value = 0;

        for(JsonNode route: route_weather_responses)
        {
            String atValue = "/properties/probabilityOfPrecipitation/values/";
            float value = 0;
            String value_response = " ";
            String date_response = "";
            int accumulator = 0;

            while (!value_response.equals(""))
            {
                value_response = route.at(atValue + accumulator + "/value").toPrettyString();
                date_response = route.at(atValue + accumulator + "/validTime").toPrettyString();
                accumulator++;
                if (!value_response.equals("") && date_response.contains(searched_date))
                {
                    value = Float.parseFloat(value_response);
                }
            }

            if(value > max_value)
            {
                max_value = value;
            }
        }
        return Math.round(max_value * 100.0) / 100.0;
    }

    double getMaxQuantitativePrecipitation(int day)
    {
        String initial_date = getDate((0));
        int curr_day = Integer.parseInt(initial_date.substring(8,10));
        int curr_month = Integer.parseInt(initial_date.substring(5,7));
        int curr_year = Integer.parseInt(initial_date.substring(0,4));
        String searched_date = DateConverter.addDays(curr_day, curr_month, curr_year, day);

        float max_count = 0;

        for(JsonNode route: route_weather_responses)
        {
            String atValue = "/properties/quantitativePrecipitation/values/";
            float count = 0;
            String value_response = " ";
            String date_response = "";
            int accumulator = 0;

            while (!value_response.equals(""))
            {
                value_response = route.at(atValue + accumulator + "/value").toPrettyString();
                date_response = route.at(atValue + accumulator + "/validTime").toPrettyString();
                accumulator++;
                if (!value_response.equals("") && date_response.contains(searched_date))
                {
                    count += Float.parseFloat(value_response);
                }
            }

            if(count > max_count)
            {
                max_count = count;
            }
        }
        return Math.round(max_count * 100.0) / 100.0;
    }

    double getMaxSnowfallTotal(int day)
    {
        String initial_date = getDate((0));
        int curr_day = Integer.parseInt(initial_date.substring(8,10));
        int curr_month = Integer.parseInt(initial_date.substring(5,7));
        int curr_year = Integer.parseInt(initial_date.substring(0,4));
        String searched_date = DateConverter.addDays(curr_day, curr_month, curr_year, day);

        float max_count = 0;

        for(JsonNode route: route_weather_responses)
        {
            String atValue = "/properties/snowfallAmount/values/";
            float count = 0;
            String value_response = " ";
            String date_response = "";
            int accumulator = 0;

            while (!value_response.equals(""))
            {
                value_response = route.at(atValue + accumulator + "/value").toPrettyString();
                date_response = route.at(atValue + accumulator + "/validTime").toPrettyString();
                accumulator++;
                if (!value_response.equals("") && date_response.contains(searched_date))
                {
                    count += Float.parseFloat(value_response);
                }
            }

            if(count > max_count)
            {
                max_count = count;
            }
        }
        return Math.round(max_count * 100.0) / 100.0;
    }

    double getMaxHumidity(int day)
    {
        String initial_date = getDate((0));
        int curr_day = Integer.parseInt(initial_date.substring(8,10));
        int curr_month = Integer.parseInt(initial_date.substring(5,7));
        int curr_year = Integer.parseInt(initial_date.substring(0,4));
        String searched_date = DateConverter.addDays(curr_day, curr_month, curr_year, day);

        float maxVal = -100;

        for(JsonNode route: route_weather_responses) {
            String atValue = "/properties/relativeHumidity/values/";

            String value_response = " ";
            String date_response = "";
            int accumulator = 0;

            while (!value_response.equals("")) {
                value_response = route.at(atValue + accumulator + "/value").toPrettyString();
                date_response = route.at(atValue + accumulator + "/validTime").toPrettyString();
                accumulator++;
                if (!value_response.equals("") && date_response.contains(searched_date))
                {
                    float currVal = Float.parseFloat(value_response);
                    if (currVal > maxVal)
                        maxVal = currVal;
                }
            }
        }
        return Math.round(maxVal * 100.0) / 100.0;
    }

    double getMinHumidity(int day)
    {
        String initial_date = getDate((0));
        int curr_day = Integer.parseInt(initial_date.substring(8,10));
        int curr_month = Integer.parseInt(initial_date.substring(5,7));
        int curr_year = Integer.parseInt(initial_date.substring(0,4));
        String searched_date = DateConverter.addDays(curr_day, curr_month, curr_year, day);

        float minVal = 150;

        for(JsonNode route: route_weather_responses) {
            String atValue = "/properties/relativeHumidity/values/";

            String value_response = " ";
            String date_response = "";
            int accumulator = 0;

            while (!value_response.equals("")) {
                value_response = route.at(atValue + accumulator + "/value").toPrettyString();
                date_response = route.at(atValue + accumulator + "/validTime").toPrettyString();
                accumulator++;
                if (!value_response.equals("") && date_response.contains(searched_date))
                {
                    float currVal = Float.parseFloat(value_response);
                    if (currVal < minVal)
                        minVal = currVal;
                }
            }
        }
        return Math.round(minVal * 100.0) / 100.0;
    }

    double getMinVisibility(int day)
    {
        String initial_date = getDate((0));
        int curr_day = Integer.parseInt(initial_date.substring(8,10));
        int curr_month = Integer.parseInt(initial_date.substring(5,7));
        int curr_year = Integer.parseInt(initial_date.substring(0,4));
        String searched_date = DateConverter.addDays(curr_day, curr_month, curr_year, day);

        float minVal = Float.MAX_VALUE;

        for(JsonNode route: route_weather_responses) {
            String atValue = "/properties/visibility/values/";

            String value_response = " ";
            String date_response = "";
            int accumulator = 0;

            while (!value_response.equals("")) {
                value_response = route.at(atValue + accumulator + "/value").toPrettyString();
                date_response = route.at(atValue + accumulator + "/validTime").toPrettyString();
                accumulator++;
                if (!value_response.equals("") && date_response.contains(searched_date))
                {
                    float currVal = Float.parseFloat(value_response);
                    if (currVal < minVal)
                        minVal = currVal;
                }
            }
        }

        if(minVal == Float.MAX_VALUE) {
            return minVal;
        }
        return Math.round(minVal * 100.0) / 100.0;
    }

    String getDate(int day)
    {
        for(JsonNode route: route_weather_responses)
        {
            String atValue = "/properties/maxTemperature/values/" + Integer.toString(day) + "/validTime";
            String initial_date = route.at(atValue).toPrettyString();
            if(!initial_date.isEmpty())
            {
                int curr_day = Integer.parseInt(initial_date.substring(9,11));
                int curr_month = Integer.parseInt(initial_date.substring(6,8));
                int curr_year = Integer.parseInt(initial_date.substring(1,5));
                return curr_year + "-" + String.format("%02d", curr_month) + "-" + String.format("%02d", curr_day);
            }
        }
        return "";
    }

    double getWindSpeed(int day)
    {
        String initial_date = getDate((0));
        int curr_day = Integer.parseInt(initial_date.substring(8,10));
        int curr_month = Integer.parseInt(initial_date.substring(5,7));
        int curr_year = Integer.parseInt(initial_date.substring(0,4));
        String searched_date = DateConverter.addDays(curr_day, curr_month, curr_year, day);

        float maxVal = -100;

        for(JsonNode route: route_weather_responses) {
            String atValue = "/properties/windSpeed/values/";

            String value_response = " ";
            String date_response = "";
            int accumulator = 0;

            while (!value_response.equals("")) {
                value_response = route.at(atValue + accumulator + "/value").toPrettyString();
                date_response = route.at(atValue + accumulator + "/validTime").toPrettyString();
                accumulator++;
                if (!value_response.equals("") && date_response.contains(searched_date))
                {
                    float currVal = Float.parseFloat(value_response);
                    if (currVal > maxVal)
                        maxVal = currVal;
                }
            }
        }
        return Math.round(maxVal * 100.0) / 100.0;
    }

    double getWindGust(int day)
    {
        String initial_date = getDate((0));
        int curr_day = Integer.parseInt(initial_date.substring(8,10));
        int curr_month = Integer.parseInt(initial_date.substring(5,7));
        int curr_year = Integer.parseInt(initial_date.substring(0,4));
        String searched_date = DateConverter.addDays(curr_day, curr_month, curr_year, day);

        float maxVal = -100;

        for(JsonNode route: route_weather_responses) {
            String atValue = "/properties/windGust/values/";

            String value_response = " ";
            String date_response = "";
            int accumulator = 0;

            while (!value_response.equals("")) {
                value_response = route.at(atValue + accumulator + "/value").toPrettyString();
                date_response = route.at(atValue + accumulator + "/validTime").toPrettyString();
                accumulator++;
                if (!value_response.equals("") && date_response.contains(searched_date))
                {
                    float currVal = Float.parseFloat(value_response);
                    if (currVal > maxVal)
                        maxVal = currVal;
                }
            }
        }
        return Math.round(maxVal * 100.0) / 100.0;
    }

    double getMinTempValue(int day)
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

        return Math.round(minVal * 100.0) / 100.0;
    }

    String getResponse(){
        return this.response.toString();
    }

}
