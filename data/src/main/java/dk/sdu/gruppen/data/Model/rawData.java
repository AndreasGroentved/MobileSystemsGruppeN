package dk.sdu.gruppen.data.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.sql.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class rawData implements Serializable {

    @JsonProperty("Location")
    String Location;

    @JsonProperty("LocalDateTime")
    String LocalDateTime;

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getLocalDateTime() {
        return LocalDateTime;
    }

    public void setLocalDateTime(Date localDateTime) {
        java.text.SimpleDateFormat sdf =
                new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        LocalDateTime = sdf.format(localDateTime);
    }
}
