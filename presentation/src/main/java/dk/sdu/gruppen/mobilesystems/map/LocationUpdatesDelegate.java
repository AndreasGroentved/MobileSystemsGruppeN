package dk.sdu.gruppen.mobilesystems.map;

import android.location.Location;

import java.util.List;

public interface LocationUpdatesDelegate {
    void locationUpdated(List<Location> locations);
}
