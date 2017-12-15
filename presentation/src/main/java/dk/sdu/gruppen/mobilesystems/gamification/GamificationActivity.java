package dk.sdu.gruppen.mobilesystems.gamification;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;
import dk.sdu.gruppen.mobilesystems.R;


public class GamificationActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    TextView score;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        ButterKnife.bind(this);
        setUpToolbar();
        score = (TextView) findViewById(R.id.points);
        getPoints();
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void getPoints() {
        SharedPreferences prefs = this.getSharedPreferences("dk.sdu.gruppen.mobilesystems", Context.MODE_PRIVATE);
        int points = prefs.getInt("points", 0);
        score.setText(String.valueOf(points));
    }
}