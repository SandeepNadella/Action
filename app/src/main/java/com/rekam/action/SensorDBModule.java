package com.rekam.action;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.Nullable;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Database helper module for creating database, tables, for fetching and setting sensor readings in tables
 *
 * @author Sandeep Nadella
 * @version 1.0
 */
public class SensorDBModule extends SQLiteOpenHelper {

    public static String ACCELEROMETER = "accl";
    public static String GYROSCOPE = "gyro";
    public static String MAGNETOMETER = "mag";

    //The column names in the database table
    private String ACTION_UID = "action_uid";
    private String TIME_STAMP_COL_NAME = "time_stamp";
    private String X_VALUE_COL_NAME = "x_value";
    private String Y_VALUE_COL_NAME = "y_value";
    private String Z_VALUE_COL_NAME = "z_value";

    private String ACTION_CLASS = "action_class";

    private String ACCL_X_VALUE_COL_NAME = "accl_x_value";
    private String ACCL_Y_VALUE_COL_NAME = "accl_y_value";
    private String ACCL_Z_VALUE_COL_NAME = "accl_z_value";

    private String GYRO_X_VALUE_COL_NAME = "gyro_x_value";
    private String GYRO_Y_VALUE_COL_NAME = "gyro_y_value";
    private String GYRO_Z_VALUE_COL_NAME = "gyro_z_value";

    private String MAG_X_VALUE_COL_NAME = "mag_x_value";
    private String MAG_Y_VALUE_COL_NAME = "mag_y_value";
    private String MAG_Z_VALUE_COL_NAME = "mag_z_value";

    private Context context;
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
        this.context = context;
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

        String uthTableName = sensorType + "_" + tableName;
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
     * Creates a table with the given name if it doesn't exist
     *
     * @param tableName
     */
    public void createTable(String tableName) {

        String uthTableName = tableName;
        try {
            String s = "CREATE TABLE IF NOT EXISTS " + uthTableName + " (" +
                    ACCL_X_VALUE_COL_NAME + " REAL, " +
                    ACCL_Y_VALUE_COL_NAME + " REAL, " +
                    ACCL_Z_VALUE_COL_NAME + " REAL, " +
                    GYRO_X_VALUE_COL_NAME + " REAL, " +
                    GYRO_Y_VALUE_COL_NAME + " REAL, " +
                    GYRO_Z_VALUE_COL_NAME + " REAL, " +
                    MAG_X_VALUE_COL_NAME + " REAL, " +
                    MAG_Y_VALUE_COL_NAME + " REAL, " +
                    MAG_Z_VALUE_COL_NAME + " REAL, " +
                    ACTION_CLASS + " REAL)";
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
        String uthTableName = sensorType + "_" + tableName;
        getWritableDatabase().execSQL("DROP TABLE IF EXISTS " + uthTableName);
    }

    /**
     * Insert the sensor reading record into the table
     *
     * @param tableName
     * @param sensorType
     */
    public void insert(String tableName, String sensorType, long actionUID, long timeStamp, float xValue, float yValue, float zValue) {
        String uthTableName = sensorType + "_" + tableName;
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
     * Insert the sensor reading record into the table
     * 1-cop 2-hungry 3-headache 4-about
     *
     * @param tableName
     */
    public void insert(String tableName, long actionClass, long timeStamp, float accl_xValue, float accl_yValue, float accl_zValue, float gyro_xValue, float gyro_yValue, float gyro_zValue, float mag_xValue, float mag_yValue, float mag_zValue) {
        String uthTableName = tableName;
        try {
            String i = "INSERT INTO " + uthTableName + " VALUES(" + actionClass + ", " + timeStamp + ", " + accl_xValue + ", " + accl_yValue + ", " + accl_zValue + ", " + gyro_xValue + ", " + gyro_yValue + ", " + gyro_zValue + ", " + mag_xValue + ", " + mag_yValue + ", " + mag_zValue+")";
            getWritableDatabase().execSQL(i);
            Log.v(MainActivity.TAG, i+"\n Database insertion complete");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(MainActivity.TAG, "Database insertion failed!");
        }
    }

    /**
     * Insert the sensor reading record into the table
     * 1-cop 2-hungry 3-headache 4-about
     *
     * @param tableName
     */
    public void insert(String tableName, float accl_xValue, float accl_yValue, float accl_zValue, float gyro_xValue, float gyro_yValue, float gyro_zValue, float mag_xValue, float mag_yValue, float mag_zValue,long actionClass) {
        String uthTableName = tableName;
        try {
            String i = "INSERT INTO " + uthTableName + " VALUES("+accl_xValue + ", " + accl_yValue + ", " + accl_zValue + ", " + gyro_xValue + ", " + gyro_yValue + ", " + gyro_zValue + ", " + mag_xValue + ", " + mag_yValue + ", " + mag_zValue+", "+actionClass+")";
            getWritableDatabase().execSQL(i);
            Log.v(MainActivity.TAG, i+"\n Database insertion complete");
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

    public void deleteTableData(String tableName) {
        getWritableDatabase().execSQL("DELETE FROM " + tableName + ";");
    }

    public void exportDB(String tableName) {

        File exportDir = new File(context.getApplicationInfo().dataDir+"//databases//CSV//");
        if (!exportDir.exists())
        {
            exportDir.mkdirs();
        }

        File file = new File(exportDir, tableName+".csv");
        try
        {
            file.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
            Cursor curCSV = getReadableDatabase().rawQuery("SELECT * FROM "+tableName,null);
            csvWrite.writeNext(curCSV.getColumnNames());
            while(curCSV.moveToNext())
            {

                if(!tableName.equals("Train") && !tableName.equals("Test") && !tableName.equals("Train_Mean_Features") && !tableName.equals("Test_Mean_Features")) {
                    String arrStr[] = {curCSV.getString(0),String.format ("%d", curCSV.getLong(1)), curCSV.getString(2),curCSV.getString(3),curCSV.getString(4)};
                    csvWrite.writeNext(arrStr);
                } else {
                    String arrStr[] = {curCSV.getString(0),curCSV.getString(1), curCSV.getString(2),curCSV.getString(3),curCSV.getString(4),curCSV.getString(5),curCSV.getString(6),curCSV.getString(7),curCSV.getString(8),curCSV.getString(9)};
                    csvWrite.writeNext(arrStr);
                }

            }
            csvWrite.close();
            curCSV.close();
        }
        catch(Exception e)
        {
            Log.e(MainActivity.TAG, "Error while exporting to CSV. Message: "+e.getMessage(), e);
        }
    }

    public void calculateFeaturesForTrain(String tableName) {
        String[] classes = {"0","1","2","3","4"};
        List<String> list = Arrays.asList(classes);
        Cursor curCSV = getReadableDatabase().rawQuery("SELECT * FROM "+tableName,null);
        String prev = "0";
        List<Float> accl_x_value = new ArrayList<>();
        List<Float> accl_y_value = new ArrayList<>();
        List<Float> accl_z_value = new ArrayList<>();
        List<Float> gyro_x_value = new ArrayList<>();
        List<Float> gyro_y_value = new ArrayList<>();
        List<Float> gyro_z_value = new ArrayList<>();
        List<Float> mag_x_value = new ArrayList<>();
        List<Float> mag_y_value = new ArrayList<>();
        List<Float> mag_z_value = new ArrayList<>();
        this.deleteTableData(tableName+"_Mean_Features");
        while(curCSV.moveToNext()) {
            String class_label = curCSV.getString(9);
            if(list.contains(class_label)) {
                if (!prev.equals(class_label)) {
                    Float mean_accl_x = findMean(accl_x_value);
                    Float mean_accl_y = findMean(accl_y_value);
                    Float mean_accl_z = findMean(accl_z_value);
                    Float mean_gyro_x = findMean(gyro_x_value);
                    Float mean_gyro_y = findMean(gyro_y_value);
                    Float mean_gyro_z = findMean(gyro_z_value);
                    Float mean_mag_x = findMean(mag_x_value);
                    Float mean_mag_y = findMean(mag_y_value);
                    Float mean_mag_z = findMean(mag_z_value);

                    this.insert(tableName+"_Mean_Features",mean_accl_x,mean_accl_y,mean_accl_z,mean_gyro_x,mean_gyro_y,mean_gyro_z,mean_mag_x,mean_mag_y,mean_mag_z,Long.valueOf(prev));

                    accl_x_value.clear();
                    accl_y_value.clear();
                    accl_z_value.clear();
                    gyro_x_value.clear();
                    gyro_y_value.clear();
                    gyro_z_value.clear();
                    mag_x_value.clear();
                    mag_y_value.clear();
                    mag_z_value.clear();

                    accl_x_value.add(curCSV.getFloat(0));
                    accl_y_value.add(curCSV.getFloat(1));
                    accl_z_value.add(curCSV.getFloat(2));
                    gyro_x_value.add(curCSV.getFloat(3));
                    gyro_y_value.add(curCSV.getFloat(4));
                    gyro_z_value.add(curCSV.getFloat(5));
                    mag_x_value.add(curCSV.getFloat(6));
                    mag_y_value.add(curCSV.getFloat(7));
                    mag_z_value.add(curCSV.getFloat(8));
                } else {
                    accl_x_value.add(curCSV.getFloat(0));
                    accl_y_value.add(curCSV.getFloat(1));
                    accl_z_value.add(curCSV.getFloat(2));
                    gyro_x_value.add(curCSV.getFloat(3));
                    gyro_y_value.add(curCSV.getFloat(4));
                    gyro_z_value.add(curCSV.getFloat(5));
                    mag_x_value.add(curCSV.getFloat(6));
                    mag_y_value.add(curCSV.getFloat(7));
                    mag_z_value.add(curCSV.getFloat(8));
                }
                prev = class_label;
            }
        }
    }

    public void calculateFeaturesForTest(String tableName) {
        String[] classes = {"0","1","2","3","4"};
        List<String> list = Arrays.asList(classes);
        Cursor curCSV = getReadableDatabase().rawQuery("SELECT * FROM "+tableName,null);
        String prev = "0";
        List<Float> accl_x_value = new ArrayList<>();
        List<Float> accl_y_value = new ArrayList<>();
        List<Float> accl_z_value = new ArrayList<>();
        List<Float> gyro_x_value = new ArrayList<>();
        List<Float> gyro_y_value = new ArrayList<>();
        List<Float> gyro_z_value = new ArrayList<>();
        List<Float> mag_x_value = new ArrayList<>();
        List<Float> mag_y_value = new ArrayList<>();
        List<Float> mag_z_value = new ArrayList<>();
        this.deleteTableData(tableName+"_Mean_Features");
        while(curCSV.moveToNext()) {
            String class_label = curCSV.getString(9);
            if(list.contains(class_label)) {
                if (!prev.equals(class_label)) {
                    Float mean_accl_x = findMean(accl_x_value);
                    Float mean_accl_y = findMean(accl_y_value);
                    Float mean_accl_z = findMean(accl_z_value);
                    Float mean_gyro_x = findMean(gyro_x_value);
                    Float mean_gyro_y = findMean(gyro_y_value);
                    Float mean_gyro_z = findMean(gyro_z_value);
                    Float mean_mag_x = findMean(mag_x_value);
                    Float mean_mag_y = findMean(mag_y_value);
                    Float mean_mag_z = findMean(mag_z_value);

                    this.insert(tableName+"_Mean_Features",mean_accl_x,mean_accl_y,mean_accl_z,mean_gyro_x,mean_gyro_y,mean_gyro_z,mean_mag_x,mean_mag_y,mean_mag_z,Long.valueOf(prev));

                    accl_x_value.clear();
                    accl_y_value.clear();
                    accl_z_value.clear();
                    gyro_x_value.clear();
                    gyro_y_value.clear();
                    gyro_z_value.clear();
                    mag_x_value.clear();
                    mag_y_value.clear();
                    mag_z_value.clear();

                    accl_x_value.add(curCSV.getFloat(0));
                    accl_y_value.add(curCSV.getFloat(1));
                    accl_z_value.add(curCSV.getFloat(2));
                    gyro_x_value.add(curCSV.getFloat(3));
                    gyro_y_value.add(curCSV.getFloat(4));
                    gyro_z_value.add(curCSV.getFloat(5));
                    mag_x_value.add(curCSV.getFloat(6));
                    mag_y_value.add(curCSV.getFloat(7));
                    mag_z_value.add(curCSV.getFloat(8));
                } else {
                    accl_x_value.add(curCSV.getFloat(0));
                    accl_y_value.add(curCSV.getFloat(1));
                    accl_z_value.add(curCSV.getFloat(2));
                    gyro_x_value.add(curCSV.getFloat(3));
                    gyro_y_value.add(curCSV.getFloat(4));
                    gyro_z_value.add(curCSV.getFloat(5));
                    mag_x_value.add(curCSV.getFloat(6));
                    mag_y_value.add(curCSV.getFloat(7));
                    mag_z_value.add(curCSV.getFloat(8));
                }
                prev = class_label;
            }
        }
    }

    private Float findMean(List<Float> list) {
        float sum =0;
        for (Float f:list) {
            sum+=f;
        }
        return (sum/list.size());
    }

    public void exportDBs(String[] tableNames) {
        File exportDir = new File(context.getApplicationInfo().dataDir+"//databases//CSV//");
        if (exportDir.exists()) {
            String[] children = exportDir.list();
            for (int i = 0; i < children.length; i++) {
                new File(exportDir, children[i]).delete();
            }
            exportDir.mkdirs();
        }
        for (String table:tableNames) {
            exportDB(table);
        }
    }
}
