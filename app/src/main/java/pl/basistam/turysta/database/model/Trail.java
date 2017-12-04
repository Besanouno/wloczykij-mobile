package pl.basistam.turysta.database.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.List;

@Entity(tableName = "trails")
public class Trail {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private Integer id;
    private int colour;
    @ColumnInfo(name = "first_point")
    private Integer firstPoint;
    @ColumnInfo(name = "last_point")
    private Integer lastPoint;
    @ColumnInfo(name = "time_to_pass")
    private Integer time;
    @ColumnInfo(name = "twin_trail")
    private Integer twinTrail;

    @Ignore
    private Place first;
    @Ignore
    private Place last;
    @Ignore
    private List<TrailPoint> points;

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public int getColour() {
        return colour;
    }
    public void setColour(int colour) {
        this.colour = colour;
    }

    public List<TrailPoint> getPoints() {
        return points;
    }
    public void setPoints(List<TrailPoint> points) {
        this.points = points;
    }

    public Place getFirst() {
        return first;
    }
    public void setFirst(Place first) {
        this.first = first;
    }

    public Integer getFirstPoint() {
        return firstPoint;
    }
    public void setFirstPoint(Integer firstPoint) {
        this.firstPoint = firstPoint;
    }

    public Integer getLastPoint() {
        return lastPoint;
    }
    public void setLastPoint(Integer lastPoint) {
        this.lastPoint = lastPoint;
    }

    public Place getLast() {
        return last;
    }
    public void setLast(Place last) {
        this.last = last;
    }

    public Integer getTime() {
        return time;
    }
    public void setTime(Integer time) {
        this.time = time;
    }

    public Integer getTwinTrail() {
        return twinTrail;
    }

    public void setTwinTrail(Integer twinTrail) {
        this.twinTrail = twinTrail;
    }
}
