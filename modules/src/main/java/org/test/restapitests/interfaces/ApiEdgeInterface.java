package org.test.testrestapi.interfaces;

import org.jboss.eap.additional.testsuite.annotations.EAT;

@EAT({"modules/testcases/v1/restApiEdgeTestsuite/src/main/java"})
public interface ApiEdgeInterface {

    /**
     * Tests the POST API endpoint with negative input values.
     * This method should verify how the API handles negative values
     * in the request body, such as negative IDs or other numerical fields.
     * It's designed to check for appropriate error handling.
     */
    public void testPostNegative();

    /**
     * Tests the PUT API endpoint with negative input values.
     * This method should verify how the API handles negative values
     * when updating existing resources via the PUT method.
     * It's designed to check for appropriate error handling during updates.
     */
    public void testPutNegative();

    /**
     * Tests the POST API endpoint with large integer values.
     * This method should verify how the API handles the maximum
     * or very large integer values in the request body.
     * It's designed to check for data type handling and potential overflow issues.
     */
    public void testPostBigInt();

    /**
     * Tests the PUT API endpoint with large integer values.
     * This method should verify how the API handles the maximum
     * or very large integer values when updating existing resources.
     * It's designed to check for data type handling and potential overflow issues during updates.
     */
    public void testPutBigInt();

    /**
     * Tests the POST API endpoint with very large string values.
     * This method should verify how the API handles requests with
     * extremely long string inputs, checking for buffer overflows or
     * other string handling issues.
     */
    public void testPostBigString();

    /**
     * Tests the PUT API endpoint with very large string values.
     * This method should verify how the API handles updates with
     * extremely long string inputs, checking for buffer overflows or
     * other string handling issues during updates.
     */
    public void testPutBigString();
}
