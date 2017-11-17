package dk.sdu.gruppen.data.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RawNode implements Serializable {

    @JsonProperty("latitude")
    String latitude;

    @JsonProperty("longitude")
    String longitude;

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
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
