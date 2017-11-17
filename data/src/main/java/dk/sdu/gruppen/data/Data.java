package dk.sdu.gruppen.data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dk.sdu.gruppen.data.API.ApiClient;
import dk.sdu.gruppen.data.Model.Node;
import dk.sdu.gruppen.data.Model.RawNode;

public class Data implements IData {

    //ApiClient api = new ApiClient("http://192.168.87.26:3000");
    ApiClient api = new ApiClient("http://mobilesystems.azurewebsites.net");

    @Override
    public List<Node> getGPSToday() {
        List<Node> nodes = new ArrayList<>();
        String response = api.get("/gpsToday");
        try {
            ObjectMapper mapper = new ObjectMapper();
            nodes = mapper.readValue(response, new TypeReference<ArrayList<Node>>() {});
        }catch (IOException e) {
            System.out.println(e);
        }
        return nodes;
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
