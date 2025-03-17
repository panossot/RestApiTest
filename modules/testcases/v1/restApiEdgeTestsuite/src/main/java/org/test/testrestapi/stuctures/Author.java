package org.test.testrestapi.stuctures;

import org.jboss.eap.additional.testsuite.annotations.EAT;

@EAT({"modules/testcases/v1/bookApiTestsuite/src/main/java","modules/testcases/v1/authorApiTestsuite/src/main/java","modules/testcases/v1/performanceApiTestsuite/src/main/java","modules/testcases/v1/restApiEdgeTestsuite/src/main/java","modules/testcases/v1/happyPathApiTestsuite/src/main/java"})
public class Author {

    // Unique identifier for the author.
    public int id;

    // First name of the author.
    public String firstName;

    // Last name of the author.
    public String lastName;

    // Identifier of the book written by this author.
    public int idBook;
}
