package com.jp.wear.phone.mywear;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
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
import java.util.Random;
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
    String datapath = "/data_path";
    float hr = 80;
    float temp = 70;
    private int count = 0;
    private TextView txtHr,txtTemp,nivelO, calorias, numP, disR;
    Sensor mHeartR, mTemp;
    SensorManager sensorManager;
    private final Handler handler = new Handler();
    private final int TIEMPO = 180000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        enviarSignos();
        txtHr = findViewById(R.id.txtHr);
        txtTemp = findViewById(R.id.txtTemp);

        nivelO = findViewById(R.id.nivelO);
        calorias = findViewById(R.id.calorias);
        numP = findViewById(R.id.numP);
        disR = findViewById(R.id.disR);

        /**
         * Inicializar Sensores
         */
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
    }

    /**
     * Metodo para ejecutar un AsyncTask
     */
    public void enviarSignos(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                /*Metodos a ejecutar*/
                sendOxigeno();
                sendPasos();
                sendCalorias();
                sendDistancia();
                sendHR(hr);
                sendTemp(temp);
                handler.postDelayed(this, TIEMPO);
            }
        }, TIEMPO);
    }

    @Override
    public void onResume() {
        super.onResume();
        Wearable.getDataClient(this).addListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        Wearable.getDataClient(this).removeListener(this);
    }

    /**
     * Metodo para recibir informacion por medio de la Api DataLayer Google.
     * https://developer.android.com/training/wearables
     */
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

                } else if (path.equals("/temp")){

                } else if (path.equals("/oxigeno")){

                } else if (path.equals("/pasos")){

                } else if (path.equals("/calorias")){

                } else if (path.equals("/distancia")){

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
     * Metodos para generar numeros int y float, el valor float se puede formatear para quitar 0's.
     */
    private int getRandomNumber(int min,int max) {
        return (new Random()).nextInt(max - min) + min;
    }

    public static float randFloat(float min, float max) {
        Random rand = new Random();
        float r = rand.nextFloat() * (max - min) + min;
        float format = formatearDecimales(r, 2);
        return format;
    }

    public static float formatearDecimales(float numero, Integer numeroDecimales) {
        return (float) (Math.round(numero * Math.pow(10, numeroDecimales)) / Math.pow(10, numeroDecimales));
    }

    /**
     * Metodo para enviar los distintos parametros, algunos valores son datos generados aleatoriamente.
     */
    private void sendOxigeno(){
        int oxigeno = getRandomNumber(55, 110);
        nivelO.setText(""+oxigeno);
        PutDataMapRequest dataMap = PutDataMapRequest.create("/oxigeno");
        dataMap.getDataMap().putInt("oxigeno", oxigeno);
        PutDataRequest request = dataMap.asPutDataRequest();
        request.setUrgent();
        Task<DataItem> dataItemTask = Wearable.getDataClient(this).putDataItem(request);
        dataItemTask
                .addOnSuccessListener(new OnSuccessListener<DataItem>() {
                    @Override
                    public void onSuccess(DataItem dataItem) {
                        Log.d("Oxigeno", "Dato enviado");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.e(TAG, "Sending message failed: " + e);
                    }
                });
    }

    private void sendPasos(){
        int pasos = getRandomNumber(0, 1500);
        numP.setText(""+pasos);
        PutDataMapRequest dataMap = PutDataMapRequest.create("/pasos");
        dataMap.getDataMap().putInt("pasos", pasos);
        PutDataRequest request = dataMap.asPutDataRequest();
        request.setUrgent();
        Task<DataItem> dataItemTask = Wearable.getDataClient(this).putDataItem(request);
        dataItemTask
                .addOnSuccessListener(new OnSuccessListener<DataItem>() {
                    @Override
                    public void onSuccess(DataItem dataItem) {
                        Log.d("Pasos", "Dato enviado");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.e(TAG, "Sending message failed: " + e);
                    }
                });
    }

    private void sendCalorias(){
        float mcalorias = randFloat(0, 1500);
        String c = Float.toString(mcalorias);
        calorias.setText(""+c);
        PutDataMapRequest dataMap = PutDataMapRequest.create("/calorias");
        dataMap.getDataMap().putFloat("calorias", mcalorias);
        PutDataRequest request = dataMap.asPutDataRequest();
        request.setUrgent();
        Task<DataItem> dataItemTask = Wearable.getDataClient(this).putDataItem(request);
        dataItemTask
                .addOnSuccessListener(new OnSuccessListener<DataItem>() {
                    @Override
                    public void onSuccess(DataItem dataItem) {
                        Log.d("Calorias", "Dato enviado");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.e(TAG, "Sending message failed: " + e);
                    }
                });
    }

    private void sendDistancia(){
        float distancia = randFloat(0, 20);
        String d = Float.toString(distancia);
        disR.setText(""+d);
        PutDataMapRequest dataMap = PutDataMapRequest.create("/distancia");
        dataMap.getDataMap().putFloat("distancia", distancia);
        PutDataRequest request = dataMap.asPutDataRequest();
        request.setUrgent();
        Task<DataItem> dataItemTask = Wearable.getDataClient(this).putDataItem(request);
        dataItemTask
                .addOnSuccessListener(new OnSuccessListener<DataItem>() {
                    @Override
                    public void onSuccess(DataItem dataItem) {
                        Log.d("Calorias", "Dato enviado");
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
                        //Log.d("temperatura", "Dato enviado");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Sending message failed: " + e);
                    }
                });
    }

    private void sendHR(float hr ){
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

    /**
     * Metodos para obtener los valores de sensores.
     */
    private SensorEventListener mSensorEventListener = new SensorEventListener() {
        /**
         * Se utiliza para informar un nuevo valor.
         */
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            Sensor sensor = sensorEvent.sensor;
            if (sensor.getType() == Sensor.TYPE_HEART_RATE){
                hr = sensorEvent.values[0];
                txtHr.setText("HR: " + hr);
                sendHR(hr);

            } else if (sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE){
                temp = sensorEvent.values[0];
                txtTemp.setText("Temp: "+ temp);
                sendTemp(temp);
            }

        }
        /**
         * Se puede utilizar cuando la exactitud del sensor cambia.
         */
        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };


}

