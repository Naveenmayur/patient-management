import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.notNullValue;

public class AuthIntegrationTest {

    @BeforeAll
    public static void setup() {
        // Setup code for authentication tests, e.g., initialize test users, mock authentication service, etc.
        RestAssured.baseURI = "http://localhost:8080";
    }

    @Test
    public void shouldReturnOkWithValidToken() {
        String loginPayload = """
                    {
                        "email": "testuser@test.com",
                        "password": "password123"
                    }
                """;

        Response response = RestAssured
                .given()
                .contentType("application/json")
                .body(loginPayload)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("token", notNullValue())
                .extract()
                .response();

        System.out.println("Generated token: " + response.jsonPath().getString("token"));
    }

    @Test
    public void shouldReturnNotFoundForInvalidEndpoint() {
        RestAssured
                .given()
                .when()
                .get("/api/auth/non-existent")
                .then()
                .statusCode(HttpStatus.SC_NOT_FOUND);
    }

    @Test
    public void shouldReturnUnauthorizedWithInvalidCredentials() {
        String loginPayload = """
                    {
                        "email": "wronguser@test.com",
                        "password": "wrongpassword"
                    }
                """;

        RestAssured
                .given()
                .contentType("application/json")
                .body(loginPayload)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED);
    }
}
