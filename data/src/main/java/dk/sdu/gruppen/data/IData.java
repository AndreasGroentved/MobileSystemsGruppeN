package dk.sdu.gruppen.data;

import java.util.List;

import dk.sdu.gruppen.data.Model.GeoNode;
import dk.sdu.gruppen.data.Model.RawNode;

/**
 * Created by LHRBO on 12-11-2017.
 */

public interface IData {
    public List<GeoNode> getGPSToday();

    public List<GeoNode> getGPSAll();

    public List<GeoNode> getGPSRange(String parameters);

    public String postGPS(List<RawNode> rawNodes);
}
