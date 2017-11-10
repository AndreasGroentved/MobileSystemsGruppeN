package dk.sdu.gruppen.mobilesystems.main;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import dk.sdu.gruppen.mobilesystems.R;

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

    }
}
