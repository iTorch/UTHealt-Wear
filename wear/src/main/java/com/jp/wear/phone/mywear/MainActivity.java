package com.jp.wear.phone.mywear;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import com.google.android.gms.wearable.CapabilityClient;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

public class MainActivity extends Activity
        implements
        DataClient.OnDataChangedListener
        /*MessageClient.OnMessageReceivedListener,
        CapabilityClient.OnCapabilityChangedListener,
        View.OnClickListener*/ {

    private final static String TAG = "Wear MainActivity";
    private TextView mTextView;
    Button myButton, Btn_sendlist;
    int num = 1;
    String datapath = "/data_path";
    float[] signosV = new float[4];
    private int count = 0;
    private TextView txtHr,txtTemp;
    Sensor mHeartR, mTemp;
    SensorManager sensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtHr = findViewById(R.id.txtHr);
        txtTemp = findViewById(R.id.txtTemp);

        //Inicializar sensores
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mHeartR = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        if (mHeartR != null){
            sensorManager.registerListener(mSensorEventListener, mHeartR, SensorManager.SENSOR_DELAY_NORMAL);
        }
        else {
            txtHr.setText("HR not supported");
        }
        mTemp = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        if (mTemp != null){
            sensorManager.registerListener(mSensorEventListener, mTemp, SensorManager.SENSOR_DELAY_NORMAL);
        }else{
            txtTemp.setText("Temp not supported");
        }

        mTextView = findViewById(R.id.text);
        //send a message from the wear.  This one will not have response.
        //myButton = findViewById(R.id.wrbutton);
        /*myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = "Hello device " + num;
                //Requires a new thread to avoid blocking the UI
                sendData(message);
                increaseCounter();
                sendArray();
                num++;
            }
        });*/
        // Enables Always-on
        //setAmbientEnabled();


    }



    //add listener.
    @Override
    public void onResume() {
        super.onResume();
        Wearable.getDataClient(this).addListener(this);
    }

    //remove listener
    @Override
    public void onPause() {
        super.onPause();
        Wearable.getDataClient(this).removeListener(this);
    }

    //receive data from the path.
    @Override
    public void onDataChanged(@NonNull DataEventBuffer dataEventBuffer) {
        Log.d(TAG, "onDataChanged: " + dataEventBuffer);
        for (DataEvent event : dataEventBuffer) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                String path = event.getDataItem().getUri().getPath();
                if (datapath.equals(path)) {
                    DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
                    String message = dataMapItem.getDataMap().getString("message");
                    Log.v(TAG, "Wear activity received message: " + message);
                    // Display message in UI
                    mTextView.setText(message);

                } else if (path.equals("/count")){


                } else if (path.equals("/hr")){

                }
                else {
                    Log.e(TAG, "Unrecognized path: " + path);
                }
            } else if (event.getType() == DataEvent.TYPE_DELETED) {
                Log.v(TAG, "Data deleted : " + event.getDataItem().toString());
            } else {
                Log.e(TAG, "Unknown data event Type = " + event.getType());
            }
        }
    }

    /**
     * Sends the data, note this is a broadcast, so we will get the message as well.
     */
    private void sendData(String message) {
        PutDataMapRequest dataMap = PutDataMapRequest.create(datapath);
        dataMap.getDataMap().putString("message", message);
        PutDataRequest request = dataMap.asPutDataRequest();
        request.setUrgent();

        Task<DataItem> dataItemTask = Wearable.getDataClient(this).putDataItem(request);
        dataItemTask
                .addOnSuccessListener(new OnSuccessListener<DataItem>() {
                    @Override
                    public void onSuccess(DataItem dataItem) {
                        Log.d("Mensaje", "Sending message was successful: " + dataItem);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Sending message failed: " + e);
                    }
                })
        ;
    }

    private void sendHR(float hr){
        PutDataMapRequest dataMap = PutDataMapRequest.create("/hr");
        dataMap.getDataMap().putFloat("hr", hr);
        PutDataRequest request = dataMap.asPutDataRequest();
        request.setUrgent();
        Task<DataItem> dataItemTask = Wearable.getDataClient(this).putDataItem(request);
        dataItemTask
                .addOnSuccessListener(new OnSuccessListener<DataItem>() {
                    @Override
                    public void onSuccess(DataItem dataItem) {
                        Log.d("Heart rate", "Dato enviado");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.e(TAG, "Sending message failed: " + e);
                    }
                });

    }
    private void sendTemp(float temp){
        PutDataMapRequest dataMap = PutDataMapRequest.create("/temp");
        dataMap.getDataMap().putFloat("temp", temp);
        PutDataRequest request = dataMap.asPutDataRequest();
        request.setUrgent();
        Task<DataItem> dataItemTask = Wearable.getDataClient(this).putDataItem(request);
        dataItemTask
                .addOnSuccessListener(new OnSuccessListener<DataItem>() {
                    @Override
                    public void onSuccess(DataItem dataItem) {
                        Log.d("temperatura", "Dato enviado");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Sending message failed: " + e);
                    }
                });
    }

    private void increaseCounter(){
        PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/count");
        putDataMapReq.getDataMap().putInt("key.count", count++);
        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
        Task<DataItem> putDataTask = Wearable.getDataClient(this).putDataItem(putDataReq);
        putDataTask.addOnSuccessListener(new OnSuccessListener<DataItem>() {
            @Override
            public void onSuccess(DataItem dataItem) {
                Log.d("Couter", "onSuccess: " + dataItem);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
    private void sendArray(){
        signosV[0] = 10.1f;
        signosV[1] = 20.2f;
        signosV[2] = 30.3f;
        signosV[3] = 40.4f;
        PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/array");
        putDataMapReq.getDataMap().putFloatArray("key.array", signosV);
        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
        Task<DataItem> putDataTask = Wearable.getDataClient(this).putDataItem(putDataReq);
        putDataTask.addOnSuccessListener(new OnSuccessListener<DataItem>() {
            @Override
            public void onSuccess(DataItem dataItem) {
                Log.d("Array", "onSuccess: " + dataItem);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Array", "onFailure: " + e);
            }
        });
    }


    private SensorEventListener mSensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            Sensor sensor = sensorEvent.sensor;

            if (sensor.getType() == Sensor.TYPE_HEART_RATE){
                float hr = sensorEvent.values[0];
                txtHr.setText("HR: " + hr);
                sendHR(hr);

            } else if (sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE){
                float temp = sensorEvent.values[0];
                txtTemp.setText("Temp: "+ temp);
                sendTemp(temp);
            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };


}

