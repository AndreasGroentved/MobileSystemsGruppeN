package dk.sdu.gruppen.mobilesystems.main;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dk.sdu.gruppen.data.Model.Node;
import dk.sdu.gruppen.domain.Domain;
import dk.sdu.gruppen.mobilesystems.R;
import dk.sdu.gruppen.mobilesystems.gamification.GamificationActivity;

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

        Button startButton= findViewById(R.id.b_start);
        startButton.setOnClickListener(v -> {

        });

        Button gameButton= findViewById(R.id.b_game);
        startButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, GamificationActivity.class);
            //intent.putExtra("key", value); //Optional parameters
            MainActivity.this.startActivity(intent);
        });


        new AsyncTask<Void, Void, List<Node>>() {
            @Override
            protected List<Node> doInBackground(Void... voids) {
                return domain.getGPSToday();
            }

            protected void onPostExecute(List<Node> nodes) {
                example.setText(nodes.get(0).getLat() + " " + nodes.get(0).getLng());
            }
        }.execute();

    }
}
