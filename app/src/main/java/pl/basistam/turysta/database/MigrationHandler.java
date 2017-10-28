package pl.basistam.turysta.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.migration.Migration;

public class MigrationHandler {
    public static Migration[] getAllMigrations() {
        return new Migration[]{
//                createEmptyMigration(2,3)
        };
    }

    private static Migration createEmptyMigration(int previousVersion, int nextVersion) {
        return new Migration(previousVersion, nextVersion) {
            @Override
            public void migrate(SupportSQLiteDatabase database) {}
        };
    }
}
