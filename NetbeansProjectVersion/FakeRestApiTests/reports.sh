#!/bin/bash

# Path to the Surefire reports directory
REPORTS_DIR="target/surefire-reports"

# Check if the reports directory exists
if [ ! -d "$REPORTS_DIR" ]; then
  echo "Error: Surefire reports directory not found: $REPORTS_DIR"
  exit 1
fi

# Find all XML report files
REPORT_FILES=$(find "$REPORTS_DIR" -name "*.xml")

# Initialize counts
TOTAL_TESTS=0
TOTAL_FAILURES=0
TOTAL_ERRORS=0
TOTAL_SKIPPED=0

# Loop through each report file
for REPORT_FILE in $REPORT_FILES; do
  # Extract test class name from the filename
  TEST_CLASS=$(basename "$REPORT_FILE" | sed 's/\.xml$//')

  echo "--------------------------------------------------------"
  echo "Test Class: $TEST_CLASS"
  echo "--------------------------------------------------------"

  # Parse testsuite information
  TESTS=$(grep -o 'tests="[0-9]*"' "$REPORT_FILE" | sed 's/tests="\([0-9]*\)"/\1/')
  FAILURES=$(grep -o 'failures="[0-9]*"' "$REPORT_FILE" | sed 's/failures="\([0-9]*\)"/\1/')
  SKIPPED=$(grep -o 'skipped="[0-9]*"' "$REPORT_FILE" | sed 's/skipped="\([0-9]*\)"/\1/')

  TOTAL_TESTS=$((TOTAL_TESTS + TESTS))
  TOTAL_FAILURES=$((TOTAL_FAILURES + FAILURES))
  TOTAL_SKIPPED=$((TOTAL_SKIPPED + SKIPPED))

  echo "  Tests: $TESTS"
  echo "  Failures: $FAILURES"
  echo "  Errors: $ERRORS"
  echo "  Skipped: $SKIPPED"

  # Process test cases for detailed failure/error information
  while IFS= read -r line; do
    TEST_METHOD=$(echo "$line" | sed -n 's/.*testcase name="\([^"]*\)".*/\1/p');
    if [[ -n "$TEST_METHOD" ]]; then
      if grep -A 10 "name=\"$TEST_METHOD\"" "$REPORT_FILE" | grep -q "<failure"; then
        echo "    FAILURE: $TEST_METHOD"
      elif grep -A 10 "name=\"$TEST_METHOD\"" "$REPORT_FILE" | grep -q "<skipped"; then
          echo "    SKIPPED: $TEST_METHOD"
      else
          echo "    PASS: $TEST_METHOD"
      fi
    fi
  done < <(grep '<testcase' "$REPORT_FILE")
done

# Print summary
echo "--------------------------------------------------------"
echo "Summary"
echo "--------------------------------------------------------"

echo "Total tests: $TOTAL_TESTS"
echo "Total failures: $TOTAL_FAILURES"
echo "Total skipped: $TOTAL_SKIPPED"

if [ "$TOTAL_FAILURES" -eq 0 ] && [ "$TOTAL_ERRORS" -eq 0 ]; then
  echo "All tests passed (excluding skipped)."
fi
