package org.test.testrestapi.stuctures;

import org.jboss.eap.additional.testsuite.annotations.EAT;

@EAT({"modules/testcases/v1/bookApiTestsuite/src/main/java","modules/testcases/v1/authorApiTestsuite/src/main/java","modules/testcases/v1/performanceApiTestsuite/src/main/java","modules/testcases/v1/restApiEdgeTestsuite/src/main/java","modules/testcases/v1/happyPathApiTestsuite/src/main/java"})
public class Book {

    // Unique identifier for the book.
    public int id;

    // Number of pages in the book.
    public int pageCount;

    // Title of the book.
    public String title;

    // A brief summary or overview of the book's content.
    public String description;

    // A short extract or sample passage from the book.
    public String excerpt;

    // Date when the book was published.
    public String publishDate;
}
