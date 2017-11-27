package pl.basistam.turysta.enums;

public enum EventUserStatus {
    ADMIN("ADMIN"),
    INVITED("INVITED"),
    BLOCKED("BLOCKED"),
    PARTICIPANT("PARTICIPANT"),
    WAITING("WAITING"),
    REJECTED("REJECTED"),
    ARCHIVED("ARCHIVED"),
    NONE("NONE");

    private final String value;

    EventUserStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
