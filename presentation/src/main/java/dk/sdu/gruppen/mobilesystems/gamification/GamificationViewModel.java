package dk.sdu.gruppen.mobilesystems.gamification;


import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;


public class GamificationViewModel extends AndroidViewModel {


    private MediatorLiveData<Integer> pointsMediator;

    public GamificationViewModel(Application app) {
        super(app);
        pointsMediator = new MediatorLiveData<>();
    }

    public LiveData<Integer> getPointsLiveData() {
        AsyncTask.execute(() -> pointsMediator.postValue(getPoints()));
        return pointsMediator;
    }

    private int getPoints() {
        int points = 0;
        try {
            SharedPreferences prefs = getApplication().getSharedPreferences("dk.sdu.gruppen.mobilesystems", Context.MODE_PRIVATE);
            points = prefs.getInt("points", 0);
        } catch (Exception e) {
            //Stuff
        }
        return points;
    }
}