package com.fd.rest.api;

import io.restassured.response.Response;
import org.json.JSONObject;

import static io.restassured.RestAssured.given;

public class RestResource {
    public static Response searchFlightAPI(String requestAddFlight){

        return given(SpecBuilder.getRequest()).
                body(requestAddFlight).
                when().post(Route.SEARCH_FLIGHT).
                then().spec(SpecBuilder.getResponseSpec()).
                extract().
                response();
    }

    public static Response addFlightPost(String token, String requestAddFlight){

        return given(SpecBuilder.addFlightToken(token)).
                body(requestAddFlight).
                //headers(map).
                when().post(Route.ADD_FLIGHT).
                then().spec(SpecBuilder.getResponseSpec()).
                extract().
                response();
    }

    public static Response optionalApi(String token,String addFlightJsonResponse){

        return given(SpecBuilder.addFlightToken(token)).
                body(addFlightJsonResponse).
                //headers(map).
                        when().post(Route.OPTIONAL_EXTRAS).
                then().spec(SpecBuilder.getResponseSpec()).
                extract().
                response();
    }

    public static Response prepareIteniratyApi(String token, String prepareBodyString) {
        return given(SpecBuilder.addFlightToken(token)).
                body(prepareBodyString).
                //headers(map).
                when().post(Route.PREPARE).
                then().spec(SpecBuilder.getResponseSpec()).
                extract().
                response();
    }

    public static Response paymentIteniratyApi(String token, String prepareBodyString) {
        return given(SpecBuilder.addFlightToken(token)).
                body(prepareBodyString).
                //headers(map).
                when().post(Route.PAYMENT).
                then().spec(SpecBuilder.getResponseSpec()).
                extract().
                response();
    }

    public static Response pciPostURLApi(String token, String pciPostURL, JSONObject payLaterBody) {
        return given(SpecBuilder.addFlightToken(token)).
                body(payLaterBody.toString()).
                when().post(pciPostURL).
                then().spec(SpecBuilder.getResponseSpec()).
                extract().
                response();
    }

    public static Response getPCISessionAPI(String token, String pciSessionURL){
        return given(SpecBuilder.getPCIRequest(token)).
                when().get(pciSessionURL).
                then().spec(SpecBuilder.getResponseSpec()).
                extract().
                response();
    }


}
