package com.fd.rest.tests;

import com.fd.rest.api.StatusCode;
import com.fd.rest.pojo.Error;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.restassured.http.Header;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.annotations.BeforeMethod;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class BaseTest {

    @BeforeMethod
    public void beforeMethod(Method m){
        System.out.println("STARTING TEST: " + m.getName());
        System.out.println("THREAD ID: " + Thread.currentThread().getId());
    }

    public Map<String, String> getHeader(Response response){

        Map<String, String> flattenedHeaders;
        Map<String, List<String>> newHeaders = response.getHeaders().asList().stream()
                .collect(Collectors.groupingBy(Header::getName, Collectors.mapping(Header::getValue, Collectors.toList())));

        System.out.println("Printint the headers");
        for (Map.Entry<String, List<String>> entry : newHeaders.entrySet()) {
            String key = entry.getKey();
            List<String> values = entry.getValue();
            String firstValue = values.isEmpty() ? "" : values.get(0);
            System.out.println(key + ": " + firstValue);
        }

        System.out.println("Printed the headers");

        // Flatten headers
        flattenedHeaders = newHeaders.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> String.join(",", entry.getValue())
                ));

// Print headers for debugging
        for (Map.Entry<String, String> entry : flattenedHeaders.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        return flattenedHeaders;
    }

    @Step
    public void assertStatusCode(int actualStatusCode, StatusCode statusCode){
        assertThat(actualStatusCode, equalTo(statusCode.code));
    }

    @Step
    public void assertError(Error responseErr, StatusCode statusCode){
        assertThat(responseErr.getError().getStatus(), equalTo(statusCode.code));
        assertThat(responseErr.getError().getMessage(), equalTo(statusCode.msg));
    }

    public static void addAttachment(String name, String content) {
        Allure.addAttachment(name, content);
    }

    //to get the json of the particular object
    public static JSONArray getSelectedFlights(Response response) throws JSONException {
        JSONObject responseBody = new JSONObject(response.getBody().asString());
        return responseBody.getJSONArray("segments").getJSONObject(0).getJSONArray("flights");
    }

}
