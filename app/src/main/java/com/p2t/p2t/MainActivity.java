package com.p2t.p2t;

import android.support.v7.app.AppCompatActivity;
import java.io.IOException;
import java.util.Date;
import android.os.Bundle;
import java.text.SimpleDateFormat;
import android.os.Environment;
import java.io.File;
import android.content.Intent;
import android.support.v4.content.FileProvider;
import android.provider.MediaStore;
import android.net.Uri;
import android.widget.Button;
import android.view.View;
import com.google.api.services.vision.v1.model.Image;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    static final int REQUEST_TAKE_PHOTO = 1;
    private Uri photoURI;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                // Save the URI so that it can be converted later
                photoURI = FileProvider.getUriForFile(this,
                        "com.p2t.p2t",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button button = findViewById(R.id.takePictureButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            // Now we can get the base64 image and pass it to the API
            Image convertedImage = ImageConverter.getBase64Image(getContentResolver(), photoURI);
            if(convertedImage != null) {
                Toast.makeText(this, "It's probably working!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
