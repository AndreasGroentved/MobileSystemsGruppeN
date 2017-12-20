package dk.sdu.gruppen.mobilesystems.gamification;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import dk.sdu.gruppen.mobilesystems.R;


public class GamificationActivity extends AppCompatActivity {

    private GamificationViewModel viewModel;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.points)
    TextView score;

    @BindView(R.id.Uachiev_1)
    ImageView achiev50;
    @BindView(R.id.Uachiev_2)
    ImageView achiev100;
    @BindView(R.id.Uachiev_3)
    ImageView achiev200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        ButterKnife.bind(this);
        setUpToolbar();
        viewModel = ViewModelProviders.of(this).get(GamificationViewModel.class);

        viewModel.getPointsLiveData().observe(this, (points) -> {
            if (points == null) return;
            score.setText(String.valueOf(points));
            setAchievements(points);
        });
    }

    private void setAchievements(int points) {
        if (points > 50) {
            achiev50.setVisibility(View.VISIBLE);
        }
        if (points > 100) {
            achiev100.setVisibility(View.VISIBLE);
        }
        if (points > 200) {
            achiev200.setVisibility(View.VISIBLE);
        }
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}