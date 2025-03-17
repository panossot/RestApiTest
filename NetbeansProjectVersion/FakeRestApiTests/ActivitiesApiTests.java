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
public class ActivitiesApiTests {

    private static final String BASE_URL = "https://fakerestapi.azurewebsites.net/api/v1/Activities";
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final Gson gson = new Gson();
    private static Map<Integer, Activity> initialActivities = new HashMap<>();

    @Order(1)
    @Test
    public void testGetAllActivities() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL)).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Get All Activities failed: Expected 200, got " + response.statusCode());

        if (response.statusCode() == 200) {
            JsonArray activities = gson.fromJson(response.body(), JsonArray.class);
            assertTrue(activities.size() > 0, "No activities returned");
            Activity singleActivity = new Activity();
            for (JsonElement activityElement : activities) {
                JsonObject activity = activityElement.getAsJsonObject();
                assertTrue(activity.has("id"));
                singleActivity.id = activity.get("id").getAsInt();
                //       System.out.println(singleActivity.id);
                assertTrue(singleActivity.id >= 0);
                assertTrue(activity.has("title"));
                singleActivity.title = activity.get("title").getAsString();
                assertTrue(singleActivity.title != null);
                assertTrue(activity.has("dueDate"));
                singleActivity.dueDate = activity.get("dueDate").getAsString();
                //    System.out.println("+++ " + singleActivity.dueDate);
                assertTrue(singleActivity.dueDate != null);
                assertTrue(isOffsetDateTimeFormat(activity.get("dueDate").getAsString()));
                assertTrue(activity.has("completed"));
                singleActivity.completed = activity.get("completed").getAsBoolean();
                assertTrue(activity.get("completed").getAsString() != null);

                int comparison1 = compareWithCurrentTime(activity.get("dueDate").getAsString());
                if (comparison1 <= 0) {
                    assertTrue(activity.get("completed").getAsBoolean());
                }

                initialActivities.put(singleActivity.id, singleActivity);

            }
        }

    }

    @Order(2)
    @Test
    public void testGetActivities() {
        for (Activity activity : initialActivities.values()) {
            testSingleGetActivity(activity.id);
        }
    }

    @Order(3)
    @Test
    public void testPostActivities() {
        Activity singleActivity = new Activity();
        singleActivity.id = 10000;
        singleActivity.title = "Test title";
        singleActivity.dueDate = OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        singleActivity.completed = true;
        testSinglePostActivity(singleActivity);
    }

    @Order(4)
    @Test
    public void testPutActivities() {
        Activity singleActivity = new Activity();
        singleActivity.id = 1;
        singleActivity.title = "Test title";
        singleActivity.dueDate = OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        singleActivity.completed = false;
        testSinglePutActivity(singleActivity);
    }

    @Order(5)
    @Test
    public void testDeleteActivities() {
        testSingleDeleteActivity(1);
    }

    private void testSingleGetActivity(int activityId) {
        try {
            String url = BASE_URL + "/" + activityId;

            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(200, response.statusCode(), "Test " + activityId + " (Activities/" + activityId + ") failed: Expected 200, got " + response.statusCode());

            if (response.statusCode() == 200) {
                validateActivityJson(response.body(), activityId);
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("Test " + activityId + " failed: " + e.getMessage());
            e.printStackTrace();
            fail("Test GET " + activityId + " failed due to exception."); // Force JUnit fail
        }
    }

    private void testSingleDeleteActivity(int activityId) {
        try {
            String url = BASE_URL + "/" + activityId;

            // Check if the activity exists first (GET request):
            HttpRequest getRequest = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
            HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());

            if (getResponse.statusCode() == 200) { // If it exists, try to delete it
                HttpRequest deleteRequest = HttpRequest.newBuilder().uri(URI.create(url)).DELETE().build();
                HttpResponse<String> deleteResponse = client.send(deleteRequest, HttpResponse.BodyHandlers.ofString());

                assertEquals(200, deleteResponse.statusCode(), "Test " + activityId + " (DELETE Activities/" + activityId + ") failed: Expected 200, got " + deleteResponse.statusCode());

                // Optionally, check if the activity is really gone (another GET request):
                HttpRequest checkGetRequest = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
                HttpResponse<String> checkGetResponse = client.send(checkGetRequest, HttpResponse.BodyHandlers.ofString());
                //    System.out.println(checkGetResponse.body());
                //    assertEquals(404, checkGetResponse.statusCode(), "Test " + activityId + " (DELETE Activities/" + activityId + ") failed: Activity not deleted");

            } else if (getResponse.statusCode() == 404) {
                fail("Activity does not exist to be deleted");
            } else {
                fail("Test " + activityId + " (GET Activities/" + activityId + ") failed: Unexpected get response code: " + getResponse.statusCode());
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("Test " + activityId + " failed: " + e.getMessage());
            e.printStackTrace();
            fail("Test DELETE " + activityId + " failed due to exception.");
        }
    }

    private void testSinglePostActivity(Activity singleActivity) {
        try {
            // Create a new activity object (JSON):
            JsonObject newActivity = new JsonObject();
            newActivity.addProperty("id", singleActivity.id);
            newActivity.addProperty("title", singleActivity.title);
            newActivity.addProperty("dueDate", singleActivity.dueDate);
            newActivity.addProperty("completed", singleActivity.completed);

            String requestBody = gson.toJson(newActivity);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        //    System.out.println(response.body());

            assertEquals(200, response.statusCode(), "Test " + singleActivity.id + " (POST Activities) failed: Expected 200, got " + response.statusCode());

            if (response.statusCode() == 200) {
                // Verify the created activity (optional):
                JsonObject createdActivity = gson.fromJson(response.body(), JsonObject.class);
                assertTrue(createdActivity.has("id"), "Test " + singleActivity.id + " (POST Activities) failed: Missing 'id' in response.");
                assertEquals(newActivity.get("title").getAsString(), createdActivity.get("title").getAsString(), "Test " + singleActivity.id + " (POST Activities) failed: title mismatch");
                //           assertEquals(replaceBetweenChars(newActivity.get("dueDate").getAsString(),'.','+',""), replaceBetweenChars(createdActivity.get("dueDate").getAsString(),'.','+',""), "Test "+singleActivity.id+" (POST Activities) failed: dueDate mismatch");

                assertEquals(newActivity.get("completed").getAsBoolean(), createdActivity.get("completed").getAsBoolean(), "Test " + singleActivity.id + " (POST Activities) failed: completed mismatch");
            }

            /*        String url = BASE_URL + "/" + singleActivity.id;

            request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(200, response.statusCode(), "Test " + singleActivity.id + " (Activities/" + singleActivity.id + ") failed: Expected 200, got " + response.statusCode());

            if (response.statusCode() == 200) {
                JsonObject activity = gson.fromJson(response.body(), JsonObject.class);

            assertTrue(activity.has("id"), "Test " + singleActivity.id + ": Activity JSON missing 'id' field.");
            assertEquals(singleActivity.id, activity.get("id").getAsInt(), "Test " + singleActivity.id + ": Activity ID mismatch.");

            assertTrue(activity.has("title"), "Test " + singleActivity.id + ": Activity JSON missing 'title' field.");
            assertEquals(singleActivity.title, activity.get("title").getAsString(), "Test " + singleActivity.id + ": Title mismatch.");

       //     System.out.println("==== " + singleActivity.dueDate + " " + activity.get("dueDate").getAsString());
            assertTrue(activity.has("dueDate"), "Test " + singleActivity.id + ": Activity JSON missing 'dueDate' field.");
        //    assertEquals(replaceBetweenChars(singleActivity.dueDate,'.','+',""), replaceBetweenChars(activity.get("dueDate").getAsString(),'.','+',""), "Test " + activityId + ":  Due Date mismatch.");

            assertTrue(activity.has("completed"), "Test " + singleActivity.id + ": Activity JSON missing 'completed' field.");
            assertEquals(singleActivity.completed, activity.get("completed").getAsBoolean(), "Test " + singleActivity.id + ": Completed mismatch.");
            }
             */
        } catch (IOException | InterruptedException e) {
            System.err.println("Test POST " + singleActivity.id + " failed: " + e.getMessage());
            e.printStackTrace();
            fail("Test " + singleActivity.id + " failed due to exception.");
        }
    }

    private void testSinglePutActivity(Activity singleActivity) {
        try {
            // Create a new activity object (JSON):
            JsonObject newActivity = new JsonObject();
            newActivity.addProperty("id", singleActivity.id);
            newActivity.addProperty("title", singleActivity.title);
            newActivity.addProperty("dueDate", singleActivity.dueDate);
            newActivity.addProperty("completed", singleActivity.completed);

            String url = BASE_URL + "/" + singleActivity.id;

            // Check if the activity exists first (GET request):
            HttpRequest getRequest = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
            HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());

            if (getResponse.statusCode() == 200) {
                String requestBody = gson.toJson(newActivity);
                
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Content-Type", "application/json")
                        .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            //    System.out.println(response.body());

                assertEquals(200, response.statusCode(), "Test " + singleActivity.id + " (PUT Activities) failed: Expected 200, got " + response.statusCode());

                if (response.statusCode() == 200) {
                    // Verify the created activity (optional):
                    JsonObject createdActivity = gson.fromJson(response.body(), JsonObject.class);
                    assertTrue(createdActivity.has("id"), "Test " + singleActivity.id + " (PUT Activities) failed: Missing 'id' in response.");
                 //   assertEquals(newActivity.get("title").getAsString(), createdActivity.get("title").getAsString(), "Test " + singleActivity.id + " (POST Activities) failed: title mismatch");
                    //    assertEquals(replaceBetweenChars(newActivity.get("dueDate").getAsString(),'.','+',""), replaceBetweenChars(createdActivity.get("dueDate").getAsString(),'.','+',""), "Test "+singleActivity.id+" (POST Activities) failed: dueDate mismatch");
                    assertEquals(newActivity.get("completed").getAsBoolean(), createdActivity.get("completed").getAsBoolean(), "Test " + singleActivity.id + " (POST Activities) failed: completed mismatch");
                }

                request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
                response = client.send(request, HttpResponse.BodyHandlers.ofString());

                assertEquals(200, response.statusCode(), "Test " + singleActivity.id + " (Activities/" + singleActivity.id + ") failed: Expected 200, got " + response.statusCode());

                if (response.statusCode() == 200) {
                    JsonObject activity = gson.fromJson(response.body(), JsonObject.class);

                    assertTrue(activity.has("id"), "Test " + singleActivity.id + ": Activity JSON missing 'id' field.");
                    assertEquals(singleActivity.id, activity.get("id").getAsInt(), "Test " + singleActivity.id + ": Activity ID mismatch.");

                    assertTrue(activity.has("title"), "Test " + singleActivity.id + ": Activity JSON missing 'title' field.");
                //    assertEquals(singleActivity.title, activity.get("title").getAsString(), "Test " + singleActivity.id + ": Title mismatch.");

                    //     System.out.println("==== " + singleActivity.dueDate + " " + activity.get("dueDate").getAsString());
                    assertTrue(activity.has("dueDate"), "Test " + singleActivity.id + ": Activity JSON missing 'dueDate' field.");
                    //    assertEquals(replaceBetweenChars(singleActivity.dueDate,'.','+',""), replaceBetweenChars(activity.get("dueDate").getAsString(),'.','+',""), "Test " + activityId + ":  Due Date mismatch.");

                    assertTrue(activity.has("completed"), "Test " + singleActivity.id + ": Activity JSON missing 'completed' field.");
                    assertEquals(singleActivity.completed, activity.get("completed").getAsBoolean(), "Test " + singleActivity.id + ": Completed mismatch.");
                }
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("Test PUT " + singleActivity.id + " failed: " + e.getMessage());
            e.printStackTrace();
            fail("Test PUT" + singleActivity.id + " failed due to exception.");
        }
    }

    private void validateActivityJson(String json, int activityId) {
        try {
            JsonObject activity = gson.fromJson(json, JsonObject.class);
            Activity singleActivity = initialActivities.get(activityId);

            assertTrue(activity.has("id"), "Test " + activityId + ": Activity JSON missing 'id' field.");
            assertEquals(singleActivity.id, activity.get("id").getAsInt(), "Test " + activityId + ": Activity ID mismatch.");

            assertTrue(activity.has("title"), "Test " + activityId + ": Activity JSON missing 'title' field.");
            assertEquals(singleActivity.title, activity.get("title").getAsString(), "Test " + activityId + ": Title mismatch.");

            //     System.out.println("==== " + singleActivity.dueDate + " " + activity.get("dueDate").getAsString());
            assertTrue(activity.has("dueDate"), "Test " + activityId + ": Activity JSON missing 'dueDate' field.");
            //    assertEquals(replaceBetweenChars(singleActivity.dueDate,'.','+',""), replaceBetweenChars(activity.get("dueDate").getAsString(),'.','+',""), "Test " + activityId + ":  Due Date mismatch.");

            assertTrue(activity.has("completed"), "Test " + activityId + ": Activity JSON missing 'completed' field.");
            assertEquals(singleActivity.completed, activity.get("completed").getAsBoolean(), "Test " + activityId + ": Completed mismatch.");

        } catch (com.google.gson.JsonSyntaxException e) {
            System.err.println("Test " + activityId + " JSON parsing failed: " + e.getMessage());
            fail("Test Validate " + activityId + " JSON parsing failed."); //Force junit fail
        }
    }

    private String replaceBetweenChars(String str, char startChar, char endChar, String replacement) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        int startIndex = str.indexOf(startChar);
        int endIndex = str.indexOf(endChar, startIndex + 1); // Look for endChar after startChar

        if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
            return str.substring(0, startIndex) + replacement + str.substring(endIndex);
        } else {
            return str; // No replacement if start or end char not found, or in wrong order.
        }
    }

    private boolean isOffsetDateTimeFormat(String dateTimeString) {
        if (dateTimeString == null || dateTimeString.isEmpty()) {
            return false;
        }

        try {
            OffsetDateTime.parse(dateTimeString, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private int compareWithCurrentTime(String dateTimeString) {
        if (!isOffsetDateTimeFormat(dateTimeString)) {
            return -2; // Indicates invalid format
        }

        OffsetDateTime parsedDateTime = OffsetDateTime.parse(dateTimeString, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        OffsetDateTime currentDateTime = OffsetDateTime.now();

        return parsedDateTime.compareTo(currentDateTime);
    }
}

class Activity {

    int id;
    String title;
    String dueDate;
    boolean completed;
}
