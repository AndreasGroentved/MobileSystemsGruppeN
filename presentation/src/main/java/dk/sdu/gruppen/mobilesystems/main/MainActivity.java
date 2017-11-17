package dk.sdu.gruppen.mobilesystems.main;

import android.arch.lifecycle.ViewModelProviders;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dk.sdu.gruppen.data.Model.Node;
import dk.sdu.gruppen.domain.Domain;
import dk.sdu.gruppen.mobilesystems.R;

public class MainActivity extends AppCompatActivity {

    private MainViewModel viewModel;

    @BindView(R.id.example)
    TextView example;
    Domain domain = Domain.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        viewModel.getExample().observe(this, s -> {
            example.setText(s);
        });

        new AsyncTask<Void, Void, List<Node>>() {
            @Override
            protected List<Node> doInBackground(Void... voids) {
                return domain.getGPSToday();
            }

            protected void onPostExecute(List<Node> nodes) {
                example.setText(nodes.get(0).getLatitude());
                //Remember to convert Node -> LatLng
            }
        }.execute();

    }
}
