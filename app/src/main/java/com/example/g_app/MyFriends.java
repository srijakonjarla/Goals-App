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

public class MyFriends extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;
    Map friends;
    String[] listItems;
    TextView text;
    ListView list;
    String[] userIds;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_friends);
        text = (TextView) findViewById(R.id.text);
        list = (ListView) findViewById(R.id.data_list_view);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();


        String userID = auth.getCurrentUser().getUid();
        DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);

        current_user_db.child("Friends").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                friends = (Map) snapshot.getValue();

                if (friends == null || friends.isEmpty()) {
                    text.setVisibility(View.VISIBLE);
                    return;
                } else {
                    text.setVisibility(View.INVISIBLE);
                }

                listItems = new String[friends.size()];
                userIds = new String[friends.size()];

                int i = 0;

                for (Object key : friends.keySet()) {


                    String name = (String) friends.get(key);

                    listItems[i] = name;
                    userIds[i] = (String)key;

                    i++;
                }

                ArrayAdapter adapter = new ArrayAdapter(MyFriends.this, android.R.layout.simple_list_item_1, listItems);
                list.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        final Context context = this;
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                Intent i = new Intent(context, PublicToDo.class);
                i.putExtra("FriendId", userIds[position]);
                i.putExtra("FriendName", listItems[position]);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);

            }

        });
    }
    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }
}