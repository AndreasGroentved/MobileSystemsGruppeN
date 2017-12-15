package dk.sdu.gruppen.mobilesystems.map;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.GeoApiContext;
import com.google.maps.RoadsApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.SnappedPoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by emilfrisk on 11/11/2017.
 */

public class MapsHelper {
    private String apiKey;
    private GeoApiContext geoApiContext;
    private final static String TAG = "MapsHelper";

    private final static double LAT_DEGREE_IN_METERS = 111111;
    private final static double LAT_METER_IN_DEGREES = 1 / LAT_DEGREE_IN_METERS;
    private final static double LONG_DEGREE_IN_METERS = LAT_DEGREE_IN_METERS * Math.cos(1);
    private final static double LONG_METER_IN_DEGREES = 1 / LONG_DEGREE_IN_METERS;


    public MapsHelper(Context context) {
        try { //TODO gør i aktivitet
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = applicationInfo.metaData;
            apiKey = bundle.getString("com.google.android.geo.API_KEY");
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        geoApiContext = new GeoApiContext.Builder().apiKey(apiKey).build();
    }

    public List<SnappedPoint> snapToRoad(List<LatLng> capturedLocations_) {
        List<com.google.maps.model.LatLng> capturedLocations = capturedLocations_.stream().map(a -> {
            return new com.google.maps.model.LatLng(a.latitude, a.longitude);
        }).collect(Collectors.toList());

        List<SnappedPoint> snappedPoints = new ArrayList<>();

        final int PAGE_OVERLAP = 10;
        final int PAGE_SIZE_LIMIT = 100;

        int offset = 0;

        while (offset < capturedLocations.size()) {
            if (offset > 0) offset -= PAGE_OVERLAP;

            int lowerBound = offset;
            int upperBound = Math.min(offset + PAGE_SIZE_LIMIT, capturedLocations.size());

            com.google.maps.model.LatLng[] page = capturedLocations.subList(lowerBound, upperBound).toArray(new com.google.maps.model.LatLng[upperBound - lowerBound]);

            SnappedPoint[] points = new SnappedPoint[0];
            try {
                points = RoadsApi.snapToRoads(geoApiContext, true, page).await(); //TODO asynk, e.g. med callback
            } catch (ApiException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            boolean passedOverlap = false;

            if (points != null) {
                for (SnappedPoint point : points) {
                    if (offset == 0 || point.originalIndex >= PAGE_OVERLAP - 1) {
                        passedOverlap = true;
                    }
                    if (passedOverlap) {
                        snappedPoints.add(point);
                    }
                }
            }

            offset = upperBound;
        }

        return snappedPoints;
    }

}