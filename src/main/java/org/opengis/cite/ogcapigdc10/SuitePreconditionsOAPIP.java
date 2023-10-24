package org.opengis.cite.ogcapigdc10;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.testng.ITestContext;
import org.testng.SkipException;
import org.testng.annotations.BeforeSuite;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Checks that various preconditions are satisfied before the test suite is run.
 * If any of these (BeforeSuite) methods fail, all tests will be skipped.
 */
public class SuitePreconditionsOAPIP {

    private static final Logger LOGR = Logger.getLogger(SuitePreconditionsOAPIP.class.getName());

    /**
     * Verifies that the referenced test subject exists and has the expected
     * type.
     *
     * @param testContext
     *            Information about the (pending) test run.
     */
    @BeforeSuite(
            groups = "process")
    @SuppressWarnings("rawtypes")
    public void verifyTestSubject(ITestContext testContext) {
        URI rootUri = (URI) testContext.getSuite().getAttribute(SuiteAttribute.IUT.getName());
        String processesUrl = rootUri.toString() + "/processes";
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode processListRootNode = objectMapper.readTree(new URL(processesUrl));
            JsonNode processListNode = processListRootNode.get("processes");
            JsonNode processNode = processListNode.get(0);
            // API supports OpeoEO processes if parameters element is present
            // we skip the API - Processes tests in that case
            if (processNode.hasNonNull("parameters")) {
                testContext.getSuite().setAttribute(SuiteAttribute.TESTOAPIP.getName(), false);
            }
        } catch (IOException e) {
            LOGR.log(Level.SEVERE, e.getMessage());
        }
    }
}
