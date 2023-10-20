package org.opengis.cite.ogcapigdc10.openeo;

import static io.restassured.http.Method.GET;
import static org.testng.Assert.assertTrue;

import org.opengis.cite.ogcapigdc10.CommonFixture;
import org.testng.annotations.Test;

import io.restassured.response.Response;

public class OpenEO extends CommonFixture {

    @Test(
            description = "Test root endpoint")
    public void testLandingpage() {
        Response response = init().baseUri(rootUri.toString()).when().request(GET, "/");
        String landingpage = response.getBody().asString();
        assertTrue(landingpage.contains("api_version"), "Landingpage does not contain 'api_version'");
        assertTrue(landingpage.contains("stac_version"), "Landingpage does not contain 'stac_version'");
        assertTrue(landingpage.contains("endpoints"), "Landingpage does not contain 'endpoints'");
    }

}
