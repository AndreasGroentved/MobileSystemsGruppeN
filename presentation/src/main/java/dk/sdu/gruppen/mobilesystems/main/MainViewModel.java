package dk.sdu.gruppen.mobilesystems.main;


import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;

import java.util.List;

import dk.sdu.gruppen.data.Model.Node;
import dk.sdu.gruppen.domain.Domain;


public class MainViewModel extends AndroidViewModel {


    Domain domain = Domain.getInstance();
    private MediatorLiveData<String> exampleLiveData;


    public List<Node> getGpsToday() {
        return domain.getGPSToday();
    }


    public MainViewModel(Application app) {
        super(app);
        exampleLiveData = new MediatorLiveData<>();
    }

    LiveData<String> getExample() {
        exampleLiveData.setValue("EXAMPLE");
        return exampleLiveData;
    }


}