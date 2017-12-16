package dk.sdu.gruppen.mobilesystems.gamification;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
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
        ImageView achiev50= (ImageView) findViewById(R.id.Uachiev_1);
        ImageView achiev100= (ImageView) findViewById(R.id.Uachiev_2);
        ImageView achiev200= (ImageView) findViewById(R.id.Uachiev_3);
        int points = 0;
        try {
            points = getPoints();
        }catch(Exception e){
            //Shhhh just eat it silently
        }

        //points = 198;

        score.setText(String.valueOf(points));

        if(points > 50){
            achiev50.setVisibility(View.VISIBLE);
        }if(points > 100){
            achiev100.setVisibility(View.VISIBLE);
        }if(points > 200){
            achiev200.setVisibility(View.VISIBLE);
        }

    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private int getPoints() {
        SharedPreferences prefs = this.getSharedPreferences("dk.sdu.gruppen.mobilesystems", Context.MODE_PRIVATE);
        int points = prefs.getInt("points", 0);
        return points;
    }
}