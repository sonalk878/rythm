package edu.gmu.cs477.fall2020.rythm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {
    ImageButton cameraButton;
    static final int CAMERA_REQUEST_CODE = 1;
    private Uri imageUri;
    AlphaAnimation buttonClick;
    private StorageReference storageRef;//need to take our user authentication to store?
    private DatabaseReference databaseRef;
    Bitmap photo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cameraButton = findViewById(R.id.imageButton3);
        storageRef = FirebaseStorage.getInstance().getReference("uploads");//will pass the image to a folder called uploads in firebase
        //databaseRef = FirebaseDatabase.getInstance().getReference("uploads");

        buttonClick = new AlphaAnimation(1F, 0.8F);//adds a mini animation when the button is clicked
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, CAMERA_REQUEST_CODE);
                }
            }
        });
    }
    //after the image is clocked
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST_CODE && resultCode ==RESULT_OK) {

            photo = (Bitmap) data.getExtras().get("data");
            submit();
        }
    }
    //this should add image to the firebase hopefully
    public void submit(){

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);

        byte[] b = stream.toByteArray();
      //  StorageReference storageReference =FirebaseStorage.getInstance().getReference());
        //StorageReference filePath = FirebaseStorage.getInstance().getReference().child("profile_images").child(userID);
        storageRef.putBytes(b).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String photoLink = uri.toString();
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getBaseContext(),"failed",Toast.LENGTH_LONG).show();


            }
        });

    }

}
