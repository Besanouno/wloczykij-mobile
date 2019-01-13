package pl.basistam.wloczykij.enums;

public enum MarkerPriority {
    CURRENT(10f),
    FLAG(5f),
    ICON(1f);

    private float value;

    MarkerPriority(float value) {
        this.value = value;
    }

    public float getValue() {
        return value;
    }
}
