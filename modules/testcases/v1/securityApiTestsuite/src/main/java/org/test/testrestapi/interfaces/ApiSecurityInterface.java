package org.test.testrestapi.interfaces;

import java.io.IOException;
import org.jboss.eap.additional.testsuite.annotations.EAT;

@EAT({"modules/testcases/v1/securityApiTestsuite/src/main/java"})
public interface ApiSecurityInterface {

    /**
     * Tests for SQL injection vulnerabilities in the Book ID parameter.
     * This method attempts to inject SQL code into the Book ID field
     * and verifies if the API is vulnerable to SQL injection attacks.
     *
     * @throws IOException If an I/O error occurs during the test.
     * @throws InterruptedException If the operation is interrupted.
     */
    void testSqlInjectionBookId() throws IOException, InterruptedException;

    /**
     * Tests for Cross-Site Scripting (XSS) vulnerabilities in the Book Title parameter.
     * This method attempts to inject JavaScript or HTML code into the Book Title
     * field and verifies if the API is vulnerable to XSS attacks.
     *
     * @throws IOException If an I/O error occurs during the test.
     * @throws InterruptedException If the operation is interrupted.
     */
    void testXssBookTitle() throws IOException, InterruptedException;

    /**
     * Tests the API's response to invalid HTTP methods.
     * This method sends requests with HTTP methods that are not supported
     * by the API endpoint and verifies if the API returns the correct error response.
     *
     * @throws IOException If an I/O error occurs during the test.
     * @throws InterruptedException If the operation is interrupted.
     */
    void testInvalidHttpMethod() throws IOException, InterruptedException;

    /**
     * Tests for SQL injection vulnerabilities in the Author ID parameter.
     * This method attempts to inject SQL code into the Author ID field
     * and verifies if the API is vulnerable to SQL injection attacks.
     *
     * @throws IOException If an I/O error occurs during the test.
     * @throws InterruptedException If the operation is interrupted.
     */
    void testSqlInjectionAuthorId() throws IOException, InterruptedException;

    /**
     * Tests for Cross-Site Scripting (XSS) vulnerabilities in the Author First Name parameter.
     * This method attempts to inject JavaScript or HTML code into the Author First Name
     * field and verifies if the API is vulnerable to XSS attacks.
     *
     * @throws IOException If an I/O error occurs during the test.
     * @throws InterruptedException If the operation is interrupted.
     */
    void testXssAuthorFirstName() throws IOException, InterruptedException;
}
