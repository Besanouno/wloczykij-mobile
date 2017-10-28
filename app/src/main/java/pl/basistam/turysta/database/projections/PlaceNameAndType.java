package pl.basistam.turysta.database.projections;


import pl.basistam.turysta.database.type.PlaceType;

public class PlaceNameAndType {
    private String name;
    private PlaceType type;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public PlaceType getType() {
        return type;
    }
    public void setType(PlaceType type) {
        this.type = type;
    }
}
