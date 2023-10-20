package org.opengis.cite.ogcapigdc10.openeo;

import static io.restassured.http.Method.GET;
import static org.testng.Assert.assertTrue;

import org.opengis.cite.ogcapigdc10.CommonFixture;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.restassured.response.Response;

public class OpenEO_Processes extends CommonFixture {

    @BeforeClass
    public void getProcessesWithExamples() {
        Response response = init().baseUri(rootUri.toString()).when().request(GET, "/");

    }

    @Test(
            description = "Test root endpoint")
    public void testHttp() {
        Response response = init().baseUri(rootUri.toString()).when().request(GET, "/");
        String apiRoot = response.getBody().asString();
        assertTrue(apiRoot.contains("api_version"), "Landingpage does not contain 'api_version'");
        assertTrue(apiRoot.contains("stac_version"), "Landingpage does not contain 'stac_version'");
        assertTrue(apiRoot.contains("endpoints"), "Landingpage does not contain 'endpoints'");
    }

}
