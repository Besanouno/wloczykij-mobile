package pl.basistam.turysta.database.type;

public enum PlaceType {
    EMPTY(0),
    PEAK(1),
    CAR_PARK(2),
    MOUNTAIN_HUT(3),
    CROSSROADS(4),
    PASS(5),
    CAVE(6);

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
            case 2: return CAR_PARK;
            case 3: return MOUNTAIN_HUT;
            case 4: return CROSSROADS;
            case 5: return PASS;
            case 6: return CAVE;
        }
        return EMPTY;
    }
}
