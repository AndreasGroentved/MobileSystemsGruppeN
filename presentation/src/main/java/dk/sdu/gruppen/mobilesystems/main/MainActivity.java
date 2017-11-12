package dk.sdu.gruppen.mobilesystems.main;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import dk.sdu.gruppen.data.API.ApiClient;
import dk.sdu.gruppen.data.Model.Node;
import dk.sdu.gruppen.domain.Domain;
import dk.sdu.gruppen.mobilesystems.R;

public class MainActivity extends AppCompatActivity {

    private MainViewModel viewModel;

    @BindView(R.id.example)
    TextView example;
    Domain domain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        viewModel.getExample().observe(this, s -> {
            example.setText(s);
        });

        //domain = new Domain();
        //List<Node> nodes = domain.getGPSToday();
        Log.w("sysout", "make post");
        ApiClient.post("/gpsToday", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray array) {

                // Do something with the response
                System.out.println("Json array: " + array.toString());
                String jarray = array.toString();
                ObjectMapper mapper = new ObjectMapper();
                Log.w("sysout", jarray);
                example.setText(jarray);
            }

            @Override
            public void onFailure(int i, Header[] h, Throwable t, JSONObject j) {
                System.out.println("Failed? " + j+t);
                Log.w("sysout", "Failed? " + j+t);
            }
        });
    }
}
