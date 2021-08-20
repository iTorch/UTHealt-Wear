package com.jp.wear.phone.mywear;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.CapabilityClient;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.jp.wear.phone.mywear.Body.LogoutBody;
import com.jp.wear.phone.mywear.Body.VitalSignsBody;
import com.jp.wear.phone.mywear.IO.HealtApiAdapter;
import com.jp.wear.phone.mywear.Model.Login;
import com.jp.wear.phone.mywear.Model.Logout;
import com.jp.wear.phone.mywear.Model.Persona;
import com.jp.wear.phone.mywear.Model.VitalSigns;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends Activity
        implements DataClient.OnDataChangedListener,
                /*MessageClient.OnMessageReceivedListener,
                CapabilityClient.OnCapabilityChangedListener,*/
                View.OnClickListener{

    String datapath = "/data_path";
    Button mybutton, logout;
    TextView logger, txtHr, txtTemp, DNO, DCQ, DPD, DDR;
    String TAG = "Mobile MainActivity";
    int num = 1;
    float myhr, mytemp, calorias, distancia;
    int oxigeno, pasos;
    Login login;
    Timer timer = new Timer();
    private final Handler handler = new Handler();
    private final int TIEMPO = 600000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        login = (Login) getIntent().getSerializableExtra("user");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        enviarSignos();
        //get the widgets
        //mybutton = findViewById(R.id.btnSend);
        //mybutton.setOnClickListener(this);
        //logger = findViewById(R.id.logger);
        txtHr = findViewById(R.id.txtHr);
        txtTemp = findViewById(R.id.txtTemp);
        DNO = findViewById(R.id.DNO);
        DCQ = findViewById(R.id.DCQ);
        DPD = findViewById(R.id.DPD);
        DDR = findViewById(R.id.DDR);
        //Button LogOut
        logout = findViewById(R.id.logout);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogoutBody logout = new LogoutBody();
                logout.setToken(login.getToken());

                Call<Logout> logoutCall = HealtApiAdapter.getApiService().Logout(logout);
                logoutCall.enqueue(new Callback<Logout>() {
                    @Override
                    public void onResponse(Call<Logout> call, Response<Logout> response) {
                        Logout logout = response.body();
                        if (response.body() != null){
                            if (response.isSuccessful()){
                                if (response.code() == 200){
                                    Toast.makeText(MainActivity.this,logout.getStatus(),Toast.LENGTH_LONG).show();
                                    Intent menu = new Intent(MainActivity.this, LoginActivity.class);
                                    startActivity(menu);
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Logout> call, Throwable t) {

                    }
                });
            }
        });

    }

    // add data listener
    @Override
    public void onResume() {
        super.onResume();
        Wearable.getDataClient(this).addListener(this);
    }

    //remove data listener
    @Override
    public void onPause() {
        super.onPause();
        Wearable.getDataClient(this).removeListener(this);
    }

    /**
     * simple method to add the log TextView.
     */
    public void logthis(String newinfo) {
        if (newinfo.compareTo("") != 0) {
            logger.append("\n" + newinfo);
        }
    }

    //button listener
    @Override
    public void onClick(View v) {

//        String message = "Hello wearable " + num;
//        //Requires a new thread to avoid blocking the UI
//        sendData(message);
//        num++;
        //getPesonas();
        insertVitalSigns();
    }

    private void getPesonas(){
        Call<ArrayList<Persona>> call = HealtApiAdapter.getApiService().getPersonas();
        call.enqueue(new Callback<ArrayList<Persona>>() {
            @Override
            public void onResponse(Call<ArrayList<Persona>> call, Response<ArrayList<Persona>> response) {
                if (response.isSuccessful()){
                    ArrayList<Persona> personas = response.body();
                    Log.d("OnResponse: ", "Size of personas => " + personas.size());
                }
            }
            @Override
            public void onFailure(Call<ArrayList<Persona>> call, Throwable t) {

            }
        });
    }

    private void insertVitalSigns(){
        VitalSignsBody v = new VitalSignsBody();
        v.setTemperatura(mytemp);
        v.setRitmo_cardiaco(myhr);
        v.setCalorias_quemadas(calorias);
        v.setDistancia_recorrida(distancia);
        v.setId_persona(login.getUser().getPersona().getId_persona());
        v.setOxigeno(oxigeno);
        v.setPasos_diario(pasos);
        v.all();

        Call<VitalSigns> vitalSignsCall = HealtApiAdapter.getApiService().registrarSignos(v);
        vitalSignsCall.enqueue(new Callback<VitalSigns>() {
            @Override
            public void onResponse(Call<VitalSigns> call, Response<VitalSigns> response) {
                if (response.isSuccessful()){
                    VitalSigns vitalSigns = response.body();
                    //Toast.makeText(MainActivity.this, vitalSigns.getId_persona(),Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<VitalSigns> call, Throwable t) {

            }
        });
    }

    public void enviarSignos(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                insertVitalSigns();
                handler.postDelayed(this, TIEMPO);
            }
        }, TIEMPO);
    }


    /**
     * Receives the data, note since we are using the same data base, we will also receive
     * data that we sent as well.  Likely should add some kind of id number to datamap, so it
     * can tell between mobile device and wear device.  or this maybe the functionality you want.
     */
    @Override
    public void onDataChanged(@NonNull DataEventBuffer dataEventBuffer) {
        //Log.d(TAG, "onDataChanged: " + dataEventBuffer);
        for (DataEvent event : dataEventBuffer) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                DataItem item = event.getDataItem();
                String path = event.getDataItem().getUri().getPath();
                if (datapath.equals(path)) {
                    DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
                    String message = dataMapItem.getDataMap().getString("message");
                    Log.v(TAG, "Wear activity received message: " + message);
                    // Display message in UI
                    logthis(message);
                } else if(item.getUri().getPath().compareTo("/count") == 0){
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    updateCount(dataMap.getInt("key.count"));
                } else if(item.getUri().getPath().compareTo("/temp") == 0){
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    recibeTemperatura(dataMap.getFloat("temp"));
                } else if (item.getUri().getPath().compareTo("/hr") == 0){
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    recibeHeart(dataMap.getFloat("hr"));
                } else if (item.getUri().getPath().compareTo("/oxigeno") == 0){
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    recibeOxigeno(dataMap.getInt("oxigeno"));
                } else if (item.getUri().getPath().compareTo("/calorias") == 0){
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    recibeCalorias(dataMap.getFloat("calorias"));
                } else if (item.getUri().getPath().compareTo("/pasos") == 0){
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    recibePasos(dataMap.getInt("pasos"));
                } else if (item.getUri().getPath().compareTo("/distancia") == 0){
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    recibeDistancia(dataMap.getFloat("distancia"));
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

    private void recibeTemperatura(float temp){
        mytemp = temp;
        String t = Float.toString(temp);
        txtTemp.setText(t);

    }
    private void recibeHeart(float hr){
        myhr = hr;
        String h = Float.toString(hr);
        txtHr.setText(h);
    }
    private void recibeOxigeno(int moxigeno){
        oxigeno = moxigeno;
        DNO.setText(""+oxigeno);
    }
    private void recibePasos(int mpasos){
        pasos = mpasos;
        DPD.setText(""+pasos);
    }
    private void recibeCalorias(float mcalorias){
        calorias = mcalorias;
        String c = Float.toString(calorias);
        DCQ.setText(c);
    }
    private void recibeDistancia(float mdistancia){
        distancia = mdistancia;
        String d = Float.toString(distancia);
        DDR.setText(d);
    }

    private void updateCount(int c) {
        Log.d(TAG, "updateCount: " + c);
    }



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
                        Log.d(TAG, "Sending message was successful: " + dataItem);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Sending message failed: " + e);
                    }
                });
    }
}