package pl.basistam.turysta.database;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseInitializationHandler {
    public void initializeDatabase(Context context, String databaseName) {
        final File databasePath = context.getDatabasePath(databaseName);

        if (databasePath.exists()) {
            return;
        }

        if (databasePath.getParentFile().mkdirs()) {
            try {
                final InputStream inputStream = context.getAssets().open(databaseName);
                final OutputStream output = new FileOutputStream(databasePath);

                byte[] buffer = new byte[4096];
                int length;

                while ((length = inputStream.read(buffer, 0, buffer.length)) > 0) {
                    output.write(buffer, 0, length);
                }

                output.flush();
                output.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
