package org.test.restapiperformance;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.test.testrestapi.abstractclasses.AbstractHappyPathApiClass;
import org.test.testrestapi.interfaces.ApiPerformanceInterface;
import org.test.testrestapi.stuctures.Book;
import org.test.testrestapi.utils.Utils;
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
import org.jboss.eap.additional.testsuite.annotations.EAT;

/**
 * Tests for the Books API endpoints using the fake REST API. This class extends
 * AbstractApiClass for common API test functionalities and implements
 * ApiInterface. The tests are ordered using @TestMethodOrder and @Order
 * annotations to ensure sequential execution.
 */

@EAT({"modules/testcases/v1/performanceApiTestsuite/src/main/java"})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BooksApiPerformanceTests extends AbstractHappyPathApiClass implements ApiPerformanceInterface {

    // Base URL for the Books API endpoint.
    private static final String BASE_URL = BASE_API_URL + "Books";
    // Logger for logging test information.
    private static final Logger logger = Logger.getLogger(BooksApiPerformanceTests.class.getName());

    /**
     * Tests the GET all books endpoint. Verifies that the endpoint returns a
     * 200 status code and a non-empty list of books. Also validates the
     * structure and data types of the returned books.
     */
    @Order(1)
    @Test
    public void testGet() throws IOException, InterruptedException {
        // Build the GET request.
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL)).GET().build();
        int numberOfRequests = 1000;
        int numberOfThreads = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        Instant start = Instant.now();

        for (int i = 0; i < numberOfRequests; i++) {
            executorService.submit(() -> {
                try {
                    // Send the request and get the response.
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    // Assert that the response status code is 200.
                    assertEquals(200, response.statusCode(), "Get All Books failed: Expected 200, got " + response.statusCode());

                    // If the response is successful, parse and validate the books.
                    if (response.statusCode() == 200) {
                        // Parse the JSON response into a JsonArray.
                        JsonArray books = gson.fromJson(response.body(), JsonArray.class);
                        // Assert that the array is not empty.
                        assertTrue(books.size() > 0, "No books returned");
                        // Create a Book object to hold the parsed data.
                        Book singleBook = new Book();
                        //    storedBooks.clear();
                        // Iterate over each book in the array.
                        for (JsonElement bookElement : books) {
                            // Log the book element for debugging.
                            logger.log(Level.FINE, "BOOK testGetAll : " + bookElement);

                            // Convert the book element to a JsonObject.
                            JsonObject book = bookElement.getAsJsonObject();
                            // Assert that each required field exists and has the correct type.
                            assertTrue(book.has("id"));
                            singleBook.id = book.get("id").getAsInt();
                            assertTrue(singleBook.id >= 0);
                            assertTrue(book.has("pageCount"));
                            singleBook.pageCount = book.get("pageCount").getAsInt();
                            assertTrue(singleBook.pageCount >= 0);
                            assertTrue(book.has("title"));
                            singleBook.title = book.get("title").getAsString();
                            assertTrue(singleBook.title != null);
                            assertTrue(book.has("description"));
                            singleBook.description = book.get("description").getAsString();
                            assertTrue(singleBook.description != null);
                            assertTrue(book.has("excerpt"));
                            singleBook.excerpt = book.get("excerpt").getAsString();
                            assertTrue(singleBook.excerpt != null);
                            assertTrue(book.has("publishDate"));
                            singleBook.publishDate = book.get("publishDate").getAsString();
                            assertTrue(singleBook.publishDate != null);
                            assertTrue(Utils.isOffsetDateTimeFormat(book.get("publishDate").getAsString()));

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
