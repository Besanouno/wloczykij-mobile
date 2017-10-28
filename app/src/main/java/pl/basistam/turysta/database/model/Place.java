package pl.basistam.turysta.database.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import pl.basistam.turysta.database.type.PlaceType;

@Entity(tableName = "places")
public class Place {
    @PrimaryKey(autoGenerate = true)
    private Long id;
    private String name;
    private String description;
    private double latitude;
    private double longitude;
    private double height;
    private PlaceType type;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public double getLatitude() {
        return latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getHeight() {
        return height;
    }
    public void setHeight(double height) {
        this.height = height;
    }

    public PlaceType getType() {
        return type;
    }
    public void setType(PlaceType type) {
        this.type = type;
    }
}
