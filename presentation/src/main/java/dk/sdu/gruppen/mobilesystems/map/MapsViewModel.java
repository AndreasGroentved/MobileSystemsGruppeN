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
import java.util.stream.Collectors;

import dk.sdu.gruppen.data.Model.Node;
import dk.sdu.gruppen.domain.Domain;
import timber.log.Timber;


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
    private Location lastLocation;

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
            //List<Node> nodes = domain.getGPSToday();
            List<Node> nodes = domain.getMockAroundUni();
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
        //TODO snap til vej -> måske køre mean/median filter på data, for at håndtere outliers, dette er temp
        Timber.i("locations " + locations);
        if (locations.isEmpty()) return new ArrayList<>();
        List<LatLng> points = locations.stream().map(l -> {
            return new LatLng(l.getLatitude(), l.getLongitude());
        }).collect(Collectors.toList());
        Timber.i("points " + points.size());

        List<SnappedPoint> snappedPoints = mapsHelper.snapToRoad(points);
        List<LatLng> snappedPointsLatLng = snappedPoints.stream().map(a -> {
            return new LatLng(a.location.lat, a.location.lng);
        }).collect(Collectors.toList());
        Timber.i("snapped points " + snappedPoints.size());

        return snappedPointsLatLng;
    }

    float[] results = new float[1];

    public void updateSpeed(Location loc) {
        if (lastLocation != null) {
            Location.distanceBetween(lastLocation.getLatitude(), lastLocation.getLongitude(), loc.getLatitude(), loc.getLongitude(), results);
            loc.setSpeed(results[0] / ((loc.getTime() - lastLocation.getTime()) / 1000));
        }

        double kmPerHour = meterPerSecondToKmPerHour(loc.getSpeed());

        //TODO hvis hastighed 0, prøv at udregne hastighed ud fra tid mellem afstand/tid mellem punkter


        locations.add(loc);

        double filteredValue = meanFilterOfLastNValuesAndValue(speeds, kmPerHour, MEAN_FILTER_N, speeds.size() - 1);
        speeds.add(kmPerHour);
        averageSpeedMediator.setValue("Speed: " + deci.format(filteredValue) + " km/h");
        rawMediator.setValue("Raw speed: " + deci.format(loc.getSpeed()) + " m/s");
        evaluateStatus(filteredValue, loc);
        //cleanLists();
        routeEndedMediator.postValue(locations.stream().map(location -> new LatLng(location.getLatitude(), location.getLongitude())).collect(Collectors.toList()));

        lastLocation = loc;
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

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
}