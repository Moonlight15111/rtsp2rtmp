package org.moonlight.rtsp2rtmp.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * @author Created by Moonlight on 2019/4/19.12:00
 */
public class HttpUtil {

    public static boolean urlIsEffective(String url) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = null;

        try {
            response = httpClient.execute(httpGet);
            return response != null && response.getStatusLine() != null && response.getStatusLine().getStatusCode() == 200;
        } catch (IOException e) {
            return false;
        } finally {
            try {
                if (httpClient != null) {
                    httpClient.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
            }
        }
    }

    public static JSONObject doGet(String url, String headerToken) throws IOException{
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        JSONObject jsonObject = null;
        CloseableHttpResponse response = null;
        try {
            if (StringUtils.isNotBlank(headerToken)) {
                httpGet.addHeader("token", headerToken);
            }
            response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String result = EntityUtils.toString(entity);
                jsonObject = JSONObject.parseObject(result);
            }
        } finally {
            try {
                if (httpClient != null) {
                    httpClient.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
            }
        }
        return jsonObject;
    }

    public static JSONObject doPost(String url, String param) throws IOException{
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        JSONObject jsonObject = null;
        CloseableHttpResponse response = null;
        try {
            httpPost.addHeader("Content-Type", "application/json;charset=UTF-8");
            httpPost.addHeader("Connection", "Keep-Alive");

            if (StringUtils.isNotBlank(param)) {
                httpPost.setEntity(new StringEntity(param, "utf-8"));
            }

            response = httpClient.execute(httpPost);
            String result = EntityUtils.toString(response.getEntity(),"utf-8");
            jsonObject =JSONObject.parseObject(result);
        }  finally {
            try {
                if (httpClient != null) {
                    httpClient.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
            }
        }
        return jsonObject;
    }
}
