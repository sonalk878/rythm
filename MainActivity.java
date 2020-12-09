package edu.gmu.cs477.fall2020.rythm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
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
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    ImageButton cameraButton;
    ImageButton galleryButton;
    static final int CAMERA_REQUEST_CODE = 1;
    private Uri imageUri;
    AlphaAnimation buttonClick;
    private StorageReference storageReference ;//need to take our user authentication to store?
    private DatabaseReference databaseRef;
    Bitmap photo;
    ImageView selectedImage;
    ListView list;
    String currentPhotoPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cameraButton = findViewById(R.id.imageButton3);
        storageReference = FirebaseStorage.getInstance().getReference();//will pass the image to a folder called uploads in firebase
        //databaseRef = FirebaseDatabase.getInstance().getReference("uploads");
        selectedImage = findViewById(R.id.imageView);
        galleryButton = findViewById(R.id.imageButton2);
        buttonClick = new AlphaAnimation(1F, 0.8F);//adds a mini animation when the button is clicked
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);
                dispatchTakePictureIntent();
                /*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, CAMERA_REQUEST_CODE);
                }*/
            }
        });
    }
    //after the image is clicked
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST_CODE && resultCode ==RESULT_OK) {
            /*Bitmap image = (Bitmap)data.getExtras().get("data");
            selectedImage.setImageBitmap(image);//to capture image and set image as background for imageview*/
            File f  = new File(currentPhotoPath);
            //selectedImage.setImageURI(Uri.fromFile(f));

            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(f);
            mediaScanIntent.setData(contentUri);
            this.sendBroadcast(mediaScanIntent);
            
            uploadImageToFirebase(f.getName(),contentUri);

        }
    }
    //to upload an image to the firebase folder images
    private void uploadImageToFirebase(String name, Uri contentUri) {
        final StorageReference image = storageReference.child("images/"+name);//adds the image to the "images" folder in firebase
        image.putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                    }
                });
                Toast.makeText(getBaseContext(), "IMAGE UPLOADED SUCCESSFULLY", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getBaseContext(), "image upload unsuccessful", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());//uses the timestamp as the name
        String imageFileName = "JPEG_" + timeStamp + "_";
       File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
       // File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
    //this should add image to the firebase hopefully
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {//checks if device is ready to give camera resources
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }

}
