package org.chaves.steven; 

import static io.restassured.RestAssured. * ; 
import static org.testng.Assert.*;

import static helpers.JsonUtils.*;

import helpers.HashUtils;
import org.apache.commons.codec.binary.Base64;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import helpers.OsUtils;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.concurrent.TimeUnit;

/**
 * Testing against password hashing api.
 */
public class PasswordPostHashingTest {

private String path = "/hash";

    @BeforeClass
    public void beforeClass() {
        // Setting BaseURI once
        baseURI = "http://127.0.0.1:8090";
    }

    @Test(description = "Post is supported with no data")
    public void testPostAPI() {
        Response response = given().post(path);
        response.then().statusCode(400);
        assertEquals(response.getBody().asString().trim(), "Malformed Input");
    }

    @Test(description = "Post API call with invalid parameters")
    public void testPostAPIInvalidParameters() {
        Response response = given().post(path + "/?key=5");
        response.then().statusCode(405);
        assertEquals(response.asString().trim(), "POST Not Supported");
    }

    @Test(description = "without any data portion of the request, SHOULD result in a 400 with any error message stating password information is needed", timeOut = 5100L)
    public void testPostWithEmptyDataProvided() {
        Response response = given().accept(ContentType.JSON).body("{}").post(path);
        response.then().statusCode(400);
        // An error message
        String responseString = response.asString();
        assertTrue(responseString.contains("Password needed"), String.format("Error message: %s should have contained: Password needed", responseString));
    }

    @Test(description = "without password in the data portion of the request, SHOULD result in a 400 with any error message stating password is needed", timeOut = 5100L)
    public void testPostWithInvalidDataNoPasswordValue() {
        Response response = given().accept(ContentType.JSON).body("{\"password\":\"\"}").post(path);
        // BUG Should not allow for empty password
        response.then().statusCode(400);
        // An error message
        String responseString = response.asString();
        assertTrue(responseString.contains("Password needed"), String.format("Error message: %s should have contained: Password needed", responseString));

    }

    @Test(description = "without password in the data portion of the request, SHOULD result in a 400 with any error message stating password is needed", timeOut = 5100L)
    public void testPostWithInvalidDataKeyValue() {
        Response response = given().accept(ContentType.JSON).body("{\"psswrd\":\"\"}").post(path);
        response.then().statusCode(200);
        //BUG Should probably returned 400 with something about fomrat being incorrect
        assertTrue(response.getBody().asString().contains("Password needed"), "Password should have been supplied");
    }

    @Test(description = "Happy path confirm Time of approximately of 5 seconds", timeOut = 5100L)
    public void testPostValidateResponseTime() {
        Response response = given().accept(ContentType.JSON).body("{\"password\":\"angrymonkey\"}").post(path);
        response.then().statusCode(200);
        long timeInMs = response.getTime();
        long timeSeconds = response.getTimeIn(TimeUnit.SECONDS);
        assertTrue(timeSeconds == 5, String.format("Response time was: %s s", timeInMs));
        // BUG - strict sense is off by 10-20 ms on average
        assertTrue(timeInMs >= 5000, String.format("Response time was: %s ms", timeInMs));
    }

    @Test(description = "Happy path confirm validate password SHA512 is being used")
    public void testPostHappyPathValidatePasswordSHA512() {
        Response response = given().accept(ContentType.JSON).body("{\"password\":\"angrymonkey\"}").post(path);
        response.then().statusCode(200);
        String stringOut = response.asString();
        assertTrue(isJSONValid(stringOut));
        int jobId = Integer.parseInt(stringOut);
        assertTrue(jobId > 0, "Value should have been an integer greater than 0");
        response = given().accept(ContentType.JSON).get(path+ "/" + jobId);
        response.then().statusCode(200);
        stringOut = response.asString();
        // BUG its not a SHA-512 hash
        assertEquals(stringOut.trim(), HashUtils.encryptThisStringSHA512("angrymonkey"));
    }

    @Test(description = "Confirm that entire data json is not being used for SHA512")
    public void testPostNotAllJsonDataSHA512() {
        String jsonBody = "{\"password\":\"angrymonkey\"}";
        Response response = given().accept(ContentType.JSON).body(jsonBody).post(path);
        response.then().statusCode(200);
        String stringOut = response.asString();
        assertTrue(isJSONValid(stringOut));
        int jobId = Integer.parseInt(stringOut);
        assertTrue(jobId > 0, "Value should have been an integer greater than 0");
        response = given().accept(ContentType.JSON).get(path+ "/" + jobId);
        response.then().statusCode(200);
        stringOut = response.asString();
        // BUG its not a SHA-512 hash
        assertEquals(stringOut.trim(), HashUtils.encryptThisStringSHA512(jsonBody));
    }
}
