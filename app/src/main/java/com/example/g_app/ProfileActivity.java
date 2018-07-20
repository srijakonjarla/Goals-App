package com.example.g_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;
    TextView emailText;
    TextView nameText;

    TextView ageText;
    private static final String TAG = "PROFILE";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        emailText = (TextView)findViewById(R.id.textView4);
        nameText = (TextView)findViewById(R.id.textView5);
        ageText = (TextView)findViewById(R.id.textView7);

        String userID= auth.getCurrentUser().getUid();
        DatabaseReference current_user_db= FirebaseDatabase.getInstance().getReference().child("Users");

        current_user_db.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Map userData = (Map)snapshot.getValue();
                nameText.setText("Welcome, "+userData.get("firstName").toString() + " " + userData.get("lastName").toString());
                ageText.setText(userData.get("age").toString());
                Log.i("ageText", userData.get("age").toString());


            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        emailText.setText(user.getEmail());



    }
    public void signOut(View v)
    {
        auth.signOut();
        finish();
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);

    }

    public void openTodo(View v){
        Intent i = new Intent(this, ToDo.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
    }

    public void addFriends(View v){
        Intent i = new Intent(this, AddFriends.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
    }

    public void myFriends(View v){
        Intent i = new Intent(this, MyFriends.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
    }



    // This method will just show the menu item (which is our button "ADD")
    @Override
    public boolean onCreateOptionsMenu(Menu menu2) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        TextView empty = (TextView)findViewById(R.id.text);
        // the menu being referenced here is the menu.xml from res/menu/menu.xml
        inflater.inflate(R.menu.menu2, menu2);
        /*if (events == null){
            empty.setVisibility(View.VISIBLE);
        } else if (events.size() == 0){
            empty.setVisibility(View.VISIBLE);
        } else {

        }*/
        return super.onCreateOptionsMenu(menu2);
    }

    /* Here is the event handler for the menu button that I forgot in class.
    The value returned by item.getItemID() is
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, String.format("" + item.getItemId()));
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.add_friend:
                Intent i = new Intent(this, AddFriends.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                break;
            case R.id.view_friend:
                Intent j = new Intent(this, MyFriends.class);
                startActivity(j);
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                break;
            case R.id.to_do:
                Intent k = new Intent(this, ToDo.class);
                startActivity(k);
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
