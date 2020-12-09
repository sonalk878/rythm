package edu.gmu.cs477.fall2020.rythm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;

public class addAnnouncement extends AppCompatActivity {
    EditText announcement;
    Button add;
    Button cancel;
    private StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_announcement);
        announcement = findViewById(R.id.announcement);
        add = findViewById(R.id.button);
        FirebaseStorage storage = FirebaseStorage.getInstance();

        storageReference = FirebaseStorage.getInstance().getReference().child("Announcements");
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String complete = announcement.getText().toString();
                addToFirebase(complete);
                finish();
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        cancel = findViewById(R.id.button2);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void addToFirebase(String complete) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());//uses the timestamp as the name
        String textFileName = "TXT_" + timeStamp + "_";
        //gets bytes of string and adds to file
        storageReference.child(textFileName).putBytes(complete.getBytes()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getBaseContext(), "Data successfully inserted", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getBaseContext(), "FILE UPLOAD UNSUCCESSFUL", Toast.LENGTH_SHORT).show();

            }
        });
    }
}
