package dk.sdu.gruppen.data.API;

import java.io.IOException;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.CloseableHttpClient;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import cz.msebera.android.httpclient.util.EntityUtils;

/**
 * Created by LHRBO on 13-11-2017.
 */

public class ApiClient {
    String realm;
    public static final String ERROR_STRING = "error";

    public ApiClient(String realm) {
        this.realm = realm;
    }

    public String post(String url, String parameters) {
        String json = "no data";
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            HttpPost request = new HttpPost(realm + url);
            if (!parameters.isEmpty()) {
                StringEntity params = new StringEntity(parameters);
                request.setEntity(params);
            }
            request.addHeader("content-type", "application/json");
            HttpResponse result = httpClient.execute(request);
            json = EntityUtils.toString(result.getEntity(), "UTF-8");
        } catch (IOException e) {
            System.out.println(e);
            return ERROR_STRING;
        }
        return json;
    }

    public String get(String url) {
        String json = "no data";
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            HttpGet request = new HttpGet(realm + url);
            request.addHeader("User-Agent", "Mozilla/5.0");
            HttpResponse result = httpClient.execute(request);
            json = EntityUtils.toString(result.getEntity(), "UTF-8");
        } catch (IOException e) {
            System.out.println(e);
        }
        return json;
    }
}
