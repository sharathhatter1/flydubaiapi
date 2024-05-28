package com.fd.rest.tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fd.rest.api.StatusCode;
import com.fd.rest.pages.APIPages;
import com.fd.rest.utils.ConfigLoader;
import io.qameta.allure.*;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Epic("PNR creation for FD")
@Feature("Creating the PNR")
public class CreatePNRTests extends BaseTest {

    private String token;
    private String searchReq;
    private String selectedFlightString;

    public JSONObject finalJsonObject;

    @BeforeClass
    public void setup() throws JsonProcessingException, JSONException {
        searchReq = ConfigLoader.getProperty("searchRequestString");
    }

    @Story("Search Flight API")
    @Test(description = "Search Flight")
    public void searchFlightTest() throws JSONException {
        Response searchFlightResponse = APIPages.searchFlight(searchReq);
        assertStatusCode(searchFlightResponse.statusCode(), StatusCode.CODE_200);
        token = searchFlightResponse.getHeader("securityToken");
        System.out.println("Security Token is -> " + token);

        JSONArray flightsArray = getSelectedFlights(searchFlightResponse);
        JSONObject selectedFlight = flightsArray.getJSONObject(0);
        JSONObject selectedFare = selectedFlight.getJSONArray("fareTypes").getJSONObject(0);
        selectedFlight.put("selectedFare", selectedFare);

        JSONArray selectedFlightArray = new JSONArray().put(selectedFlight);
        JSONObject finalJsonObject = new JSONObject().put("searchRequest", new JSONObject(searchReq)).put("selectedFlights", selectedFlightArray);

        selectedFlightString = finalJsonObject.toString();
    }

    @Story("Add Flight API")
    @Test(description = "Add Flight", dependsOnMethods = "searchFlightTest")
    public void addFlightTest() {
        Response addFlightResponse = APIPages.addFlight(token, selectedFlightString);
        assertStatusCode(addFlightResponse.statusCode(), StatusCode.CODE_200);
        System.out.println("Add flight response -" + addFlightResponse.prettyPrint());
    }

    @Story("Optional Extras API")
    @Test(description = "Optional Extras", dependsOnMethods = "addFlightTest")
    public void optionalExtrasTest() {
        Response optionalExtrasResponse = APIPages.optionalExtras(token, selectedFlightString);
        assertStatusCode(optionalExtrasResponse.statusCode(), StatusCode.CODE_200);
        System.out.println("Optional Extras response -" + optionalExtrasResponse.prettyPrint());
    }

    @Story("Prepare API")
    @Test(description = "Prepare", dependsOnMethods = "optionalExtrasTest")
    public void prepareTest() throws JSONException {
        finalJsonObject = new JSONObject(selectedFlightString);
        finalJsonObject.put("itineraryAction", "3").put("passengerList", new JSONArray());

        Response prepareResponse = APIPages.prepareAPI(token, finalJsonObject.toString());
        assertStatusCode(prepareResponse.statusCode(), StatusCode.CODE_200);
        System.out.println("Prepare response -" + prepareResponse.prettyPrint());
    }

    @Story("Payment API")
    @Test(description = "Payment", dependsOnMethods = "prepareTest")
    public void paymentTest() throws JSONException {
        finalJsonObject = new JSONObject(selectedFlightString);
        finalJsonObject.put("itineraryAction", "3").put("passengerList", new JSONArray());
        Response paymentResponse = APIPages.paymentAPI(token, finalJsonObject.toString());
        assertStatusCode(paymentResponse.statusCode(), StatusCode.CODE_200);
        System.out.println("Payment response -" + paymentResponse.prettyPrint());

        JsonPath paymentJsonPath = paymentResponse.jsonPath();
        String pciURLtoRedirect = paymentJsonPath.getString("pciURLtoRedirect");
        String pciSessionUrl = pciURLtoRedirect.replace("paymentui/", "payments/getSessionDetails");
        String pciPostUrl = pciURLtoRedirect.replace("paymentui/", "payments/0.1/cards");
        System.out.println("pciSession URL: " + pciSessionUrl);
        System.out.println("pciPost URL: " + pciPostUrl);
    }

    @Story("PCI Session API")
    @Test(description = "PCI Session", dependsOnMethods = "paymentTest")
    public void pciSessionTest() throws JSONException {
        finalJsonObject = new JSONObject(selectedFlightString);
        finalJsonObject.put("itineraryAction", "3").put("passengerList", new JSONArray());
        JsonPath paymentJsonPath = new JsonPath(APIPages.paymentAPI(token, finalJsonObject.toString()).asString());
        String pciSessionUrl = paymentJsonPath.getString("pciURLtoRedirect").replace("paymentui/", "payments/getSessionDetails");

        Response pciSessionResponse = APIPages.pciSessionAPI(token, pciSessionUrl);
        assertStatusCode(pciSessionResponse.statusCode(), StatusCode.CODE_200);
        System.out.println("pciSession response -" + pciSessionResponse.prettyPrint());

        JsonPath pciSessionJsonPath = pciSessionResponse.jsonPath();


        JSONObject payLaterBody = new JSONObject()
                .put("systemId", pciSessionJsonPath.getString("systemId"))
                .put("paymentId", pciSessionJsonPath.getString("paymentId"))
                .put("currency", pciSessionJsonPath.getString("currency"))
                .put("amount", pciSessionJsonPath.getString("amount"))
                .put("sessionId", pciSessionJsonPath.getString("sessionId"))
                .put("paymentMethod", "PAYLATER");
        System.out.println("PayLater Body: " + payLaterBody.toString());

        String pciPostUrl = new JsonPath(APIPages.paymentAPI(token, finalJsonObject.toString()).asString()).getString("pciURLtoRedirect").replace("paymentui/", "payments/0.1/cards");
        Response pciPostURLResponse = APIPages.pciPostAPI(token, pciPostUrl, payLaterBody);
        assertStatusCode(pciPostURLResponse.statusCode(), StatusCode.CODE_200);
        System.out.println("pciPostURL response -" + pciPostURLResponse.prettyPrint());

        JsonPath jsonResponse = pciPostURLResponse.jsonPath();
        boolean success = jsonResponse.getBoolean("success");
        String entityId = null;
        if (success) {
            entityId = jsonResponse.getString("entityId");
            System.out.println("PNR ID is : " + entityId);
        } else {
            System.out.println("Request was not successful");
        }
        addAttachment("PNR ID is - ", entityId);
    }
    }

    /*@Story("PCI Post API")
    @Test(description = "PCI Post", dependsOnMethods = "pciSessionTest")
    public void pciPostTest() throws JSONException {
        JsonPath pciSessionJsonPath = new JsonPath(APIPages.pciSessionAPI(token, new JsonPath(APIPages.paymentAPI(token, selectedFlightString).
                asString()).getString("pciURLtoRedirect").replace("paymentui/", "payments/getSessionDetails")).asString());

        finalJsonObject = new JSONObject(selectedFlightString);
        finalJsonObject.put("itineraryAction", "3").put("passengerList", new JSONArray());
        JsonPath paymentJsonPath = new JsonPath(APIPages.paymentAPI(token, finalJsonObject.toString()).asString());
        String pciSessionUrl = paymentJsonPath.getString("pciURLtoRedirect").replace("paymentui/", "payments/getSessionDetails");

        JSONObject payLaterBody = new JSONObject()
                .put("systemId", pciSessionJsonPath.getString("systemId"))
                .put("paymentId", pciSessionJsonPath.getString("paymentId"))
                .put("currency", pciSessionJsonPath.getString("currency"))
                .put("amount", pciSessionJsonPath.getString("amount"))
                .put("sessionId", pciSessionJsonPath.getString("sessionId"))
                .put("paymentMethod", "PAYLATER");
        System.out.println("PayLater Body: " + payLaterBody.toString());

        String pciPostUrl = new JsonPath(APIPages.paymentAPI(token, selectedFlightString).asString()).getString("pciURLtoRedirect").replace("paymentui/", "payments/0.1/cards");
        Response pciPostURLResponse = APIPages.pciPostAPI(token, pciPostUrl, payLaterBody);
        assertStatusCode(pciPostURLResponse.statusCode(), StatusCode.CODE_200);
        System.out.println("pciPostURL response -" + pciPostURLResponse.prettyPrint());

        JsonPath jsonResponse = pciPostURLResponse.jsonPath();
        boolean success = jsonResponse.getBoolean("success");
        String entityId = null;
        if (success) {
            entityId = jsonResponse.getString("entityId");
            System.out.println("PNR ID is : " + entityId);
        } else {
            System.out.println("Request was not successful");
        }
        addAttachment("PNR ID is - ", entityId);
    }*/

