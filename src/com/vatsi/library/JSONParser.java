package com.vatsi.library;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.http.entity.mime.MultipartEntity;

import android.util.Log;

public class JSONParser {

    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";

    // constructor
    public JSONParser() {

    }
    public JSONObject getJSONFromUrl(String url, List<NameValuePair> params) {

        // Making HTTP request
        try {
            // defaultHttpClient
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new UrlEncodedFormEntity(params));
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
            Log.e("JSON", json);
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }

        // try parse the string to a JSON object
        try {
            jObj = new JSONObject(json);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }

        // return JSON String
        return jObj;

    }
    public JSONObject getJSONFromUrl(String url, MultipartEntity params) {

        // Making HTTP request
        try {
            // defaultHttpClient
        	System.out.println("1");
            DefaultHttpClient httpClient = new DefaultHttpClient();
            System.out.println("2");
            HttpPost httpPost = new HttpPost(url);
            System.out.println("3");
            HttpContext localContext = new BasicHttpContext();
            httpPost.setEntity(params);
            System.out.println("4");
            HttpResponse httpResponse = httpClient.execute(httpPost,localContext);
            System.out.println("5");
            HttpEntity httpEntity = httpResponse.getEntity();
            System.out.println("6");
            is = httpEntity.getContent();
            System.out.println("7");
        } catch (UnsupportedEncodingException e) {
        	System.out.println("E1");
            e.printStackTrace();
        } catch (ClientProtocolException e) {
        	System.out.println("E2");
            e.printStackTrace();
        } catch (IOException e) {
        	System.out.println("E3");
            e.printStackTrace();
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            System.out.println("8");
            is.close();
            System.out.println("9");
            json = sb.toString();
            Log.e("JSON", json);
        } catch (Exception e) {
        	System.out.println("E4");
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }

        // try parse the string to a JSON object
        try {
        	System.out.println("10"+json);
            jObj = new JSONObject(json);
            System.out.println("11");
        } catch (JSONException e) {
        	System.out.println("E5");
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }
        System.out.println("12");
        // return JSON String
        return jObj;
    }
}
