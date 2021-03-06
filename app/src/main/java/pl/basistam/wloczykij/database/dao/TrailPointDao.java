package pl.basistam.wloczykij.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import java.util.List;

import pl.basistam.wloczykij.database.model.TrailPoint;


@Dao
public interface TrailPointDao {
    @Query("SELECT * FROM trail_points WHERE trail_id = :trailId")
    List<TrailPoint> getByTrailId(Integer trailId);
}
