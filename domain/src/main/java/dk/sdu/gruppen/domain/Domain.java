package dk.sdu.gruppen.domain;

public class Domain {

    private static Domain instance = null;

    private Domain() {
        // Exists only to defeat instantiation.
    }

    public static Domain getInstance() {
        if (instance == null) instance = new Domain();
        return instance;
    }


}
