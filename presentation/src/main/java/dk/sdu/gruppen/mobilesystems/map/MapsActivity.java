package dk.sdu.gruppen.mobilesystems.map;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dk.sdu.gruppen.mobilesystems.R;
import timber.log.Timber;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private MapsViewModel viewModel;

    private GoogleMap mMap;
    private LocationManager locationManager;

    @BindView(R.id.speed)
    TextView speedView;

    @BindView(R.id.status)
    TextView statusView;

    @BindView(R.id.rawView)
    TextView rawView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        viewModel = ViewModelProviders.of(this).get(MapsViewModel.class);
        ButterKnife.bind(this);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        setViewModelBindings();
    }

    private void setViewModelBindings() {
        viewModel.getAverageSpeed().observe(this, s -> speedView.setText(s));
        viewModel.getStatus().observe(this, s -> statusView.setText(s));
        viewModel.getRaw().observe(this, s -> rawView.setText(s));
    }


    private void drawMakersOnMap(List<LatLng> markers) { //TODO indtil videre cirkler
        mMap.clear();
        markers.forEach(latLng -> {
            //TODO håndter vægte
            mMap.addMarker((new MarkerOptions().position(latLng)));
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i("return", "return");
            return;
        }
        viewModel.getMarkers().observe(this, this::drawMakersOnMap);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, false);
        locationManager.requestLocationUpdates(provider, 0L, 0f, locationListener);
    }

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            LOG("location lat " + location.getLatitude() + " long " + location.getLongitude() + " , speed" + location.getSpeed());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 20f));
            viewModel.updateSpeed(location);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
            LOG("status changed " + s);
        }

        @Override
        public void onProviderEnabled(String s) {
            LOG("provider enabled");
        }

        @Override
        public void onProviderDisabled(String s) {
            LOG("provider disabled " + s);
        }
    };

    public void LOG(String log) {
        Timber.d(log);
        Log.d("LOGLOG", log);
    }
}


