package dk.sdu.gruppen.mobilesystems.main;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dk.sdu.gruppen.data.Model.Node;
import dk.sdu.gruppen.mobilesystems.R;
import dk.sdu.gruppen.mobilesystems.gamification.GamificationActivity;
import dk.sdu.gruppen.mobilesystems.map.MapsActivity;

public class MainActivity extends AppCompatActivity {

    private MainViewModel viewModel;

    @BindView(R.id.example)
    TextView example;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        viewModel.getExample().observe(this, s -> {
            example.setText(s);
        });

        Button startButton = findViewById(R.id.b_start);
        startButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MapsActivity.class);
            MainActivity.this.startActivity(intent);

        });

        Button gameButton = findViewById(R.id.b_game);
        gameButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, GamificationActivity.class);
            MainActivity.this.startActivity(intent);
        });


        new AsyncTask<Void, Void, List<Node>>() {
            @Override
            protected List<Node> doInBackground(Void... voids) {
                return viewModel.getGpsToday();
            }

            protected void onPostExecute(List<Node> nodes) {
                example.setText(nodes.get(0).getLat() + " " + nodes.get(0).getLng());
            }
        }.execute();

        askPermission();
    }

    private void askPermission() { //TODO om dette faktisk er tilfældet...


        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                    }
                }).check();
    }
}
