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
public class CoverPhotosApiTests {

    private static final String BASE_URL = "https://fakerestapi.azurewebsites.net/api/v1/CoverPhotos";
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final Gson gson = new Gson();
    private static Map<Integer, CoverPhoto> initialCoverPhotos = new HashMap<>();

    @Order(1)
    @Test
    public void testGetAllCoverPhotos() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL)).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Get All CoverPhotos failed: Expected 200, got " + response.statusCode());

        if (response.statusCode() == 200) {
            JsonArray coverPhotos = gson.fromJson(response.body(), JsonArray.class);
            System.out.println(coverPhotos.toString());
            assertTrue(coverPhotos.size() > 0, "No coverPhotos returned");
            CoverPhoto singleCoverPhoto = new CoverPhoto();
            for (JsonElement coverPhotoElement : coverPhotos) {
                JsonObject coverPhoto = coverPhotoElement.getAsJsonObject();
                assertTrue(coverPhoto.has("id"));
                singleCoverPhoto.id = coverPhoto.get("id").getAsInt();
                //       System.out.println(singleCoverPhoto.id);
                assertTrue(singleCoverPhoto.id >= 0);
                assertTrue(coverPhoto.has("url"));
                singleCoverPhoto.url = coverPhoto.get("url").getAsString();
                assertTrue(singleCoverPhoto.url != null);
               
                assertTrue(coverPhoto.has("idBook"));
                singleCoverPhoto.idBook = coverPhoto.get("idBook").getAsInt();
                assertTrue(coverPhoto.get("idBook").getAsString() != null);

                System.out.println(singleCoverPhoto.id);
                initialCoverPhotos.put(singleCoverPhoto.id, singleCoverPhoto);

            }
        }

    }

    @Order(2)
    @Test
    public void testGetCoverPhotos() {
        for (CoverPhoto coverPhoto : initialCoverPhotos.values()) {
            System.out.println(coverPhoto.id);
            testSingleGetCoverPhoto(coverPhoto.id);
        }
    }

    @Order(3)
    @Test
    public void testPostCoverPhotos() {
        CoverPhoto singleCoverPhoto = new CoverPhoto();
        singleCoverPhoto.id = 10000;
        singleCoverPhoto.url = "Test url";
        singleCoverPhoto.idBook = 1000;
        testSinglePostCoverPhoto(singleCoverPhoto);
    }

    @Order(4)
    @Test
    public void testPutCoverPhotos() {
        CoverPhoto singleCoverPhoto = new CoverPhoto();
        singleCoverPhoto.id = 1;
        singleCoverPhoto.url = "Test url";
        singleCoverPhoto.idBook = 1000;
        testSinglePutCoverPhoto(singleCoverPhoto);
    }

    @Order(5)
    @Test
    public void testDeleteCoverPhotos() {
        testSingleDeleteCoverPhoto(1);
    }

    private void testSingleGetCoverPhoto(int coverPhotoId) {
        try {
            String url = BASE_URL + "/" + coverPhotoId;

            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println(response.body());
            assertEquals(200, response.statusCode(), "Test " + coverPhotoId + " (CoverPhotos/" + coverPhotoId + ") failed: Expected 200, got " + response.statusCode());

            if (response.statusCode() == 200) {
                validateCoverPhotoJson(response.body(), coverPhotoId);
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("Test " + coverPhotoId + " failed: " + e.getMessage());
            e.printStackTrace();
            fail("Test GET " + coverPhotoId + " failed due to exception."); // Force JUnit fail
        }
    }

    private void testSingleDeleteCoverPhoto(int coverPhotoId) {
        try {
            String url = BASE_URL + "/" + coverPhotoId;

            // Check if the coverPhoto exists first (GET request):
            HttpRequest getRequest = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
            HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());

            if (getResponse.statusCode() == 200) { // If it exists, try to delete it
                HttpRequest deleteRequest = HttpRequest.newBuilder().uri(URI.create(url)).DELETE().build();
                HttpResponse<String> deleteResponse = client.send(deleteRequest, HttpResponse.BodyHandlers.ofString());

                assertEquals(200, deleteResponse.statusCode(), "Test " + coverPhotoId + " (DELETE CoverPhotos/" + coverPhotoId + ") failed: Expected 200, got " + deleteResponse.statusCode());

                // Optionally, check if the coverPhoto is really gone (another GET request):
                HttpRequest checkGetRequest = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
                HttpResponse<String> checkGetResponse = client.send(checkGetRequest, HttpResponse.BodyHandlers.ofString());
                //    System.out.println(checkGetResponse.body());
                    assertEquals(404, checkGetResponse.statusCode(), "Test " + coverPhotoId + " (DELETE CoverPhotos/" + coverPhotoId + ") failed: CoverPhoto not deleted");

            } else if (getResponse.statusCode() == 404) {
                fail("CoverPhoto does not exist to be deleted");
            } else {
                fail("Test " + coverPhotoId + " (GET CoverPhotos/" + coverPhotoId + ") failed: Unexpected get response code: " + getResponse.statusCode());
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("Test " + coverPhotoId + " failed: " + e.getMessage());
            e.printStackTrace();
            fail("Test DELETE " + coverPhotoId + " failed due to exception.");
        }
    }

    private void testSinglePostCoverPhoto(CoverPhoto singleCoverPhoto) {
        try {
            // Create a new coverPhoto object (JSON):
            JsonObject newCoverPhoto = new JsonObject();
            newCoverPhoto.addProperty("id", singleCoverPhoto.id);
            newCoverPhoto.addProperty("url", singleCoverPhoto.url);
            newCoverPhoto.addProperty("idBook", singleCoverPhoto.idBook);

            String requestBody = gson.toJson(newCoverPhoto);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        //    System.out.println(response.body());

            assertEquals(200, response.statusCode(), "Test " + singleCoverPhoto.id + " (POST CoverPhotos) failed: Expected 200, got " + response.statusCode());

            if (response.statusCode() == 200) {
                // Verify the created coverPhoto (optional):
                JsonObject createdCoverPhoto = gson.fromJson(response.body(), JsonObject.class);
                assertTrue(createdCoverPhoto.has("id"), "Test " + singleCoverPhoto.id + " (POST CoverPhotos) failed: Missing 'id' in response.");
                assertEquals(singleCoverPhoto.url, createdCoverPhoto.get("url").getAsString(), "Test " + singleCoverPhoto.id + " (POST CoverPhotos) failed: url mismatch");

                assertEquals(singleCoverPhoto.idBook, createdCoverPhoto.get("idBook").getAsInt(), "Test " + singleCoverPhoto.id + " (POST CoverPhotos) failed: idBook mismatch");
            }

                    String url = BASE_URL + "/" + singleCoverPhoto.id;

            request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println(response.body());
            assertEquals(200, response.statusCode(), "Test " + singleCoverPhoto.id + " (CoverPhotos/" + singleCoverPhoto.id + ") failed: Expected 200, got " + response.statusCode());

            if (response.statusCode() == 200) {
                JsonObject coverPhoto = gson.fromJson(response.body(), JsonObject.class);

            assertTrue(coverPhoto.has("id"), "Test " + singleCoverPhoto.id + ": CoverPhoto JSON missing 'id' field.");
            assertEquals(singleCoverPhoto.id, coverPhoto.get("id").getAsInt(), "Test " + singleCoverPhoto.id + ": CoverPhoto ID mismatch.");

            assertTrue(coverPhoto.has("url"), "Test " + singleCoverPhoto.id + ": CoverPhoto JSON missing 'url' field.");
            assertEquals(singleCoverPhoto.url, coverPhoto.get("url").getAsString(), "Test " + singleCoverPhoto.id + ": Firstname mismatch.");

      
            assertTrue(coverPhoto.has("idBook"), "Test " + singleCoverPhoto.id + ": CoverPhoto JSON missing 'idBook' field.");
            assertEquals(singleCoverPhoto.idBook, coverPhoto.get("idBook").getAsInt(), "Test " + singleCoverPhoto.id + ": idBook mismatch.");
            }
             
        } catch (IOException | InterruptedException e) {
            System.err.println("Test POST " + singleCoverPhoto.id + " failed: " + e.getMessage());
            e.printStackTrace();
            fail("Test " + singleCoverPhoto.id + " failed due to exception.");
        }
    }

    private void testSinglePutCoverPhoto(CoverPhoto singleCoverPhoto) {
        try {
            // Create a new coverPhoto object (JSON):
            JsonObject newCoverPhoto = new JsonObject();
            newCoverPhoto.addProperty("id", singleCoverPhoto.id);
            newCoverPhoto.addProperty("url", singleCoverPhoto.url);
            newCoverPhoto.addProperty("idBook", singleCoverPhoto.idBook);

            String url = BASE_URL + "/" + singleCoverPhoto.id;

            // Check if the coverPhoto exists first (GET request):
            HttpRequest getRequest = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
            HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());

            if (getResponse.statusCode() == 200) {
                String requestBody = gson.toJson(newCoverPhoto);
                
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Content-Type", "application/json")
                        .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            //    System.out.println(response.body());

                assertEquals(200, response.statusCode(), "Test " + singleCoverPhoto.id + " (PUT CoverPhotos) failed: Expected 200, got " + response.statusCode());

                if (response.statusCode() == 200) {
                    // Verify the created coverPhoto (optional):
                    JsonObject createdCoverPhoto = gson.fromJson(response.body(), JsonObject.class);
                    assertTrue(createdCoverPhoto.has("id"), "Test " + singleCoverPhoto.id + " (PUT CoverPhotos) failed: Missing 'id' in response.");
                    assertEquals(newCoverPhoto.get("url").getAsString(), createdCoverPhoto.get("url").getAsString(), "Test " + singleCoverPhoto.id + " (POST CoverPhotos) failed: url mismatch");
                    assertEquals(newCoverPhoto.get("idBook").getAsInt(), createdCoverPhoto.get("idBook").getAsInt(), "Test " + singleCoverPhoto.id + " (POST CoverPhotos) failed: idBook mismatch");
                }

                request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
                response = client.send(request, HttpResponse.BodyHandlers.ofString());

                assertEquals(200, response.statusCode(), "Test " + singleCoverPhoto.id + " (CoverPhotos/" + singleCoverPhoto.id + ") failed: Expected 200, got " + response.statusCode());

                if (response.statusCode() == 200) {
                    JsonObject coverPhoto = gson.fromJson(response.body(), JsonObject.class);

                    assertTrue(coverPhoto.has("id"), "Test " + singleCoverPhoto.id + ": CoverPhoto JSON missing 'id' field.");
                    assertEquals(singleCoverPhoto.id, coverPhoto.get("id").getAsInt(), "Test " + singleCoverPhoto.id + ": CoverPhoto ID mismatch.");

                    assertTrue(coverPhoto.has("url"), "Test " + singleCoverPhoto.id + ": CoverPhoto JSON missing 'url' field.");
                    assertEquals(singleCoverPhoto.url, coverPhoto.get("url").getAsString(), "Test " + singleCoverPhoto.id + ": Firstname mismatch.");

                    assertTrue(coverPhoto.has("idBook"), "Test " + singleCoverPhoto.id + ": CoverPhoto JSON missing 'idBook' field.");
                    assertEquals(singleCoverPhoto.idBook, coverPhoto.get("idBook").getAsInt(), "Test " + singleCoverPhoto.id + ": idBook mismatch.");
                }
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("Test PUT " + singleCoverPhoto.id + " failed: " + e.getMessage());
            e.printStackTrace();
            fail("Test PUT" + singleCoverPhoto.id + " failed due to exception.");
        }
    }

    private void validateCoverPhotoJson(String json, int coverPhotoId) {
        try {
            System.out.println("==== " + json.toString());
            JsonObject coverPhoto = gson.fromJson(json, JsonObject.class);
            CoverPhoto singleCoverPhoto = initialCoverPhotos.get(coverPhotoId);

            System.out.println(coverPhotoId + " ==== " + json.toString() + " ==== " + singleCoverPhoto.id + singleCoverPhoto.url + singleCoverPhoto.idBook);
            
            assertTrue(coverPhoto.has("id"), "Test " + coverPhotoId + ": CoverPhoto JSON missing 'id' field.");
            assertEquals(singleCoverPhoto.id, coverPhoto.get("id").getAsInt(), "Test " + coverPhotoId + ": CoverPhoto ID mismatch.");

            assertTrue(coverPhoto.has("url"), "Test " + coverPhotoId + ": CoverPhoto JSON missing 'url' field.");
            assertEquals(singleCoverPhoto.url, coverPhoto.get("url").getAsString(), "Test " + coverPhotoId + ": Firstname mismatch.");

          
            System.out.println("==== " + singleCoverPhoto.idBook + " " + coverPhoto.get("idBook").getAsString());
            assertTrue(coverPhoto.has("idBook"), "Test " + coverPhotoId + ": CoverPhoto JSON missing 'idBook' field.");
            assertEquals(singleCoverPhoto.idBook, coverPhoto.get("idBook").getAsInt(), "Test " + coverPhotoId + ": idBook mismatch.");

        } catch (com.google.gson.JsonSyntaxException e) {
            System.err.println("Test " + coverPhotoId + " JSON parsing failed: " + e.getMessage());
            fail("Test Validate " + coverPhotoId + " JSON parsing failed."); //Force junit fail
        }
    }

}

class CoverPhoto {

    int id;
    String url;
    int idBook;
}
