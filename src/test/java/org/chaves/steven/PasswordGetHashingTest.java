package org.chaves.steven; 

import static io.restassured.RestAssured. * ; 
import static org.testng.Assert.*;
import static helpers.JsonUtils.*;

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

@Test(description = "Get is not supported")
public void testGetAPI() {
    Response response = given().get(path);
    response.then().statusCode(405);
    assertEquals(response.getBody().asString().trim(), "GET Not Supported");
}

@Test(description = "Post is supported with no data")
public void testPostAPI() {
    Response response = given().post(path);
    response.then().statusCode(400);
    assertEquals(response.getBody().asString().trim(), "Malformed Input");
}



@Test(description = "Get API call with invalid parameters")
public void testGetAPIInvalidParameters() {
    Response response = given().get("/?key=5");
    response.then().statusCode(404);
    assertEquals(response.asString().trim(), "404 page not found");
}

    //@Test(description = "without any data portion of the request, SHOULD result in a 400 with any error message stating password information is needed")
    public void testWithEmptyDataProvided() {
        Response response = given().accept(ContentType.JSON).body("{}").post(path); 
        response.then().statusCode(400);
        assertTrue(response.getBody().asString().contains("Password needed"), "Password should have been supplied");
    }

    //@Test(description = "without password in the data portion of the request, SHOULD result in a 400 with any error message stating password is needed")
    public void testWithInvalidDataNoPasswordValue() {
        Response response = given().accept(ContentType.JSON).body("{\"password\":\"\"}").post(path); 
        response.then().statusCode(400);
        assertTrue(response.getBody().asString().contains("Password needed"), "Password should have been supplied");
    }

    @Test(description = "Happy path")
    public void testHappyPath() {
        Response response = given().accept(ContentType.JSON).body("{\"password\":\"angrymokey\"}").post(path); 
        response.then().statusCode(200); 
        stringOut = response.asString();
        assertTrue(isJSONValid(stringOut));
        assertTrue(Integer.parseInt(stringOut) > 0, "Value should have been an integer greater than 0");
    }
}
