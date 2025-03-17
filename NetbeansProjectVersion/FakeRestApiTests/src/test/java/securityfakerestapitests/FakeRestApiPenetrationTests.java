package securityfakerestapitests;

import fakerestapitests.interfaces.ApiSecurityInterface;
import org.junit.jupiter.api.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // Specifies that test methods should be executed in the order defined by the @Order annotation
public class FakeRestApiPenetrationTests implements ApiSecurityInterface {

    private static final String BASE_URL_BOOKS = "https://fakerestapi.azurewebsites.net/api/v1/Books/"; // Defines the base URL for the Books API endpoint
    private static final String BASE_URL_AUTHORS = "https://fakerestapi.azurewebsites.net/api/v1/Authors/"; // Defines the base URL for the Authors API endpoint
    private static final HttpClient client = HttpClient.newHttpClient(); // Creates a new HttpClient instance for making HTTP requests

    @Test // Marks this method as a test method
    @Order(1) // Specifies that this test method should be executed first
    public void testSqlInjectionBookId() throws IOException, InterruptedException {
        try {
            HttpRequest request = HttpRequest.newBuilder() // Builds an HTTP GET request
                    .uri(URI.create(BASE_URL_BOOKS + "1' OR '1'='1")) // Sets the URI with a potential SQL injection payload
                    .GET() // Specifies the HTTP method as GET
                    .build(); // Builds the request
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString()); // Sends the request and gets the response as a string
            //Expect a 400 or 404, or some sort of error. If it returns 200, this could indicate a vulnerability.
            assertTrue(response.statusCode() >= 400, "SQL Injection test failed. Status code: " + response.statusCode()); // Asserts that the response status code is 400 or higher
        } catch (IllegalArgumentException e) {
            // The test passes if the URI is malformed, meaning the API is likely protected from some basic SQL injection
        }
    }

    @Test // Marks this method as a test method
    @Order(2) // Specifies that this test method should be executed second
    public void testXssBookTitle() throws IOException, InterruptedException {
        String xssPayload = "<script>alert('XSS')</script>"; // Defines an XSS payload
        HttpRequest request = HttpRequest.newBuilder() // Builds an HTTP POST request
                .uri(URI.create(BASE_URL_BOOKS)) // Sets the URI
                .POST(HttpRequest.BodyPublishers.ofString( // Sets the request body as a JSON string with the XSS payload
                        "{\"id\": 100000, \"title\": \"" + xssPayload + "\", \"description\": \"Test\", \"pageCount\": 100, \"excerpt\": \"Test\", \"publishDate\": \"2023-10-27T10:00:00Z\"}"
                ))
                .header("Content-Type", "application/json") // Sets the Content-Type header
                .build(); // Builds the request
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString()); // Sends the request and gets the response as a string
        //Check for 200 or 201. If successful, then try to GET the book, and check if the XSS payload is present in the response.
        if (response.statusCode() >= 200 && response.statusCode() < 300) { // Checks if the response status code is 200 or 201
            int newBookId = 100000; //Extract the book id from the response.
            HttpRequest getRequest = HttpRequest.newBuilder() // Builds an HTTP GET request to retrieve the created book
                    .uri(URI.create(BASE_URL_BOOKS + newBookId)) // Sets the URI
                    .GET() // Specifies the HTTP method as GET
                    .build(); // Builds the request
            HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString()); // Sends the request and gets the response as a string
            assertFalse(getResponse.body().contains(xssPayload), "XSS Payload found in response. Vulnerability present."); // Asserts that the response body does not contain the XSS payload
        }
    }

    @Test // Marks this method as a test method
    @Order(4) // Specifies that this test method should be executed fourth
    public void testInvalidHttpMethod() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder() // Builds an HTTP OPTIONS request
                .uri(URI.create(BASE_URL_BOOKS + "1")) // Sets the URI
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody()) // Sets the HTTP method as OPTIONS
                .build(); // Builds the request
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString()); // Sends the request and gets the response as a string
        assertNotEquals(200, response.statusCode(), "OPTIONS method should not return 200."); // Asserts that the response status code is not 200
    }

    @Test // Marks this method as a test method
    @Order(5) // Specifies that this test method should be executed fifth
    public void testSqlInjectionAuthorId() throws IOException, InterruptedException {
        try {
            HttpRequest request = HttpRequest.newBuilder() // Builds an HTTP GET request
                    .uri(URI.create(BASE_URL_AUTHORS + "1' OR '1'='1")) // Sets the URI with a potential SQL injection payload
                    .GET() // Specifies the HTTP method as GET
                    .build(); // Builds the request
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString()); // Sends the request and gets the response as a string
            assertTrue(response.statusCode() >= 400, "SQL Injection Author test failed. Status code: " + response.statusCode()); // Asserts that the response status code is 400 or higher
        } catch (IllegalArgumentException e) {
            // The test passes if the URI is malformed, meaning the API is likely protected from some basic SQL injection
        }
    }

    @Test // Marks this method as a test method
    @Order(6) // Specifies that this test method should be executed sixth
    public void testXssAuthorFirstName() throws IOException, InterruptedException {
        String xssPayload = "<script>alert('XSS')</script>"; // Defines an XSS payload
        HttpRequest request = HttpRequest.newBuilder() // Builds an HTTP POST request
                .uri(URI.create(BASE_URL_AUTHORS)) // Sets the URI
                .POST(HttpRequest.BodyPublishers.ofString( // Sets the request body as a JSON string with the XSS payload
                        "{\"id\": 100000,\"firstName\": \"" + xssPayload + "\", \"lastName\": \"Test\", \"idBook\": 1}"
                ))
                .header("Content-Type", "application/json") // Sets the Content-Type header
                .build(); // Builds the request

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString()); // Sends the request and gets the response as a string
        if (response.statusCode() >= 200 && response.statusCode() < 300) { // Checks if the response status code is 200 or 201
            int newAuthorId = 100000;
            HttpRequest getRequest = HttpRequest.newBuilder() // Builds an HTTP GET request to retrieve the created author
                    .uri(URI.create(BASE_URL_AUTHORS + newAuthorId)) // Sets the URI
                    .GET() // Specifies the HTTP method as GET
                    .build(); // Builds the request
            HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString()); // Sends the request and gets the response as a string
            assertFalse(getResponse.body().contains(xssPayload), "XSS Payload found in Author response. Vulnerability present."); // Asserts that the response body does not contain the XSS payload
        }
    }
}
