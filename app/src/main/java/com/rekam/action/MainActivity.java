package com.rekam.action;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.meta.MultiClassClassifier;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;

/**
 * Implements Sensor Event Change listener for recording sensor readings.
 *
 * @author Sandeep Nadella
 * @version 1.0
 */
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
    private static boolean mergeSensorReadings = false;
    private static boolean doTrain = true;
    private static long previousRecordedTimestamp = 0;

    private static float accl_x_value = 0;
    private static float accl_y_value = 0;
    private static float accl_z_value = 0;
    private static float gyro_x_value = 0;
    private static float gyro_y_value = 0;
    private static float gyro_z_value = 0;
    private static float mag_x_value = 0;
    private static float mag_y_value = 0;
    private static float mag_z_value = 0;
    private static long class_label = 0;

    private static String TRAIN_TABLE_NAME = "Train";
    private static String TEST_TABLE_NAME = "Test";

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
                    TextView trainingStatusArea = findViewById(R.id.training_status);
                    trainingStatusArea.setText("");
                    doTrain = true;
                    return true;
                case R.id.test_dashboard:
                    testView.setVisibility(View.VISIBLE);
                    doTrain = false;
                    return true;
                case R.id.settings_dashboard:
                    settingsView.setVisibility(View.VISIBLE);
                    doTrain = false;
                    return true;
            }
            return false;
        }
    };
    private Instances mTrain;
    private Instances mTest;

    private static Classifier loadModel(String modelFilePath) throws Exception {

        Classifier classifier;

        FileInputStream fis = new FileInputStream(modelFilePath);
        ObjectInputStream ois = new ObjectInputStream(fis);

        classifier = (Classifier) ois.readObject();
        ois.close();

        return classifier;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        SharedPreferences preferences = getSharedPreferences("ActionAppPreferences", 0);
        actionUID = preferences.getLong("actionUID", 0);

        CheckBox doItMyWayBtn = findViewById(R.id.do_it_my_way_btn);
        doItMyWayBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mergeSensorReadings = isChecked;
            }
        });

        findViewById(R.id.cop_train).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    captureSensorReading = true;
                    trainTableName = COP_TRAIN_TABLE;
                    testTableName = null;
                    actionUID++;
                    class_label = 1;
                    startSession();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    captureSensorReading = false;
                    trainTableName = null;
                    testTableName = null;
                    class_label = 0;
                    stopSession();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(MainActivity.this, "Count: " + actionUID, Toast.LENGTH_LONG).show();
                        }
                    });
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
                    class_label = 2;
                    startSession();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    captureSensorReading = false;
                    trainTableName = null;
                    testTableName = null;
                    class_label = 0;
                    stopSession();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(MainActivity.this, "Count: " + actionUID, Toast.LENGTH_LONG).show();
                        }
                    });
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
                    class_label = 3;
                    startSession();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    captureSensorReading = false;
                    trainTableName = null;
                    testTableName = null;
                    class_label = 0;
                    stopSession();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(MainActivity.this, "Count: " + actionUID, Toast.LENGTH_LONG).show();
                        }
                    });
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
                    class_label = 4;
                    startSession();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    captureSensorReading = false;
                    trainTableName = null;
                    testTableName = null;
                    class_label = 0;
                    stopSession();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(MainActivity.this, "Count: " + actionUID, Toast.LENGTH_LONG).show();
                        }
                    });
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
                    class_label = 1;
                    startSession();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    captureSensorReading = false;
                    trainTableName = null;
                    testTableName = null;
                    class_label = 0;
                    stopSession();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(MainActivity.this, "Count: " + actionUID, Toast.LENGTH_LONG).show();
                        }
                    });
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
                    class_label = 2;
                    startSession();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    captureSensorReading = false;
                    trainTableName = null;
                    testTableName = null;
                    class_label = 0;
                    stopSession();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(MainActivity.this, "Count: " + actionUID, Toast.LENGTH_LONG).show();
                        }
                    });
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
                    class_label = 3;
                    startSession();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    captureSensorReading = false;
                    trainTableName = null;
                    testTableName = null;
                    class_label = 0;
                    stopSession();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(MainActivity.this, "Count: " + actionUID, Toast.LENGTH_LONG).show();
                        }
                    });
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
                    class_label = 4;
                    startSession();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    captureSensorReading = false;
                    trainTableName = null;
                    testTableName = null;
                    class_label = 0;
                    stopSession();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(MainActivity.this, "Count: " + actionUID, Toast.LENGTH_LONG).show();
                        }
                    });
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
        final Spinner algoDropDown = findViewById(R.id.algo_drop_down);

        findViewById(R.id.start_training).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String[] tables = getResources().getStringArray(R.array.tables_available);
                database.calculateFeaturesForTrain(TRAIN_TABLE_NAME);
                database.calculateFeaturesForTest(TEST_TABLE_NAME);
                database.exportDBs(tables);
                //Build models using train data and save
                getTrainInstances(getApplicationInfo().dataDir + "//databases//CSV//Train_Mean_Features");
                simpleWekaTrain(getApplicationInfo().dataDir + "//databases//Models");
            }
        });

        findViewById(R.id.generate_acc_btn).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String[] tables = getResources().getStringArray(R.array.tables_available);
                database.calculateFeaturesForTrain(TRAIN_TABLE_NAME);
                database.calculateFeaturesForTest(TEST_TABLE_NAME);
                database.exportDBs(tables);
                getTrainInstances(getApplicationInfo().dataDir + "//databases//CSV//Train_Mean_Features");
                getTestInstances(getApplicationInfo().dataDir + "//databases//CSV//Test_Mean_Features");
                simpleWekaTest(getApplicationInfo().dataDir + "//databases//Models", algoDropDown.getSelectedItem().toString());
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

    private void CSVToARFF(String srcPath, String destPath) {
        CSVLoader loader = new CSVLoader();
        try {
            loader.setSource(new File(srcPath));
            Instances data = loader.getDataSet();
            ArffSaver saver = new ArffSaver();
            saver.setInstances(data);
            saver.setFile(new File(destPath));
            saver.writeBatch();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
    }

    private void getTrainInstances(String trainReadFilePath) {
        try {
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(new FileReader(trainReadFilePath + ".csv"));
            int maxLines = 50000, lineCounter = 0;
            String line;
            while ((line = br.readLine()) != null && lineCounter < maxLines) {
                sb.append(line).append("\n");
                lineCounter++;
            }
            br.close();
            CSVLoader csvLoader = new CSVLoader();
            csvLoader.setSource(new ByteArrayInputStream(sb.toString().getBytes()));

            mTrain = csvLoader.getDataSet();
            mTrain.setClassIndex(mTrain.numAttributes() - 1);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Something went wrong while loading training set...");
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(MainActivity.this, "Something went wrong while loading training set...", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void getTestInstances(String testReadFilePath) {
        try {
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(new FileReader(testReadFilePath + ".csv"));
            int maxLines = 50000, lineCounter = 0;
            String line;
            while ((line = br.readLine()) != null && lineCounter < maxLines) {
                sb.append(line).append("\n");
                lineCounter++;
            }
            br.close();
            CSVLoader csvLoader = new CSVLoader();
            csvLoader.setSource(new ByteArrayInputStream(sb.toString().getBytes()));

            mTest = csvLoader.getDataSet();
            mTest.setClassIndex(mTest.numAttributes() - 1);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Something went wrong while loading testing set...");
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(MainActivity.this, "Something went wrong while loading testing set...", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void simpleWekaTrain(String modelsStorePath) {
        File modelsDir = new File(modelsStorePath);
        if (!modelsDir.exists()) {
            modelsDir.mkdirs();
        }
        try {
            if (mTrain != null) {
                TextView trainingStatusArea = findViewById(R.id.training_status);
                trainingStatusArea.setText("\nTraining MultilayerPerceptron");
                MultilayerPerceptron mlp = new MultilayerPerceptron();
                mlp.setOptions(new String[]{"-L", "0.3", "-M", "0.2", "-N", "500", "-V", "0", "-S", "0", "-E", "20", "-H", "a"});
                mlp.buildClassifier(mTrain);
                weka.core.SerializationHelper.write(modelsStorePath + "//MultilayerPerceptron.model", mlp);
                trainingStatusArea.setText("\nTraining NaiveBayes");
                NaiveBayes naiveBayes = new NaiveBayes();
                naiveBayes.buildClassifier(mTrain);
                weka.core.SerializationHelper.write(modelsStorePath + "//NaiveBayes.model", naiveBayes);
                trainingStatusArea.setText("\nTraining MultiClassClassifier");
                MultiClassClassifier multiClassClassifier = new MultiClassClassifier();
                multiClassClassifier.setOptions(new String[]{"-M", "0", "-R", "2.0", "-S", "1", "-W", "weka.classifiers.functions.Logistic", "--", "-R", "1.0E-8", "-M", "-1"});
                multiClassClassifier.buildClassifier(mTrain);
                weka.core.SerializationHelper.write(modelsStorePath + "//MultiClassClassifier.model", multiClassClassifier);
                trainingStatusArea.setText("\nTraining RandomForest");
                RandomForest randomForest = new RandomForest();
                randomForest.setOptions(new String[]{"-I", "100", "-K", "0", "-S", "1"});
                randomForest.buildClassifier(mTrain);
                weka.core.SerializationHelper.write(modelsStorePath + "//RandomForest.model", randomForest);
                trainingStatusArea.setText("\nTraining Completed");
            } else {
                Log.e(TAG, "Something went wrong... Train set is NULL");
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(MainActivity.this, "Something is wrong with train dataset...", Toast.LENGTH_LONG).show();
                    }
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e(TAG, "Something went wrong while training...");
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(MainActivity.this, "Something went wrong while training...", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void simpleWekaTest(String modelPath, String algorithmChosen) {
        try {
            Classifier classifier = null;
            int i = -1;
            if ("MultilayerPerceptron".equals(algorithmChosen)) {
                i = 0;
                classifier = loadModel(modelPath + "//MultilayerPerceptron.model");
            } else if ("NaiveBayes".equals(algorithmChosen)) {
                i = 1;
                classifier = loadModel(modelPath + "//NaiveBayes.model");
            } else if ("MultiClassClassifier".equals(algorithmChosen)) {
                i = 2;
                classifier = loadModel(modelPath + "//MultiClassClassifier.model");
            } else if ("RandomForest".equals(algorithmChosen)) {
                i = 3;
                classifier = loadModel(modelPath + "//RandomForest.model");
            }
            String label = "";
            if (i == 0) {
                label = "cop";
            } else if (i == 1) {
                label = "hungry";
            } else if (i == 2) {
                label = "headache";
            } else if (i == 3) {
                label = "about";
            }
            Evaluation eval = new Evaluation(mTrain);
            eval.evaluateModel(classifier, mTest);

            Log.v(TAG, "Error Rate: " + eval.errorRate());
            Log.v(TAG, "Summary: " + eval.toSummaryString());
            Log.v(TAG, "Confusion Matrix: " + Arrays.deepToString(eval.confusionMatrix()));
            TextView resultArea = findViewById(R.id.generated_accuracies);
            resultArea.setMovementMethod(new ScrollingMovementMethod());
            resultArea.setText("\n\n---Generated Results---\n\nAlgorithm: " + algorithmChosen + "\n\nTrue Positive Rate of " + label + ": " + eval.truePositiveRate(i) + "\n\nFalse Positive Rate of " + label + ": " + eval.falsePositiveRate(i) + "\n\nConfusion Matrix: \n" + Arrays.deepToString(eval.confusionMatrix()).replace("], ", "]\n").replace("[[", "[").replace("]]", "]") + "\n\nError Rate: " + eval.errorRate() + "\n\nSummary:" + eval.toSummaryString());
        } catch (Exception e) {
            e.printStackTrace();
            Log.v(TAG, "Error: " + e.getMessage());
        }
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

        database.createTable(TRAIN_TABLE_NAME);
        database.createTable(TEST_TABLE_NAME);
        database.createTable(TRAIN_TABLE_NAME + "_Mean_Features");
        database.createTable(TEST_TABLE_NAME + "_Mean_Features");

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
        String tableName = (trainTableName == null) ? testTableName : trainTableName;
        long currentTime = System.currentTimeMillis();
        if (captureSensorReading) {
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
        if (mergeSensorReadings) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                accl_x_value = event.values[0];
                accl_y_value = event.values[1];
                accl_z_value = event.values[2];
            }
            if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                gyro_x_value = event.values[0];
                gyro_y_value = event.values[1];
                gyro_z_value = event.values[2];
            }
            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                mag_x_value = event.values[0];
                mag_y_value = event.values[1];
                mag_z_value = event.values[2];
            }
            if (currentTime - previousRecordedTimestamp >= 60) {
                if (accl_x_value != Float.MIN_VALUE && accl_y_value != Float.MIN_VALUE && accl_z_value != Float.MIN_VALUE && gyro_x_value != Float.MIN_VALUE && gyro_y_value != Float.MIN_VALUE && gyro_z_value != Float.MIN_VALUE && mag_x_value != Float.MIN_VALUE && mag_y_value != Float.MIN_VALUE && mag_z_value != Float.MIN_VALUE) {
                    if (doTrain) {
                        database.insert(TRAIN_TABLE_NAME, accl_x_value, accl_y_value, accl_z_value, gyro_x_value, gyro_y_value, gyro_z_value, mag_x_value, mag_y_value, mag_z_value, class_label);
                        previousRecordedTimestamp = currentTime;
                        accl_x_value = Float.MIN_VALUE;
                        accl_y_value = Float.MIN_VALUE;
                        accl_z_value = Float.MIN_VALUE;
                        gyro_x_value = Float.MIN_VALUE;
                        gyro_y_value = Float.MIN_VALUE;
                        gyro_z_value = Float.MIN_VALUE;
                        mag_x_value = Float.MIN_VALUE;
                        mag_y_value = Float.MIN_VALUE;
                        mag_z_value = Float.MIN_VALUE;
                    } else {
                        database.insert(TEST_TABLE_NAME, accl_x_value, accl_y_value, accl_z_value, gyro_x_value, gyro_y_value, gyro_z_value, mag_x_value, mag_y_value, mag_z_value, class_label);
                        previousRecordedTimestamp = currentTime;
                        accl_x_value = Float.MIN_VALUE;
                        accl_y_value = Float.MIN_VALUE;
                        accl_z_value = Float.MIN_VALUE;
                        gyro_x_value = Float.MIN_VALUE;
                        gyro_y_value = Float.MIN_VALUE;
                        gyro_z_value = Float.MIN_VALUE;
                        mag_x_value = Float.MIN_VALUE;
                        mag_y_value = Float.MIN_VALUE;
                        mag_z_value = Float.MIN_VALUE;
                    }
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
