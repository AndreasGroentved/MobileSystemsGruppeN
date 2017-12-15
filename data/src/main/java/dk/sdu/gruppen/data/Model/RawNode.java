package dk.sdu.gruppen.data.Model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@Entity(primaryKeys = {"lat","lng"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class RawNode implements Serializable {


    @JsonProperty("lat")
    @NonNull
    public String lat = "";

    @JsonProperty("lng")
    @NonNull
    public String lng = "";

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public RawNode(){
    }

    public RawNode(String lat, String lng) {
        this.lat = lat;
        this.lng = lng;
    }

    /*@JsonProperty("LocalDateTime")
    String LocalDateTime;*/

   /* public String getLocalDateTime() {
        return LocalDateTime;
    }

    public void setLocalDateTime(Date localDateTime) {
        java.text.SimpleDateFormat sdf =
                new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        LocalDateTime = sdf.format(localDateTime);
    }*/
}
