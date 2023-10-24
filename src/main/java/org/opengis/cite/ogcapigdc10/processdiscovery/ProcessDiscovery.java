package org.opengis.cite.ogcapigdc10.processdiscovery;

import static io.restassured.http.ContentType.JSON;
import static io.restassured.http.Method.GET;
import static org.testng.Assert.assertTrue;

import org.opengis.cite.ogcapigdc10.util.ClientUtils;
import org.opengis.cite.ogcapigdc10.CommonFixture;
import org.testng.ITestContext;
import org.testng.annotations.Test;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class ProcessDiscovery extends CommonFixture {
    
    @Test(
            description = "Test process list")
    public void testProcessList() {
        String processListEndpoint = rootUri.toString() + "/processes";
        Response response = init().baseUri(processListEndpoint).accept(JSON).when().request(GET);
        JsonPath jsonPath = response.jsonPath();
        assertTrue(jsonPath.get("processes")!= null);
    }
    
    @Test(
            description = "Test process graphs")
    public void testProcessGraphs(ITestContext testContext) {
        String processGraphsEndpoint = rootUri.toString() + "/process_graphs";
        Response response = init().baseUri(processGraphsEndpoint).accept(JSON).when().request(GET);
        if(response.getStatusCode() == 401) {
            response = ClientUtils.getRequestWithBearerToken2(testContext, processGraphsEndpoint, init().baseUri(processGraphsEndpoint).accept(JSON).when());
        }
        assertTrue(response.getStatusCode() == 200, "Error getting process graphs.");
        JsonPath jsonPath = response.jsonPath();
        assertTrue(jsonPath.get("processes")!= null);
    }

}
