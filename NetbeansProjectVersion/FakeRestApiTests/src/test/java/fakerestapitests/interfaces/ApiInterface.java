package fakerestapitests.interfaces;

import java.io.IOException;

public interface ApiInterface {

    /**
     * Tests the retrieval of all resources from the API.
     *
     * @throws IOException          If an I/O error occurs during the API call.
     * @throws InterruptedException If the thread is interrupted during the API call.
     */
    public void testGet() throws IOException, InterruptedException;


    /**
     * Tests the creation of a new resource via a POST request to the API.
     */
    public void testPost();

    /**
     * Tests the update of an existing resource via a PUT request to the API.
     */
    public void testPut();

    /**
     * Tests the deletion of a resource via a DELETE request to the API.
     */
    public void testDelete();
}
