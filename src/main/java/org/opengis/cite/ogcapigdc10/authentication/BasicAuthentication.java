package org.opengis.cite.ogcapigdc10.authentication;

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
import org.opengis.cite.ogcapigdc10.SuiteAttribute;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BasicAuthentication extends CommonFixture {

    private String token;

    private String userName;

    private String password;

    @BeforeClass
    public void setUp(ITestContext testContext) {
        userName = (String) testContext.getSuite().getAttribute(SuiteAttribute.USER_NAME.getName());
        password = (String) testContext.getSuite().getAttribute(SuiteAttribute.PASSWORD.getName());
    }

    @Test(
            description = "Test basic authentication")
    public void testBasicAuth(ITestContext testContext) {

        final HttpGet request = new HttpGet(rootUri.toString() + "/credentials/basic");
        final String auth = userName + ":" + password;
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
            assertNotNull(response, "Response is null");
            try {
                JsonNode responseNode = new ObjectMapper().readTree(response);
                JsonNode accessTokenNode = responseNode.get("access_token");
                assertNotNull(accessTokenNode, "No JSON element named 'access_token' found.");
                token = accessTokenNode.asText();
                assertNotNull(token);
                testContext.getSuite().setAttribute(SuiteAttribute.TOKEN.getName(), token);
            } catch (Exception e) {
                Assert.fail("Could not read response as JSON node: " + response);
            }

        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test(
            description = "Test user information",
            dependsOnMethods = { "testBasicAuth" })
    public void testUserInformation() {

        String userInfoEndpoint = rootUri.toString() + "/me";

        final HttpGet request = new HttpGet(userInfoEndpoint);
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

            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == 401) {
                Assert.fail(userInfoEndpoint + " returned 401 although bearer token " + token.substring(0, 3)
                        + "XXXX was sent along with request.");
            } else if (statusCode > 400) {
                throw new SkipException(userInfoEndpoint + " endpoint not supported by this API.");
            }

        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }

    }

}
