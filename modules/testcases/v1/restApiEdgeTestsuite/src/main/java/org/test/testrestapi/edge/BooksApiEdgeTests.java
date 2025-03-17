package org.test.testrestapi.edge;

import org.junit.jupiter.api.Test;

import com.google.gson.JsonObject;
import org.test.testrestapi.abstractclasses.AbstractHappyPathApiClass;
import org.test.testrestapi.interfaces.ApiEdgeInterface;
import org.test.testrestapi.utils.Utils;
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
import org.jboss.eap.additional.testsuite.annotations.EAT;

/**
 * Tests for the Books API endpoints using the fake REST API. This class extends
 * AbstractApiClass for common API test functionalities and implements
 * ApiInterface. The tests are ordered using @TestMethodOrder and @Order
 * annotations to ensure sequential execution.
 */
@EAT({"modules/testcases/v1/restApiEdgeTestsuite/src/main/java"})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BooksApiEdgeTests extends AbstractHappyPathApiClass implements ApiEdgeInterface {

    // Base URL for the Books API endpoint.
    private static final String BASE_URL = BASE_API_URL + "Books";
    // Logger for logging test information.
    private static final Logger logger = Logger.getLogger(BooksApiEdgeTests.class.getName());

    /**
     * Tests the POST endpoint to create a new book. Creates a new book with a
     * unique ID and validates the response.
     */
    @Order(1)
    @Test
    public void testPostNegative() {
        // Generate a unique ID for the new book.
        int id = -10000;
        // Create a JsonObject representing the new book.
        JsonObject newBook = new JsonObject();
        newBook.addProperty("id", id);
        newBook.addProperty("pageCount", -1000);
        newBook.addProperty("title", "Test title");
        newBook.addProperty("description", "Test description");
        newBook.addProperty("excerpt", "Test excerpt");
        newBook.addProperty("publishDate", OffsetDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        // Create a BookProcessor instance to validate the book update.
        BookProcessor processor = new BookProcessor();
        // Use testSinglePost from AbstractApiClass to perform the test.
        testSinglePost(id, newBook, BASE_URL, "Books", logger, processor::checkBookUpdate);

        fail("Negative ids should throw an exception..."); // Fails the test if negative IDs are accepted, indicating an error.
    }

    /**
     * Tests the PUT endpoint to update an existing book. Updates a book with a
     * specific ID and validates the response.
     */
    @Order(2)
    @Test
    public void testPutNegative() {
        // Specify the ID of the book to update.
        int id = 1;
        // Create a JsonObject representing the updated book.
        JsonObject updatedBook = new JsonObject();
        updatedBook.addProperty("id", id);
        updatedBook.addProperty("pageCount", -1001);
        updatedBook.addProperty("title", "Test title2");
        updatedBook.addProperty("description", "Test description2");
        updatedBook.addProperty("excerpt", "Test excerpt2");
        updatedBook.addProperty("publishDate", OffsetDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        // Create a BookProcessor instance to validate the book update.
        BookProcessor processor = new BookProcessor();
        // Use testSinglePut from AbstractApiClass to perform the test.
        testSinglePut(id, updatedBook, BASE_URL, "Books", logger, processor::checkBookUpdate);

        fail("Negative ids should throw an exception..."); // Fails the test if negative IDs are accepted, indicating an error.
    }

    /**
     * Tests the POST endpoint to create a new book. Creates a new book with a
     * unique ID and validates the response.
     */
    @Order(3)
    @Test
    public void testPostBigInt() {
        // Generate a unique ID for the new book.
        int id = 2147483647;
        // Create a JsonObject representing the new book.
        JsonObject newBook = new JsonObject();
        newBook.addProperty("id", id);
        newBook.addProperty("pageCount", 2147483647);
        newBook.addProperty("title", "Test titleTest title");
        newBook.addProperty("description", "Test description");
        newBook.addProperty("excerpt", "Test excerpt");
        newBook.addProperty("publishDate", OffsetDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        // Create a BookProcessor instance to validate the book update.
        BookProcessor processor = new BookProcessor();
        // Use testSinglePost from AbstractApiClass to perform the test.
        testSinglePost(id, newBook, BASE_URL, "Books", logger, processor::checkBookUpdate);
    }

    /**
     * Tests the PUT endpoint to update an existing book. Updates a book with a
     * specific ID and validates the response.
     */
    @Order(4)
    @Test
    public void testPutBigInt() {
        // Specify the ID of the book to update.
        int id = 1;
        // Create a JsonObject representing the updated book.
        JsonObject updatedBook = new JsonObject();
        updatedBook.addProperty("id", id);
        updatedBook.addProperty("pageCount", 2147483647);
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
     * Tests the POST endpoint to create a new book. Creates a new book with a
     * unique ID and validates the response.
     */
    @Order(5)
    @Test
    public void testPostBigString() {
        // Generate a unique ID for the new book.
        int id = 100;
        // Create a JsonObject representing the new book.
        JsonObject newBook = new JsonObject();
        newBook.addProperty("id", id);
        newBook.addProperty("pageCount", 1000);
        newBook.addProperty("title", "Test titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest title");
        newBook.addProperty("description", "Test descriptionTest descriptionTest descriptionTest descriptionTest descriptionTest descriptionTest descriptionTest descriptionTest descriptionTest descriptionTest descriptionTest descriptionTest descriptionTest descriptionTest descriptionTest descriptionTest descriptionTest descriptionTest descriptionTest descriptionTest descriptionTest descriptionTest descriptionTest descriptionTest descriptionTest descriptionTest descriptionTest descriptionTest descriptionTest descriptionTest descriptionTest descriptionTest descriptionTest descriptionTest descriptionTest descriptionTest descriptionTest descriptionTest descriptionTest descriptionTest descriptionTest descriptionTest descriptionTest description");
        newBook.addProperty("excerpt", "Test excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerpt");

        newBook.addProperty("publishDate", OffsetDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        // Create a BookProcessor instance to validate the book update.
        BookProcessor processor = new BookProcessor();
        // Use testSinglePost from AbstractApiClass to perform the test.
        testSinglePost(id, newBook, BASE_URL, "Books", logger, processor::checkBookUpdate);
    }

    /**
     * Tests the PUT endpoint to update an existing book. Updates a book with a
     * specific ID and validates the response.
     */
    @Order(6)
    @Test
    public void testPutBigString() {
        // Specify the ID of the book to update.
        int id = 1;
        // Create a JsonObject representing the updated book.
        JsonObject updatedBook = new JsonObject();
        updatedBook.addProperty("id", id);
        updatedBook.addProperty("pageCount", 1001);
        updatedBook.addProperty("title", "Test titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest titleTest title");
        updatedBook.addProperty("description", "Test descriptionTest descriptionTest descriptionTest descriptionTest descriptionTest descriptionTest descriptionTest descriptionTest descriptionTest descriptionTest descriptionTest descriptionTest descriptionTest descriptionTest descriptionTest descriptionTest descriptionTest descriptionTest descriptionTest descriptionTest descriptionTest descriptionTest descriptionTest descriptionTest descriptionTest descriptionTest descriptionTest descriptionTest descriptionTest descriptionTest descriptionTest descriptionTest descriptionTest descriptionTest descriptionTest descriptionTest descriptionTest descriptionTest descriptionTest descriptionTest descriptionTest descriptionTest descriptionTest description");
        updatedBook.addProperty("excerpt", "Test excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerptTest excerpt");
        updatedBook.addProperty("publishDate", OffsetDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        // Create a BookProcessor instance to validate the book update.
        BookProcessor processor = new BookProcessor();
        // Use testSinglePut from AbstractApiClass to perform the test.
        testSinglePut(id, updatedBook, BASE_URL, "Books", logger, processor::checkBookUpdate);
    }

    /**
     * Inner class to process and validate book data.
     */
    static class BookProcessor {

        /**
         * Validates the JSON response of a book creation or update request.
         *
         * @param responseBody The JSON string from the response body.
         * @param newBook The JsonObject representing the expected book data.
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
