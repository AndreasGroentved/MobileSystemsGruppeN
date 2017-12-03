package dk.sdu.gruppen.mobilesystems.map;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;


import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by emilfrisk on 23/11/2017.
 */

public class LocationLogger {

    private Activity activity;
    private LocationUpdatesDelegate locationUpdatesDelegate;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    private boolean currentlyLogging = false;
    private List<Location> locations;

    private final int REQUEST_CHECK_SETTINGS = 1;
    private final int MY_PERMISSION_ACCESS_FINE_LOCATION = 1;

    /**
     * Initializes a new instance of the Logger object with an established
     * connection to location services.
     *
     * @param activity The activity that this method is called from.
     */
    public LocationLogger(Activity activity, LocationUpdatesDelegate locationUpdatesDelegate) {
        this.activity = activity;

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);

        locationRequest = new LocationRequest()
                .setInterval(5000)
                .setFastestInterval(5000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        SettingsClient settingsClient = LocationServices.getSettingsClient(activity);
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());

        task.addOnSuccessListener(activity, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // TODO: Things are good
            }
        });

        task.addOnFailureListener(activity, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                int statusCode = ((ApiException) e).getStatusCode();
                switch (statusCode) {
                    case CommonStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult(activity, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException sendEx) {
                            // Ignore the error
                        }

                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // TODO: Settings are not satisfied, and there is no way of doing so. Tell the user that he can't get his location data.
                        break;
                }
            }
        });


        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                for (Location location : locationResult.getLocations()) {
                    locations.add(location);
                    locationUpdatesDelegate.locationUpdated(location);
                }
            }
        };

        locations = new ArrayList<>();
    }

    /**
     * Starts logging of location data.
     */
    public void startLogging() {
        if (ContextCompat.checkSelfPermission(this.activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_ACCESS_FINE_LOCATION);
            // TODO: If permission is requested and given, start logging should be run again - so that the user doesn't have to click the button (start the process) again.
        } else {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
            currentlyLogging = true;
        }
    }

    /**
     * Stops logging of location data.
     */
    public void stopLogging() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
        currentlyLogging = false;
    }

    /**
     * Extracts the gathered location data. Data can only be extracted once
     * as the Loggers location data is cleared after extraction.
     *
     * @return Location data
     */
    public List<Location> extractLocationData() {
        List<Location> result = locations;
        locations = new ArrayList<>();

        return result;
    }

}
