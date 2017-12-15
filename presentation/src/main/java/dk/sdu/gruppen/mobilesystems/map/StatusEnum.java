package dk.sdu.gruppen.mobilesystems.map;

/**
 * Created by Andreas Grøntved on 02-12-2017.
 **/

public enum StatusEnum {
    WAITING,
    DRIVING,
    QUEUEING;

    public String getString() {
        switch (this) {
            case WAITING:
                return "waiting";
            case DRIVING:
                return "driving";
            case QUEUEING:
                return "queuing";
        }
        return "ERROR";
    }
}
