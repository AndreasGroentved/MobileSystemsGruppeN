package dk.sdu.gruppen.mobilesystems.map;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dk.sdu.gruppen.mobilesystems.R;
import timber.log.Timber;

import android.content.Context;
import android.content.SharedPreferences;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnCameraChangeListener {


    public static final float DEFAULT_ZOOM_LEVEL = 18f;
    private MapsViewModel viewModel;

    private GoogleMap map;
    private float zoomLevel = DEFAULT_ZOOM_LEVEL;
    private MapsHelper mapsHelper;
    private LocationLogger locationLogger;
    private boolean isMapReady = false;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.speed)
    TextView speedView;
    @BindView(R.id.status)
    TextView statusView;
    @BindView(R.id.lengthView)
    TextView timeView;
    @BindView(R.id.end_button)
    Button endButton;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;


    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            //TODO: add some checks to make sure user is actually driving
            addPoints(10);
            timerHandler.postDelayed(this, 10000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        viewModel = ViewModelProviders.of(this).get(MapsViewModel.class);
        ButterKnife.bind(this);

        mapsHelper = new MapsHelper(this);
        locationLogger = new LocationLogger(this, viewModel.getLocationSubject());
        locationLogger.startLogging();
        setViewModelBindings();
        setClickListeners();
        setUpToolbar();

        preferences = this.getSharedPreferences("dk.sdu.gruppen.mobilesystems", Context.MODE_PRIVATE);
        editor = preferences.edit();
        timerHandler.postDelayed(timerRunnable, 10000);
    }

    private void addPoints(int points){
        editor.putInt("points", points);
        editor.apply();
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void scheduleUpload() {
        ComponentName serviceComponent = new ComponentName(this, UploadJob.class);
        JobInfo.Builder builder = new JobInfo.Builder(0, serviceComponent);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        JobScheduler jobScheduler = getSystemService(JobScheduler.class);
        assert jobScheduler != null;
        jobScheduler.schedule(builder.build());
    }


    private void setClickListeners() {
        endButton.setOnClickListener(view -> {
            Timber.i("end");
            viewModel.endRoute(mapsHelper);
            scheduleUpload();
        });
    }

    private void setViewModelBindings() {
        viewModel.getAverageSpeed().observe(this, s -> speedView.setText(s));
        viewModel.getStatus().observe(this, s -> statusView.setText(s));
        viewModel.getTime().observe(this, s -> timeView.setText(s));
        viewModel.getRouteEnded().observe(this, this::drawRoute);
        viewModel.getQueueMarkers().observe(this, latLngs -> {
            drawMarkers(latLngs, BitmapDescriptorFactory.fromResource(R.drawable.waiting_icon));
        });

        viewModel.getCurrentLocation().observe(this, location -> {
            if (location == null || !isMapReady) return;
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), zoomLevel));
        });

    }

    private void drawRoute(List<LatLng> latLngs) {
        if (!isMapReady) return;
        LOG("draw route, size " + latLngs.size());
        Timber.i("draw route, size " + latLngs.size());
        if (latLngs.isEmpty()) return;
        PolylineOptions polyLine = new PolylineOptions().width(5).color(Color.GREEN);

        for (int i = 0; i < latLngs.size(); i++) {
            polyLine.add(latLngs.get(i));
        }

        map.addPolyline(polyLine);
    }

    //TODO kun den nyeste tilføjes i stedet for, at der kommer duplikater
    private void drawMarkers(List<LatLng> markers, BitmapDescriptor bitmapDescriptor) {
        if (!isMapReady) return;
        markers.forEach(latLng -> {
            //TODO håndter vægte
            //TODO find ud af standardformat for ikoner
            map.addMarker((new MarkerOptions().position(latLng)).icon(bitmapDescriptor));
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        isMapReady = true;

        viewModel.getMarkers().observe(this, markers -> {
            drawMarkers(markers, BitmapDescriptorFactory.fromResource(R.drawable.angry_icon));
        });


    }

    public static void LOG(String log) {
        Timber.d(log);
        Log.d("LOGLOG", log);
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        zoomLevel = cameraPosition.zoom;
    }
}


