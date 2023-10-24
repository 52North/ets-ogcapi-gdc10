package org.opengis.cite.ogcapigdc10.datadiscovery;

import static io.restassured.http.ContentType.JSON;
import static io.restassured.http.Method.GET;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.opengis.cite.ogcapigdc10.CommonFixture;
import org.opengis.cite.ogcapigdc10.SuiteAttribute;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.Test;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class DataDiscovery extends CommonFixture {
    
    private final String RANGETYPE = "rangetype";
    private final String DOMAINSET = "domainset";
    private final String COLLECTIONS = "collections";
    private final String TYPE = "type";
    private final String QUERYABLES = "queryables";
    private final String COVERAGE = "coverage";
    private String collectionId;
    
    @Test(
            description = "Test basic metadata for all datasets")
    public void testBasicMetadata() {
        String basicMetadataEndpoint = rootUri.toString() + "/" + COLLECTIONS;
        Response response = init().baseUri(basicMetadataEndpoint).accept(JSON).when().request(GET);
        JsonPath jsonPath = response.jsonPath();
        List<Object> collectionsPath = jsonPath.getList(COLLECTIONS);
        assertTrue(collectionsPath != null);
        Object firstCollection = collectionsPath.get(0);
        if(firstCollection instanceof HashMap<?, ?>) {
            HashMap<?, ?> firstCollectionMap = (HashMap<?, ?>)firstCollection;
            collectionId = firstCollectionMap.get("id").toString();
        }
        
    }
    
    @Test(
            description = "Test full metadata for a specific dataset", dependsOnMethods = "testBasicMetadata")
    public void testFullMetadata() {
        String fullMetadataEndpoint = rootUri.toString() + "/" + COLLECTIONS + "/" + collectionId;
        Response response = init().baseUri(fullMetadataEndpoint).accept(JSON).when().request(GET);
        JsonPath jsonPath = response.jsonPath();
        assertTrue(jsonPath.get(TYPE) != null);
    }
    
    @Test(
            description = "Metadata filters for a specific dataset", dependsOnMethods = "testBasicMetadata")
    public void testMetadataFilters() {
        String metadataFilterEndpoint = rootUri.toString() + "/" + COLLECTIONS + "/" + collectionId + "/" + QUERYABLES;
        Response response = init().baseUri(metadataFilterEndpoint).accept(JSON).when().request(GET);
        assertTrue(response.getStatusCode() == 200, "Error getting queryables for collection " + collectionId);
    }
    
    @Test(
            description = "Retrieve a coverage", dependsOnMethods = "testBasicMetadata")
    public void testRetrieveCoverage(ITestContext testContext) {
        String retrieveCoverageEndpoint = rootUri.toString() + "/" + COLLECTIONS + "/" + collectionId + "/" + COVERAGE;
        Response response = init().baseUri(retrieveCoverageEndpoint).accept(JSON).when().request(GET);
        if(response.getStatusCode() == 401) {
            getRequestWithBearerToken(testContext, retrieveCoverageEndpoint);
        }
        assertTrue(response.getStatusCode() == 200, "Error getting coverage for collection " + collectionId);
    }
    
    @Test(
            description = "Retrieve a coverage's domainset", dependsOnMethods = "testBasicMetadata")
    public void testRetrieveCoverageDomainset(ITestContext testContext) {
        String retrieveCoverageDomainsetEndpoint = rootUri.toString() + "/" + COLLECTIONS + "/" + collectionId + "/" + COVERAGE + "/" + DOMAINSET;
        Response response = init().baseUri(retrieveCoverageDomainsetEndpoint).accept(JSON).when().request(GET);
        if(response.getStatusCode() == 401) {
            getRequestWithBearerToken(testContext, retrieveCoverageDomainsetEndpoint);
        }
        assertTrue(response.getStatusCode() == 200, "Error getting coverage's domainset for collection " + collectionId);
    }
    
    @Test(
            description = "Retrieve a coverage's rangetype", dependsOnMethods = "testBasicMetadata")
    public void testRetrieveCoverageRangetype(ITestContext testContext) {
        String retrieveCoverageRangetypeEndpoint = rootUri.toString() + "/" + COLLECTIONS + "/" + collectionId + "/" + COVERAGE + "/" + RANGETYPE;
        Response response = init().baseUri(retrieveCoverageRangetypeEndpoint).accept(JSON).when().request(GET);
        if(response.getStatusCode() == 401) {
            getRequestWithBearerToken(testContext, retrieveCoverageRangetypeEndpoint);
        }
        assertTrue(response.getStatusCode() == 200, "Error getting coverage's rangetype for collection " + collectionId);
    }
    
    private void getRequestWithBearerToken(ITestContext testContext,
            String endpoint) {
        String token = (String) testContext.getSuite().getAttribute(SuiteAttribute.TOKEN.getName());

        final HttpGet request = new HttpGet(endpoint);

        final String authHeader = "Bearer basic//" + token;
        request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);

        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {

            CloseableHttpResponse response = client.execute(request, new ResponseHandler<CloseableHttpResponse>() {

                @Override
                public CloseableHttpResponse handleResponse(HttpResponse arg0)
                        throws ClientProtocolException, IOException {
                    return (CloseableHttpResponse) arg0;
                }
            });

            assertTrue(response.getStatusLine().getStatusCode() == 200,
                    endpoint + " returned " + response.getStatusLine().getStatusCode());

        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
    }

}
