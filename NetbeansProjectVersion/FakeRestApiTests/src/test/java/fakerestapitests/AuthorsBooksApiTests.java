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
import fakerestapitests.stuctures.Book;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // Specifies that the test methods in this class will be executed in the order defined by the @Order annotation.
public class AuthorsBooksApiTests extends AbstractApiClass {

    private static final String BASE_URL_AUTHORS = BASE_API_URL + "Authors"; // Defines the base URL for the Authors API endpoint by appending "Authors" to the base API URL defined in AbstractApiClass.
    // Base URL for the Books API endpoint.
    private static final String BASE_URL_BOOKS = BASE_API_URL + "Books";
    private static final Logger logger = Logger.getLogger(AuthorsBooksApiTests.class.getName()); // Initializes a logger to log information and errors during test execution.

    @Order(1) // Specifies that this test method should be executed first.
    @Test // Marks this method as a JUnit 5 test method.
    public void testGet() throws IOException, InterruptedException {
        // Build the GET request.
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL_BOOKS)).GET().build();
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
            //  storedBooks.clear(); //Clear stored books. commented out.
            BooksApiTests.BookProcessor processor = new BooksApiTests.BookProcessor(); //create book processor object.
            // Iterate over each book in the array.
            for (JsonElement bookElement : books) {
                // Log the book element for debugging.
                logger.log(Level.FINE, "BOOK testGetAll : " + bookElement);

                // Convert the book element to a JsonObject.
                JsonObject book = bookElement.getAsJsonObject();
                // Assert that each required field exists and has the correct type.
                assertTrue(book.has("id"));
                singleBook.id = book.get("id").getAsInt();
                // Check GET Author by the specific id and check if data is the same
                logger.log(Level.FINE, "Authors testGet - author.idBook : " + singleBook.id);
                try {
                    // Construct the URL for the GET request.
                    String url = BASE_URL_AUTHORS + "/books/" + singleBook.id;

                    // Build the GET request.
                    request = HttpRequest.newBuilder().uri(URI.create(url)).GET().header("accept", "application/json; v=1").build();
                    // Send the request and get the response.
                    response = client.send(request, HttpResponse.BodyHandlers.ofString());

                    // Log the response status code.
                    logger.log(Level.FINE, "Get author from bookId :" + singleBook.id + " had response.statusCode() : " + response.statusCode());

                    if (response.statusCode() != 200) {
                        fail("Get author from bookId :" + singleBook.id + " had response.statusCode() : " + response.statusCode());
                    }
                } catch (IOException | InterruptedException e) {
                    // Log the exception and fail the test.
                    logger.log(Level.SEVERE, "Get author from bookId : " + singleBook.id + " failed : " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }
}