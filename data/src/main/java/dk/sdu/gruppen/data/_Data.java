package dk.sdu.gruppen.data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.loopj.android.http.JsonHttpResponseHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;

import cz.msebera.android.httpclient.Header;
import dk.sdu.gruppen.data.API._ApiClient;
import dk.sdu.gruppen.data.Model.Node;
import dk.sdu.gruppen.data.Model.RawNode;

public class _Data implements IData {

    @Override
    public List<Node> getGPSToday() {
        final CountDownLatch cdl = new CountDownLatch(1);
        System.out.println("Making post");
        _ApiClient.get("/gpsToday", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray array) {
                List<Node> nodes;
                // Do something with the response
                System.out.println("Json array: " + array.toString());
                String jarray = array.toString();
                ObjectMapper mapper = new ObjectMapper();
                try {
                    nodes = mapper.readValue(jarray, new TypeReference<ArrayList<Node>>() {});
                    System.out.println("Mapped json to object");
                } catch (IOException e) {
                    System.out.println(e);
                }
            }

            @Override
            public void onFailure(int i, Header[] h, Throwable t, JSONArray j) {
                System.out.println("Failed? " + j + t);
            }
        });
        return null;
    }

    @Override
    public List<Node> getGPSRange(String parameters) {
        return null;
    }

    @Override
    public String postGPS(List<RawNode> rawNodes) {
        return null;
    }

}
