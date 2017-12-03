package dk.sdu.gruppen.mobilesystems.map;


import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.location.Location;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import dk.sdu.gruppen.data.Model.Node;
import dk.sdu.gruppen.domain.Domain;


public class MapsViewModel extends AndroidViewModel {


    public static final int MEAN_FILTER_N = 10;
    public static final float AT_REST_VALUE = 2.5f; //Til testning med gang, 15 er nok mere passende for biler
    public static final float STATUS_CHANGE = 0.5f;
    private MediatorLiveData<String> averageSpeedMediator;
    private MediatorLiveData<String> statusMediator;
    private MediatorLiveData<String> rawMediator;
    private MediatorLiveData<List<LatLng>> markerMediator; //Dem fra server
    private MediatorLiveData<List<LatLng>> routeEndedMediator; //Koordinater til tegning af rute
    private MediatorLiveData<List<LatLng>> queueMarkers; //Dem fra køretur

    private StatusEnum currentStatus = StatusEnum.WAITING;
    private DecimalFormat deci;
    private List<Double> speeds;
    private List<Location> locations;
    private List<LatLng> queuePoints;
    private Domain domain;
    private LinkedList<Double> rollingAverage;
    private long startTimeForCurrentSection = -1; //Hvornår startede kø/køre sektionen
    private long lastQueueTime = System.currentTimeMillis(); //Til ændring i hastigheder skal den tid man kigger tilbage, ikke være længere end sidste knudepunkt


    public MapsViewModel(Application app) {
        super(app);
        averageSpeedMediator = new MediatorLiveData<>();
        statusMediator = new MediatorLiveData<>();
        markerMediator = new MediatorLiveData<>();
        rawMediator = new MediatorLiveData<>();
        routeEndedMediator = new MediatorLiveData<>();
        queueMarkers = new MediatorLiveData<>();
        speeds = new ArrayList<>();
        deci = new DecimalFormat("##.##");
        locations = new ArrayList<>();
        domain = Domain.getInstance();
        rollingAverage = new LinkedList<>();
        queuePoints = new ArrayList<>();
    }

    LiveData<String> getAverageSpeed() {
        averageSpeedMediator.setValue("0 km/h");
        return averageSpeedMediator;
    }

    LiveData<String> getStatus() {
        statusMediator.setValue("Waiting");
        return statusMediator;
    }

    LiveData<String> getRaw() {
        rawMediator.setValue("Raw: 0m/s");
        return rawMediator;
    }

    LiveData<List<LatLng>> getQueueMarkers() {
        return queueMarkers;
    }

    LiveData<List<LatLng>> getMarkers() {
        markerMediator.setValue(new ArrayList<>());
        AsyncTask.execute(() -> {

            //TODO få data fra server
            List<Node> nodes = domain.getGPSToday();
            List<LatLng> latLngs = nodes.stream().map(node -> {
                return new LatLng(Double.parseDouble(node.getLat()), Double.parseDouble(node.getLng()));
            }).collect(Collectors.toList());
            markerMediator.postValue(latLngs);
        });
        return markerMediator;
    }

    LiveData<List<LatLng>> getRouteEnded() {
        return routeEndedMediator;
    }

    public void endRoute(List<LatLng> points) {
        //TODO updater routeEndedMediator med alle køsteder
        //TODO evt genudregn køpunkter, nu hvor de er snappet til vej
        routeEndedMediator.postValue(points);
    }

    private List<LatLng> snapRoute() {
        //TODO snap til vej -> måske køre mean/median filter på data, for at håndtere outliers, dette er temp
        return locations.stream().map(location -> new LatLng(location.getLatitude(), location.getLongitude())).collect(Collectors.toList());
    }

    public void updateSpeed(Location loc) {
        double kmPerHour = meterPerSecondToKmPerHour(loc.getSpeed());

        //TODO hvis hastighed 0, prøv at udregne hastighed ud fra tid mellem afstand/tid mellem punkter

        locations.add(loc);

        double filteredValue = meanFilterOfLastNValuesAndValue(speeds, kmPerHour, MEAN_FILTER_N, speeds.size() - 1);
        speeds.add(kmPerHour);
        averageSpeedMediator.setValue("Speed: " + deci.format(filteredValue) + " km/h");
        rawMediator.setValue("Raw speed: " + deci.format(loc.getSpeed()) + " m/s");
        evaluateStatus(filteredValue, loc);
        //cleanLists();
    }

    private void cleanLists() { //TODO find ud af, om det giver mening at gemme al data, og lave analyse efter tur - i forhold til hukommelse
        if (locations.size() > 50) {
            for (int i = 0; i < locations.size() - 50; i++) {
                locations.remove(i); //TODO gør til linkedlist, dette er dyrt
                speeds.remove(i);
            }
        }
    }

    private double meterPerSecondToKmPerHour(double meterPerSecond) {
        return meterPerSecond * 3.6;
    }

    private void evaluateStatus(double filteredSpeed, Location location) {
        /*Helt sikkert (i den ideele verden kun med tilnærmelsestvis godt data og ingen tunneller...) knudepunkt */
        if (filteredSpeed < AT_REST_VALUE/*For test med gang*/) {
            if (currentStatus.equals(StatusEnum.DRIVING)) {
                currentStatus = StatusEnum.WAITING;
                statusMediator.setValue(currentStatus.getString());
                queuePoints.add(new LatLng(location.getLatitude(), location.getLongitude()));
                queueMarkers.postValue(queuePoints);
            }
        } else {
            if (currentStatus.equals(StatusEnum.WAITING)) {
                currentStatus = StatusEnum.DRIVING;
                statusMediator.setValue(currentStatus.getString());
            }
        }

        if (currentStatus.equals(StatusEnum.WAITING)) lastQueueTime = System.currentTimeMillis();

        /*Bremsesektion ud fra ændring i hastighed */
        /*TODO... måske her, måske efter køreturen, hvor data er snappet til rute
            for biler ville det måske give mening af match med hastighedsgrænser
        */

    }

    public static double meanFilterOfLastNValuesAndValue(List<Double> values, double lastValue, int n, int startingOffset) {
        if (values.isEmpty()) return lastValue;
        double sum = lastValue;
        if (startingOffset > values.size() - 1) startingOffset = values.size() - 1;
        for (int i = 0; i < n - 1; i++) {
            int index = startingOffset - i;
            if (index < 0) break;
            sum += values.get(index);
        }
        return (sum * 1f) / n;
    }
}