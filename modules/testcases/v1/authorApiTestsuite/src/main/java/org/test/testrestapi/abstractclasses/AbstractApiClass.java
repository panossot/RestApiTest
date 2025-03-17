package org.test.testrestapi.abstractclasses;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.test.testrestapi.stuctures.Author;
import org.test.testrestapi.stuctures.Book;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import org.jboss.eap.additional.testsuite.annotations.EAT;

/**
 * Abstract base class for API tests, providing common methods for testing REST API endpoints with BASE_API_URL.
 */
@EAT({"modules/testcases/v1/authorApiTestsuite/src/main/java","modules/testcases/v1/bookApiTestsuite/src/main/java","modules/testcases/v1/performanceApiTestsuite/src/main/java","modules/testcases/v1/restApiEdgeTestsuite/src/main/java"})
public abstract class AbstractApiClass {

    // Base URL for the fake REST API.
    protected static final String BASE_API_URL = "https://fakerestapi.azurewebsites.net/api/v1/";
    // HttpClient for making HTTP requests.
    protected static final HttpClient client = HttpClient.newHttpClient();
    // Gson for JSON serialization/deserialization.
    protected static final Gson gson = new Gson();
    // Maps to store authors and books for cross-test data sharing.
//    protected static Map<Integer, Author> storedAuthors = new HashMap<>();
 //   protected static Map<Integer, Book> storedBooks = new HashMap<>();

    /**
     * Tests a single GET request for a specific object.
     *
     * @param objectId   The ID of the object to retrieve.
     * @param singleObject   The JsonObject that is expected to be retrieved.
     * @param baseUrl    The base URL for the API endpoint.
     * @param objectName The name of the object being tested (for logging).
     * @param logger     The logger for logging test information.
     * @param validate   A BiConsumer to validate the response body and object ID.
     */
    protected void testSingleGet(int objectId, JsonObject singleObject, String baseUrl, String objectName, Logger logger, BiConsumer<String, JsonObject> validate) {
        try {
            // Construct the URL for the GET request.
            String url = baseUrl + "/" + objectId;

            // Build the GET request.
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().header("accept", "application/json; v=1").build();
            // Send the request and get the response.
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Log the response status code.
            logger.log(Level.FINE, "testSingleGet - " + objectName + " response.statusCode() : " + response.statusCode());
            // Assert that the response status code is 200 (OK).
            assertEquals(200, response.statusCode(), "testSingleGet of " + objectName + " failed: Expected 200, got " + response.statusCode());

            // If the response status code is 200, validate the response body.
            if (response.statusCode() == 200) {
                validate.accept(response.body(), singleObject);
            }

        } catch (IOException | InterruptedException e) {
            // Log the exception and fail the test.
            logger.log(Level.SEVERE, "testSingleGet of " + objectName + " failed : " + e.getMessage());
            e.printStackTrace();
            fail("testSingleGet of " + objectName + " failed due to exception." + e.getMessage()); // Force JUnit fail
        }
    }
    
    /**
     * Tests a single GET request for a specific object.
     *
     * @param objectId   The ID of the object to retrieve.
     * @param baseUrl    The base URL for the API endpoint.
     * @param objectName The name of the object being tested (for logging).
     * @param logger     The logger for logging test information.
     * @return boolean 
     */
    protected boolean testSingleGet(int objectId, String baseUrl, String objectName, Logger logger) {
        try {
            // Construct the URL for the GET request.
            String url = baseUrl + "/" + objectId;

            // Build the GET request.
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().header("accept", "application/json; v=1").build();
            // Send the request and get the response.
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Log the response status code.
            logger.log(Level.FINE, "testSingleGet - " + objectName + " response.statusCode() : " + response.statusCode());
            
            if(response.statusCode()==200)
                return true;
            else
                return false;
        } catch (IOException | InterruptedException e) {
            // Log the exception and fail the test.
            logger.log(Level.SEVERE, "testSingleGet of " + objectName + " failed : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
   
    /**
     * Tests a single DELETE request for a specific object.
     *
     * @param objectId   The ID of the object to delete.
     * @param baseUrl    The base URL for the API endpoint.
     * @param objectName The name of the object being tested (for logging).
     * @param logger     The logger for logging test information.
     */
    protected void testSingleDelete(int objectId, String baseUrl, String objectName, Logger logger) {
        try {
            // Construct the URL for the DELETE request.
            String url = baseUrl + "/" + objectId;

            // Check if the object exists first (GET request):
            HttpRequest getRequest = HttpRequest.newBuilder().uri(URI.create(url)).GET().header("accept", "application/json; v=1").build();
            HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());

            // If the object exists, try to delete it.
            if (getResponse.statusCode() == 200) {
                HttpRequest deleteRequest = HttpRequest.newBuilder().uri(URI.create(url)).DELETE().header("accept", "application/json; v=1").build();
                HttpResponse<String> deleteResponse = client.send(deleteRequest, HttpResponse.BodyHandlers.ofString());

                // Assert that the delete response status code is 200 (OK).
                assertEquals(200, deleteResponse.statusCode(), "testSingleDelete of " + objectName + " (DELETE with " + objectId + ") failed: Expected 200, got " + deleteResponse.statusCode());

                // Optionally, check if the object is really gone (another GET request):
                HttpRequest checkGetRequest = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
                HttpResponse<String> checkGetResponse = client.send(checkGetRequest, HttpResponse.BodyHandlers.ofString());
                logger.log(Level.FINE, objectName + " testSingleDelete - checkGetResponse.body() : " + checkGetResponse.body());
                // Assert that the check GET response status code is 404 (Not Found).
                assertEquals(404, checkGetResponse.statusCode(), "Test " + objectId + " (DELETE Books/" + objectId + ") failed: Book not deleted");

            } else if (getResponse.statusCode() == 404) {
                // Fail the test if the object does not exist.
                fail(objectName + " does not exist to be deleted");
            } else {
                // Fail the test if the GET request returned an unexpected status code.
                fail(objectName + "testSingleDelete failed: Unexpected get response code: " + getResponse.statusCode());
            }

        } catch (IOException | InterruptedException e) {
            // Log the exception and fail the test.
            System.err.println("Test " + objectId + " failed: " + e.getMessage());
            e.printStackTrace();
            fail("Test DELETE " + objectId + " failed due to exception.");
        }
    }

    /**
     * Tests a single POST request to create a new object.
     *
     * @param objectId       The ID of the object to be created.
     * @param newObject      The JsonObject representing the new object to be created.
     * @param baseUrl        The base URL for the API endpoint.
     * @param objectName     The name of the object being tested (for logging purposes).
     * @param logger         The logger for logging test information.
     * @param checkPostResult A BiConsumer to validate the POST request result.
     */
    protected void testSinglePost(int objectId, JsonObject newObject, String baseUrl, String objectName, Logger logger, BiConsumer<String, JsonObject> checkPostResult) {
        try {
            // Log the start of the POST test, including the new object's details.
            logger.log(Level.FINE, "testSinglePost - " + objectName + ": JsonObject newObject : " + newObject);

            // Convert the JsonObject representing the new object to a JSON string.
            String requestBody = gson.toJson(newObject);

            // Build the HTTP POST request.
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl))
                    .header("Content-Type", "application/json; v=1")
                    .header("accept", "application/json; v=1")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            // Send the POST request and get the HTTP response.
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Log the response body of the POST request.
            logger.log(Level.FINE, "testSinglePost - " + objectName + ": response.body() : " + response.body());

            // Assert that the POST request returned a 200 status code (OK).
            assertEquals(200, response.statusCode(), "testSinglePost - " + objectName + " failed: Expected 200, got " + response.statusCode());

            // If the POST request was successful (200), validate the response body.
            if (response.statusCode() == 200) {
                // Verify the created object (optional):
                checkPostResult.accept(response.body(), newObject);
            }

            // Construct the URL to retrieve the newly created object using its ID.
            String url = baseUrl + "/" + objectId;

            // Build a GET request to verify the creation of the object.
            request = HttpRequest.newBuilder().uri(URI.create(url)).GET().header("accept", "application/json; v=1").build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Assert that the GET request to retrieve the created object also returned a 200 status code.
            assertEquals(200, response.statusCode(), objectName + " testSinglePost get response failed: Expected 200, got " + response.statusCode());

            // If the GET request was successful (200), validate the retrieved object's details.
            if (response.statusCode() == 200) {
                checkPostResult.accept(response.body(), newObject);
            }

        } catch (IOException | InterruptedException e) {
            // Log the exception and fail the test if an error occurs during the POST or GET request.
            logger.log(Level.SEVERE, objectName + "testSinglePost failed: " + e.getMessage());

            e.printStackTrace();
            fail(objectName + " testSinglePost failed due to exception.");
        }
    }

    /**
     * Tests a single PUT request to update an existing object.
     *
     * @param objectId      The ID of the object to be updated.
     * @param updatedObject The JsonObject representing the updated object.
     * @param baseUrl       The base URL for the API endpoint.
     * @param objectName    The name of the object being tested (for logging).
     * @param logger        The logger for logging test information.
     * @param checkPutResult A BiConsumer to validate the PUT request result.
     */
    protected void testSinglePut(int objectId, JsonObject updatedObject, String baseUrl, String objectName, Logger logger, BiConsumer<String, JsonObject> checkPutResult) {
        try {
            // Log the start of the PUT test with the updated object's details.
            logger.log(Level.FINE, "testSinglePost - " + objectName + ": JsonObject newObject : " + updatedObject);

            // Construct the URL for the PUT request.
            String url = baseUrl + "/" + objectId;

            // Check if the object exists first (GET request):
            HttpRequest getRequest = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
            HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());

            // If the object exists (GET request returns 200), proceed with the PUT request.
            if (getResponse.statusCode() == 200) {
                // Convert the updated JsonObject to a JSON string.
                String requestBody = gson.toJson(updatedObject);

                // Build the PUT request with the updated object in the request body.
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Content-Type", "application/json; v=1")
                        .header("accept", "application/json; v=1")
                        .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                        .build();

                // Send the PUT request and get the response.
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                // Assert that the PUT request returned a 200 status code (OK).
                assertEquals(200, response.statusCode(), objectName + " testSinglePut failed: Expected 200, got " + response.statusCode());

                // If the PUT request was successful (200), validate the response body.
                if (response.statusCode() == 200) {
                    // Verify the updated object (optional):
                    checkPutResult.accept(response.body(), updatedObject);
                }

                // Make a GET request to verify that the object was updated correctly.
                request = HttpRequest.newBuilder().uri(URI.create(url)).GET().header("accept", "application/json; v=1").build();
                response = client.send(request, HttpResponse.BodyHandlers.ofString());

                // Assert that the subsequent GET request also returns a 200 status code.
                assertEquals(200, response.statusCode(), objectName + " testSinglePut get response failed: Expected 200, got " + response.statusCode());

                // If the GET request was successful, validate the response body again.
                if (response.statusCode() == 200) {
                    checkPutResult.accept(response.body(), updatedObject);
                }
            }

        } catch (IOException | InterruptedException e) {
            // Log the exception and fail the test if an error occurs.
            logger.log(Level.SEVERE, objectName + "testSinglePut failed: " + e.getMessage());
            e.printStackTrace();
            fail(objectName + " testSinglePut failed due to exception.");
        }
    }

}
