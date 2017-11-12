package dk.sdu.gruppen.data;

import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.fasterxml.jackson.core.type.TypeReference;
import com.loopj.android.http.JsonHttpResponseHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import dk.sdu.gruppen.data.API.ApiClient;
import dk.sdu.gruppen.data.API.Callback;
import dk.sdu.gruppen.data.Model.Node;
import dk.sdu.gruppen.data.Model.RawNode;

public class Data implements IData {

    @Override
    public List<Node> getGPSToday(){
        gpsToday cb = new gpsToday();
        return cb.doInBackground();
    }

    @Override
    public List<Node> getGPSRange(String parameters) {
        return null;
    }

    @Override
    public String postGPS(List<RawNode> rawNodes) {
        return null;
    }

    static class gpsToday extends AsyncTask<Void, Void, List<Node>> {

        @Override
        protected List<Node> doInBackground(Void... voids) {
            final Callback callback = new Callback();
            ApiClient.post("/gpsToday", null, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray array) {

                    // Do something with the response
                    System.out.println("Json array: " + array.toString());
                    String jarray = array.toString();
                    ObjectMapper mapper = new ObjectMapper();

                    try {
                        callback.setResult(mapper.readValue(jarray, new TypeReference<ArrayList<Node>>() {}));
                        System.out.println("Mapped json to object");
                    } catch (IOException e) {
                        callback.setResult(null);
                        System.out.println(e);
                    }
                }

                @Override
                public void onFailure(int i, Header[] h, Throwable t, JSONObject j) {
                    System.out.println("Failed? " + j+t);
                    callback.setResult(null);
                }
            });
            return (List<Node>)callback.getResult();
        }
    }

}
