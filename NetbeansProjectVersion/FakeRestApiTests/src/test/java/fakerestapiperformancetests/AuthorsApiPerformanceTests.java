package fakerestapiperformancetests;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fakerestapitests.abstractclasses.AbstractHappyPathApiClass;
import fakerestapitests.interfaces.ApiPerformanceInterface;
import fakerestapitests.stuctures.Author;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // Specifies that the test methods in this class will be executed in the order defined by the @Order annotation.
public class AuthorsApiPerformanceTests extends AbstractHappyPathApiClass implements ApiPerformanceInterface {

    private static final String BASE_URL = BASE_API_URL + "Authors"; // Defines the base URL for the Authors API endpoint by appending "Authors" to the base API URL defined in AbstractApiClass.
    private static final Logger logger = Logger.getLogger(AuthorsApiPerformanceTests.class.getName()); // Initializes a logger to log information and errors during test execution.

    @Order(1) // Specifies that this test method should be executed first.
    @Test // Marks this method as a JUnit 5 test method.
    public void testGet() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL)).GET().build(); // Creates an HTTP GET request to retrieve all authors from the API.

        int numberOfRequests = 1000;
        int numberOfThreads = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        Instant start = Instant.now();

        for (int i = 0; i < numberOfRequests; i++) {
            executorService.submit(() -> {
                try {
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString()); // Sends the HTTP request and stores the response, handling it as a string.
                    logger.log(Level.FINE, "Authors testGetAll - response.statusCode() : " + response.statusCode()); // Logs the HTTP status code of the response.
                    assertEquals(200, response.statusCode(), "Get All Authors failed: Expected 200, got " + response.statusCode()); // Asserts that the HTTP status code is 200 (OK).

                    if (response.statusCode() == 200) { // Checks if the response was successful.
                        JsonArray authors = gson.fromJson(response.body(), JsonArray.class); // Parses the JSON response body into a JsonArray.
                        logger.log(Level.FINE, "Authors testGetAll - authors.size() : " + authors.size()); // Logs the number of authors retrieved.
                        assertTrue(authors.size() > 0, "No authors returned"); // Asserts that at least one author was returned.
                        Author singleAuthor = new Author(); // Creates an Author object to store author data.
                        //    storedAuthors.clear();
                        for (JsonElement authorElement : authors) { // Iterates through each author element in the JSON array.
                            logger.log(Level.FINE, "Authors testGetAll - JsonElement authorElement : " + authorElement); // Logs each author element.
                            JsonObject author = authorElement.getAsJsonObject(); // Converts the author element to a JsonObject.
                            assertTrue(author.has("id")); // Asserts that the author object contains the "id" field.
                            singleAuthor.id = author.get("id").getAsInt(); // Retrieves the author ID and stores it.
                            assertTrue(singleAuthor.id >= 0); // Asserts that the author ID is non-negative.
                            assertTrue(author.has("firstName")); // Asserts that the author object contains the "firstName" field.
                            singleAuthor.firstName = author.get("firstName").getAsString(); // Retrieves the author's first name and stores it.
                            assertTrue(singleAuthor.firstName != null); // Asserts that the first name is not null.
                            assertTrue(author.has("lastName")); // Asserts that the author object contains the "lastName" field.
                            singleAuthor.lastName = author.get("lastName").getAsString(); // Retrieves the author's last name and stores it.
                            assertTrue(singleAuthor.lastName != null); // Asserts that the last name is not null.
                            assertTrue(author.has("idBook")); // Asserts that the author object contains the "idBook" field.
                            singleAuthor.idBook = author.get("idBook").getAsInt(); // Retrieves the author's book ID and stores it.
                            assertTrue(author.get("idBook").getAsString() != null); // Asserts that the book ID is not null.

                        }
                    }
                } catch (IOException ex) {
                    Logger.getLogger(AuthorsApiPerformanceTests.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InterruptedException ex) {
                    Logger.getLogger(AuthorsApiPerformanceTests.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        }

        executorService.shutdown();
        boolean finished = executorService.awaitTermination(1, TimeUnit.MINUTES);
        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);

        assertTrue(finished, "Books API performance test did not complete within the timeout.");

        logger.log(Level.INFO, "Books API performance test completed in: " + duration.toMillis() + "ms");
        logger.log(Level.INFO, "Requests per second : " + (double) numberOfRequests / duration.toSeconds());
    }

}
