package com.NotJustAWeatherApp.API.RouteWeatherController;

import com.fasterxml.jackson.databind.JsonNode;
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

            double[] maxTemp = getMaxTempValue(currentDay);
            dayForecast.put("maxTemp",maxTemp[0]);
            dayForecast.put("maxTempCoords",maxTemp[1] +"," + maxTemp[2]);

            double[] minTemp = getMinTempValue(currentDay);
            dayForecast.put("minTemp",minTemp[0]);
            dayForecast.put("minTempCoords",minTemp[1] +"," + minTemp[2]);

            dayForecast.put("maxWindSpeed",getWindSpeed(currentDay));
            double[] maxWindSpeed = getWindSpeed(currentDay);
            dayForecast.put("maxWindSpeed",maxWindSpeed[0]);
            dayForecast.put("maxWindSpeedCoords",maxWindSpeed[1] +"," + maxWindSpeed[2]);

            double[] maxWindGust = getWindGust(currentDay);
            dayForecast.put("maxWindGust",maxWindGust[0]);
            dayForecast.put("maxWindGustCoords",maxWindGust[1]
                    +"," + maxWindGust[2]);

            dayForecast.put("maxHumidity",getMaxHumidity(currentDay));
            double[] maxHumidity = getMaxHumidity(currentDay);
            dayForecast.put("maxHumidity",maxHumidity[0]);
            dayForecast.put("maxHumidityCoords",maxHumidity[1] +"," + maxHumidity[2]);

            dayForecast.put("minHumidity",getMinHumidity(currentDay));
            double[] minHumidity = getMinHumidity(currentDay);
            dayForecast.put("minHumidity",minHumidity[0]);
            dayForecast.put("minHumidityCoords",minHumidity[1] +"," + minHumidity[2]);

            double[] maxSnowfallTotal = getMaxSnowfallTotal(currentDay);
            dayForecast.put("maxSnowfallTotal",maxSnowfallTotal[0]);
            dayForecast.put("maxSnowfallTotalCoords",maxSnowfallTotal[1] +"," + maxSnowfallTotal[2]);

            double[] maxQuantitativePrecipitation = getMaxQuantitativePrecipitation(currentDay);
            dayForecast.put("maxQuantitativePrecipitation",maxQuantitativePrecipitation[0]);
            dayForecast.put("maxQuantitativePrecipitationCoords",maxQuantitativePrecipitation[1]
                    +"," + maxQuantitativePrecipitation[2]);

            double[] maxAvgProbabilityOfPrecipitation = getMaxAvgProbabilityOfPrecipitation(currentDay);
            dayForecast.put("maxAvgProbabilityOfPrecipitation",maxAvgProbabilityOfPrecipitation[0]);
            dayForecast.put("maxAvgProbabilityOfPrecipitationCoords",maxAvgProbabilityOfPrecipitation[1]
                    +"," + maxAvgProbabilityOfPrecipitation[2]);

            double[] maxProbabilityOfPrecipitation = getMaxProbabilityOfPrecipitation(currentDay);
            dayForecast.put("maxProbabilityOfPrecipitation",maxProbabilityOfPrecipitation[0]);
            dayForecast.put("maxProbabilityOfPrecipitationCoords",maxProbabilityOfPrecipitation[1]
                    +"," + maxProbabilityOfPrecipitation[2]);

            double[] visibility = getMinVisibility(currentDay);
            if(visibility[0] < Float.MAX_VALUE - 1) {
                dayForecast.put("minVisibility",visibility[0]);
                dayForecast.put("minVisibilityCoords",visibility[1]
                        +"," + visibility[2]);
            }
            else
            {
                dayForecast.put("minVisibility", "null");
            }
            this.response.put(dayForecast);
        }
    }

    double[] getMaxTempValue(int day)
    {
        float maxVal = -100;
        String lat = "";
        String lon = "";

        for(JsonNode route: route_weather_responses)
        {
            String atValue = "/properties/maxTemperature/values/" + Integer.toString(day) + "/value";
            String data = route.at(atValue).toPrettyString();
            if(!data.isEmpty())
            {
                float currVal = Float.parseFloat(data);
                if (currVal > maxVal)
                {
                    maxVal = currVal;
                    lat = route.at("/geometry/coordinates/0/0/1").toPrettyString();
                    lon = route.at("/geometry/coordinates/0/0/0").toPrettyString();
                }
            }
        }
        double[] temp = new double[3];
        temp[1] = Double.parseDouble(lat);
        temp[2] = Double.parseDouble(lon);
        temp[0] = Math.round(maxVal * 100.0) / 100.0;
        return temp;
    }

    double[] getMaxAvgProbabilityOfPrecipitation(int day)
    {
        String initial_date = getDate((0));
        int curr_day = Integer.parseInt(initial_date.substring(8,10));
        int curr_month = Integer.parseInt(initial_date.substring(5,7));
        int curr_year = Integer.parseInt(initial_date.substring(0,4));
        String searched_date = DateConverter.addDays(curr_day, curr_month, curr_year, day);

        float max_avg = 0;
        String lat = route_weather_responses.get(0).at("/geometry/coordinates/0/0/1").toPrettyString();
        String lon = route_weather_responses.get(0).at("/geometry/coordinates/0/0/0").toPrettyString();

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
                lat = route.at("/geometry/coordinates/0/0/1").toPrettyString();
                lon = route.at("/geometry/coordinates/0/0/0").toPrettyString();
            }
        }

        double[] temp = new double[3];
        temp[1] = Double.parseDouble(lat);
        temp[2] = Double.parseDouble(lon);
        temp[0] = Math.round(max_avg * 100.0) / 100.0;
        return temp;
    }

    double[] getMaxProbabilityOfPrecipitation(int day)
    {
        String initial_date = getDate((0));
        int curr_day = Integer.parseInt(initial_date.substring(8,10));
        int curr_month = Integer.parseInt(initial_date.substring(5,7));
        int curr_year = Integer.parseInt(initial_date.substring(0,4));
        String searched_date = DateConverter.addDays(curr_day, curr_month, curr_year, day);

        float max_value = 0;
        String lat = route_weather_responses.get(0).at("/geometry/coordinates/0/0/1").toPrettyString();
        String lon = route_weather_responses.get(0).at("/geometry/coordinates/0/0/0").toPrettyString();

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
                lat = route.at("/geometry/coordinates/0/0/1").toPrettyString();
                lon = route.at("/geometry/coordinates/0/0/0").toPrettyString();
            }
        }
        if(max_value == 0) {
            double[] temp = new double[3];
            temp[1] = 0;
            temp[2] = 0;
            temp[0] = 0;
            return temp;
        }
        double[] temp = new double[3];
        temp[1] = Double.parseDouble(lat);
        temp[2] = Double.parseDouble(lon);
        temp[0] = Math.round(max_value * 100.0) / 100.0;
        return temp;
    }

    double[] getMaxQuantitativePrecipitation(int day)
    {
        String initial_date = getDate((0));
        int curr_day = Integer.parseInt(initial_date.substring(8,10));
        int curr_month = Integer.parseInt(initial_date.substring(5,7));
        int curr_year = Integer.parseInt(initial_date.substring(0,4));
        String searched_date = DateConverter.addDays(curr_day, curr_month, curr_year, day);

        float max_count = 0;
        String lat = route_weather_responses.get(0).at("/geometry/coordinates/0/0/1").toPrettyString();
        String lon = route_weather_responses.get(0).at("/geometry/coordinates/0/0/0").toPrettyString();

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
                lat = route.at("/geometry/coordinates/0/0/1").toPrettyString();
                lon = route.at("/geometry/coordinates/0/0/0").toPrettyString();
            }
        }
        if(max_count == 0) {
            double[] temp = new double[3];
            temp[1] = 0;
            temp[2] = 0;
            temp[0] = 0;
            return temp;
        }
        double[] temp = new double[3];
        temp[1] = Double.parseDouble(lat);
        temp[2] = Double.parseDouble(lon);
        temp[0] = Math.round(max_count * 100.0) / 100.0;
        return temp;
    }

    double[] getMaxSnowfallTotal(int day)
    {
        String initial_date = getDate((0));
        int curr_day = Integer.parseInt(initial_date.substring(8,10));
        int curr_month = Integer.parseInt(initial_date.substring(5,7));
        int curr_year = Integer.parseInt(initial_date.substring(0,4));
        String searched_date = DateConverter.addDays(curr_day, curr_month, curr_year, day);

        float max_count = 0;
        String lat = route_weather_responses.get(0).at("/geometry/coordinates/0/0/1").toPrettyString();
        String lon = route_weather_responses.get(0).at("/geometry/coordinates/0/0/0").toPrettyString();

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
                lat = route.at("/geometry/coordinates/0/0/1").toPrettyString();
                lon = route.at("/geometry/coordinates/0/0/0").toPrettyString();
            }
        }
        if(max_count == 0) {
            double[] temp = new double[3];
            temp[1] = 0;
            temp[2] = 0;
            temp[0] = 0;
            return temp;
        }
        double[] temp = new double[3];
        temp[1] = Double.parseDouble(lat);
        temp[2] = Double.parseDouble(lon);
        temp[0] = Math.round(max_count * 100.0) / 100.0;
        return temp;
    }

    double[] getMaxHumidity(int day)
    {
        String initial_date = getDate((0));
        int curr_day = Integer.parseInt(initial_date.substring(8,10));
        int curr_month = Integer.parseInt(initial_date.substring(5,7));
        int curr_year = Integer.parseInt(initial_date.substring(0,4));
        String searched_date = DateConverter.addDays(curr_day, curr_month, curr_year, day);

        float maxVal = -100;
        String lat = route_weather_responses.get(0).at("/geometry/coordinates/0/0/1").toPrettyString();
        String lon = route_weather_responses.get(0).at("/geometry/coordinates/0/0/0").toPrettyString();

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
                    {
                        maxVal = currVal;
                        lat = route.at("/geometry/coordinates/0/0/1").toPrettyString();
                        lon = route.at("/geometry/coordinates/0/0/0").toPrettyString();
                    }
                }
            }
        }
        double[] temp = new double[3];
        temp[1] = Double.parseDouble(lat);
        temp[2] = Double.parseDouble(lon);
        temp[0] = Math.round(maxVal * 100.0) / 100.0;
        return temp;
    }

    double[] getMinHumidity(int day)
    {
        String initial_date = getDate((0));
        int curr_day = Integer.parseInt(initial_date.substring(8,10));
        int curr_month = Integer.parseInt(initial_date.substring(5,7));
        int curr_year = Integer.parseInt(initial_date.substring(0,4));
        String searched_date = DateConverter.addDays(curr_day, curr_month, curr_year, day);

        float minVal = 150;
        String lat = route_weather_responses.get(0).at("/geometry/coordinates/0/0/1").toPrettyString();
        String lon = route_weather_responses.get(0).at("/geometry/coordinates/0/0/0").toPrettyString();

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
                    if (currVal < minVal) {
                        minVal = currVal;
                        lat = route.at("/geometry/coordinates/0/0/1").toPrettyString();
                        lon = route.at("/geometry/coordinates/0/0/0").toPrettyString();
                    }
                }
            }
        }
        double[] temp = new double[3];
        temp[1] = Double.parseDouble(lat);
        temp[2] = Double.parseDouble(lon);
        temp[0] = Math.round(minVal * 100.0) / 100.0;
        return temp;
    }

    double[] getMinVisibility(int day)
    {
        String initial_date = getDate((0));
        int curr_day = Integer.parseInt(initial_date.substring(8,10));
        int curr_month = Integer.parseInt(initial_date.substring(5,7));
        int curr_year = Integer.parseInt(initial_date.substring(0,4));
        String searched_date = DateConverter.addDays(curr_day, curr_month, curr_year, day);

        float minVal = Float.MAX_VALUE;
        String lat = route_weather_responses.get(0).at("/geometry/coordinates/0/0/1").toPrettyString();
        String lon = route_weather_responses.get(0).at("/geometry/coordinates/0/0/0").toPrettyString();

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
                    if (currVal < minVal) {
                        minVal = currVal;
                        lat = route.at("/geometry/coordinates/0/0/1").toPrettyString();
                        lon = route.at("/geometry/coordinates/0/0/0").toPrettyString();
                    }
                }
            }
        }

        if(minVal == Float.MAX_VALUE) {
            double[] temp = new double[3];
            temp[1] = 0;
            temp[2] = 0;
            temp[0] = minVal;
            return temp;
        }
        double[] temp = new double[3];
        temp[1] = Double.parseDouble(lat);
        temp[2] = Double.parseDouble(lon);
        temp[0] = Math.round(minVal * 100.0) / 100.0;
        return temp;
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

    double[] getWindSpeed(int day)
    {
        String initial_date = getDate((0));
        int curr_day = Integer.parseInt(initial_date.substring(8,10));
        int curr_month = Integer.parseInt(initial_date.substring(5,7));
        int curr_year = Integer.parseInt(initial_date.substring(0,4));
        String searched_date = DateConverter.addDays(curr_day, curr_month, curr_year, day);

        float maxVal = -100;
        String lat = "";
        String lon = "";

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
                    if (currVal > maxVal) {
                        maxVal = currVal;
                        lat = route.at("/geometry/coordinates/0/0/1").toPrettyString();
                        lon = route.at("/geometry/coordinates/0/0/0").toPrettyString();
                    }
                }
            }
        }
        double[] temp = new double[3];
        temp[1] = Double.parseDouble(lat);
        temp[2] = Double.parseDouble(lon);
        temp[0] = Math.round(maxVal * 100.0) / 100.0;
        return temp;
    }

    double[] getWindGust(int day)
    {
        String initial_date = getDate((0));
        int curr_day = Integer.parseInt(initial_date.substring(8,10));
        int curr_month = Integer.parseInt(initial_date.substring(5,7));
        int curr_year = Integer.parseInt(initial_date.substring(0,4));
        String searched_date = DateConverter.addDays(curr_day, curr_month, curr_year, day);

        float maxVal = -100;
        String lat = "";
        String lon = "";

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
                    {
                        maxVal = currVal;
                        lat = route.at("/geometry/coordinates/0/0/1").toPrettyString();
                        lon = route.at("/geometry/coordinates/0/0/0").toPrettyString();
                    }
                }
            }
        }
        double[] temp = new double[3];
        temp[1] = Double.parseDouble(lat);
        temp[2] = Double.parseDouble(lon);
        temp[0] = Math.round(maxVal * 100.0) / 100.0;
        return temp;
    }

    double[] getMinTempValue(int day)
    {
        float minVal = 100;
        String lat = "";
        String lon = "";

        for(JsonNode route: route_weather_responses)
        {
            String atValue = "/properties/minTemperature/values/" + Integer.toString(day) + "/value";
            String data = route.at(atValue).toPrettyString();
            if(!data.isEmpty())
            {
                float currVal = Float.parseFloat(data);
                if (currVal < minVal)
                    minVal = currVal;
                    lat = route.at("/geometry/coordinates/0/0/1").toPrettyString();
                    lon = route.at("/geometry/coordinates/0/0/0").toPrettyString();
            }
        }

        double[] temp = new double[3];
        temp[1] = Double.parseDouble(lat);
        temp[2] = Double.parseDouble(lon);
        temp[0] = Math.round(minVal * 100.0) / 100.0;
        return temp;
    }

    String getResponse(){
        return this.response.toString();
    }

}
