package org.chaves.steven;

import helpers.OsUtils;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.ConnectException;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.testng.Assert.*;

/**
 * Testing password hashing api concurrency
 */
public class PasswordShutdownTest {

    private String path = "/hash";

    @BeforeClass
    public void beforeClass() {
        // Setting BaseURI once
        baseURI = "http://127.0.0.1:8090";
    }

    @Test(description = "Multiple calls at least the first one should be allowed to continue and the others will depend on threading")
    public void testMultipleCalls() {
        int i = 0;
        try {
            for (; i < 5; i++) {
                given().
                        contentType(ContentType.JSON).
                        body("{ \"password\": \"HELLLO12345!\"}").
                        when().
                        post(path).andReturn().statusCode();
            }
        } catch(final Exception ex) {
            assertTrue(i > 0, "This error should only happen after the first call");
        }

    }

    @Test(description = "shutdown and then try another request")
    public void testShutdown() {
        Response response = given().body("shutdown").post(path);
        response.then().statusCode(200);
        assertEquals(response.asString().trim(), "", "Response of shutdown should have been empty");
        try {
            response = given().post(path);
            response.then().statusCode(503);
            assertEquals(response.getBody().asString().trim(), "Service Unavailable");
            // depending on timing
        } catch (Exception e) {
            assertTrue(e.getLocalizedMessage().trim().equals("Connection refused: connect"), "Possibly the server did not shutdown");
        } finally {
            // restart server
            if (OsUtils.isWindows()) {
                try {
                    Thread.sleep(5000); // wait a little while before restart
                    assertTrue(Runtime.getRuntime().exec("cmd /c start broken-hashserve_win.exe").waitFor(1, TimeUnit.SECONDS),
                            "Unable to restart the server");
                } catch (Exception e) {
                    // BUG - Shutdown does not always release the PORT
                    System.out.println(e.toString());
                    System.err.print("Unable to restart the server");
                    System.exit(-1);
                    e.printStackTrace();
                }
            } else {
                System.err.println("WE ARE NOT ON WINDOWS - need to start up server manually!!!");
                fail("WE ARE NOT ON WINDOWS - need to start up server manually!!!");
            }
        }
    }
}
