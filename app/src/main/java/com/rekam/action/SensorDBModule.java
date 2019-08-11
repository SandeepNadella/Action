package com.rekam.action;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

/**
 * Database helper module for creating database, tables, for fetching and setting sensor readings in tables
 *
 * @author Sandeep Nadella
 * @version 1.0
 */
public class SensorDBModule extends SQLiteOpenHelper {

    public static String ACCELEROMETER = "accl";
    public static String GYROSCOPE = "gyro";

    //The column names in the database table
    private String ACTION_UID = "action_uid";
    private String TIME_STAMP_COL_NAME = "time_stamp";
    private String X_VALUE_COL_NAME = "x_value";
    private String Y_VALUE_COL_NAME = "y_value";
    private String Z_VALUE_COL_NAME = "z_value";

    private String tableName;

    /**
     * Create a helper object to create, open, and/or manage a database.
     * This method always returns very quickly.  The database is not actually
     * created or opened until one of {@link #getWritableDatabase} or
     * {@link #getReadableDatabase} is called.
     *
     * @param context to use to open or create the database
     * @param name    of the database file, or null for an in-memory database. Can be full path to
     *                the database file in case its stored on external storage like SDCARD
     * @param version number of the database (starting at 1); if the database is older,
     *                {@link #onUpgrade} will be used to upgrade the database; if the database is
     *                newer, {@link #onDowngrade} will be used to downgrade the database
     */
    public SensorDBModule(@Nullable Context context, @Nullable String name, int version) {
        super(context, name, null, version);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        db.disableWriteAheadLogging();
        super.onOpen(db);
    }

    /**
     * Creates a table with the given name if it doesn't exist
     *
     * @param tableName
     * @param sensorType
     */
    public void createTable(String tableName, String sensorType) {

        String uthTableName = sensorType+"_"+tableName;
        try {
            String s = "CREATE TABLE IF NOT EXISTS " + uthTableName + " (" +
                    ACTION_UID + " REAL, " +
                    TIME_STAMP_COL_NAME + " REAL, " +
                    X_VALUE_COL_NAME + " REAL, " +
                    Y_VALUE_COL_NAME + " REAL, " +
                    Z_VALUE_COL_NAME + " REAL);";
            getWritableDatabase().execSQL(s);
            Log.v(MainActivity.TAG, "Database table creation complete");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(MainActivity.TAG, "Database table creation failed!");
        }
    }

    /**
     * Drops/deletes the table with the table name used while creating the table
     */
    public void delete(String tableName, String sensorType) {
        String uthTableName = sensorType+"_"+tableName;
        getWritableDatabase().execSQL("DROP TABLE IF EXISTS " + uthTableName);
    }

    /**
     * Insert the sensor reading record into the table
     *
     * @param tableName
     * @param sensorType
     */
    public void insert(String tableName, String sensorType, long actionUID, long timeStamp, float xValue, float yValue, float zValue) {
        String uthTableName = sensorType+"_"+tableName;
        try {
            String i = "INSERT INTO " + uthTableName + " VALUES(" + actionUID + ", " + timeStamp + ", " + xValue + ", " + yValue + ", " + zValue + ")";
            getWritableDatabase().execSQL(i);
            Log.v(MainActivity.TAG, "Database insertion complete");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(MainActivity.TAG, "Database insertion failed!");
        }
    }

    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // NOT needed
    }

    /**
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     *
     * <p>
     * The SQLite ALTER TABLE documentation can be found
     * <a href="http://sqlite.org/lang_altertable.html">here</a>. If you add new columns
     * you can use ALTER TABLE to insert them into a live table. If you rename or remove columns
     * you can use ALTER TABLE to rename the old table, then create the new table and then
     * populate the new table with the contents of the old table.
     * </p><p>
     * This method executes within a transaction.  If an exception is thrown, all changes
     * will automatically be rolled back.
     * </p>
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // NOT needed
    }
}
