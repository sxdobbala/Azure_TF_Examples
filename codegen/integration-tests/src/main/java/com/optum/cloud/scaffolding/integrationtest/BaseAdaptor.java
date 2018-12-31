package com.optum.cloud.scaffolding.integrationtest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class BaseAdaptor {
    protected ObjectMapper mapper = new ObjectMapper();
    protected String host;
    protected CloseableHttpClient client;

    public BaseAdaptor(String host) {
        if (host == null || host.isEmpty()) {
            //DO NOT CHANGE THIS. IT WILL MESS UP JENKINS INTEGRATION TESTS
            //OVERRIDE USING ENV VARIABLE "CODEGEN_SERVICE_HOST"
            //this.host = "http://localhost:8080";
            this.host = "https://codegen-jboss-tomcat-pbi-devops-test.ocp-ctc-core-nonprod.optum.com";
        } else {
            this.host = host;
        }

        String protocol = this.host.substring(0, this.host.indexOf(":"));

        CredentialsProvider provider = new BasicCredentialsProvider();
        UsernamePasswordCredentials credentials
            = new UsernamePasswordCredentials("user1", "user1Pass");
        provider.setCredentials(AuthScope.ANY, credentials);

        if (protocol.equals("https")) {
            try {
                TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }};

                // Install the all-trusting trust manager
                SSLContext sc = SSLContext.getInstance("TLS");
                sc.init(null, trustAllCerts, new SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

                SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(
                    sc,
                    new String[]{"TLSv1", "SSLv3"},
                    null,
                    NoopHostnameVerifier.INSTANCE
                );

                Registry<ConnectionSocketFactory> registry = RegistryBuilder
                    .<ConnectionSocketFactory>create()
                    .register("https", sslConnectionSocketFactory)
                    .build();
                PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);

                this.client = HttpClientBuilder.create()
                    .setSSLSocketFactory(sslConnectionSocketFactory)
                    .setConnectionManager(cm)
                    .setDefaultCredentialsProvider(provider)
                    .build();

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            this.client = HttpClientBuilder.create().setDefaultCredentialsProvider(provider).build();
        }

    }

    public BaseAdaptor() {
        this(System.getenv("CODEGEN_SERVICE_HOST"));
    }

    public String get(String context) {
        try {
            return this.executeToString(new HttpGet(this.host + context));
        } catch (IOException ex) {
            return ex.getMessage();
        }
    }

    public String post(String context, Object objBody) {
        try {
            String e = objBody instanceof String ? (String) objBody : this.mapper.writeValueAsString(objBody);
            HttpPost req = new HttpPost(this.host + context);
            req.setEntity(new StringEntity(e, ContentType.APPLICATION_JSON));
            return this.executeToString(req);
        } catch (IOException ex) {
            return ex.getMessage();
        }
    }

    public CloseableHttpResponse postWithHeadersReturnHttpResponse(String context, Object body, Map<String, String> headers) {
        try {
            String e = body instanceof String ? (String) body : this.mapper.writeValueAsString(body);
            HttpPost req = new HttpPost(this.host + context);
            req.setEntity(new StringEntity(e, ContentType.APPLICATION_JSON));

            headers(headers, req);
            return this.execute(req);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public CloseableHttpResponse putWithHeadersReturnHttpResponse(String context, Object body, Map<String, String> headers) {
        try {
            String e = body instanceof String ? (String) body : this.mapper.writeValueAsString(body);
            HttpPut req = new HttpPut(this.host + context);
            req.setEntity(new StringEntity(e, ContentType.APPLICATION_JSON));

            headers(headers, req);
            return this.execute(req);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public CloseableHttpResponse postWithHeadersReturnHttpResponse(String context, Map<String, String> headers) {
        try {
            HttpPost req = new HttpPost(this.host + context);
            headers(headers, req);
            return this.execute(req);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    public CloseableHttpResponse getWithHeadersReturnHttpResponse(String context, Map<String, String> headers) {
        try {
            HttpGet req = new HttpGet(this.host + context);
            headers(headers, req);
            return this.execute(req);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public HttpEntity postReturnEntity(String context, Object objBody) throws UnsupportedEncodingException {
        try {
            String e = objBody instanceof String ? (String) objBody : this.mapper.writeValueAsString(objBody);
            HttpPost req = new HttpPost(this.host + context);
            req.setEntity(new StringEntity(e, ContentType.APPLICATION_JSON));
            return this.executeReturnEntity(req);
        } catch (IOException ex) {
            return new StringEntity(ex.getMessage());
        }
    }

    public String post(String context) {
        try {
            return this.executeToString(new HttpPost(this.host + context));
        } catch (IOException ex) {
            return ex.getMessage();
        }
    }

    public String delete(String context) {
        try {
            return this.executeToString(new HttpDelete(this.host + context));
        } catch (IOException ex) {
            return ex.getMessage();
        }
    }

    public HttpRequestBase headers(Map<String, String> headers, HttpRequestBase req) {
        headers.keySet().forEach(key -> req.addHeader(key, headers.get(key)));
        return req;
    }

    public CloseableHttpResponse execute(HttpRequestBase req) throws IOException {
        System.out.println("Executing request " + req.getRequestLine());
        CloseableHttpResponse response = this.client.execute(req);
        System.out.println("Received a " + response.getStatusLine().getStatusCode() + " response");
        return response;
    }

    public String executeToString(HttpRequestBase req) throws IOException {
        return EntityUtils.toString(this.executeReturnEntity(req));
    }

    public HttpEntity executeReturnEntity(HttpRequestBase req) throws IOException {
        return this.execute(req).getEntity();
    }

    public static String urlEncode(String code) {
        try {
            return URLEncoder.encode(code, "UTF-8");
        } catch (UnsupportedEncodingException var2) {
            throw new RuntimeException(var2);
        }
    }

    public static String githubUrlEncode(String code) {
        return urlEncode(code).replace("+", "-");
    }
}
