package com.fd.rest.pages;

import com.fd.rest.api.applicationApi.AddFlightAPI;
import com.fd.rest.api.applicationApi.SearchFlightAPI;
import io.restassured.response.Response;
import org.json.JSONObject;

public class APIPages {

    public static Response searchFlight(String searchReq) {
        return SearchFlightAPI.searchFlight(searchReq);
    }

    public static Response addFlight(String token, String requestBody) {
        return AddFlightAPI.addFlight(token, requestBody);
    }

    public static Response optionalExtras(String token, String requestBody) {
        return AddFlightAPI.optionalExtras(token, requestBody);
    }

    public static Response prepareAPI(String token, String requestBody) {
        return AddFlightAPI.prepareAPI(token, requestBody);
    }

    public static Response paymentAPI(String token, String requestBody) {
        return AddFlightAPI.paymentAPI(token, requestBody);
    }

    public static Response pciSessionAPI(String token, String url) {
        return AddFlightAPI.pciSessionAPI(token, url);
    }

    public static Response pciPostAPI(String token, String url, JSONObject requestBody) {
        return AddFlightAPI.pciPostAPI(token, url, requestBody);
    }
}
