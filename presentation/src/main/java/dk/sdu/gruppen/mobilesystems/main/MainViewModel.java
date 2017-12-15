package dk.sdu.gruppen.mobilesystems.main;


import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.content.Context;

import java.util.List;

import dk.sdu.gruppen.data.Model.GeoNode;
import dk.sdu.gruppen.domain.Domain;


public class MainViewModel extends AndroidViewModel {


    Domain domain;
    private MediatorLiveData<String> exampleLiveData;

    public List<GeoNode> getGpsToday() {
        return domain.getGPSToday();
    }


    public MainViewModel(Application app) {
        super(app);
        exampleLiveData = new MediatorLiveData<>();
        domain = Domain.getInstance(app);
    }

    LiveData<String> getExample() {
        exampleLiveData.setValue("EXAMPLE");
        return exampleLiveData;
    }


}