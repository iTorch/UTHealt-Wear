package com.jp.wear.phone.mywear;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

public class MainActivity extends Activity
        implements DataClient.OnDataChangedListener,
                /*MessageClient.OnMessageReceivedListener,
                CapabilityClient.OnCapabilityChangedListener,*/
                View.OnClickListener{

    String datapath = "/data_path";
    Button mybutton, logout;
    TextView logger, txtHr, txtTemp;
    String TAG = "Mobile MainActivity";
    int num = 1;
    String m = "H";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //get the widgets
        //mybutton = findViewById(R.id.btnSend);
        //mybutton.setOnClickListener(this);
        //logger = findViewById(R.id.logger);
        txtHr = findViewById(R.id.txtHr);
        txtTemp = findViewById(R.id.txtTemp);
        //Button LogOut
        logout = findViewById(R.id.logout);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent(MainActivity.this,LoginActivity.class));
                finish();
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
        String message = "Hello wearable " + num;
        //Requires a new thread to avoid blocking the UI
        sendData(message);
        num++;
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
                } else if (item.getUri().getPath().compareTo("/array") == 0){
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    recibeArray(dataMap.getFloatArray("key.array"));
                } else if(item.getUri().getPath().compareTo("/temp") == 0){
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    recibeTemperatura(dataMap.getFloat("temp"));
                } else if (item.getUri().getPath().compareTo("/hr") == 0){
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    recibeHeart(dataMap.getFloat("hr"));
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
        String t = Float.toString(temp);
        txtTemp.setText(t);

    }
    private void recibeHeart(float hr){
        String h = Float.toString(hr);
        txtHr.setText(h);

    }
    private void recibeArray(float[] floatArray) {
        Log.d(TAG, "recibeArray: " + floatArray);
    }

    private void updateCount(int c) {
        Log.d(TAG, "updateCount: " + c);
    }


    /**
     * Sends the data.  Since it specify a client, everyone who is listening to the path, will
     * get the data.
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