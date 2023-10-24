package org.opengis.cite.ogcapigdc10.openeo;

import static io.restassured.http.ContentType.JSON;
import static io.restassured.http.Method.GET;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.opengis.cite.ogcapigdc10.CommonFixture;
import org.opengis.cite.ogcapigdc10.SuiteAttribute;
import org.opengis.cite.ogcapigdc10.util.ClientUtils;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.Test;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class OpenEO_Processes extends CommonFixture {
    
    private final String JOBS = "jobs";

    private String jobId;    
    
    @Test(
            description = "Test synchronous process execution")
    public void executeSync(ITestContext testContext) {
        String token = (String) testContext.getSuite().getAttribute(SuiteAttribute.TOKEN.getName());
        String syncExecuteEndpoint = rootUri.toString() + "/result";
        String processGraph = (String) testContext.getSuite().getAttribute(SuiteAttribute.PROCESSGRAPH.getName());

        final HttpPost request = new HttpPost(syncExecuteEndpoint);

        request.setEntity(new StringEntity(processGraph, ContentType.APPLICATION_JSON));

        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {

            CloseableHttpResponse response = client.execute(request, new ResponseHandler<CloseableHttpResponse>() {

                @Override
                public CloseableHttpResponse handleResponse(HttpResponse arg0)
                        throws ClientProtocolException, IOException {
                    return (CloseableHttpResponse) arg0;
                }
            });

            if (response.getStatusLine().getStatusCode() == 401) {
                // try again with authorization added to request

                final String authHeader = "Bearer basic//" + token;
                request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);

                response = client.execute(request, new ResponseHandler<CloseableHttpResponse>() {

                    @Override
                    public CloseableHttpResponse handleResponse(HttpResponse arg0)
                            throws ClientProtocolException, IOException {
                        return (CloseableHttpResponse) arg0;
                    }
                });

                assertTrue(response.getStatusLine().getStatusCode() == 200,
                        syncExecuteEndpoint + " returned " + response.getStatusLine().getStatusCode());
            }

        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }

    }
    
    @Test(description = "List file formats")
    public void testGetFileFormats(ITestContext testContext) {
        String getFileFormatsEndpoint = rootUri.toString() + "/file_formats";
        Response response = init().baseUri(getFileFormatsEndpoint).accept(JSON).when().request(GET);
        if(response.getStatusCode() == 401) {
            response = ClientUtils.getRequestWithBearerToken2(testContext, getFileFormatsEndpoint, init().baseUri(getFileFormatsEndpoint).accept(JSON));
        }
        assertTrue(response.getStatusCode() == 200, "Error getting file formats.");
    }
    
    @Test(description = "List all batch jobs")
    public void testListBatchJobs(ITestContext testContext) {
        String listBatchJobsEndpoint = rootUri.toString() + "/" + JOBS;
        Response response = init().baseUri(listBatchJobsEndpoint).accept(JSON).when().request(GET);
        if(response.getStatusCode() == 401) {
            response = ClientUtils.getRequestWithBearerToken2(testContext, listBatchJobsEndpoint, init().baseUri(listBatchJobsEndpoint).accept(JSON));
        }
        assertTrue(response.getStatusCode() == 200, "Error getting batch jobs.");
        JsonPath jsonPath = response.jsonPath();
        List<Object> jobsPath = jsonPath.getList(JOBS);
        assertTrue(jobsPath != null);
        Object firstJob = jobsPath.get(0);
        if(firstJob instanceof HashMap<?, ?>) {
            HashMap<?, ?> firstJobMap = (HashMap<?, ?>)firstJob;
            jobId = firstJobMap.get("id").toString();
        }
    }
    
    @Test(description = "Full metadata for a batch job", dependsOnMethods = "testListBatchJobs")
    public void testMetadataOfBatchJob(ITestContext testContext) {
        String metadataOfJobEndpoint = rootUri.toString() + "/" + JOBS + "/" + jobId;
        Response response = init().baseUri(metadataOfJobEndpoint).accept(JSON).when().request(GET);
        if(response.getStatusCode() == 401) {
            response = ClientUtils.getRequestWithBearerToken2(testContext, metadataOfJobEndpoint, init().baseUri(metadataOfJobEndpoint).accept(JSON));
        }
        assertTrue(response.getStatusCode() == 200, "Error getting metadata of job " + jobId);
    }

}
