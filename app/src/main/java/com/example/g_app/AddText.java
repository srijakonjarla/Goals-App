package com.example.g_app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.widget.EditText;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.IOException;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.text.DateFormat;
import java.util.Date;
import java.util.Calendar;
import java.util.Map;


import android.view.View;
import android.content.Intent;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static java.text.DateFormat.*;

// Started with code from ListWithJSON from Dustin

public class AddText extends AppCompatActivity {

    public static JSONObject jos = null;
    public static JSONArray ja = null;
    private final String TAG = "TESTGPS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // This statement requests permission to the user.
        // If permissions are not set in the Manifest file, then access
        // will automatically be denied. Once the user chooses an option,
        // onRequestPermissionsResult is called.
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                99);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_text);

        final EditText first = (EditText) findViewById(R.id.editText);
        final EditText second = (EditText) findViewById(R.id.editText2);

        Button b = (Button) findViewById(R.id.button);


        b.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                String firstText = first.getText().toString();
                String secondText = second.getText().toString();
                String longitude = "";
                String latitude = "";
                String time = getTimeInstance().format(new Date());

                Calendar calendar = Calendar.getInstance();
                String date = DateFormat.getDateInstance().format(calendar.getTime());

                //JSONObject temp = new JSONObject();

                // A reference to the location manager. The LocationManager has already
                // been set up in MyService, we're just getting a reference here.
                LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                List<String> providers = lm.getProviders(true);
                Location l;
                // Go through the location providers starting with GPS, stop as soon
                // as we find one.
                for (int k=providers.size()-1; k >= 0; k--) {
                    l = lm.getLastKnownLocation(providers.get(k));
                    longitude = Double.toString(l.getLongitude());
                    latitude = Double.toString(l.getLatitude());
                    if (l != null) break;
                }

                /*Map<String, Map> map = new HashMap<String, Map>();
                Map<String, String> map2 = new HashMap<String, String>();
                map.put("EventTitle", map2);
                map2.put("type", "data");

                DatabaseReference newRef = current_user_db.child("ToDo List");
                newRef.setValue(map);*/

                FirebaseAuth auth = FirebaseAuth.getInstance();
                String userID = auth.getCurrentUser().getUid();
                DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
                DatabaseReference todoRef = current_user_db.child("ToDo List");



                Map eventData = new HashMap();

                eventData.put("first", firstText);
                eventData.put("second", secondText);
                eventData.put("latitude", latitude);
                eventData.put("longitude", longitude);
                eventData.put("time", time);
                eventData.put("date", date);



                DatabaseReference fin = todoRef.push();
                fin.setValue(eventData);

                //pop the activity off the stack
                Intent i = new Intent(AddText.this, ToDo.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
            }
        });
    }

    // This class implements OnRequestPermissionsResultCallback, so when the
    // user is prompted for location permission, the below method is called
    // as soon as the user chooses an option.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.d(TAG, "callback");
        switch (requestCode) {
            case 99:
                // If the permissions aren't set, then return. Otherwise, proceed.
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}
                                , 10);
                    }
                    Log.d(TAG, "returning program");
                    return;
                }
                else{
                    // Create Intent to reference MyService, start the Service.
                    Log.d(TAG, "starting service");
                    Intent i = new Intent(this, MyService.class);
                    if(i==null)
                        Log.d(TAG, "intent null");
                    else{
                        startService(i);
                    }

                }
                break;
            default:
                break;
        }
    }
    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }
}
