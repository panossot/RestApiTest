/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fakerestapitests;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fakerestapitests.abstractclasses.AbstractApiClass;
import fakerestapitests.interfaces.ApiInterface;
import fakerestapitests.stuctures.Author;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthorsApiTests extends AbstractApiClass implements ApiInterface{

    private static final String BASE_URL = BASE_API_URL + "Authors";
    private static final Logger logger = Logger.getLogger(AuthorsApiTests.class.getName());


    @Order(1)
    @Test
    public void testGetAll() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL)).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        logger.log(Level.INFO, "Authors testGetAll - response.statusCode() : " + response.statusCode());
        assertEquals(200, response.statusCode(), "Get All Authors failed: Expected 200, got " + response.statusCode());

        if (response.statusCode() == 200) {
            JsonArray authors = gson.fromJson(response.body(), JsonArray.class);
            logger.log(Level.INFO, "Authors testGetAll - authors.size() : " + authors.size());
            assertTrue(authors.size() > 0, "No authors returned");
            Author singleAuthor = new Author();
            for (JsonElement authorElement : authors) {
                logger.log(Level.INFO, "Authors testGetAll - JsonElement authorElement : " + authorElement);
                JsonObject author = authorElement.getAsJsonObject();
                assertTrue(author.has("id"));
                singleAuthor.id = author.get("id").getAsInt();
                assertTrue(singleAuthor.id >= 0);
                assertTrue(author.has("firstName"));
                singleAuthor.firstName = author.get("firstName").getAsString();
                assertTrue(singleAuthor.firstName != null);
                assertTrue(author.has("lastName"));
                singleAuthor.lastName = author.get("lastName").getAsString();
                assertTrue(singleAuthor.lastName != null);
                assertTrue(author.has("idBook"));
                singleAuthor.idBook = author.get("idBook").getAsInt();
                assertTrue(author.get("idBook").getAsString() != null);

                storedAuthors.put(singleAuthor.id, singleAuthor);

            }
        }

    }

    @Order(2)
    @Test
    public void testGet() {
        for (Author author : storedAuthors.values()) {
            logger.log(Level.INFO, "Authors testGet - author.id : " + author.id);
            testSingleGet(author.id);
        }
    }

    @Order(3)
    @Test
    public void testPost() {
        Author singleAuthor = new Author();
        singleAuthor.id = 10000;
        singleAuthor.firstName = "Test firstName";
        singleAuthor.lastName = "Test lastName";
        singleAuthor.idBook = 1000;
        testSinglePostAuthor(singleAuthor);
    }

    @Order(4)
    @Test
    public void testPut() {
        Author singleAuthor = new Author();
        singleAuthor.id = 1;
        singleAuthor.firstName = "Test firstName";
        singleAuthor.lastName = "Test lastName";
        singleAuthor.idBook = 1000;
        testSinglePutAuthor(singleAuthor);
    }

    @Order(5)
    @Test
    public void testDelete() {
        testSingleDeleteAuthor(1);
    }

    private void testSingleGetAuthor(int authorId) {
        try {
            String url = BASE_URL + "/" + authorId;

            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println(response.body());
            assertEquals(200, response.statusCode(), "Test " + authorId + " (Authors/" + authorId + ") failed: Expected 200, got " + response.statusCode());

            if (response.statusCode() == 200) {
                validateAuthorJson(response.body(), authorId);
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("Test " + authorId + " failed: " + e.getMessage());
            e.printStackTrace();
            fail("Test GET " + authorId + " failed due to exception."); // Force JUnit fail
        }
    }

    private void testSingleDeleteAuthor(int authorId) {
        try {
            String url = BASE_URL + "/" + authorId;

            // Check if the author exists first (GET request):
            HttpRequest getRequest = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
            HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());

            if (getResponse.statusCode() == 200) { // If it exists, try to delete it
                HttpRequest deleteRequest = HttpRequest.newBuilder().uri(URI.create(url)).DELETE().build();
                HttpResponse<String> deleteResponse = client.send(deleteRequest, HttpResponse.BodyHandlers.ofString());

                assertEquals(200, deleteResponse.statusCode(), "Test " + authorId + " (DELETE Authors/" + authorId + ") failed: Expected 200, got " + deleteResponse.statusCode());

                // Optionally, check if the author is really gone (another GET request):
                HttpRequest checkGetRequest = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
                HttpResponse<String> checkGetResponse = client.send(checkGetRequest, HttpResponse.BodyHandlers.ofString());
                //    System.out.println(checkGetResponse.body());
                    assertEquals(404, checkGetResponse.statusCode(), "Test " + authorId + " (DELETE Authors/" + authorId + ") failed: Author not deleted");

            } else if (getResponse.statusCode() == 404) {
                fail("Author does not exist to be deleted");
            } else {
                fail("Test " + authorId + " (GET Authors/" + authorId + ") failed: Unexpected get response code: " + getResponse.statusCode());
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("Test " + authorId + " failed: " + e.getMessage());
            e.printStackTrace();
            fail("Test DELETE " + authorId + " failed due to exception.");
        }
    }

    private void testSinglePostAuthor(Author singleAuthor) {
        try {
            // Create a new author object (JSON):
            JsonObject newAuthor = new JsonObject();
            newAuthor.addProperty("id", singleAuthor.id);
            newAuthor.addProperty("firstName", singleAuthor.firstName);
            newAuthor.addProperty("lastName", singleAuthor.lastName);
            newAuthor.addProperty("idBook", singleAuthor.idBook);

            String requestBody = gson.toJson(newAuthor);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        //    System.out.println(response.body());

            assertEquals(200, response.statusCode(), "Test " + singleAuthor.id + " (POST Authors) failed: Expected 200, got " + response.statusCode());

            if (response.statusCode() == 200) {
                // Verify the created author (optional):
                JsonObject createdAuthor = gson.fromJson(response.body(), JsonObject.class);
                assertTrue(createdAuthor.has("id"), "Test " + singleAuthor.id + " (POST Authors) failed: Missing 'id' in response.");
                assertEquals(singleAuthor.firstName, createdAuthor.get("firstName").getAsString(), "Test " + singleAuthor.id + " (POST Authors) failed: firstName mismatch");
                assertEquals(singleAuthor.lastName, createdAuthor.get("lastName").getAsString(), "Test "+singleAuthor.id+" (POST Authors) failed: lastName mismatch");

                assertEquals(singleAuthor.idBook, createdAuthor.get("idBook").getAsInt(), "Test " + singleAuthor.id + " (POST Authors) failed: idBook mismatch");
            }

                    String url = BASE_URL + "/" + singleAuthor.id;

            request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println(response.body());
            assertEquals(200, response.statusCode(), "Test " + singleAuthor.id + " (Authors/" + singleAuthor.id + ") failed: Expected 200, got " + response.statusCode());

            if (response.statusCode() == 200) {
                JsonObject author = gson.fromJson(response.body(), JsonObject.class);

            assertTrue(author.has("id"), "Test " + singleAuthor.id + ": Author JSON missing 'id' field.");
            assertEquals(singleAuthor.id, author.get("id").getAsInt(), "Test " + singleAuthor.id + ": Author ID mismatch.");

            assertTrue(author.has("firstName"), "Test " + singleAuthor.id + ": Author JSON missing 'firstName' field.");
            assertEquals(singleAuthor.firstName, author.get("firstName").getAsString(), "Test " + singleAuthor.id + ": Firstname mismatch.");

       //     System.out.println("==== " + singleAuthor.lastName + " " + author.get("lastName").getAsString());
            assertTrue(author.has("lastName"), "Test " + singleAuthor.id + ": Author JSON missing 'lastName' field.");
                       assertEquals(singleAuthor.lastName, author.get("lastName").getAsString(), "Test "+singleAuthor.id+" (POST Authors) failed: lastName mismatch");

            assertTrue(author.has("idBook"), "Test " + singleAuthor.id + ": Author JSON missing 'idBook' field.");
            assertEquals(singleAuthor.idBook, author.get("idBook").getAsInt(), "Test " + singleAuthor.id + ": idBook mismatch.");
            }
             
        } catch (IOException | InterruptedException e) {
            System.err.println("Test POST " + singleAuthor.id + " failed: " + e.getMessage());
            e.printStackTrace();
            fail("Test " + singleAuthor.id + " failed due to exception.");
        }
    }

    private void testSinglePutAuthor(Author singleAuthor) {
        try {
            // Create a new author object (JSON):
            JsonObject newAuthor = new JsonObject();
            newAuthor.addProperty("id", singleAuthor.id);
            newAuthor.addProperty("firstName", singleAuthor.firstName);
            newAuthor.addProperty("lastName", singleAuthor.lastName);
            newAuthor.addProperty("idBook", singleAuthor.idBook);

            String url = BASE_URL + "/" + singleAuthor.id;

            // Check if the author exists first (GET request):
            HttpRequest getRequest = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
            HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());

            if (getResponse.statusCode() == 200) {
                String requestBody = gson.toJson(newAuthor);
                
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Content-Type", "application/json")
                        .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            //    System.out.println(response.body());

                assertEquals(200, response.statusCode(), "Test " + singleAuthor.id + " (PUT Authors) failed: Expected 200, got " + response.statusCode());

                if (response.statusCode() == 200) {
                    // Verify the created author (optional):
                    JsonObject createdAuthor = gson.fromJson(response.body(), JsonObject.class);
                    assertTrue(createdAuthor.has("id"), "Test " + singleAuthor.id + " (PUT Authors) failed: Missing 'id' in response.");
                    assertEquals(newAuthor.get("firstName").getAsString(), createdAuthor.get("firstName").getAsString(), "Test " + singleAuthor.id + " (POST Authors) failed: firstName mismatch");
                    assertEquals(newAuthor.get("lastName").getAsString(), createdAuthor.get("lastName").getAsString(), "Test "+singleAuthor.id+" (POST Authors) failed: lastName mismatch");
                    assertEquals(newAuthor.get("idBook").getAsInt(), createdAuthor.get("idBook").getAsInt(), "Test " + singleAuthor.id + " (POST Authors) failed: idBook mismatch");
                }

                request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
                response = client.send(request, HttpResponse.BodyHandlers.ofString());

                assertEquals(200, response.statusCode(), "Test " + singleAuthor.id + " (Authors/" + singleAuthor.id + ") failed: Expected 200, got " + response.statusCode());

                if (response.statusCode() == 200) {
                    JsonObject author = gson.fromJson(response.body(), JsonObject.class);

                    assertTrue(author.has("id"), "Test " + singleAuthor.id + ": Author JSON missing 'id' field.");
                    assertEquals(singleAuthor.id, author.get("id").getAsInt(), "Test " + singleAuthor.id + ": Author ID mismatch.");

                    assertTrue(author.has("firstName"), "Test " + singleAuthor.id + ": Author JSON missing 'firstName' field.");
                    assertEquals(singleAuthor.firstName, author.get("firstName").getAsString(), "Test " + singleAuthor.id + ": Firstname mismatch.");

                    //     System.out.println("==== " + singleAuthor.lastName + " " + author.get("lastName").getAsString());
                    assertTrue(author.has("lastName"), "Test " + singleAuthor.id + ": Author JSON missing 'lastName' field.");
                        assertEquals(singleAuthor.lastName, author.get("lastName").getAsString(), "Test " + singleAuthor.id + ":  Lastname mismatch.");

                    assertTrue(author.has("idBook"), "Test " + singleAuthor.id + ": Author JSON missing 'idBook' field.");
                    assertEquals(singleAuthor.idBook, author.get("idBook").getAsInt(), "Test " + singleAuthor.id + ": idBook mismatch.");
                }
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("Test PUT " + singleAuthor.id + " failed: " + e.getMessage());
            e.printStackTrace();
            fail("Test PUT" + singleAuthor.id + " failed due to exception.");
        }
    }

    private void validateAuthorJson(String json, int authorId) {
        try {
            System.out.println("==== " + json.toString());
            JsonObject author = gson.fromJson(json, JsonObject.class);
            Author singleAuthor = storedAuthors.get(authorId);

            System.out.println(authorId + " ==== " + json.toString() + " ==== " + singleAuthor.id + singleAuthor.firstName + singleAuthor.lastName + singleAuthor.idBook);
            
            assertTrue(author.has("id"), "Test " + authorId + ": Author JSON missing 'id' field.");
            assertEquals(singleAuthor.id, author.get("id").getAsInt(), "Test " + authorId + ": Author ID mismatch.");

            assertTrue(author.has("firstName"), "Test " + authorId + ": Author JSON missing 'firstName' field.");
            assertEquals(singleAuthor.firstName, author.get("firstName").getAsString(), "Test " + authorId + ": Firstname mismatch.");

            //     System.out.println("==== " + singleAuthor.lastName + " " + author.get("lastName").getAsString());
            assertTrue(author.has("lastName"), "Test " + authorId + ": Author JSON missing 'lastName' field.");
            assertEquals(singleAuthor.lastName, author.get("lastName").getAsString(), "Test " + authorId + ":  Lastname mismatch.");

            System.out.println("==== " + singleAuthor.idBook + " " + author.get("idBook").getAsString());
            assertTrue(author.has("idBook"), "Test " + authorId + ": Author JSON missing 'idBook' field.");
            assertEquals(singleAuthor.idBook, author.get("idBook").getAsInt(), "Test " + authorId + ": idBook mismatch.");

        } catch (com.google.gson.JsonSyntaxException e) {
            System.err.println("Test " + authorId + " JSON parsing failed: " + e.getMessage());
            fail("Test Validate " + authorId + " JSON parsing failed."); //Force junit fail
        }
    }

}


