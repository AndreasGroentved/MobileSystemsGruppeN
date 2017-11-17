package dk.sdu.gruppen.domain;

import java.util.List;

import dk.sdu.gruppen.data.Data;
import dk.sdu.gruppen.data.IData;
import dk.sdu.gruppen.data.Model.Node;
import dk.sdu.gruppen.data.Model.RawNode;

public class Domain implements IData {

    private static Domain instance = null;
    private Data data = new Data();

    private Domain() {
        // Exists only to defeat instantiation.
    }

    public static Domain getInstance() {
        if (instance == null) instance = new Domain();
        return instance;
    }

    @Override
    public List<Node> getGPSToday() {
        return data.getGPSToday();
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
