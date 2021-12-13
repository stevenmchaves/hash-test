package org.chaves.steven;

import io.restassured.http.ContentType;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.ConnectException;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;

/**
 * Testing password hashing api concurrency
 */
public class PasswordPortTest {

    private String path = "/hash";


    @Test(description = "Invalid Port", expectedExceptions = { ConnectException.class })
    public void testInvalidPort() {
        baseURI = "http://127.0.0.1:8091";
        given().
                contentType(ContentType.JSON).
                body("{ \"password\": \"HELLLO12345!\"}").
                when().
                post(path).andReturn().statusCode();
    }

}
