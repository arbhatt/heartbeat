package com.adp.enets.heartbeat.service;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

@Slf4j
public class HbServiceHelper {

    public String putApi(String url, String jsonInput) {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpPut httpPut = new HttpPut(url);
        log.debug("Calling PUT " + url);
        try {
            StringEntity input = new StringEntity(jsonInput);
            input.setContentType("application/json");

            httpPut.setEntity(input);
            HttpResponse httpResponse = httpClient.execute(httpPut);

            if (httpResponse.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Failed to receive the response from HB Service with HTTP Return code "
                        + httpResponse.getStatusLine().getStatusCode());

            } else {
                return EntityUtils.toString(httpResponse.getEntity());
            }

        } catch (Exception e) {
            log.error("Unable to make a call to service", e);
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                log.error("Unable to close Http Client, this may cause the detrimental effect on test execution. Please resolve and re run");
            }
        }

        return StringUtils.EMPTY;
    }

    public String postApi(String url, String jsonInput) {

        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost postRequest = new HttpPost(url);
        log.debug("Calling POST " + url);
        try {
            StringEntity input = new StringEntity(jsonInput);
            input.setContentType("application/json");

            postRequest.setEntity(input);
            HttpResponse httpResponse = httpClient.execute(postRequest);

            if (httpResponse.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Failed to receive the response from HB Service with HTTP Return code "
                        + httpResponse.getStatusLine().getStatusCode());

            } else {
                return EntityUtils.toString(httpResponse.getEntity());
            }

        } catch (Exception e) {
            log.error("Unable to make a call to service", e);
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                log.error("Unable to close Http Client, this may cause the detrimental effect on test execution. Please resolve and re run");
            }
        }

        return StringUtils.EMPTY;
    }
    
    
    public String getApi(String url, Map<String, String> params) {

        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        String input = mapToString(params);
        HttpGet request = new HttpGet(url+"?"+input);
        
        try {
            HttpResponse httpResponse = httpClient.execute(request);
            if (httpResponse.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Failed to receive the response from HB Service with HTTP Return code "
                        + httpResponse.getStatusLine().getStatusCode());
            } else
            {
                return EntityUtils.toString(httpResponse.getEntity());
            }

        } catch (Exception e) {
            log.error("Unable to make a call to service", e);
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                log.error("Unable to close Http Client, this may cause the detrimental effect on test execution. Please resolve and re run");
            }
        }

        return StringUtils.EMPTY;
    }
    
    public static String mapToString(Map<String, String> map) {
    	   StringBuilder stringBuilder = new StringBuilder();

    	   for (String key : map.keySet()) {
    	    if (stringBuilder.length() > 0) {
    	     stringBuilder.append("&");
    	    }
    	    String value = map.get(key);
    	    try {
    	     stringBuilder.append((key != null ? URLEncoder.encode(key, "UTF-8") : ""));
    	     stringBuilder.append("=");
    	     stringBuilder.append(value != null ? URLEncoder.encode(value, "UTF-8") : "");
    	    } catch (UnsupportedEncodingException e) {
    	     throw new RuntimeException("This method requires UTF-8 encoding support", e);
    	    }
    	   }

    	   return stringBuilder.toString();
    	  }
}
