package dk.sdu.gruppen.mobilesystems.map;


import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.location.Location;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.model.SnappedPoint;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import dk.sdu.gruppen.data.Model.GeoNode;
import dk.sdu.gruppen.data.Model.RawNode;
import dk.sdu.gruppen.domain.Domain;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import timber.log.Timber;


public class MapsViewModel extends AndroidViewModel {


    public static final int MEAN_FILTER_N = 5;
    public static final float AT_REST_VALUE = 2.5f; //Til testning med gang, 15 er nok mere passende for biler
    public static final float STATUS_CHANGE_VALUE = 0.5f;
    public static final int STATUS_CHANGE_TIME = 10;

    private MediatorLiveData<String> averageSpeedMediator;
    private MediatorLiveData<String> statusMediator;
    private MediatorLiveData<String> timeMediator;
    private MediatorLiveData<List<LatLng>> markerMediator; //Dem fra server
    private MediatorLiveData<List<LatLng>> routeEndedMediator; //Koordinater til tegning af rute
    private MediatorLiveData<List<LatLng>> queueMarkers; //Dem fra køretur
    private MediatorLiveData<Location> currentLocation;
    private long time = 0;
    private long timeOffset;
    private PublishSubject<List<Location>> locationSubject;

    private StatusEnum currentStatus = StatusEnum.WAITING;
    private DecimalFormat deci;
    private List<Double> speeds;
    private List<Location> locations;
    private List<LatLng> queuePoints;
    private List<LatLng> breakPoints;
    private Domain domain;
    private LinkedList<Double> rollingAverage;
    private long startTimeForCurrentSection = -1; //Hvornår startede kø/køre sektionen
    private long lastQueueTime = System.currentTimeMillis(); //Til ændring i hastigheder skal den tid man kigger tilbage, ikke være længere end sidste knudepunkt

    private Location lastLocation;

    public MapsViewModel(Application app) {
        super(app);
        averageSpeedMediator = new MediatorLiveData<>();
        statusMediator = new MediatorLiveData<>();
        markerMediator = new MediatorLiveData<>();
        timeMediator = new MediatorLiveData<>();
        routeEndedMediator = new MediatorLiveData<>();
        queueMarkers = new MediatorLiveData<>();
        currentLocation = new MediatorLiveData<>();
        speeds = new ArrayList<>();
        deci = new DecimalFormat("##.##");
        locations = new ArrayList<>();
        domain = Domain.getInstance(app);
        rollingAverage = new LinkedList<>();
        queuePoints = new ArrayList<>();
        breakPoints = new ArrayList<>();
        locationSubject = PublishSubject.create();
        locationSubject.subscribe(a -> MapsActivity.LOG("WHAT HAT"));
        locationSubject.subscribeOn(Schedulers.computation()).buffer(1000, TimeUnit.MILLISECONDS).subscribe(locations -> {
            MapsActivity.LOG("sub");
            if (locations.isEmpty()) return;
            List<Location> output = locations.get(0);
            for (int i = 1; i < locations.size(); i++) {
                output.addAll(locations.get(i));
            }
            updateSpeed(output);
        });
    }

    PublishSubject<List<Location>> getLocationSubject() {
        return locationSubject;
    }

    LiveData<String> getAverageSpeed() {
        averageSpeedMediator.postValue("0 km/h");
        return averageSpeedMediator;
    }

    LiveData<String> getStatus() {
        statusMediator.postValue("Waiting");
        return statusMediator;
    }

    LiveData<String> getTime() {
        timeOffset = System.currentTimeMillis();
        timeMediator.postValue(TimeString.msToString(0));

        Observable.interval(1, TimeUnit.SECONDS).subscribe(ts -> {
            time = System.currentTimeMillis() - timeOffset;
            timeMediator.postValue(TimeString.msToString(time));
        });
        return timeMediator;
    }

    LiveData<Location> getCurrentLocation() {
        return currentLocation;
    }

    LiveData<List<LatLng>> getQueueMarkers() {
        return queueMarkers;
    }

    LiveData<List<LatLng>> getMarkers() {
        markerMediator.postValue(new ArrayList<>());
        AsyncTask.execute(() -> {
            List<GeoNode> nodes = domain.getGPSAll();
            //List<GeoNode> nodes = domain.getMockAroundUni();
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

    public void endRoute(MapsHelper mapsHelper) {
        //TODO updater routeEndedMediator med alle køsteder
        //TODO evt genudregn køpunkter, nu hvor de er snappet til vej

        List<LatLng> route = snapRoute(mapsHelper);
        routeEndedMediator.postValue(route);
    }

    private List<LatLng> snapRoute(MapsHelper mapsHelper) {
        if (locations.isEmpty()) return new ArrayList<>();
        List<LatLng> points = locations.stream().map(l -> {
            return new LatLng(l.getLatitude(), l.getLongitude());
        }).collect(Collectors.toList());

        List<SnappedPoint> snappedPoints = mapsHelper.snapToRoad(points);
        List<LatLng> snappedPointsLatLng = snappedPoints.stream().map(a -> {
            return new LatLng(a.location.lat, a.location.lng);
        }).collect(Collectors.toList());
        Timber.i("snapped points " + snappedPoints.size());

        return snappedPointsLatLng;
    }

    private float[] results = new float[1];

    public void updateSpeed(List<Location> loc) {
        if (loc.isEmpty()) return;

        MapsActivity.LOG("UPDATE SIZE " + loc.size());
        Location previousLast = lastLocation;
        if (lastLocation != null) {
            for (int i = 0; i < loc.size(); i++) {
                Location location = loc.get(i);
                if (location.getSpeed() == 0) {//KUN TIL DEBUG VED 1X speed
                    float distance = lastLocation.distanceTo(location);
                    location.setSpeed(distance / (((location.getTime() - lastLocation.getTime()) / 1000)));
                }
                lastLocation = location;
            }
        } else lastLocation = loc.get(loc.size() - 1);

        loc.forEach(location -> speeds.add(meterPerSecondToKmPerHour(location.getSpeed())));
        locations.addAll(loc);
        double filteredValue = meanFilterOfLastNValuesAndValue(speeds, MEAN_FILTER_N, speeds.size() - 1);

        averageSpeedMediator.postValue("Speed: " + deci.format(filteredValue) + " km/h");
        evaluateStatus(filteredValue, lastLocation);


        if (previousLast != null) loc.add(0, previousLast);

        routeEndedMediator.postValue(loc.stream().map(location -> new LatLng(location.getLatitude(), location.getLongitude())).collect(Collectors.toList()));

        MapsActivity.LOG("mapsViewModel");
        currentLocation.postValue(lastLocation);
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
                MapsActivity.LOG("CHANGE");
                currentStatus = StatusEnum.WAITING;
                statusMediator.postValue(currentStatus.getString());
                breakPoints.add(new LatLng(location.getLatitude(), location.getLongitude()));
                queueMarkers.postValue(breakPoints);
                saveToDB();
            }
            lastQueueTime = System.currentTimeMillis();
        } else {
            if (currentStatus.equals(StatusEnum.WAITING) || currentStatus.equals(StatusEnum.QUEUEING)) {
                currentStatus = StatusEnum.DRIVING;
                statusMediator.postValue(currentStatus.getString());
            }
        }

        if (currentStatus.equals(StatusEnum.WAITING)) lastQueueTime = System.currentTimeMillis();
        else {
            if (!currentStatus.equals(StatusEnum.QUEUEING)) {
                if (speedChangeLargerThanQueueMaxChange(filteredSpeed) && queueTimeMoreThanMin()) {
                    currentStatus = StatusEnum.QUEUEING;
                    statusMediator.postValue(currentStatus.getString());
                    queuePoints.add(new LatLng(location.getLatitude(), location.getLongitude()));
                    queueMarkers.postValue(queuePoints);
                    lastQueueTime = System.currentTimeMillis();
                    saveToDB();
                    MapsActivity.LOG("QUEUEING:::::::::::::::::::::::::::::::::::::::::::");
                }
            }
        }


        /*Bremsesektion ud fra ændring i hastighed */
        /*TODO... måske her, måske efter køreturen, hvor data er snappet til rute
            for biler ville det måske give mening af match med hastighedsgrænser
        */

    }

    private boolean queueTimeMoreThanMin() {
        return ((System.currentTimeMillis() - lastQueueTime) / 1000) > STATUS_CHANGE_TIME;
    }

    private boolean speedChangeLargerThanQueueMaxChange(double filteredSpeed) {
        return meanFilterOfLastNValuesAndValue(speeds, MEAN_FILTER_N, speeds.size() - STATUS_CHANGE_TIME - 1)
                <= (filteredSpeed * STATUS_CHANGE_VALUE);
    }

    private static double meanFilterOfLastNValuesAndValue(List<Double> values, int n, int startingOffset) {
        if (values.isEmpty()) return 0;
        double sum = 0;
        int numberOfValues = 0;
        if (startingOffset > values.size() - 1) startingOffset = values.size() - 1;
        for (int i = 0; i < n; i++) {
            int index = startingOffset - i;
            if (index < 0) break;
            sum += values.get(index);
            numberOfValues++;
        }
        return (sum * 1f) / numberOfValues;
    }

    private void saveToDB() {
        domain.insertRawNodes(getCongestionPoints().stream().toArray(RawNode[]::new));
    }

    public List<RawNode> getCongestionPoints() {
        List<RawNode> congestionPoints = new ArrayList<>();
        queuePoints.forEach(latLng -> congestionPoints.add(new RawNode(latLng.latitude + "", latLng.longitude + ""))); //TODO weight 0.5
        breakPoints.forEach(latLng -> congestionPoints.add(new RawNode(latLng.latitude + "", latLng.longitude + ""))); //TODO weight 1
        return congestionPoints;
    }


}