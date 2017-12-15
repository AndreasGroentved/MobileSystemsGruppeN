package dk.sdu.gruppen.mobilesystems.map;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.AsyncTask;

import java.util.List;

import dk.sdu.gruppen.data.Model.RawNode;
import dk.sdu.gruppen.domain.Domain;

import static dk.sdu.gruppen.data.API.ApiClient.ERROR_STRING;

/**
 * Created by Andreas Grøntved on 15-12-2017.
 **/

public class UploadJob extends JobService {

    @Override
    public boolean onStartJob(JobParameters params) {
        AsyncTask.execute(() -> {
            Domain domain = Domain.getInstance(getApplicationContext());
            List<RawNode> rawNodes = domain.getAllNodes();
            if (rawNodes.isEmpty()) {
                jobFinished(params, false);
                MapsActivity.LOG("UPLOAD SUCCES");
                return;
            }
            if (!domain.postGPS(rawNodes).equals(ERROR_STRING)) {
                jobFinished(params, false);
                domain.clearDb();
                MapsActivity.LOG("UPLOAD SUCCES");
            }
        });
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}
