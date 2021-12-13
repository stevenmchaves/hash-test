package org.chaves.steven;

import static io.restassured.RestAssured.*;
import static org.testng.Assert.*;
import static helpers.JsonUtils.*;

import org.apache.commons.codec.binary.Base64;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

/**
 * Testing against password hashing api.
 */
public class PasswordGetHashingTest {

    private String path = "/hash";
    private String stringOut;

    @BeforeClass
    public void beforeClass() {
        // Setting BaseURI once
        baseURI = "http://127.0.0.1:8090";
    }

    @Test(description = "Get is not supported seems like without parameters")
    public void testGetAPI() {
        Response response = given().get(path);
        response.then().statusCode(405);
        assertEquals(response.getBody().asString().trim(), "GET Not Supported");
    }

    @Test(description = "Get API call with invalid parameters")
    public void testGetAPIInvalidParameters() {
        Response response = given().get("/?key=5");
        response.then().statusCode(404);
        assertEquals(response.asString().trim(), "404 page not found");
    }

    @Test(description = "Data not allowed with no job identified")
    public void testWithEmptyDataProvided() {
        Response response = given().accept(ContentType.JSON).body("{}").get(path);
        response.then().statusCode(405);
        assertEquals(response.getBody().asString().trim(),"GET Not Supported");
    }

    @Test(description = "Job Id does not exist")
    public void testJobIdNotFound() {
        Response response = given().accept(ContentType.JSON).body("{\"password\":\"angrymokey\"}").get(path+ "/123456789");
        response.then().statusCode(400);
        stringOut = response.asString();
        assertFalse(isJSONValid(stringOut));
        assertEquals(stringOut.trim(),  "Hash not found");
    }

    @Test(description = "Job Id does exist and encoded base64")
    public void testHappyPath() {
        Response response = given().accept(ContentType.JSON).body("{\"password\":\"angrymonkey\"}").post(path);
        response.then().statusCode(200);
        stringOut = response.asString();
        assertTrue(isJSONValid(stringOut));
        response = given().accept(ContentType.JSON).get(path+ "/" + Integer.parseInt(stringOut));
        response.then().statusCode(200);
        stringOut = response.asString();
        assertFalse(isJSONValid(stringOut));
        assertTrue(Base64.isBase64(stringOut.trim()),  "Return string is not a valid base64 encoding");
    }
}
