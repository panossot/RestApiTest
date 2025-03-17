package fakeresthappypathapiedgetests;

import fakeresthappypathapitests.*;
import fakerestapitests.*;
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
import fakerestapitests.interfaces.ApiEdgeInterface;
import fakerestapitests.interfaces.ApiInterface;
import fakerestapitests.stuctures.Author;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // Configures test method execution order based on @Order annotations.
public class AuthorsApiEdgeTests extends AbstractHappyPathApiClass implements ApiEdgeInterface {

    private static final String BASE_URL = BASE_API_URL + "Authors"; // Constructs the base URL for Authors API endpoints.
    private static final Logger logger = Logger.getLogger(AuthorsApiEdgeTests.class.getName()); // Initializes a logger for this test class.

    @Order(1) // Specifies this test method should run first.
    @Test // Marks this method as a JUnit 5 test.
    public void testPostNegative() {
        int id = -1; // Defines a negative ID for the new author (for negative testing).
        JsonObject newAuthor = new JsonObject(); // Creates a JSON object for the new author's data.
        newAuthor.addProperty("id", id); // Adds the negative ID to the JSON object.
        newAuthor.addProperty("idBook", -11); // Adds a negative book ID to the JSON object.
        newAuthor.addProperty("firstName", "Test firstName"); // Adds a test first name.
        newAuthor.addProperty("lastName", "Test lastName"); // Adds a test last name.

        AuthorsApiEdgeTests.AuthorProcessor processor = new AuthorsApiEdgeTests.AuthorProcessor(); // Creates an AuthorProcessor instance for response validation.
        testSinglePost(id, newAuthor, BASE_URL, "Authors", logger, processor::checkAuthorUpdate); // Executes the POST request and validation.

        fail("Negative ids should throw an exception..."); // Fails the test if negative IDs are accepted, indicating an error.
    }

    @Order(2) // Specifies this test method should run second.
    @Test // Marks this method as a JUnit 5 test.
    public void testPutNegative() {
        int id = 1; // Defines a positive ID for the author to update.
        JsonObject updatedAuthor = new JsonObject(); // Creates a JSON object for the updated author's data.
        updatedAuthor.addProperty("id", id); // Adds the author ID.
        updatedAuthor.addProperty("idBook", -1001); // Adds a negative book ID for update.
        updatedAuthor.addProperty("firstName", "Test firstName2"); // Adds an updated first name.
        updatedAuthor.addProperty("lastName", "Test lastName2"); // Adds an updated last name.

        AuthorsApiEdgeTests.AuthorProcessor processor = new AuthorsApiEdgeTests.AuthorProcessor(); // Creates an AuthorProcessor instance for response validation.
        testSinglePut(id, updatedAuthor, BASE_URL, "Authors", logger, processor::checkAuthorUpdate); // Executes the PUT request and validation.

        fail("Negative ids should throw an exception..."); // Fails the test if negative IDs are accepted, indicating an error.
    }

    @Order(3) // Specifies this test method should run third.
    @Test // Marks this method as a JUnit 5 test.
    public void testPostBigInt() {
        int id = 2147483647; // Defines the maximum integer value for the author ID.
        JsonObject newAuthor = new JsonObject(); // Creates a JSON object for the new author's data.
        newAuthor.addProperty("id", id); // Adds the maximum integer ID.
        newAuthor.addProperty("idBook", 2147483647); // Adds the maximum integer book ID.(idBook is not checked in HappyPath)
        newAuthor.addProperty("firstName", "Test firstName"); // Adds a test first name.
        newAuthor.addProperty("lastName", "Test lastName"); // Adds a test last name.

        AuthorsApiEdgeTests.AuthorProcessor processor = new AuthorsApiEdgeTests.AuthorProcessor(); // Creates an AuthorProcessor instance for response validation.
        testSinglePost(id, newAuthor, BASE_URL, "Authors", logger, processor::checkAuthorUpdate); // Executes the POST request and validation.
    }

    @Order(4) // Specifies this test method should run fourth.
    @Test // Marks this method as a JUnit 5 test.
    public void testPutBigInt() {
        int id = 1; // Defines a positive ID for the author to update.
        JsonObject updatedAuthor = new JsonObject(); // Creates a JSON object for the updated author's data.
        updatedAuthor.addProperty("id", id); // Adds the author ID.
        updatedAuthor.addProperty("idBook", 2147483647); // Adds the maximum integer book ID for update. (idBook is not checked in HappyPath)
        updatedAuthor.addProperty("firstName", "Test firstName2"); // Adds an updated first name.
        updatedAuthor.addProperty("lastName", "Test lastName2"); // Adds an updated last name.

        AuthorsApiEdgeTests.AuthorProcessor processor = new AuthorsApiEdgeTests.AuthorProcessor(); // Creates an AuthorProcessor instance for response validation.
        testSinglePut(id, updatedAuthor, BASE_URL, "Authors", logger, processor::checkAuthorUpdate); // Executes the PUT request and validation.
    }

    @Order(5) // Specifies this test method should run fifth.
    @Test // Marks this method as a JUnit 5 test.
    public void testPostBigString() {
        int id = 1; // Defines a positive ID for the new author.
        JsonObject newAuthor = new JsonObject(); // Creates a JSON object for the new author's data.
        newAuthor.addProperty("id", id); // Adds the author ID.
        newAuthor.addProperty("idBook", 1); // Adds a book ID.
        newAuthor.addProperty("firstName", "Test firstName... (very long string)"); // Adds a very long first name string.
        newAuthor.addProperty("lastName", "Test lastName... (very long string)"); // Adds a very long last name string.

        AuthorsApiEdgeTests.AuthorProcessor processor = new AuthorsApiEdgeTests.AuthorProcessor(); // Creates an AuthorProcessor instance for response validation.
        testSinglePost(id, newAuthor, BASE_URL, "Authors", logger, processor::checkAuthorUpdate); // Executes the POST request and validation.
    }

    @Order(6) // Specifies this test method should run sixth.
    @Test // Marks this method as a JUnit 5 test.
    public void testPutBigString() {
        int id = 1; // Defines a positive ID for the author to update.
        JsonObject updatedAuthor = new JsonObject(); // Creates a JSON object for the updated author's data.
        updatedAuthor.addProperty("id", id); // Adds the author ID.
        updatedAuthor.addProperty("idBook", 1); // Adds a book ID.
        updatedAuthor.addProperty("firstName", "Test firstName2... (very long string)"); // Adds a very long updated first name string.
        updatedAuthor.addProperty("lastName", "Test lastName2... (very long string)"); // Adds a very long updated last name string.

        AuthorsApiEdgeTests.AuthorProcessor processor = new AuthorsApiEdgeTests.AuthorProcessor(); // Creates an AuthorProcessor instance for response validation.
        testSinglePut(id, updatedAuthor, BASE_URL, "Authors", logger, processor::checkAuthorUpdate); // Executes the PUT request and validation.
    }

    static class AuthorProcessor { // Defines a static inner class named AuthorProcessor, used to process and validate author data.

        /**
         * Validates the JSON response of a author creation or update request.
         *
         * @param responseBody The JSON string from the response body.
         * @param newAuthor The JsonObject representing the expected author
         * data.
         */
        void checkAuthorUpdate(String responseBody, JsonObject newAuthor) { // Method to validate the JSON response of an author creation or update request.
            // Parse the response body JSON string into a JsonObject.
            JsonObject createdAuthor = gson.fromJson(responseBody, JsonObject.class); // Parses the response body JSON into a JsonObject.

            logger.log(Level.FINE, "Expected : " + newAuthor + " Found : " + createdAuthor); // Logs the expected and received author data.

            // Assert that all required fields are present in the created author's JSON.
            assertTrue(createdAuthor.has("id"), "New Author registration/update failed : Missing 'id' in response."); // Asserts that the response contains the "id" field.
            assertTrue(createdAuthor.has("idBook"), "New Author registration/update failed : Missing 'idBook' in response."); // Asserts that the response contains the "idBook" field.
            assertTrue(createdAuthor.has("firstName"), "New Author registration/update failed : Missing 'firstName' in response."); // Asserts that the response contains the "firstName" field.
            assertTrue(createdAuthor.has("lastName"), "New Author registration/update failed : Missing 'lastName' in response."); // Asserts that the response contains the "lastName" field.
            // Assert that each field matches the expected values from the newAuthor JsonObject.
            assertEquals(newAuthor.get("id").getAsInt(), createdAuthor.get("id").getAsInt(), "New Author registration/update failed : id mismatch"); // Asserts that the ID matches the expected value.
            //   assertEquals(newAuthor.get("idBook").getAsInt(), createdAuthor.get("idBook").getAsInt(), "New Author registration/update failed : idBook mismatch"); // Asserts that the book ID matches the expected value.
            assertEquals(newAuthor.get("firstName").getAsString(), createdAuthor.get("firstName").getAsString(), "New Author registration/update failed : firstName mismatch"); // Asserts that the first name matches the expected value.
            assertEquals(newAuthor.get("lastName").getAsString(), createdAuthor.get("lastName").getAsString(), "New Author registration/update failed : lastName mismatch"); // Asserts that the last name matches the expected value.
        }
    }

}
