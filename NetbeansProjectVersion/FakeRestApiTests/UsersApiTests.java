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
import java.util.Random;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UsersApiTests {

    private static final String BASE_URL = "https://fakerestapi.azurewebsites.net/api/v1/Users";
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final Gson gson = new Gson();
    private static Map<Integer, User> initialUsers = new HashMap<>();

    @Order(1)
    @Test
    public void testGetAllUsers() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL)).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Get All Users failed: Expected 200, got " + response.statusCode());

        if (response.statusCode() == 200) {
            JsonArray users = gson.fromJson(response.body(), JsonArray.class);
            System.out.println(users.toString());
            assertTrue(users.size() > 0, "No users returned");
            User singleUser = new User();
            for (JsonElement userElement : users) {
                JsonObject user = userElement.getAsJsonObject();
                assertTrue(user.has("id"));
                singleUser.id = user.get("id").getAsInt();
                //       System.out.println(singleUser.id);
                assertTrue(singleUser.id >= 0);
                assertTrue(user.has("userName"));
                singleUser.userName = user.get("userName").getAsString();
                assertTrue(singleUser.userName != null);
                assertTrue(user.has("password"));
                singleUser.password = user.get("password").getAsString();
                //    System.out.println("+++ " + singleUser.password);
                assertTrue(singleUser.password != null);
                
                System.out.println(singleUser.id);
                initialUsers.put(singleUser.id, singleUser);

            }
        }

    }

    @Order(2)
    @Test
    public void testGetUsers() {
        for (User user : initialUsers.values()) {
            System.out.println(user.id);
            testSingleGetUser(user.id);
        }
    }

    @Order(3)
    @Test
    public void testPostUsers() {
        User singleUser = new User();
        singleUser.id = 10000;
        singleUser.userName = "Test userName";
        singleUser.password = "Test password";
        testSinglePostUser(singleUser);
    }

    @Order(4)
    @Test
    public void testPutUsers() {
        User singleUser = new User();
        singleUser.id = 1;
        singleUser.userName = "Test userName";
        singleUser.password = "Test password";
        testSinglePutUser(singleUser);
    }

    @Order(5)
    @Test
    public void testDeleteUsers() {
        testSingleDeleteUser(1);
    }

    private void testSingleGetUser(int userId) {
        try {
            String url = BASE_URL + "/" + userId;

            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println(response.body());
            assertEquals(200, response.statusCode(), "Test " + userId + " (Users/" + userId + ") failed: Expected 200, got " + response.statusCode());

            if (response.statusCode() == 200) {
                validateUserJson(response.body(), userId);
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("Test " + userId + " failed: " + e.getMessage());
            e.printStackTrace();
            fail("Test GET " + userId + " failed due to exception."); // Force JUnit fail
        }
    }

    private void testSingleDeleteUser(int userId) {
        try {
            String url = BASE_URL + "/" + userId;

            // Check if the user exists first (GET request):
            HttpRequest getRequest = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
            HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());

            if (getResponse.statusCode() == 200) { // If it exists, try to delete it
                HttpRequest deleteRequest = HttpRequest.newBuilder().uri(URI.create(url)).DELETE().build();
                HttpResponse<String> deleteResponse = client.send(deleteRequest, HttpResponse.BodyHandlers.ofString());

                assertEquals(200, deleteResponse.statusCode(), "Test " + userId + " (DELETE Users/" + userId + ") failed: Expected 200, got " + deleteResponse.statusCode());

                // Optionally, check if the user is really gone (another GET request):
                HttpRequest checkGetRequest = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
                HttpResponse<String> checkGetResponse = client.send(checkGetRequest, HttpResponse.BodyHandlers.ofString());
                //    System.out.println(checkGetResponse.body());
                    assertEquals(404, checkGetResponse.statusCode(), "Test " + userId + " (DELETE Users/" + userId + ") failed: User not deleted");

            } else if (getResponse.statusCode() == 404) {
                fail("User does not exist to be deleted");
            } else {
                fail("Test " + userId + " (GET Users/" + userId + ") failed: Unexpected get response code: " + getResponse.statusCode());
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("Test " + userId + " failed: " + e.getMessage());
            e.printStackTrace();
            fail("Test DELETE " + userId + " failed due to exception.");
        }
    }

    private void testSinglePostUser(User singleUser) {
        try {
            // Create a new user object (JSON):
            JsonObject newUser = new JsonObject();
            newUser.addProperty("id", singleUser.id);
            newUser.addProperty("userName", singleUser.userName);
            newUser.addProperty("password", singleUser.password);

            String requestBody = gson.toJson(newUser);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        //    System.out.println(response.body());

            assertEquals(200, response.statusCode(), "Test " + singleUser.id + " (POST Users) failed: Expected 200, got " + response.statusCode());

            if (response.statusCode() == 200) {
                // Verify the created user (optional):
                JsonObject createdUser = gson.fromJson(response.body(), JsonObject.class);
                assertTrue(createdUser.has("id"), "Test " + singleUser.id + " (POST Users) failed: Missing 'id' in response.");
                assertEquals(singleUser.userName, createdUser.get("userName").getAsString(), "Test " + singleUser.id + " (POST Users) failed: userName mismatch");
                assertEquals(singleUser.password, createdUser.get("password").getAsString(), "Test "+singleUser.id+" (POST Users) failed: password mismatch");

            }

                    String url = BASE_URL + "/" + singleUser.id;

            request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println(response.body());
            
            assertEquals(200, response.statusCode(), "Test " + singleUser.id + " (Users/" + singleUser.id + ") failed: Expected 200, got " + response.statusCode());

            if (response.statusCode() == 200) {
                JsonObject user = gson.fromJson(response.body(), JsonObject.class);

            assertTrue(user.has("id"), "Test " + singleUser.id + ": User JSON missing 'id' field.");
            assertEquals(singleUser.id, user.get("id").getAsInt(), "Test " + singleUser.id + ": User ID mismatch.");

            assertTrue(user.has("userName"), "Test " + singleUser.id + ": User JSON missing 'userName' field.");
            assertEquals(singleUser.userName, user.get("userName").getAsString(), "Test " + singleUser.id + ": Firstname mismatch.");

       //     System.out.println("==== " + singleUser.password + " " + user.get("password").getAsString());
            assertTrue(user.has("password"), "Test " + singleUser.id + ": User JSON missing 'password' field.");
                       assertEquals(singleUser.password, user.get("password").getAsString(), "Test "+singleUser.id+" (POST Users) failed: password mismatch");

          
            }
             
        } catch (IOException | InterruptedException e) {
            System.err.println("Test POST " + singleUser.id + " failed: " + e.getMessage());
            e.printStackTrace();
            fail("Test " + singleUser.id + " failed due to exception.");
        }
    }

    private void testSinglePutUser(User singleUser) {
        try {
            // Create a new user object (JSON):
            JsonObject newUser = new JsonObject();
            newUser.addProperty("id", singleUser.id);
            newUser.addProperty("userName", singleUser.userName);
            newUser.addProperty("password", singleUser.password);

            String url = BASE_URL + "/" + singleUser.id;

            // Check if the user exists first (GET request):
            HttpRequest getRequest = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
            HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());

            if (getResponse.statusCode() == 200) {
                String requestBody = gson.toJson(newUser);
                
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Content-Type", "application/json")
                        .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            //    System.out.println(response.body());

                assertEquals(200, response.statusCode(), "Test " + singleUser.id + " (PUT Users) failed: Expected 200, got " + response.statusCode());

                if (response.statusCode() == 200) {
                    // Verify the created user (optional):
                    JsonObject createdUser = gson.fromJson(response.body(), JsonObject.class);
                    assertTrue(createdUser.has("id"), "Test " + singleUser.id + " (PUT Users) failed: Missing 'id' in response.");
                    assertEquals(newUser.get("userName").getAsString(), createdUser.get("userName").getAsString(), "Test " + singleUser.id + " (POST Users) failed: userName mismatch");
                    assertEquals(newUser.get("password").getAsString(), createdUser.get("password").getAsString(), "Test "+singleUser.id+" (POST Users) failed: password mismatch");
                }

                request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
                response = client.send(request, HttpResponse.BodyHandlers.ofString());

                assertEquals(200, response.statusCode(), "Test " + singleUser.id + " (Users/" + singleUser.id + ") failed: Expected 200, got " + response.statusCode());

                if (response.statusCode() == 200) {
                    JsonObject user = gson.fromJson(response.body(), JsonObject.class);

                    assertTrue(user.has("id"), "Test " + singleUser.id + ": User JSON missing 'id' field.");
                    assertEquals(singleUser.id, user.get("id").getAsInt(), "Test " + singleUser.id + ": User ID mismatch.");

                    assertTrue(user.has("userName"), "Test " + singleUser.id + ": User JSON missing 'userName' field.");
                    assertEquals(singleUser.userName, user.get("userName").getAsString(), "Test " + singleUser.id + ": Firstname mismatch.");

                    //     System.out.println("==== " + singleUser.password + " " + user.get("password").getAsString());
                    assertTrue(user.has("password"), "Test " + singleUser.id + ": User JSON missing 'password' field.");
                        assertEquals(singleUser.password, user.get("password").getAsString(), "Test " + singleUser.id + ":  Lastname mismatch.");

                
                }
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("Test PUT " + singleUser.id + " failed: " + e.getMessage());
            e.printStackTrace();
            fail("Test PUT" + singleUser.id + " failed due to exception.");
        }
    }

    private void validateUserJson(String json, int userId) {
        try {
            System.out.println("==== " + json.toString());
            JsonObject user = gson.fromJson(json, JsonObject.class);
            User singleUser = initialUsers.get(userId);

            System.out.println(userId + " ==== " + json.toString() + " ==== " + singleUser.id + singleUser.userName + singleUser.password );
            
            assertTrue(user.has("id"), "Test " + userId + ": User JSON missing 'id' field.");
            assertEquals(singleUser.id, user.get("id").getAsInt(), "Test " + userId + ": User ID mismatch.");

            assertTrue(user.has("userName"), "Test " + userId + ": User JSON missing 'userName' field.");
            assertEquals(singleUser.userName, user.get("userName").getAsString(), "Test " + userId + ": Firstname mismatch.");

            //     System.out.println("==== " + singleUser.password + " " + user.get("password").getAsString());
            assertTrue(user.has("password"), "Test " + userId + ": User JSON missing 'password' field.");
            assertEquals(singleUser.password, user.get("password").getAsString(), "Test " + userId + ":  Lastname mismatch.");

          

        } catch (com.google.gson.JsonSyntaxException e) {
            System.err.println("Test " + userId + " JSON parsing failed: " + e.getMessage());
            fail("Test Validate " + userId + " JSON parsing failed."); //Force junit fail
        }
    }

}

class User {

    int id;
    String userName;
    String password;
}
