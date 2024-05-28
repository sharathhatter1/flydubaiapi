package com.fd.rest.api.applicationApi;

import com.fd.rest.api.RestResource;
import io.qameta.allure.Step;
import io.restassured.response.Response;

public class SearchFlightAPI {


    @Step
    public static Response searchFlight(String s ){
        return  RestResource.searchFlightAPI(s);
    }

}
