package dk.sdu.gruppen.domain;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import dk.sdu.gruppen.data.Data;
import dk.sdu.gruppen.data.IData;
import dk.sdu.gruppen.data.Model.GeoNode;
import dk.sdu.gruppen.data.Model.RawNode;

public class Domain implements IData {

    private static Domain instance = null;
    private Data data;

    private Domain(Context context) {
        data = new Data(context);
    }

    public static Domain getInstance(Context context) {
        if (instance == null) instance = new Domain(context);
        return instance;
    }

    @Override
    public List<GeoNode> getGPSToday() {
        return data.getGPSToday();
    }

    @Override
    public List<GeoNode> getGPSAll() {
        return data.getGPSAll();
    }

    public List<GeoNode> getMockAroundUni() {
        List<GeoNode> nodes = new ArrayList<>();
        GeoNode nodeA = new GeoNode("55.366985", "10.430832");
        GeoNode nodeB = new GeoNode("55.365114", "10.431547");
        GeoNode nodeC = new GeoNode("55.366773", "10.431129");
        GeoNode nodeD = new GeoNode("55.364139", "10.439590");
        GeoNode nodeE = new GeoNode("55.365688", "10.445148");
        nodes.add(nodeA);
        nodes.add(nodeB);
        nodes.add(nodeC);
        nodes.add(nodeD);
        nodes.add(nodeE);
        return nodes;
    }

    @Override
    public List<GeoNode> getGPSRange(String parameters) {
        return null;
    }

    @Override
    public String postGPS(List<RawNode> rawNodes) {
        return data.postGPS(rawNodes);
    }

    public List<RawNode> getAllNodes() {
        return data.getAllNodes();
    }


    public void insertRawNodes(RawNode[] rawnodes) {
        data.insertRawNodes(rawnodes);
    }

    public void clearDb() {
        data.clearDb();
    }
}
