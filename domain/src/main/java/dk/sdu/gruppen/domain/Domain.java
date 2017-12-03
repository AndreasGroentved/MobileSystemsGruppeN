package dk.sdu.gruppen.domain;

import java.util.ArrayList;
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

    public List<Node> getMockAroundUni() {
        List<Node> nodes = new ArrayList<>();
        Node nodeA = new Node("55.366985", "10.430832");
        Node nodeB = new Node("55.365114", "10.431547");
        Node nodeC = new Node("55.366773", "10.431129");
        Node nodeD = new Node("55.364139", "10.439590");
        Node nodeE = new Node("55.365688", "10.445148");
        nodes.add(nodeA);
        nodes.add(nodeB);
        nodes.add(nodeC);
        nodes.add(nodeD);
        nodes.add(nodeE);
        return nodes;
    }

    @Override
    public List<Node> getGPSRange(String parameters) {
        return null;
    }

    @Override
    public String postGPS(List<RawNode> rawNodes) {
        return data.postGPS(rawNodes);
    }
}
