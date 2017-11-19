package pl.basistam.turysta.enums;

public enum EventUserStatus {
    INVITED("INVITED"),
    BLOCKED("BLOCKED"),
    PARTICIPANT("PARTICIPANT"),
    WAITING("WAITING"),
    REJECTED("REJECTED"),
    ARCHIVED("ARCHIVED");

    private final String value;

    EventUserStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
