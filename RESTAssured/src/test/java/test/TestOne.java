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
		.statusCode(200)
		.contentType(ContentType.JSON)
		.body("status", equalTo("OK"))
		.body("code", equalTo(200))
		.body("total", equalTo(10))
		.body("data", hasSize(10))
		.body("data[0]", hasKey("firstname"))
		.body("data[0]", hasKey("email"));
	}

	@Test(description = "Verify that seed parameter have consistent results")
	public void testSeedConsistency() {
		int seedValue = 12345;
		
		String response1 = given().queryParam("_seed", seedValue).get("/products").asString();
		
		String response2 = given().queryParam("_seed", seedValue).get("/products").asString();
	
		assert response1.equals(response2);
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
		.body("status", equalTo("OK"));
		
	}

	@Test(description = "Verify Maximum Quantity Limit")

	public void testMaximum() {
		given ()
		.queryParam("_quantity", 1001)
		.when()
		.get("/companies")
		.then()
		.statusCode(200)
		.body("total", equalTo(1000));

	}

	@Test(description = "Negative test for Invalid resource ")

	public void testNegative() {
		given ()
		.queryParam("_quantity", 1000)
		.when()
		.get("/building")
		.then()
		.statusCode(404);
	}
	
}
