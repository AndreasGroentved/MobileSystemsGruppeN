package dk.sdu.gruppen.mobilesystems;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import dk.sdu.gruppen.domain.test.Tester;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Tester t = new Tester();

    }
}
