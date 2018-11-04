package pl.basistam.wloczykij.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import pl.basistam.wloczykij.database.converter.PlaceTypeConverter;
import pl.basistam.wloczykij.database.dao.PlaceDao;
import pl.basistam.wloczykij.database.dao.TrailDao;
import pl.basistam.wloczykij.database.dao.TrailPointDao;
import pl.basistam.wloczykij.database.model.Place;
import pl.basistam.wloczykij.database.model.Trail;
import pl.basistam.wloczykij.database.model.TrailPoint;


@Database(entities =
        {
                Place.class,
                Trail.class,
                TrailPoint.class
        },
        version = 1)
@TypeConverters({PlaceTypeConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase instance = null;
    private static final String DATABASE_NAME = "wloczykij.db";

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    new DatabaseInitializationHandler().initializeDatabase(context, DATABASE_NAME);
                    instance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DATABASE_NAME)
                            .addMigrations(MigrationHandler.getAllMigrations())
                            .build();
                }
            }
        }
        return instance;
    }

    public abstract PlaceDao placeDao();
    public abstract TrailDao trailDao();
    public abstract TrailPointDao trailPointDao();
}
