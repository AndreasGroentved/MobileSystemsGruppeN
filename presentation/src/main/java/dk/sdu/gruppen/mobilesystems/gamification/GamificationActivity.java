package dk.sdu.gruppen.mobilesystems.gamification;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import dk.sdu.gruppen.mobilesystems.R;

/**
 * Created by Andreas Grøntved on 10-11-2017.
 **/

public class GamificationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

    }

    private void getPoints(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String points = preferences.getString("points", "DEFAULT");
    }
}