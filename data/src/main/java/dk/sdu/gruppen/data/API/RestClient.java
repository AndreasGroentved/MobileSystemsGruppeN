package dk.sdu.gruppen.data.API;

        import com.fasterxml.jackson.core.type.TypeReference;
        import com.fasterxml.jackson.databind.ObjectMapper;
        import com.loopj.android.http.*;
        import com.sdu.fitnesshelper.Model.Workout;

        import org.json.JSONArray;
        import org.json.JSONObject;

        import java.io.IOException;
        import java.util.ArrayList;
        import java.util.Arrays;

        import cz.msebera.android.httpclient.Header;

public class FitnessRestClient {
    //private static final String BASE_URL = "http://localhost:8080/api";
    private static final String BASE_URL = "http://207.154.214.233:8080/api";
    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }


}
