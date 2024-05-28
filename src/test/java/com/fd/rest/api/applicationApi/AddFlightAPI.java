package com.fd.rest.api.applicationApi;

import com.fd.rest.api.RestResource;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.json.JSONObject;

public class AddFlightAPI {

    //Add flight itinerary
    @Step
    public static Response addFlight(String token, String addFlightJson )
    {
        return  RestResource.addFlightPost(token, addFlightJson);
    }

    @Step
    public static Response optionalExtras(String token, String addFlightJsonResponse )
    {
        return  RestResource.optionalApi(token, addFlightJsonResponse);
    }

    @Step
    public static Response prepareAPI(String token, String prepareBodyString )
    {
        return  RestResource.prepareIteniratyApi(token, prepareBodyString);
    }

    @Step
    public static Response paymentAPI(String token, String prepareBodyString )
    {
        return  RestResource.paymentIteniratyApi(token, prepareBodyString);
    }

    @Step
    public static Response pciSessionAPI(String token, String pciSessionURl )
    {
        return  RestResource.getPCISessionAPI(token, pciSessionURl);
    }

    @Step
    public static Response pciPostAPI(String token, String pciPostURL, JSONObject payLaterBody)
    {
        return  RestResource.pciPostURLApi(token, pciPostURL, payLaterBody);
    }
}
