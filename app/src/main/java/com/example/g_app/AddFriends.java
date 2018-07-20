package com.example.g_app;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddFriends extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;
    Map users;
    String[] listItems;
    TextView text;
    ListView list;
    String[] userIds;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);
        text = (TextView) findViewById(R.id.text);
        list = (ListView) findViewById(R.id.data_list_view);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();


        userID = auth.getCurrentUser().getUid();
        DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Users");

        current_user_db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                users = (Map)snapshot.getValue();
                Log.i("map users", users.toString());

                if(users.isEmpty()){
                    text.setVisibility(View.VISIBLE);
                    return;
                }else{
                    text.setVisibility(View.INVISIBLE);
                }

                listItems = new String[users.size()-1];
                userIds = new String[users.size()-1];

                int i = 0;

                for (Object key : users.keySet()) {


                    Map friend = (Map)users.get(key);

                    if(userID.equals((String)key)){
                        continue;
                    }
                    userIds[i] = (String)key;

                    String firstName = (String)friend.get("firstName");
                    String lastName = (String)friend.get("lastName");

                    listItems[i] = firstName + " " + lastName;

                    i++;
                }

                ArrayAdapter adapter = new ArrayAdapter(AddFriends.this, android.R.layout.simple_list_item_1, listItems);
                list.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });





        // Show the list view with the each list item an element from listItems


        // Create an array and assign each element to be the title
        // field of each of the ListData objects (from the array list)
            /*String[] listItems = new String[aList.size()];

            for(int i = 0; i < aList.size(); i++){
                ListData listD = aList.get(i);
                listItems[i] = listD.firstText;
            }

            // Show the list view with the each list item an element from listItems
            ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listItems);
            list.setAdapter(adapter);*/

        // Set an OnItemClickListener for each of the list items
        final Context context = this;
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                String userID = auth.getCurrentUser().getUid();
                final DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);

                current_user_db.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        Map userData = (Map)snapshot.getValue();
                        if(userData.containsKey("Friends")){
                            Map friendsList = (Map)userData.get("Friends");
                            friendsList.put(userIds[position], listItems[position]);
                            userData.put("Friends", friendsList);
                            current_user_db.setValue(userData);
                        }else{
                            Map friendsList = new HashMap();
                            friendsList.put(userIds[position], listItems[position]);
                            userData.put("Friends", friendsList);
                            current_user_db.setValue(userData);
                        }

                        Toast.makeText(getApplicationContext(),
                                listItems[position] + " added as a friend!",Toast.LENGTH_SHORT).show();
                        return;
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

            }

        });

    }
    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }
}
