package test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class TestOne {

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = "https://fakerapi.it/api/v2";
    }

    @Test(description = "Verify that fetching persons returns 10 items by default")
    public void testGetDefaultPersons() {
        given()
            .queryParam("_quantity", 10)
        .when()
            .get("/persons")
        .then()
            .log().ifValidationFails()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("status", equalTo("OK"))
            .body("total", equalTo(10))
            .body("data", hasSize(10))
            .body("data", everyItem(hasKey("firstname")))
            .body("data", everyItem(hasKey("email")));
    }

    @Test(description = "Verify that same seed parameter results in identical data")
    public void testSeedConsistency() {
        int seedValue = 12345;

        
        String firstResponse = given()
            .queryParam("_seed", seedValue) 
            .get("/products")
            .then()
            .extract().asString();

        
        given()
            .queryParam("_seed", seedValue)
        .when()
            .get("/products")
        .then()
            .assertThat()
            .body(equalTo(firstResponse));
    }

    @Test(description = "Verify localized data for French locale")
    public void testLocalization() {
        given()
            .queryParam("_locale", "fr_FR")
            .queryParam("_quantity", 1)
        .when()
            .get("/persons")
        .then()
            .statusCode(200)
            .body("data[0].phone", anyOf(startsWith("+33"), startsWith("0")));
    }

    @Test(description = "Verify Maximum Quantity Limit caps at 1000")
    public void testMaximum() {
        given()
            .queryParam("_quantity", 1001)
        .when()
            .get("/companies")
        .then()
            .statusCode(200)
            .body("total", equalTo(1000))
            .body("data", hasSize(1000));
    }

    @Test(description = "Negative test for Invalid resource")
    public void testNegative() {
        given()
        .when()
            .get("/non-existent-resource")
        .then()
            .statusCode(404);
    }
}