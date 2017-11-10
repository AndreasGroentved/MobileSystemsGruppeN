package dk.sdu.gruppen.data.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.sql.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class data implements Serializable {

    @JsonProperty("Location")
    String Location;

    @JsonProperty("Weight")
    double Weight;

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public double getWeight() {
        return Weight;
    }

    public void setWeight(double weight) {
        Weight = weight;
    }
}
