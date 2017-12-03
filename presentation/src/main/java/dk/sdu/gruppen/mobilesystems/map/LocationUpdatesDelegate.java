package dk.sdu.gruppen.mobilesystems.map;

import android.location.Location;

public interface LocationUpdatesDelegate {
    public void locationUpdated(Location location);
}
