package pl.basistam.turysta.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import java.util.List;

import pl.basistam.turysta.database.model.Place;
import pl.basistam.turysta.database.projections.PlaceNameAndType;

@Dao
public interface PlaceDao {
    @Query("SELECT * FROM places")
    List<Place> getAll();

    @Query("SELECT name, type FROM places")
    List<PlaceNameAndType> getNamesAndTypes();

    @Query("SELECT * FROM places WHERE UPPER(name) = UPPER(:name)")
    Place getByName(String name);
}
