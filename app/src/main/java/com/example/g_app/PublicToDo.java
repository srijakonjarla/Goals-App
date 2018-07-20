package com.example.g_app;

// Code from ListWithJSON example from Dustin

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import org.w3c.dom.Text;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.view.View;
import android.content.Intent;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PublicToDo extends AppCompatActivity {

    private static final String TAG = "JSON_LIST";
    public static Map events = null;
    public static String[] todoIds;
    public static String[] todoID;
    public static  ArrayList<ListData> aList;
    public static ListView list;
    FirebaseAuth auth;
    public static TextView text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_todo);


    }
    protected void onResume() {
        super.onResume();
        list = (ListView) findViewById(R.id.data_list_view);
        text = (TextView) findViewById(R.id.text);
        text.setVisibility(View.INVISIBLE);

        auth = FirebaseAuth.getInstance();
        final String userID = getIntent().getStringExtra("FriendId");
        DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
        Log.i("in on resume", " //");


        current_user_db.child("ToDo List").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                events = (Map)snapshot.getValue();
                aList = new ArrayList<ListData>();

                if (events == null || events.isEmpty()) {
                    text.setVisibility(View.VISIBLE);
                    return;
                } else {
                    text.setVisibility(View.INVISIBLE);
                }

                String[] listItems = new String[events.size()];
                todoIds = new String[events.size()];
                todoID = new String[events.size()];

                int i = 0;

                for (Object key : events.keySet()) {

                    ListData ld = new ListData();
                    Map eventData = (Map) events.get(key);

                    listItems[i] = (String)eventData.get("first") + "\n"+ (String)eventData.get("date");
                    todoID[i] = (String)key;

                    ld.firstText = (String) eventData.get("first");
                    ld.secondText = (String) eventData.get("second");
                    ld.latitude = (String) eventData.get("latitude");
                    ld.longitude = (String) eventData.get("longitude");
                    ld.time = (String) eventData.get("time");
                    ld.date = (String) eventData.get("date");

                    todoIds[i] = (String)key;

                    aList.add(ld);
                    i++;
                }

                // Show the list view with the each list item an element from listItems
                ArrayAdapter adapter = new ArrayAdapter(PublicToDo.this, android.R.layout.simple_list_item_1, listItems);
                list.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });



        // Set an OnItemClickListener for each of the list items
        final Context context = this;
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ListData selected = aList.get(position);

                // Create an Intent to reference our new activity, then call startActivity
                // to transition into the new Activity.
                Intent detailIntent = new Intent(context, PublicDetailActivity.class);

                // pass some key value pairs to the next Activity (via the Intent)
                detailIntent.putExtra("first", selected.firstText);
                detailIntent.putExtra("second", selected.secondText);
                detailIntent.putExtra("latitude", selected.latitude);
                detailIntent.putExtra("longitude", selected.longitude);
                detailIntent.putExtra("time", selected.time);
                detailIntent.putExtra("date", selected.date);
                detailIntent.putExtra("todoId", todoIds[position]);
                detailIntent.putExtra("todoID", todoID[position]);
                detailIntent.putExtra("friendId", getIntent().getStringExtra("FriendId"));
                detailIntent.putExtra("friendName", getIntent().getStringExtra("FriendName"));

                startActivity(detailIntent);
            }

        });

        Button delete = (Button)findViewById(R.id.deleteFriend);
        delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String userID = auth.getCurrentUser().getUid();
                final DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);

                current_user_db.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        Map userData = (Map) snapshot.getValue();
                        if (userData.containsKey("Friends")) {
                            Map friendsList = (Map) userData.get("Friends");
                            friendsList.remove(getIntent().getStringExtra("FriendId"));
                            userData.put("Friends", friendsList);
                            current_user_db.setValue(userData);
                        } else{
                            ///
                        }

                        Toast.makeText(getApplicationContext(), getIntent().getStringExtra("FriendName") + " deleted as a friend.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                Intent i = new Intent(context, MyFriends.class);
                startActivity(i);

            }
        });
    }

    // This method will just show the menu item (which is our button "ADD")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        TextView empty = (TextView)findViewById(R.id.text);
        // the menu being referenced here is the menu.xml from res/menu/menu.xml
        inflater.inflate(R.menu.menu, menu);
        if (events == null){
            empty.setVisibility(View.VISIBLE);
        } else if (events.size() == 0){
            empty.setVisibility(View.VISIBLE);
        } else {

        }
        return super.onCreateOptionsMenu(menu);
    }

    /* Here is the event handler for the menu button that I forgot in class.
    The value returned by item.getItemID() is
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, String.format("" + item.getItemId()));
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_favorite:
                Intent i = new Intent(this, AddText.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                break;
            case R.id.my_profile:
                Intent j = new Intent(this, ProfileActivity.class);
                startActivity(j);
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                break;
            default:
                super.onOptionsItemSelected(item);
        }
        return true;
    }
    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }
}

