package pl.basistam.turysta.database.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "trail_points")
public class TrailPoint {
    @PrimaryKey(autoGenerate = true)
    private Long id;
    @ColumnInfo(name = "trail_id")
    private Long trailId;
    private double latitude;
    private double longitude;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Long getTrailId() {
        return trailId;
    }
    public void setTrailId(Long trailId) {
        this.trailId = trailId;
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
}
