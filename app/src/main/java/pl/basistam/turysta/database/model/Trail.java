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
}
