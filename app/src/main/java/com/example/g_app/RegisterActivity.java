package com.example.g_app;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class    RegisterActivity extends AppCompatActivity {
    EditText userEmail, userPassword, firstName, lastName, userAge;
    FirebaseAuth auth;
    String email, password, first, last, age;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userEmail = (EditText)findViewById(R.id.editText);
        userPassword = (EditText)findViewById(R.id.editText2);
        firstName = (EditText)findViewById(R.id.editText4);
        lastName = (EditText) findViewById(R.id.editText5);
        userAge = (EditText) findViewById(R.id.editText6);


        auth = FirebaseAuth.getInstance();
        if(auth.equals(null)){
            Log.i("auth", "onCreate: auth is null");
        }

    }
    public void createUser(View v){
        if(userEmail.getText().toString().equals("") && userPassword.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(),"no blanks",Toast.LENGTH_SHORT).show();
        }
        else{
            email = userEmail.getText().toString();
            password = userPassword.getText().toString();
            first = firstName.getText().toString();
            last = lastName.getText().toString();
            age = userAge.getText().toString();


            auth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getApplicationContext(),"registered",Toast.LENGTH_SHORT).show();
                                String userID= auth.getCurrentUser().getUid();
                                DatabaseReference current_user_db= FirebaseDatabase.getInstance().getReference().child("Users").child(userID);

                                Map userData = new HashMap();
                                userData.put("firstName", first);
                                userData.put("lastName",last);
                                userData.put("age", age);

                                current_user_db.setValue(userData);

                                Log.i("database", current_user_db.getKey());
                                finish();
                                Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
                                startActivity(i);
                                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);

                            }
                            else{
                                Toast.makeText(getApplicationContext(),"not registered",Toast.LENGTH_SHORT).show();

                            }

                        }
                    });
        }

    }
    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }
}
