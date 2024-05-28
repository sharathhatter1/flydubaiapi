package com.fd.rest.api;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import java.util.Map;

public class SpecBuilder {

    public static ResponseSpecification getResponseSpec(){
        return new ResponseSpecBuilder().
                //log(LogDetail.ALL).
                build();
    }


    public static RequestSpecification getRequest(){
        return new RequestSpecBuilder().
                setBaseUri("https://qa1-flights2.np.flydubai.com/").
                setBasePath(Route.REST).
                setContentType(ContentType.JSON).
                addHeader("User-Agent", "PostmanRuntime/7.39.0").
                addFilter(new AllureRestAssured()).
                log(LogDetail.ALL).
                build();
    }

    public static RequestSpecification getRequestWithHeaders(Map<String,String> map){
        return new RequestSpecBuilder().
                setBaseUri("https://qa1-flights2.np.flydubai.com/").
                setBasePath(Route.REST).
                addHeaders(map).
                //setContentType(ContentType.JSON).
                //addHeader("User-Agent", "PostmanRuntime/7.39.0").
                addFilter(new AllureRestAssured()).
                log(LogDetail.ALL).
                build();
    }

    public static RequestSpecification getRequestAddFlight(Map<String,String> map){
        return new RequestSpecBuilder().
                setBaseUri("https://qa1-flights2.np.flydubai.com/").
                setBasePath(Route.REST).
                addHeaders(map).
                //setContentType(ContentType.JSON).
                //addHeader("User-Agent", "PostmanRuntime/7.39.0").
                        addFilter(new AllureRestAssured()).
                log(LogDetail.ALL).
                build();
    }

    public static RequestSpecification addFlightToken(String token){
        return new RequestSpecBuilder().
                setBaseUri("https://qa1-flights2.np.flydubai.com/").
                setBasePath(Route.REST).
                addHeader("securityToken",token).
                setContentType(ContentType.JSON).
                addHeader("User-Agent", "PostmanRuntime/7.39.0").
                addFilter(new AllureRestAssured()).
                log(LogDetail.ALL).
                build();
    }


    public static RequestSpecification getPCIRequest(String token){
        return new RequestSpecBuilder().
                setBaseUri("https://qa1-flights2.np.flydubai.com/").
                setBasePath(Route.REST).
                setContentType(ContentType.JSON).
                addHeader("securityToken",token).
                addHeader("User-Agent", "PostmanRuntime/7.39.0").
                addFilter(new AllureRestAssured()).
                log(LogDetail.ALL).
                build();
    }


}
