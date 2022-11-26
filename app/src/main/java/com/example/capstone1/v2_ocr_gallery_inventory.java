package com.example.capstone1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.capstone1.v2.SharedPref;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.TextRecognizerOptions;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Locale;

public class v2_ocr_gallery_inventory extends AppCompatActivity {
    Button captureImage, saveText;
    ImageView viewImage;
    Uri imageUri;
    TextToSpeech textToSpeech;
    String text;
    int ocrChoice;
    int REQUEST_IMAGE_CAPTURE = 1;
    int REQUEST_IMAGE_COUNT = 3;
    String storagePermission[];
    private static final int STORAGE_REQUEST = 200;
    TextView displayText;

    SharedPref sf;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_optical_character_recognition_one);

        sf = new SharedPref(getApplicationContext());

        captureImage = (Button) findViewById(R.id.captureCount);
        saveText = (Button) findViewById(R.id.pasteCount);


        captureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                String fileName = "photo";
                File storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                try {
                    File image_file = File.createTempFile(fileName, ".jpg", storageDirectory);
                    //currentPhotoPath = image_file.getAbsolutePath();
                    ContentValues values = new ContentValues();
                    //imageUri = FileProvider.getUriForFile(MainActivity.this, "com.example.android.fileprovider", image_file );
                    imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                 */

                if (!checkStoragePermission()) {
                    requestStoragePermission();
                } else {
                    pickFromGallery();
                }
            }
        });


        ocrChoice = getIntent().getIntExtra("ocrchoice", 0);
        Log.d("Tag", "henlo"+ ocrChoice);

        saveText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String numberOnly = displayText.getText().toString().replaceAll("[^0-9]", "");
                if (ocrChoice == 1)
                {
                    try {
                        new_medications.inventory.setText(numberOnly);
                        finish();
                    }catch (Exception e)
                    {
                        Toast.makeText(v2_ocr_gallery_inventory.this, "Scan your medicine inventory", Toast.LENGTH_SHORT).show();

                    }

                }
                else if (ocrChoice == 2)
                {
                    try{
                        edit_delete_medications.medName.setText(displayText.getText().toString());
                        finish();
                    }catch (Exception e)
                    {
                        Toast.makeText(v2_ocr_gallery_inventory.this, "Scan your medicine inventory", Toast.LENGTH_SHORT).show();

                    }

                }
            }
        });



    }

    private Boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    // Requesting  gallery permission
    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(storagePermission, STORAGE_REQUEST);
        }
    }
    private void pickFromGallery() {
        CropImage.activity().start(v2_ocr_gallery_inventory.this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int rotationDegree = 0;
        displayText = (TextView) findViewById(R.id.medCountTV);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON).start(this);

            }
            if (requestCode == 2) {
                requestCode = REQUEST_IMAGE_COUNT;
            }

            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);

                {
                    Uri resultUri = result.getUri();
                    ImageView imageView = findViewById(R.id.imageMedsCount);
                    imageView.setImageURI(resultUri);
                    BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
                    Bitmap bitmap_orig = drawable.getBitmap();

                    Bitmap bitmap = enhanceImageDPI(bitmap_orig, 300);


                    //create image object to be read by visionmlkit
                    InputImage image = InputImage.fromBitmap(bitmap, rotationDegree);
                    //instance of text recognizer
                    TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
                    //task that processes the image
                    Task<Text> resultText = recognizer.process(image).addOnSuccessListener(new OnSuccessListener<Text>() {
                        @Override
                        public void onSuccess(Text visionText) {
                            String a = visionText.getText();
                            displayText.setText(a);
                            String b = a.toLowerCase();
                        }
                    }).addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                }
            }
        }

    }


    public void ocr_To_med(View view) {
        Intent intent = new Intent(v2_ocr_gallery_inventory.this, new_medications.class);
        startActivity(intent);
    }



    public Bitmap enhanceImageDPI(Bitmap image, int dpi) {
        ByteArrayOutputStream imageByteArray = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, imageByteArray);
        byte[] imageData = imageByteArray.toByteArray();
        setDpi(imageData, 300);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
        return bitmap;
    }

    public void setDpi(byte[] imageData, int dpi) {
        imageData[13] = 1;
        imageData[14] = (byte) (dpi >> 8);
        imageData[15] = (byte) (dpi & 0xff);
        imageData[16] = (byte) (dpi >> 8);
        imageData[17] = (byte) (dpi & 0xff);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case STORAGE_REQUEST: {
                if (grantResults.length > 0) {
                    boolean writeStorageaccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageaccepted) {
                        pickFromGallery();
                    } else {
                        Toast.makeText(this, "Please Enable Storage Permissions", Toast.LENGTH_LONG).show();
                    }
                }
            }
            break;
        }
    }

}