package com.example.g_app;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.view.View;
import android.content.Intent;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class PublicDetailActivity extends AppCompatActivity {

    private Button btnChoose, btnUpload;
    private ImageView imageView;
    FirebaseAuth auth;

    private Uri filePath;

    private final int PICK_IMAGE_REQUEST = 71;
    FirebaseStorage storage;
    StorageReference storageReference;

    public String todoID;
    public String url;
    public static String friendId;
    public static Button verify;
    public static TextView verified;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_detail);
        verified = (TextView) findViewById(R.id.verified);
        verified.setVisibility(View.INVISIBLE);

        Intent i = getIntent();
        String title = i.getStringExtra("first");
        String description = i.getStringExtra("second");
        String latitude = i.getStringExtra("latitude");
        String longitude = i.getStringExtra("longitude");
        String time = i.getStringExtra("time");
        String date = i.getStringExtra("date");
        todoID = i.getStringExtra("todoID");
        friendId = i.getStringExtra("friendId");

        TextView t = (TextView) findViewById(R.id.textView3);
        TextView d = (TextView) findViewById(R.id.textView4);
        TextView lat = (TextView) findViewById(R.id.lat);
        //TextView longi = (TextView)findViewById(R.id.longi);
        TextView ti = (TextView) findViewById(R.id.time);
        TextView da = (TextView) findViewById(R.id.date);



        imageView = (ImageView) findViewById(R.id.imgView);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        url = "https://firebasestorage.googleapis.com/v0/b/goals-app-54494.appspot.com/o/images%2F" +
                todoID + "?alt=media&token=264b56a0-747a-4ca0-ba9d-06550559bac1";
        storageReference.child("images");
        storageReference.child("images").child(todoID).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext()).load(url).into(imageView);
            }
        });

        t.setText(title);
        d.setText(description);
        lat.setText(latitude+","+longitude);
        ti.setText(time);
        da.setText(date);

        if (url != null) {
            Glide.with(getApplicationContext()).load(url).into(imageView);
        }

        final Context context = this;
        verify = (Button)findViewById(R.id.verify);
        verify.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child(friendId).child("ToDo List").child(todoID);

                current_user_db.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        Map todoData = (Map) snapshot.getValue();
                        todoData.put("Verify", "true");
                        Log.i("verify", todoData.toString());
                        current_user_db.setValue(todoData);


                        Toast.makeText(getApplicationContext(), getIntent().getStringExtra("first") + " goal is verified!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                verify.setVisibility(View.INVISIBLE);
                verified.setVisibility(View.VISIBLE);
                Intent i = new Intent(context, PublicToDo.class);
                i.putExtra("FriendId", friendId);
                i.putExtra("FriendName", getIntent().getStringExtra("friendName"));
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }
}

