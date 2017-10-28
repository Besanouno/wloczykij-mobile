package pl.basistam.turysta.database.type;

public enum PlaceType {
    EMPTY(0),
    PEAK(1),
    PASS(2),
    CROSSROADS(3),
    MOUNTAIN_HUT(4),
    CAVE(5),
    CAR_PARK(6);

    private final int value;

    PlaceType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static PlaceType fromValue(int i) {
        switch(i) {
            case 1: return PEAK;
            case 2: return PASS;
            case 3: return CROSSROADS;
            case 4: return MOUNTAIN_HUT;
            case 5: return CAVE;
            case 6: return CAR_PARK;
        }
        return EMPTY;
    }
}
