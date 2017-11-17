package dk.sdu.gruppen.data.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RawNode implements Serializable {

    @JsonProperty("lat")
    String lat;

    @JsonProperty("lng")
    String lng;

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
