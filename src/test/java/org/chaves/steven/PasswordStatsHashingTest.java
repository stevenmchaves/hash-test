package org.chaves.steven;

import static io.restassured.RestAssured.*;
import static org.testng.Assert.*;
import static helpers.JsonUtils.*;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

/**
 * Testing against password hashing api the stats path. Checks and validates
 * possible API calls using the path `stats`.
 */
public class PasswordStatsHashingTest {

    private String path = "/stats";
    private String stringOut;
    private String asString;
    private int totalRequests;

    @BeforeClass
    public void beforeClass() {
        // Setting BaseURI once
        baseURI = "http://127.0.0.1:8090";
    }

    @Test(description = "Post is supported with no data")
    public void testPostAPI() {
        Response response = given().post(path);
        response.then().statusCode(200);
        assertEquals(response.getBody().asString().trim(), "Malformed Input");
    }

    @Test(description = "Get API call with invalid parameters")
    public void testGetAPIInvalidParameters() {
        Response response = given().get(path + "/?key=5");
        response.then().statusCode(404);
        assertEquals(response.asString().trim(), "404 page not found");
    }

    @Test(description = "Supply empty data SHOULD result in a 200")
    public void testWithEmptyDataProvided() {
        Response response = given().accept(ContentType.JSON).body("{}").post(path);
        response.then().statusCode(200);
        System.out.println(asString + "\n-----------");        assertTrue(asString.contains("Password needed"), "Password should have been supplied");
    }

    //@Test(description = "Supply data portion of the request, SHOULD result in a 400 with any error message stating Data not allowed")
    public void testWithProvingData() {
        Response response = given().accept(ContentType.JSON).body("{\"password\":\"\"}").post(path);
        response.then().statusCode(400);
        assertTrue(response.getBody().asString().contains("No data should be provided"));
    }

    @Test(description = "Happy path")
    public void testHappyPath() {
        Response response = given().post(path);
        response.then().statusCode(200);
        stringOut = response.asString();
        assertTrue(isJSONValid(stringOut));
        JsonPath jsonPath = response.jsonPath();
        assertTrue(jsonPath.getInt("TotalRequests") > 0, "Total Requests should be a positive number");
        assertTrue(jsonPath.getInt("AverageTime") > 0, "Total Requests should be a positive number");
    }

    @Test(description = "Total requests increments")
    public void testStatsTotalRequests() {
        Response response = given().post(path);
        response.then().statusCode(200);
        stringOut = response.asString();
        JsonPath jsonPath = response.jsonPath();
        totalRequests = jsonPath.getInt("TotalRequests");
        System.out.println(totalRequests);
        Response responsePost = given().accept(ContentType.JSON).body("{\"password\":\"angrymokey\"}").post(path);
        responsePost.then().statusCode(200); 
        response = given().post(path);
        jsonPath = response.jsonPath();
        assertEquals(jsonPath.getInt("TotalRequests"), ++totalRequests, "Total Requests should be " + totalRequests);
    }
}
