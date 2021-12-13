package org.chaves.steven; 

import static io.restassured.RestAssured. * ; 
import static org.testng.Assert. * ;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.testng.annotations.BeforeClass; 
import org.testng.annotations.Test; 

import helpers.OsUtils; 
import io.restassured.http.ContentType; 
import io.restassured.response.Response; 

/**
 * Testing password hashing api concurrency
 */
public class PasswordConcurrencyTest {

    private String path = "/hash"; 

    @BeforeClass
    public void beforeClass() {
        // Setting BaseURI once
        baseURI = "http://127.0.0.1:8090";
    }

    @Test(description = "Two calls")
    public void testTwoCallsA() {
        for (int i = 0; i < 2; i++) {
            given().
            contentType(ContentType.JSON).
            body("{ \"password\": \"HELLLO12345!\"}").
            when().
            post(path).andReturn().statusCode();
        }
    }

    @Test(description = "Two calls")
    public void testTwoCallsB() {
        for (int i = 0; i < 2; i++) {
            given().
                    contentType(ContentType.JSON).
                    body("{ \"password\": \"HELLLO12345!\"}").
                    when().
                    post(path).andReturn().statusCode();
        }
    }
}
