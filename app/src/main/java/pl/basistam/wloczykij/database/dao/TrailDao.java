package pl.basistam.wloczykij.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import java.util.List;

import pl.basistam.wloczykij.database.model.Trail;

@Dao
public interface TrailDao {
    @Query("SELECT * FROM trails")
    List<Trail> getAll();
}
