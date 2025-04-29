package cz.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * This class provides an API client for retrieving population, city, currency,
 * flag, and dialing code information from the countriesnow.space public API.
 */
public class CountriesCitiesApiClient {

    private final OkHttpClient client; // HTTP client for making requests
    private final Gson gson;           // Gson library for parsing JSON

    /**
     * Constructs a new instance of the CountriesCitiesApiClient.
     */
    public CountriesCitiesApiClient() {
        this.client = new OkHttpClient();
        this.gson = new Gson();
    }

    /**
     * Sends a GET request to the specified URL and parses the response into a JsonObject.
     * 
     * @param url The URL to send the GET request to.
     * @return The response as a JsonObject.
     * @throws IOException If an I/O error occurs during the request.
     */
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

    /**
     * Sends a POST request with a JSON body and parses the response into a JsonObject.
     * 
     * @param url The URL to send the POST request to.
     * @param body The JSON object to send in the body of the POST request.
     * @return The response as a JsonObject.
     * @throws IOException If an I/O error occurs during the request.
     */
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

    /**
     * Validates the API response to check for errors.
     * 
     * @param response The API response in the form of a JsonObject.
     * @throws IOException If the API response contains an error.
     */
    private void validateResponse(JsonObject response) throws IOException {
        if (response.has("error") && response.get("error").getAsBoolean()) {
            String message = response.has("msg") ? response.get("msg").getAsString() : "Unknown error";
            throw new IOException("API Error: " + message);
        }
    }

    /**
     * Retrieves historical population data for the Czech Republic.
     * 
     * @return The population data as a JsonObject.
     * @throws IOException If an error occurs during the API request.
     */
    public JsonObject getPopulationOfCzechRepublic() throws IOException {
        String url = "https://countriesnow.space/api/v0.1/countries/population/q?country=Czech%20Republic";
        JsonObject response = get(url);
        validateResponse(response);
        return response;
    }

    /**
     * Retrieves a full list of cities in the Czech Republic.
     * 
     * @return A list of cities in the Czech Republic as a JsonObject.
     * @throws IOException If an error occurs during the API request.
     */
    public JsonObject getAllCitiesOfCzechRepublic() throws IOException {
        String url = "https://countriesnow.space/api/v0.1/countries/cities/q?country=Czech%20Republic";
        JsonObject response = get(url);
        validateResponse(response);
        return response;
    }

    /**
     * Retrieves the first three cities (alphabetically sorted) from the Czech Republic.
     * 
     * @return A JsonObject containing the first three cities.
     * @throws IOException If an error occurs during the API request.
     */
    public JsonObject get3CitiesofCzechRepublic() throws IOException {
        JsonObject allCitiesResponse = get("https://countriesnow.space/api/v0.1/countries/cities/q?country=Czech%20Republic");
        validateResponse(allCitiesResponse);

        JsonArray citiesArray = allCitiesResponse.getAsJsonArray("data");

        List<String> cities = new ArrayList<>();
        for (JsonElement cityElement : citiesArray) {
            cities.add(cityElement.getAsString());
        }

        Collections.sort(cities); // Sort alphabetically
        List<String> first3Cities = cities.subList(0, Math.min(3, cities.size()));

        JsonObject result = new JsonObject();
        JsonArray first3CitiesJsonArray = new JsonArray();
        for (String city : first3Cities) {
            first3CitiesJsonArray.add(city);
        }
        result.add("cities", first3CitiesJsonArray);

        return result;
    }

    /**
     * Retrieves the flag image URL for the Czech Republic.
     * 
     * @return A JsonObject containing the flag URL.
     * @throws IOException If an error occurs during the API request.
     */
    public JsonObject getFlagOfCzechRepublic() throws IOException {
        String url = "https://countriesnow.space/api/v0.1/countries/flag/images";
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("country", "Czech Republic");

        JsonObject response = post(url, requestBody);
        validateResponse(response);

        JsonObject data = response.getAsJsonObject("data");

        JsonObject result = new JsonObject();
        result.addProperty("flagUri", data.get("flag").getAsString());
        return result;
    }

    /**
     * Retrieves a list of all countries with their currencies.
     * 
     * @return A JsonObject containing the list of countries and their currencies.
     * @throws IOException If an error occurs during the API request.
     */
    public JsonObject getAllCountriesAndCurrencies() throws IOException {
        String url = "https://countriesnow.space/api/v0.1/countries/currency";
        JsonObject fullResponse = get(url);
        validateResponse(fullResponse);

        JsonArray originalData = fullResponse.getAsJsonArray("data");
        JsonArray filteredData = new JsonArray();

        for (JsonElement element : originalData) {
            JsonObject originalItem = element.getAsJsonObject();
            JsonObject filteredItem = new JsonObject();
            filteredItem.addProperty("country", originalItem.get("name").getAsString());
            filteredItem.addProperty("currency", originalItem.get("currency").getAsString());
            filteredData.add(filteredItem);
        }

        JsonObject result = new JsonObject();
        result.add("currency", filteredData);
        return result;
    }

    /**
     * Retrieves countries with both currency and dialing code information.
     * 
     * @return A JsonObject containing the countries, currencies, and dialing codes.
     * @throws IOException If an error occurs during the API request.
     */
    public JsonObject getAllCountriesCurrenciesAndDialCodes() throws IOException {
        String currencyUrl = "https://countriesnow.space/api/v0.1/countries/currency";
        String dialCodeUrl = "https://countriesnow.space/api/v0.1/countries/codes";

        JsonObject currenciesResponse = get(currencyUrl);
        validateResponse(currenciesResponse);
        JsonObject dialCodesResponse = get(dialCodeUrl);
        validateResponse(dialCodesResponse);

        JsonArray currenciesData = currenciesResponse.getAsJsonArray("data");
        JsonArray dialCodesData = dialCodesResponse.getAsJsonArray("data");

        // Create a map of country name -> dial code
        Map<String, String> dialCodeMap = new HashMap<>();
        for (JsonElement element : dialCodesData) {
            JsonObject obj = element.getAsJsonObject();
            dialCodeMap.put(obj.get("name").getAsString(), obj.get("dial_code").getAsString());
        }

        JsonArray combinedData = new JsonArray();
        for (JsonElement element : currenciesData) {
            JsonObject currencyObj = element.getAsJsonObject();
            String countryName = currencyObj.get("name").getAsString();
            String currency = currencyObj.get("currency").getAsString();

            // Add only if dialing code exists for the country
            if (dialCodeMap.containsKey(countryName)) {
                JsonObject combined = new JsonObject();
                combined.addProperty("country", countryName);
                combined.addProperty("currency", currency);
                combined.addProperty("dial_code", dialCodeMap.get(countryName));
                combinedData.add(combined);
            }
        }

        JsonObject result = new JsonObject();
        result.add("codes", combinedData);
        return result;
    }

    /**
     * Compares population growth from the first to latest year among selected countries.
     * 
     * @return A JsonObject containing the population and growth data for selected countries.
     * @throws IOException If an error occurs during the API request.
     */
    public JsonObject comparePopulationsAndGrowths() throws IOException {
        String[] countries = {"Czech Republic", "Germany", "Austria", "Slovak Republic", "Poland"};
        JsonArray resultArray = new JsonArray();

        for (String country : countries) {
            String url = "https://countriesnow.space/api/v0.1/countries/population/q?country=" + country.replace(" ", "%20");
            JsonObject response = get(url);
            validateResponse(response);

            JsonObject data = response.getAsJsonObject("data");
            JsonArray populationCounts = data.getAsJsonArray("populationCounts");

            if (populationCounts.size() == 0) continue;

            JsonObject latest = populationCounts.get(populationCounts.size() - 1).getAsJsonObject();
            JsonObject first = populationCounts.get(0).getAsJsonObject();

            int latestPopulation = latest.get("value").getAsInt();
            int latestYear = latest.get("year").getAsInt();
            int firstPopulation = first.get("value").getAsInt();

            int growth = latestPopulation - firstPopulation;

            JsonObject countryResult = new JsonObject();
            countryResult.addProperty("country", country);
            countryResult.addProperty("latest_year", latestYear);
            countryResult.addProperty("population", latestPopulation);
            countryResult.addProperty("growth", growth);

            resultArray.add(countryResult);
        }

        JsonObject finalResult = new JsonObject();
        finalResult.add("country", resultArray);
        return finalResult;
    }

    /**
     * Gets population and growth data for selected cities in the Czech Republic.
     * 
     * @return A JsonObject containing the population and growth data for selected cities.
     * @throws IOException If an error occurs during the API request.
     */
    public JsonObject getCityPopulations() throws IOException {
        String url = "https://countriesnow.space/api/v0.1/countries/population/cities";
        String[] cities = {"Praha", "Brno", "Ostrava", "Plzen"};

        JsonArray resultsArray = new JsonArray();

        for (String city : cities) {
            JsonObject body = new JsonObject();
            body.addProperty("country", "Czech Republic");
            body.addProperty("city", city);

            JsonObject response = post(url, body);
            validateResponse(response);

            JsonObject result = new JsonObject();
            result.addProperty("city", city);

            JsonObject data = response.getAsJsonObject("data");

            if (data.has("populationCounts")) {
                JsonArray populationCounts = data.getAsJsonArray("populationCounts");

                if (populationCounts.size() > 0) {
                    JsonObject earliest = populationCounts.get(0).getAsJsonObject();
                    JsonObject latest = populationCounts.get(populationCounts.size() - 1).getAsJsonObject();

                    int earliestVal = Integer.parseInt(earliest.get("value").getAsString().replaceAll(",", ""));
                    int latestVal = Integer.parseInt(latest.get("value").getAsString().replaceAll(",", ""));
                    int growth = latestVal - earliestVal;

                    result.addProperty("latest_year", latest.get("year").getAsString());
                    result.addProperty("population", latestVal);
                    result.addProperty("growth", growth);
                }
            }

            resultsArray.add(result);
        }

        JsonObject finalResult = new JsonObject();
        finalResult.add("cities", resultsArray);
        return finalResult;
    }
}
