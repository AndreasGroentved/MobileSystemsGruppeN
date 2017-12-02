package dk.sdu.gruppen.mobilesystems.map;

/**
 * Created by Andreas Grøntved on 02-12-2017.
 **/

public enum StatusEnum {
    WAITING,
    DRIVING;

    public String getString() {
        switch (this) {
            case WAITING:
                return "waiting";
            case DRIVING:
                return "driving";
        }
        return "ERROR";
    }
}
