package cz.client;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CountriesCitiesApiClient {

    private final OkHttpClient client;
    private final Gson gson;

    public CountriesCitiesApiClient() {
        this.client = new OkHttpClient();
        this.gson = new Gson();
    }

    private JsonObject get(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code: " + response);
            }
            String responseBody = response.body().string();
            return gson.fromJson(responseBody, JsonObject.class);
        }
    }
    private JsonObject post(String url, JsonObject body) throws IOException {
        okhttp3.MediaType JSON = okhttp3.MediaType.parse("application/json; charset=utf-8");
        okhttp3.RequestBody requestBody = okhttp3.RequestBody.create(body.toString(), JSON);
    
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
    
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code: " + response);
            }
            String responseBody = response.body().string();
            return gson.fromJson(responseBody, JsonObject.class);
        }
    }
    


    
    public JsonObject getPopulationOfCzechRepublic() throws IOException {
        String url = "https://countriesnow.space/api/v0.1/countries/population/q?country=Czech%20Republic";
        return get(url);
    }
    public JsonObject getAllCitiesOfCzechRepublic() throws IOException {
        String url = "https://countriesnow.space/api/v0.1/countries/cities/q?country=Czech%20Republic";
        return get(url);
    }
    public JsonObject get3CitiesofCzechRepublic() throws IOException {
        // Get all cities first
        JsonObject allCitiesResponse = get("https://countriesnow.space/api/v0.1/countries/cities/q?country=Czech%20Republic");

        // Extract the 'data' array from the response
        JsonArray citiesArray = allCitiesResponse.getAsJsonArray("data");

        // Convert JsonArray to List<String> to sort it
        List<String> cities = new ArrayList<>();
        for (JsonElement cityElement : citiesArray) {
            cities.add(cityElement.getAsString());
        }

        // Sort the list ascending
        Collections.sort(cities);

        // Take first 3 cities
        List<String> first3Cities = cities.subList(0, Math.min(3, cities.size()));

        // Create a new JsonObject to return
        JsonObject result = new JsonObject();
        JsonArray first3CitiesJsonArray = new JsonArray();
        for (String city : first3Cities) {
            first3CitiesJsonArray.add(city);
        }
        result.add("cities", first3CitiesJsonArray);

        return result;
    }
    public JsonObject getFlagOfCzechRepublic() throws IOException {
        String url = "https://countriesnow.space/api/v0.1/countries/flag/images";
    
        // Připrav JSON s tělem požadavku
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("country", "Czech Republic");
    
        // Udělej POST požadavek (musíš implementovat metodu post(url, body))
        JsonObject response = post(url, requestBody);
    
        // Zpracuj odpověď
        JsonObject data = response.getAsJsonObject("data");
    
        JsonObject result = new JsonObject();
        result.addProperty("flagUri", data.get("flag").getAsString());
        return result;
    }
    public JsonObject getAllCountriesAndCurrencies() throws IOException {
        String url = "https://countriesnow.space/api/v0.1/countries/currency";
        return get(url);
    }
    public JsonObject getAllCountriesCurrenciesAndDialCodes() throws IOException {
        String currencyUrl = "https://countriesnow.space/api/v0.1/countries/currency";
        String dialCodeUrl = "https://countriesnow.space/api/v0.1/countries/codes";
    
        JsonObject currenciesResponse = get(currencyUrl);
        JsonObject dialCodesResponse = get(dialCodeUrl);
    
        JsonArray currenciesArray = currenciesResponse.getAsJsonArray("data");
        JsonArray dialCodesArray = dialCodesResponse.getAsJsonArray("data");
    
        JsonObject result = new JsonObject();
        JsonArray countriesData = new JsonArray();
    
        for (JsonElement currencyElement : currenciesArray) {
            JsonObject currencyObj = currencyElement.getAsJsonObject();
            String countryName = currencyObj.get("country").getAsString();
            
            // Find matching dial code for the country
            JsonElement dialCodeData = null;
            for (JsonElement dialCodeElement : dialCodesArray) {
                JsonObject dialCodeObj = dialCodeElement.getAsJsonObject();
                if (dialCodeObj.get("country").getAsString().equals(countryName)) {
                    dialCodeData = dialCodeObj;
                    break;
                }
            }
            
            String dialCode = dialCodeData != null ? dialCodeData.getAsJsonObject().get("dial_code").getAsString() : "N/A";
            String currency = currencyObj.get("currency").getAsString();
    
            // Add country, currency, and dial code to the result
            JsonObject countryData = new JsonObject();
            countryData.addProperty("country", countryName);
            countryData.addProperty("currency", currency);
            countryData.addProperty("dial_code", dialCode);
    
            countriesData.add(countryData);
        }
    
        result.add("countries", countriesData);
        return result;
    }
    
    
    
    
    
    
    

    
}
