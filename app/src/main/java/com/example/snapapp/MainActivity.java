package com.example.snapapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;



public class MainActivity extends AppCompatActivity {
    Button browseButton, uploadButton;
    EditText textData;
    ImageView imageView;
    Uri pathUri;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    int ImageRequestCode = 7;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        storageReference = FirebaseStorage.getInstance().getReference("Images");
        databaseReference = FirebaseDatabase.getInstance().getReference("Images");
        browseButton = findViewById(R.id.browseButton);
        uploadButton = findViewById(R.id.uploadButton);
        textData = findViewById(R.id.textData);
        imageView = findViewById(R.id.imageView);
        progressDialog = new ProgressDialog(this);

        browseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), ImageRequestCode);
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ImageRequestCode && resultCode == RESULT_OK && data != null & data.getData() != null) {
            pathUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), pathUri);
                imageView.setImageBitmap(bitmap);
            }catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String getExtention(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mtm = MimeTypeMap.getSingleton();
        return mtm.getExtensionFromMimeType(cr.getType(uri));
    }


    public void uploadImage() {
        if (pathUri != null) {
            progressDialog.setTitle("uploading..");
            progressDialog.show();
            StorageReference sr = storageReference.child(System.currentTimeMillis() + "." + getExtention(pathUri));
            sr.putFile(pathUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String imgName = textData.getText().toString().trim();
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Upload Successful", Toast.LENGTH_LONG).show();

                    ImageUploadInfo imageUploadInfo = new ImageUploadInfo(imgName, taskSnapshot.getUploadSessionUri().toString());
                    String uploadedImgId = databaseReference.push().getKey();
                    databaseReference.child(uploadedImgId).setValue(imageUploadInfo);
                }
            });
        }else {
            Toast.makeText(this, "Select Image", Toast.LENGTH_LONG).show();
        }
    }
}