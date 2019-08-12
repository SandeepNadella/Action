package com.rekam.action;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    public static String TAG = "Action";
    private static boolean captureSensorReading = false;
    private static String trainTableName = null;
    private static String testTableName = null;
    private static String COP_TRAIN_TABLE = "cop_train";
    private static String HUNGRY_TRAIN_TABLE = "hungry_train";
    private static String HEADACHE_TRAIN_TABLE = "headache_train";
    private static String ABOUT_TRAIN_TABLE = "about_train";
    private static String COP_TEST_TABLE = "cop_test";
    private static String HUNGRY_TEST_TABLE = "hungry_test";
    private static String HEADACHE_TEST_TABLE = "headache_test";
    private static String ABOUT_TEST_TABLE = "about_test";
    private static long actionUID = 0;
    private static SensorDBModule database = null;
    private static String DATABASE_NAME = "SensorDB";
    private static SensorManager sensorManager = null;
    private static Sensor accelerometer;
    private static Sensor gyroscope;
    private static Sensor magnetometer;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            View trainView = findViewById(R.id.train_layout);
            View testView = findViewById(R.id.test_layout);
            View settingsView = findViewById(R.id.settings_layout);
            trainView.setVisibility(View.INVISIBLE);
            testView.setVisibility(View.INVISIBLE);
            settingsView.setVisibility(View.INVISIBLE);
            switch (item.getItemId()) {
                case R.id.train_dashboard:
                    trainView.setVisibility(View.VISIBLE);
                    return true;
                case R.id.test_dashboard:
                    testView.setVisibility(View.VISIBLE);
                    return true;
                case R.id.settings_dashboard:
                    settingsView.setVisibility(View.VISIBLE);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        SharedPreferences preferences = getSharedPreferences("ActionAppPreferences", 0);
        actionUID = preferences.getLong("actionUID", 0);

        findViewById(R.id.cop_train).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    captureSensorReading = true;
                    trainTableName = COP_TRAIN_TABLE;
                    testTableName = null;
                    actionUID++;
                    startSession();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    captureSensorReading = false;
                    trainTableName = null;
                    testTableName = null;
                    stopSession();
                }
                return false;
            }
        });

        findViewById(R.id.hungry_train).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    captureSensorReading = true;
                    trainTableName = HUNGRY_TRAIN_TABLE;
                    testTableName = null;
                    actionUID++;
                    startSession();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    captureSensorReading = false;
                    trainTableName = null;
                    testTableName = null;
                    stopSession();
                }
                return false;
            }
        });

        findViewById(R.id.headache_train).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    captureSensorReading = true;
                    trainTableName = HEADACHE_TRAIN_TABLE;
                    testTableName = null;
                    actionUID++;
                    startSession();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    captureSensorReading = false;
                    trainTableName = null;
                    testTableName = null;
                    stopSession();
                }
                return false;
            }
        });

        findViewById(R.id.about_train).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    captureSensorReading = true;
                    trainTableName = ABOUT_TRAIN_TABLE;
                    testTableName = null;
                    actionUID++;
                    startSession();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    captureSensorReading = false;
                    trainTableName = null;
                    testTableName = null;
                    stopSession();
                }
                return false;
            }
        });

        findViewById(R.id.cop_test).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    captureSensorReading = true;
                    trainTableName = null;
                    testTableName = COP_TEST_TABLE;
                    actionUID++;
                    startSession();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    captureSensorReading = false;
                    trainTableName = null;
                    testTableName = null;
                    stopSession();
                }
                return false;
            }
        });

        findViewById(R.id.hungry_test).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    captureSensorReading = true;
                    trainTableName = null;
                    testTableName = HUNGRY_TEST_TABLE;
                    actionUID++;
                    startSession();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    captureSensorReading = false;
                    trainTableName = null;
                    testTableName = null;
                    stopSession();
                }
                return false;
            }
        });

        findViewById(R.id.headache_test).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    captureSensorReading = true;
                    trainTableName = null;
                    testTableName = HEADACHE_TEST_TABLE;
                    actionUID++;
                    startSession();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    captureSensorReading = false;
                    trainTableName = null;
                    testTableName = null;
                    stopSession();
                }
                return false;
            }
        });

        findViewById(R.id.about_test).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    captureSensorReading = true;
                    trainTableName = null;
                    testTableName = ABOUT_TEST_TABLE;
                    actionUID++;
                    startSession();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    captureSensorReading = false;
                    trainTableName = null;
                    testTableName = null;
                    stopSession();
                }
                return false;
            }
        });

        findViewById(R.id.del_db_btn).setOnClickListener(new View.OnClickListener() {
            int countBtnDelDBPress = 0;

            @Override
            public void onClick(View v) {
                countBtnDelDBPress++;
                if (countBtnDelDBPress == 10) {
                    countBtnDelDBPress = 0;
                    File dbfolder = new File(getApplicationContext().getApplicationInfo().dataDir + "//databases//");
                    if (dbfolder.exists()) {
                        if (dbfolder.isDirectory()) {
                            String[] children = dbfolder.list();
                            for (int i = 0; i < children.length; i++) {
                                new File(dbfolder, children[i]).delete();
                            }
                        }
                    }
                    databaseInit();
                    actionUID = 0;
                    SharedPreferences preferences = getSharedPreferences("ActionAppPreferences", 0);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putLong("actionUID", actionUID).commit();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(MainActivity.this, "DB file deleted successfully", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });

        final Spinner dropTableName = findViewById(R.id.table_drop_down);
        findViewById(R.id.drop_table).setOnClickListener(new View.OnClickListener() {
            int countDropTable = 0;

            @Override
            public void onClick(View v) {
                countDropTable++;
                if (countDropTable == 10) {
                    countDropTable = 0;
                    database.deleteTableData(dropTableName.getSelectedItem().toString());
                    database.getWritableDatabase().close();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(MainActivity.this, "Table data deleted", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });


        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);

        databaseInit();

    }

    private void databaseInit() {
        database = new SensorDBModule(getApplicationContext(), DATABASE_NAME, 1);
        database.createTable(COP_TRAIN_TABLE, SensorDBModule.ACCELEROMETER);
        database.createTable(HUNGRY_TRAIN_TABLE, SensorDBModule.ACCELEROMETER);
        database.createTable(HEADACHE_TRAIN_TABLE, SensorDBModule.ACCELEROMETER);
        database.createTable(ABOUT_TRAIN_TABLE, SensorDBModule.ACCELEROMETER);
        database.createTable(COP_TRAIN_TABLE, SensorDBModule.GYROSCOPE);
        database.createTable(HUNGRY_TRAIN_TABLE, SensorDBModule.GYROSCOPE);
        database.createTable(HEADACHE_TRAIN_TABLE, SensorDBModule.GYROSCOPE);
        database.createTable(ABOUT_TRAIN_TABLE, SensorDBModule.GYROSCOPE);
        database.createTable(COP_TRAIN_TABLE, SensorDBModule.MAGNETOMETER);
        database.createTable(HUNGRY_TRAIN_TABLE, SensorDBModule.MAGNETOMETER);
        database.createTable(HEADACHE_TRAIN_TABLE, SensorDBModule.MAGNETOMETER);
        database.createTable(ABOUT_TRAIN_TABLE, SensorDBModule.MAGNETOMETER);

        database.createTable(COP_TEST_TABLE, SensorDBModule.ACCELEROMETER);
        database.createTable(HUNGRY_TEST_TABLE, SensorDBModule.ACCELEROMETER);
        database.createTable(HEADACHE_TEST_TABLE, SensorDBModule.ACCELEROMETER);
        database.createTable(ABOUT_TEST_TABLE, SensorDBModule.ACCELEROMETER);
        database.createTable(COP_TEST_TABLE, SensorDBModule.GYROSCOPE);
        database.createTable(HUNGRY_TEST_TABLE, SensorDBModule.GYROSCOPE);
        database.createTable(HEADACHE_TEST_TABLE, SensorDBModule.GYROSCOPE);
        database.createTable(ABOUT_TEST_TABLE, SensorDBModule.GYROSCOPE);
        database.createTable(COP_TEST_TABLE, SensorDBModule.MAGNETOMETER);
        database.createTable(HUNGRY_TEST_TABLE, SensorDBModule.MAGNETOMETER);
        database.createTable(HEADACHE_TEST_TABLE, SensorDBModule.MAGNETOMETER);
        database.createTable(ABOUT_TEST_TABLE, SensorDBModule.MAGNETOMETER);

        database.getWritableDatabase().close();
    }

    private void startSession() {
        findViewById(R.id.container).setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorRecord));
    }

    private void stopSession() {
        findViewById(R.id.container).setBackgroundColor(Color.TRANSPARENT);
        database.getWritableDatabase().close();
    }

    /**
     * Called when there is a new sensor event.  Note that "on changed"
     * is somewhat of a misnomer, as this will also be called if we have a
     * new reading from a sensor with the exact same sensor values (but a
     * newer timestamp).
     *
     * <p>See {@link SensorManager SensorManager}
     * for details on possible sensor types.
     * <p>See also {@link SensorEvent SensorEvent}.
     *
     * <p><b>NOTE:</b> The application doesn't own the
     * {@link SensorEvent event}
     * object passed as a parameter and therefore cannot hold on to it.
     * The object may be part of an internal pool and may be reused by
     * the framework.
     *
     * @param event the {@link SensorEvent SensorEvent}.
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (captureSensorReading) {
            String tableName = (trainTableName == null) ? (testTableName == null) ? null : testTableName : trainTableName;
            if (tableName != null) {
                if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    database.insert(tableName, SensorDBModule.ACCELEROMETER, actionUID, event.timestamp, event.values[0], event.values[1], event.values[2]);
                }
                if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                    database.insert(tableName, SensorDBModule.GYROSCOPE, actionUID, event.timestamp, event.values[0], event.values[1], event.values[2]);
                }
                if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                    database.insert(tableName, SensorDBModule.MAGNETOMETER, actionUID, event.timestamp, event.values[0], event.values[1], event.values[2]);
                }
            }
        }
    }

    /**
     * Unregister sensor listener on app pause
     */
    protected void onPause() {
        super.onPause();
        Log.v(TAG, "Entering onPause");
        SharedPreferences preferences = getSharedPreferences("ActionAppPreferences", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong("actionUID", actionUID).commit();
        sensorManager.unregisterListener(this);
        try {
            if (database.getWritableDatabase().isOpen()) {
                database.getWritableDatabase().close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Database can't be accessed: " + e.getMessage());
        }
    }

    /**
     * ®
     * Register sensor listener on app resume
     */
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences = getSharedPreferences("ActionAppPreferences", 0);
        actionUID = preferences.getLong("actionUID", 0);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    /**
     * Unregister listeners on app destroy
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        SharedPreferences preferences = getSharedPreferences("ActionAppPreferences", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong("actionUID", actionUID).commit();
        sensorManager.unregisterListener(this);
        try {
            if (database.getWritableDatabase().isOpen()) {
                database.getWritableDatabase().close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Database can't be accessed: " + e.getMessage());
        }
    }

    /**
     * Called when the accuracy of the registered sensor has changed.  Unlike
     * onSensorChanged(), this is only called when this accuracy value changes.
     *
     * <p>See the SENSOR_STATUS_* constants in
     * {@link SensorManager SensorManager} for details.
     *
     * @param sensor
     * @param accuracy The new accuracy of this sensor, one of
     *                 {@code SensorManager.SENSOR_STATUS_*}
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // NOT needed
    }
}
