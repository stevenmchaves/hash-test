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

    @BeforeClass
    public void beforeClass() {
        // Setting BaseURI once
        baseURI = "http://127.0.0.1:8090";
    }
    @Test(description = "with invalid parameters")
    public void testWithParameters() {
        Response response = given().get(path + "/?key=5");
        response.then().statusCode(404);
        assertEquals(response.asString().trim(), "404 page not found");
    }

    @Test(description = "Supply empty data SHOULD result in a 400 with an error message")
    public void testGetStatsWithEmptyDataProvided() {
        Response response = given().accept(ContentType.JSON).body("{}").post(path);
        response.then().statusCode(400);
        // BUG executed a 400 here
        assertTrue(response.asString().trim().contains(("SOME ERROR MESSAGE")));
    }

    @Test(description = "Supply data portion of the request, SHOULD result in a 400 with any error message stating Data not allowed")
    public void testWithProvingData() {
        Response response = given().accept(ContentType.JSON).body("{\"password\":\"\"}").post(path);
        assertTrue(response.getBody().asString().contains("No data should be provided"));
        response.then().statusCode(400);
    }

    @Test(description = "Happy path - Validated that the data structure response in JSON")
    public void testHappyPath() {
        Response response = given().post(path);
        response.then().statusCode(200);
        String stringOut = response.asString();
        assertTrue(isJSONValid(stringOut));
        JsonPath jsonPath = response.jsonPath();
        assertTrue(jsonPath.getInt("TotalRequests") >= 0, "Total Requests should be a positive number");
        assertTrue(jsonPath.getInt("AverageTime") >= 0, "Total Requests should be a positive number");
    }

    @Test(description = "Total requests increments")
    public void testStatsTotalRequests() {
        Response response = given().post(path);
        response.then().statusCode(200);
        JsonPath jsonPath = response.jsonPath();
        int totalRequests = jsonPath.getInt("TotalRequests");
        Response responsePost = given().accept(ContentType.JSON).body("{\"password\":\"angryhhhmokey\"}").post(path);
        responsePost.then().statusCode(200); 
        response = given().post(path);
        jsonPath = response.jsonPath();
        // BUG: Doesn't seem like it increments immediately
        assertEquals(jsonPath.getInt("TotalRequests"), ++totalRequests, "Total Requests should be " + totalRequests);
    }
}
