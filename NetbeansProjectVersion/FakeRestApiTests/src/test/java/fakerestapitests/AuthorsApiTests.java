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
import fakerestapitests.stuctures.Author;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // Specifies that the test methods in this class will be executed in the order defined by the @Order annotation.
public class AuthorsApiTests extends AbstractApiClass implements ApiInterface {

    private static final String BASE_URL = BASE_API_URL + "Authors"; // Defines the base URL for the Authors API endpoint by appending "Authors" to the base API URL defined in AbstractApiClass.
    private static final Logger logger = Logger.getLogger(AuthorsApiTests.class.getName()); // Initializes a logger to log information and errors during test execution.

    @Order(1) // Specifies that this test method should be executed first.
    @Test // Marks this method as a JUnit 5 test method.
    public void testGet() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL)).GET().build(); // Creates an HTTP GET request to retrieve all authors from the API.
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString()); // Sends the HTTP request and stores the response, handling it as a string.
        logger.log(Level.FINE, "Authors testGetAll - response.statusCode() : " + response.statusCode()); // Logs the HTTP status code of the response.
        assertEquals(200, response.statusCode(), "Get All Authors failed: Expected 200, got " + response.statusCode()); // Asserts that the HTTP status code is 200 (OK).

        if (response.statusCode() == 200) { // Checks if the response was successful.
            JsonArray authors = gson.fromJson(response.body(), JsonArray.class); // Parses the JSON response body into a JsonArray.
            logger.log(Level.FINE, "Authors testGetAll - authors.size() : " + authors.size()); // Logs the number of authors retrieved.
            assertTrue(authors.size() > 0, "No authors returned"); // Asserts that at least one author was returned.
            Author singleAuthor = new Author(); // Creates an Author object to store author data.
        //    storedAuthors.clear();
            AuthorsApiTests.AuthorProcessor processor = new AuthorsApiTests.AuthorProcessor();
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

                    // Check GET Author by the specific id and check if data is the same
                logger.log(Level.FINE, "Authors testGet - author.id : " + singleAuthor.id); // Logs the ID of the author being retrieved.
                testSingleGet(singleAuthor.id, author, BASE_URL, "Authors", logger, processor::validateAuthorJson); // Tests the GET endpoint for a single author.
         //       storedAuthors.put(singleAuthor.id, singleAuthor); // Stores the retrieved author in a map for later use in other tests.
                
            }
        }
    }

    @Order(3) // Specifies that this test method should be executed third.
    @Test // Marks this method as a JUnit 5 test method.
    public void testPost() {
        int id = 10000; // Defines the ID for the new author to be created.
        JsonObject newAuthor = new JsonObject(); // Creates a JsonObject representing the new author.
        newAuthor.addProperty("id", id); // Adds the author ID to the JSON object.
        newAuthor.addProperty("idBook", 1000); // Adds the book ID to the JSON object.
        newAuthor.addProperty("firstName", "Test firstName"); // Adds the author's first name to the JSON object.
        newAuthor.addProperty("lastName", "Test lastName"); // Adds the author's last name to the JSON object.

        AuthorsApiTests.AuthorProcessor processor = new AuthorsApiTests.AuthorProcessor(); // Creates an instance of the AuthorProcessor inner class.
        testSinglePost(id, newAuthor, BASE_URL, "Authors", logger, processor::checkAuthorUpdate); // Tests the POST endpoint for creating a new author.
    }

    @Order(4) // Specifies that this test method should be executed fourth.
    @Test // Marks this method as a JUnit 5 test method.
    public void testPut() {
        int id = 1; // Defines the ID of the author to be updated.
        JsonObject updatedAuthor = new JsonObject(); // Creates a JsonObject representing the updated author.
        updatedAuthor.addProperty("id", id); // Adds the author ID to the JSON object.
        updatedAuthor.addProperty("idBook", 1001); // Adds the updated book ID to the JSON object.
        updatedAuthor.addProperty("firstName", "Test firstName2"); // Adds the updated first name to the JSON object.
        updatedAuthor.addProperty("lastName", "Test lastName2"); // Adds the updated last name to the JSON object.
     
        AuthorsApiTests.AuthorProcessor processor = new AuthorsApiTests.AuthorProcessor(); // Creates an instance of the AuthorProcessor inner class.
        testSinglePut(id, updatedAuthor, BASE_URL, "Authors", logger, processor::checkAuthorUpdate); // Tests the PUT endpoint for updating an author.
    }

    @Order(5) // Specifies that this test method should be executed fifth.
    @Test // Marks this method as a JUnit 5 test method.
    public void testDelete() {
        int id = 1; // Defines the ID for the author to be deleted.
       
        testSingleDelete(id, BASE_URL, "Authors", logger); // Tests the DELETE endpoint for deleting an author.
    }

    static class AuthorProcessor { // Defines a static inner class named AuthorProcessor, used to process and validate author data.

        /**
         * Validates the JSON response of a single author GET request against
         * stored author data.
         *
         * @param json The JSON string representing the author data from the
         * response.
         * @param objectId The ID of the author being validated.
         */
        void validateAuthorJson(String json, JsonObject singleAuthor) { // Method to validate the JSON response of a single author GET request.
            try {

                JsonObject author = gson.fromJson(json, JsonObject.class); // Parses the JSON string into a JsonObject.
                int objectId = singleAuthor.get("id").getAsInt();
                
                logger.log(Level.FINE, "AUTHOR validateJson - GOT : " + author + " EXPECTED : id : " + singleAuthor.get("id").getAsInt() + " firstName : " + singleAuthor.get("firstName").getAsString() + " lastName : " + singleAuthor.get("lastName").getAsString() + " idBook : " + singleAuthor.get("idBook").getAsInt()); // Logs the received and expected author data.
                assertTrue(author.has("id"), "Test id " + objectId + ": Author JSON missing 'id' field."); // Asserts that the JSON object contains the "id" field.
                assertEquals(singleAuthor.get("id").getAsInt(), author.get("id").getAsInt(), "Test id " + objectId + ": Author ID mismatch."); // Asserts that the author ID matches the expected value.

                assertTrue(author.has("firstName"), "Test id " + objectId + ": Author JSON missing 'firstName' field."); // Asserts that the JSON object contains the "firstName" field.
                assertEquals(singleAuthor.get("firstName").getAsString(), author.get("firstName").getAsString(), "Test id " + objectId + ": Firstname mismatch."); // Asserts that the first name matches the expected value.

                assertTrue(author.has("lastName"), "Test id " + objectId + ": Author JSON missing 'lastName' field."); // Asserts that the JSON object contains the "lastName" field.
                assertEquals(singleAuthor.get("lastName").getAsString(), author.get("lastName").getAsString(), "Test id " + objectId + ":  Lastname mismatch."); // Asserts that the last name matches the expected value.

                assertTrue(author.has("idBook"), "Test id " + objectId + ": Author JSON missing 'idBook' field."); // Asserts that the JSON object contains the "idBook" field.
                assertEquals(singleAuthor.get("idBook").getAsInt(), author.get("idBook").getAsInt(), "Test id " + objectId + ": idBook mismatch."); // Asserts that the book ID matches the expected value.
            } catch (com.google.gson.JsonSyntaxException e) { // Catches JsonSyntaxException, which occurs when JSON parsing fails.
                // Handle JSON parsing errors by logging and failing the test.
                logger.log(Level.SEVERE, "Test id " + singleAuthor.get("id").getAsInt() + " JSON parsing failed: " + e.getMessage()); // Logs the JSON parsing error.
                fail("Test Validate " + singleAuthor.get("id").getAsInt() + " JSON parsing failed."); // Forces the JUnit test to fail.
            }
        }

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
            assertEquals(newAuthor.get("idBook").getAsInt(), createdAuthor.get("idBook").getAsInt(), "New Author registration/update failed : idBook mismatch"); // Asserts that the book ID matches the expected value.
            assertEquals(newAuthor.get("firstName").getAsString(), createdAuthor.get("firstName").getAsString(), "New Author registration/update failed : firstName mismatch"); // Asserts that the first name matches the expected value.
            assertEquals(newAuthor.get("lastName").getAsString(), createdAuthor.get("lastName").getAsString(), "New Author registration/update failed : lastName mismatch"); // Asserts that the last name matches the expected value.
        }
    }

}
