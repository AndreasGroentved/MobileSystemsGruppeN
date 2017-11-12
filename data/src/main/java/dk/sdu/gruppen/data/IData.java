package dk.sdu.gruppen.data;

import java.util.List;

import dk.sdu.gruppen.data.Model.Node;
import dk.sdu.gruppen.data.Model.RawNode;

/**
 * Created by LHRBO on 12-11-2017.
 */

public interface IData {
    public List<Node> getGPSToday();

    public List<Node> getGPSRange(String parameters);

    public String postGPS(List<RawNode> rawNodes);
}
