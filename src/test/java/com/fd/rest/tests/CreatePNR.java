package com.fd.rest.tests;

import com.fd.rest.api.StatusCode;
import com.fd.rest.pages.APIPages;
import com.fd.rest.utils.ConfigLoader;
import io.qameta.allure.*;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.annotations.Test;

@Epic("PNR creation for FD")
@Feature("Creating the PNR")
public class CreatePNR extends BaseTest {

    @Story("Creating PNR from APIs")
    @Description("This test create the PNR by calling differnt APIs")
    @Test(description = "Creates PNR using pay later")
    public void searchFlightPNR() throws JSONException {
        String searchReq = ConfigLoader.getProperty("searchRequestString");

        // Search flight
        Response searchFlightResponse = APIPages.searchFlight(searchReq);
        assertStatusCode(searchFlightResponse.statusCode(), StatusCode.CODE_200);

        //Getting Security Token
        String Token = searchFlightResponse.getHeader("securityToken");
        System.out.println("Security Token is -> "+Token);

        //For getting the selected flights json
        JSONArray flightsArray = getSelectedFlights(searchFlightResponse);
        JSONObject selectedFlight = flightsArray.getJSONObject(0);
        JSONObject selectedFare = selectedFlight.getJSONArray("fareTypes").getJSONObject(0);
        // Add selectedFare to selectedFlight
        selectedFlight.put("selectedFare", selectedFare);
        JSONArray selectedFlightArray = new JSONArray().put(selectedFlight);
        JSONObject selectedFlightJson = new JSONObject().put("searchRequest", new JSONObject(searchReq)).put("selectedFlights", selectedFlightArray);

        //TO GET ADD FLIGHT API RESPONSE
        Response addFlightResponse = APIPages.addFlight(Token,selectedFlightJson.toString());
        assertStatusCode(addFlightResponse.statusCode(), StatusCode.CODE_200);
        System.out.println("Add flight response -"+addFlightResponse.prettyPrint());

        //calling optional Extras api
        Response optionalExtrasResponse = APIPages.optionalExtras(Token,addFlightResponse.getBody().asString());
        assertStatusCode(optionalExtrasResponse.statusCode(), StatusCode.CODE_200);
        System.out.println("Optinal Extras response -"+optionalExtrasResponse.prettyPrint());
        selectedFlightJson.put("itineraryAction", "3").put("passengerList", new JSONArray());

        //Calling prepare api
        Response prepareResponse = APIPages.prepareAPI(Token,selectedFlightJson.toString());
        assertStatusCode(prepareResponse.statusCode(), StatusCode.CODE_200);
        System.out.println("Prepare response -"+prepareResponse.prettyPrint());

        //Calling payment api
        Response paymentResponse = APIPages.paymentAPI(Token,selectedFlightJson.toString());
        assertStatusCode(paymentResponse.statusCode(), StatusCode.CODE_200);
        System.out.println("Payment response -"+paymentResponse.prettyPrint());

        JsonPath paymentJsonPath = paymentResponse.jsonPath();
        String pciURLtoRedirect = paymentJsonPath.getString("pciURLtoRedirect");
        String pciSessionUrl = pciURLtoRedirect.replace("paymentui/", "payments/getSessionDetails");
        String pciPostUrl = pciURLtoRedirect.replace("paymentui/", "payments/0.1/cards");

        //To call GetPciSession API
        Response pciSessionResponse = APIPages.pciSessionAPI(Token,pciSessionUrl);
        assertStatusCode(pciSessionResponse.statusCode(), StatusCode.CODE_200);
        System.out.println("pciSession response -"+pciSessionResponse.prettyPrint());

        JsonPath pciSessionJsonPath = pciSessionResponse.jsonPath();
        JSONObject payLaterBody = new JSONObject()
                .put("systemId", pciSessionJsonPath.getString("systemId"))
                .put("paymentId", pciSessionJsonPath.getString("paymentId"))
                .put("currency", pciSessionJsonPath.getString("currency"))
                .put("amount", pciSessionJsonPath.getString("amount"))
                .put("sessionId", pciSessionJsonPath.getString("sessionId"))
                .put("paymentMethod", "PAYLATER");

        // Calling pci Post API
        Response pciPostURLResponse = APIPages.pciPostAPI(Token,pciPostUrl, payLaterBody);
        assertStatusCode(pciPostURLResponse.statusCode(), StatusCode.CODE_200);
        System.out.println("pciPostURL response -"+pciPostURLResponse.prettyPrint());
        //getting PNR based on success is true
        JsonPath jsonResponse = pciPostURLResponse.jsonPath();
        boolean success = jsonResponse.getBoolean("success");
        String entityId = null;
        if (success) {
            entityId = jsonResponse.getString("entityId");
            System.out.println("PNR ID is : " + entityId);
        } else {
            System.out.println("Request was not successful");
        }
        //addAttachment("PNR ID is - ",entityId);
        Allure.addAttachment("PNR ID is - ",entityId);

    }
}
