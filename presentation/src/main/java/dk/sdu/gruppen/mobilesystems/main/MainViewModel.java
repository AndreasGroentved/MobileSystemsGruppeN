package dk.sdu.gruppen.mobilesystems.main;


import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;


public class MainViewModel extends AndroidViewModel {

    private MediatorLiveData<String> exampleLiveData;


    public MainViewModel(Application app) {
        super(app);
        exampleLiveData = new MediatorLiveData<>();
    }

    LiveData<String> getExample() {
        exampleLiveData.setValue("EXAMPLE");
        return exampleLiveData;
    }


}