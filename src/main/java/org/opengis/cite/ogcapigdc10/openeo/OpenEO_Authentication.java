package org.opengis.cite.ogcapigdc10.openeo;

import static io.restassured.http.Method.GET;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.opengis.cite.ogcapigdc10.CommonFixture;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.restassured.response.Response;

public class OpenEO_Authentication extends CommonFixture {

    private String token;

    @Test(
            description = "Test basic authentication")
    public void testBasicAuth() {

        final HttpGet request = new HttpGet(rootUri.toString() + "/credentials/basic");
        final String auth = "guest" + ":" + "changeme";
        final byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.ISO_8859_1));
        final String authHeader = "Basic " + new String(encodedAuth);
        request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);

        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {

            String response = client.execute(request, new ResponseHandler<String>() {

                @Override
                public String handleResponse(HttpResponse arg0) throws ClientProtocolException, IOException {
                    StringWriter writer = new StringWriter();
                    String encoding = StandardCharsets.UTF_8.name();
                    IOUtils.copy(arg0.getEntity().getContent(), writer, encoding);
                    return writer.toString();
                }
            });

            JsonNode responseNode = new ObjectMapper().readTree(response);

            token = responseNode.get("access_token").asText();

            assertNotNull(token);

        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test(
            description = "Test user information",
            dependsOnMethods = { "testBasicAuth" })
    public void testUserInformation() {

        final HttpGet request = new HttpGet(rootUri.toString() + "/me");
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

            assertTrue(response.getStatusLine().getStatusCode() == 200);

        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
