package dk.sdu.gruppen.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Room;
import android.content.Context;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dk.sdu.gruppen.data.API.ApiClient;
import dk.sdu.gruppen.data.Model.GeoNode;
import dk.sdu.gruppen.data.Model.RawNode;
import dk.sdu.gruppen.data.db.MobileDatabase;

public class Data implements IData {

    public Data(Context context) {
        db = Room.databaseBuilder(context.getApplicationContext(),
                MobileDatabase.class, "rawdb").build();
    }

    MobileDatabase db;

    //ApiClient api = new ApiClient("http://192.168.87.26:3000");
    ApiClient api = new ApiClient("http://mobilesystems.azurewebsites.net");

    @Override
    public List<GeoNode> getGPSToday() {
        List<GeoNode> nodes = new ArrayList<>();
        String response = api.get("/gpsToday");
        try {
            ObjectMapper mapper = new ObjectMapper();
            nodes = mapper.readValue(response, new TypeReference<ArrayList<GeoNode>>() {
            });
        } catch (IOException e) {
            System.out.println(e);
        }
        return nodes;
    }

    @Override
    public List<GeoNode> getGPSAll() {
        List<GeoNode> nodes = new ArrayList<>();
        String response = api.get("/gpsAll");
        try {
            ObjectMapper mapper = new ObjectMapper();
            nodes = mapper.readValue(response, new TypeReference<ArrayList<GeoNode>>() {
            });
        } catch (IOException e) {
            System.out.println(e);
        }
        return nodes;
    }

    @Override
    public List<GeoNode> getGPSRange(String parameters) {
        return null;
    }

    @Override
    public String postGPS(List<RawNode> rawNodes) {
        String jnodes = "";
        try {
            ObjectMapper mapper = new ObjectMapper();
            jnodes = mapper.writeValueAsString(rawNodes);
        } catch (IOException e) {
            System.out.println(e);
        }
        return api.post("/gpsPost", jnodes);
    }


    public List<RawNode> getAllNodes() {
        return db.rawNodeDao().getAllNodes();
    }

    public void insertRawNodes(RawNode[] rawnodes) {
        db.rawNodeDao().insertRawNodes(rawnodes);
    }

    public void clearDb() {
        db.rawNodeDao().emptyTable();
    }


}
