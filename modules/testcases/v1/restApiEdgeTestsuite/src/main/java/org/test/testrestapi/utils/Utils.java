package org.test.testrestapi.utils;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import org.jboss.eap.additional.testsuite.annotations.EAT;

@EAT({"modules/testcases/v1/bookApiTestsuite/src/main/java","modules/testcases/v1/authorApiTestsuite/src/main/java","modules/testcases/v1/performanceApiTestsuite/src/main/java","modules/testcases/v1/restApiEdgeTestsuite/src/main/java"})
public class Utils {

    /**
     * Replaces the substring between two specified characters in a given string with a replacement string.
     *
     * @param str         The input string.
     * @param startChar   The starting character of the substring to be replaced.
     * @param endChar     The ending character of the substring to be replaced.
     * @param replacement The string to replace the substring with.
     * @return The modified string, or the original string if the start or end characters are not found,
     * or if they are in the wrong order, or if the input string is null or empty.
     */
    private static String replaceBetweenChars(String str, char startChar, char endChar, String replacement) {
        // Handle null or empty input string
        if (str == null || str.isEmpty()) {
            return str;
        }

        // Find the index of the starting character
        int startIndex = str.indexOf(startChar);
        // Find the index of the ending character, starting the search after the start character
        int endIndex = str.indexOf(endChar, startIndex + 1);

        // Check if both start and end characters are found and are in the correct order
        if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
            // Replace the substring between the characters with the replacement string
            return str.substring(0, startIndex) + replacement + str.substring(endIndex);
        } else {
            // Return the original string if the characters are not found or are in the wrong order
            return str;
        }
    }
    
    public static String getGmtDateTime(String dateTimeString) {
        OffsetDateTime offsetDateTime = OffsetDateTime.parse(dateTimeString, DateTimeFormatter.ISO_DATE_TIME);
        // Ensure the OffsetDateTime is in GMT/UTC.
        OffsetDateTime gmtDateTime = offsetDateTime.withOffsetSameInstant(ZoneOffset.UTC);

        // Format the OffsetDateTime as a String.
        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME; // Or use a custom formatter
        return replaceBetweenChars(gmtDateTime.format(formatter),'.','Z',"");
    }

    /**
     * Checks if a given string is in the ISO_DATE_TIME format.
     *
     * @param dateTimeString The string to check.
     * @return true if the string is in the ISO_DATE_TIME format, false otherwise.
     */
    public static boolean isOffsetDateTimeFormat(String dateTimeString) {
        // Handle null or empty input string
        if (dateTimeString == null || dateTimeString.isEmpty()) {
            return false;
        }

        try {
            // Attempt to parse the string using ISO_OFFSET_DATE_TIME format
            OffsetDateTime.parse(dateTimeString, DateTimeFormatter.ISO_DATE_TIME);
            // If parsing succeeds, the string is in the correct format
            return true;
        } catch (DateTimeParseException e) {
            // If parsing fails, the string is not in the correct format
            return false;
        }
    }
}
