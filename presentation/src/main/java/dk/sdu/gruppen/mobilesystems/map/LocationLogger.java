package dk.sdu.gruppen.mobilesystems.map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.IntentSender;
import android.location.Location;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;

import java.util.List;

import io.reactivex.subjects.PublishSubject;

/**
 * Created by emilfrisk on 23/11/2017.
 */

public class LocationLogger {

    private LocationUpdatesDelegate locationUpdatesDelegate;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    private final
    int REQUEST_CHECK_SETTINGS = 1;

    /**
     * Initializes a new instance of the Logger object with an established
     * connection to location services.
     *
     * @param activity The activity that this method is called from.
     */
    public LocationLogger(Activity activity, PublishSubject<List<Location>> locationPublishSubject) {

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);

        locationRequest = new LocationRequest()
                .setInterval(0)
                .setFastestInterval(0)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        SettingsClient settingsClient = LocationServices.getSettingsClient(activity);
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());

        task.addOnSuccessListener(activity, locationSettingsResponse -> {
            // TODO: Things are good
        });

        task.addOnFailureListener(activity, e -> {
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
        });


        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                locationPublishSubject.onNext(locationResult.getLocations());
            }
        };

    }

    /**
     * Starts logging of location data.
     */
    @SuppressLint("MissingPermission")
    public void startLogging() {
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    /**
     * Stops logging of location data.
     */
    public void stopLogging() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }


}
