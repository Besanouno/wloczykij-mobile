package pl.basistam.wloczykij.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import java.util.List;

import pl.basistam.wloczykij.database.model.Trail;

@Dao
public interface TrailDao {
    @Query("SELECT * FROM trails WHERE twin_trail IS null")
    List<Trail> getDrawable();

    @Query("SELECT * FROM trails " +
            "WHERE (first_point = :startId AND last_point = :endId)")
    Trail findByStartAndEndpoint(Integer startId, Integer endId);

    @Query("SELECT * FROM trails WHERE id IN (:trailIds)")
    List<Trail> findAllByIds(List<Integer> trailIds);

    @Query("SELECT * FROM trails WHERE first_point = :id")
    List<Trail> findAvailableTrails(Integer id);
}
