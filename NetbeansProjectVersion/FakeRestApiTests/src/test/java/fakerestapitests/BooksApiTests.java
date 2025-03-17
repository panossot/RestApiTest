package fakerestapitests;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fakerestapitests.abstractclasses.AbstractApiClass;
import fakerestapitests.abstractclasses.AbstractHappyPathApiClass;
import fakerestapitests.interfaces.ApiInterface;
import fakerestapitests.stuctures.Book;
import fakerestapitests.utils.Utils;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * Tests for the Books API endpoints using the fake REST API.
 * This class extends AbstractApiClass for common API test functionalities and implements ApiInterface.
 * The tests are ordered using @TestMethodOrder and @Order annotations to ensure sequential execution.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BooksApiTests extends AbstractApiClass implements ApiInterface {

    // Base URL for the Books API endpoint.
    private static final String BASE_URL = BASE_API_URL + "Books";
    // Logger for logging test information.
    private static final Logger logger = Logger.getLogger(BooksApiTests.class.getName());

    /**
     * Tests the GET all books endpoint.
     * Verifies that the endpoint returns a 200 status code and a non-empty list of books.
     * Also validates the structure and data types of the returned books.
     */
    @Order(1)
    @Test
    public void testGet() throws IOException, InterruptedException {
        // Build the GET request.
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL)).GET().build();
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
            BookProcessor processor = new BookProcessor();
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

                // Store the book in the storedBooks map for later tests.
           //     storedBooks.put(singleBook.id, singleBook);
                logger.log(Level.FINE, "Books testGet - book.id : " + singleBook.id); // Logs the ID of the book being retrieved.
                testSingleGet(singleBook.id, book, BASE_URL, "Books", logger, processor::validateBookJson); // Tests the GET endpoint for a single book.
            }
        }
    }


    /**
     * Tests the POST endpoint to create a new book.
     * Creates a new book with a unique ID and validates the response.
     */
    @Order(3)
    @Test
    public void testPost() {
        // Generate a unique ID for the new book.
        int id = 10000;
        // Create a JsonObject representing the new book.
        JsonObject newBook = new JsonObject();
        newBook.addProperty("id", id);
        newBook.addProperty("pageCount", 1000);
        newBook.addProperty("title", "Test title");
        newBook.addProperty("description", "Test description");
        newBook.addProperty("excerpt", "Test excerpt");
        newBook.addProperty("publishDate", OffsetDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        // Create a BookProcessor instance to validate the book update.
        BookProcessor processor = new BookProcessor();
        // Use testSinglePost from AbstractApiClass to perform the test.
        testSinglePost(id, newBook, BASE_URL, "Books", logger, processor::checkBookUpdate);
    }

    /**
     * Tests the PUT endpoint to update an existing book.
     * Updates a book with a specific ID and validates the response.
     */
    @Order(4)
    @Test
    public void testPut() {
        // Specify the ID of the book to update.
        int id = 1;
        // Create a JsonObject representing the updated book.
        JsonObject updatedBook = new JsonObject();
        updatedBook.addProperty("id", id);
        updatedBook.addProperty("pageCount", 1001);
        updatedBook.addProperty("title", "Test title2");
        updatedBook.addProperty("description", "Test description2");
        updatedBook.addProperty("excerpt", "Test excerpt2");
        updatedBook.addProperty("publishDate", OffsetDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        // Create a BookProcessor instance to validate the book update.
        BookProcessor processor = new BookProcessor();
        // Use testSinglePut from AbstractApiClass to perform the test.
        testSinglePut(id, updatedBook, BASE_URL, "Books", logger, processor::checkBookUpdate);
    }

    /**
     * Tests the DELETE endpoint to delete an existing book.
     * Deletes the first book retrieved in testGetAll and validates the response.
     */
    @Order(5)
    @Test
    public void testDelete() {
        int id = 1; // Defines the ID for the new book to be created.

        // Use testSingleDelete from AbstractApiClass to perform the test.
        testSingleDelete(id, BASE_URL, "Books", logger);
    }

    /**
     * Inner class to process and validate book data.
     */
    static class BookProcessor {

        /**
         * Validates the JSON response of a single book GET request against stored book data.
         *
         * @param json     The JSON string representing the book data from the response.
         * @param objectId The ID of the book being validated.
         */
        void validateBookJson(String json, JsonObject singleBook) {
            try {
                // Parse the JSON string into a JsonObject.
                JsonObject book = gson.fromJson(json, JsonObject.class);
                int objectId = singleBook.get("id").getAsInt();
                // Log the retrieved and expected book data for debugging.
                logger.log(Level.FINE, "BOOK validateJson - GOT : " + book + " EXPECTED : id : " + objectId + " title : " + singleBook.get("title").getAsString() + " description : " + singleBook.get("description").getAsString() + " excerpt : " + singleBook.get("excerpt").getAsString() + " pageCount : " + singleBook.get("pageCount").getAsInt() + " publishDate : " + singleBook.get("publishDate").getAsString());

                // Assert that the 'id' field exists in the JSON and matches the stored book's ID.
                assertTrue(book.has("id"), "Test id " + objectId + ": Book JSON missing 'id' field.");
                assertEquals(objectId, book.get("id").getAsInt(), "Test id " + objectId + ": Book ID mismatch.");

                // Assert that the 'pageCount' field exists and matches the stored book's page count.
                assertTrue(book.has("pageCount"), "Test id " + objectId + ": Book JSON missing 'pageCount' field.");
                assertEquals(singleBook.get("pageCount").getAsInt(), book.get("pageCount").getAsInt(), "Test id " + objectId + ": Page count mismatch.");

                // Assert that the 'title' field exists and matches the stored book's title.
                assertTrue(book.has("title"), "Test id " + objectId + ": Book JSON missing 'title' field.");
                assertEquals(singleBook.get("title").getAsString(), book.get("title").getAsString(), "Test id " + objectId + ": Title mismatch.");

                // Assert that the 'description' field exists and matches the stored book's description.
                assertTrue(book.has("description"), "Test id " + objectId + ": Book JSON missing 'description' field.");
                assertEquals(singleBook.get("description").getAsString(), book.get("description").getAsString(), "Test id " + objectId + ": description mismatch.");

                // Assert that the 'excerpt' field exists and matches the stored book's excerpt.
                assertTrue(book.has("excerpt"), "Test id " + objectId + ": Book JSON missing 'excerpt' field.");
                assertEquals(singleBook.get("excerpt").getAsString(), book.get("excerpt").getAsString(), "Test id " + objectId + ": excerpt mismatch.");

                // Assert that the 'publishDate' field exists and matches the stored book's publish date.
                assertTrue(book.has("publishDate"), "Test id " + objectId + ": Book JSON missing 'publishDate' field.");
                // Compare the publish dates after removing milliseconds and timezone information.
                assertEquals(Utils.getGmtDateTime(singleBook.get("publishDate").getAsString()), Utils.getGmtDateTime(book.get("publishDate").getAsString()), "Test id " + objectId + ": Due Date mismatch.");

            } catch (com.google.gson.JsonSyntaxException e) {
                // Handle JSON parsing errors by logging and failing the test.
                logger.log(Level.SEVERE, "Test id " + singleBook.get("id").getAsInt() + " JSON parsing failed: " + e.getMessage());
                
                fail("Test Validate " + singleBook.get("id").getAsInt() + " JSON parsing failed."); // Force JUnit fail
            }
        }

        /**
         * Validates the JSON response of a book creation or update request.
         *
         * @param responseBody The JSON string from the response body.
         * @param newBook      The JsonObject representing the expected book data.
         */
        void checkBookUpdate(String responseBody, JsonObject newBook) {
            // Parse the response body JSON string into a JsonObject.
            JsonObject createdBook = gson.fromJson(responseBody, JsonObject.class);
            
            logger.log(Level.FINE, "Expected : " + newBook + " Found : " + createdBook);
            // Assert that all required fields are present in the created book's JSON.
            assertTrue(createdBook.has("id"), "New Book registration/update failed : Missing 'id' in response.");
            assertTrue(createdBook.has("pageCount"), "New Book registration/update failed : Missing 'pageCount' in response.");
            assertTrue(createdBook.has("title"), "New Book registration/update failed : Missing 'title' in response.");
            assertTrue(createdBook.has("description"), "New Book registration/update failed : Missing 'description' in response.");
            assertTrue(createdBook.has("excerpt"), "New Book registration/update failed : Missing 'excerpt' in response.");
            assertTrue(createdBook.has("publishDate"), "New Book registration/update failed : Missing 'publishDate' in response.");
            // Assert that each field matches the expected values from the newBook JsonObject.
            assertEquals(newBook.get("id").getAsInt(), createdBook.get("id").getAsInt(), "New Book registration/update failed : id mismatch");
            assertEquals(newBook.get("pageCount").getAsInt(), createdBook.get("pageCount").getAsInt(), "New Book registration/update failed : pageCount mismatch");
            assertEquals(newBook.get("title").getAsString(), createdBook.get("title").getAsString(), "New Book registration/update failed : title mismatch");
            assertEquals(newBook.get("description").getAsString(), createdBook.get("description").getAsString(), "New Book registration/update failed : description mismatch");
            assertEquals(newBook.get("excerpt").getAsString(), createdBook.get("excerpt").getAsString(), "New Book registration/update failed : excerpt mismatch");
            // Compare publish dates after removing milliseconds and timezone information.
            assertEquals(Utils.getGmtDateTime(newBook.get("publishDate").getAsString()), Utils.getGmtDateTime(createdBook.get("publishDate").getAsString()), "New Book registration/update failed : publishDate mismatch");
        }
    }
}
