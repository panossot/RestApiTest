package org.test.testrestapi.interfaces;

import java.io.IOException;
import org.jboss.eap.additional.testsuite.annotations.EAT;

@EAT({"modules/testcases/v1/performanceApiTestsuite/src/main/java"})
public interface ApiPerformanceInterface {

    /**
     * Tests the retrieval of all resources from the API.
     *
     * @throws IOException          If an I/O error occurs during the API call.
     * @throws InterruptedException If the thread is interrupted during the API call.
     */
    public void testGet() throws IOException, InterruptedException;

}
