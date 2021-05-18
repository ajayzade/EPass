package com.rawtalent.epass_user.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.rawtalent.epass_user.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

public class CreateEPass extends AppCompatActivity {


    EditText source, destination, months;
    ImageView imageView;

    private byte[] byteArray;
    @Nullable
    private Uri mImageHolder;

    Button selectImage, submit;

    private FirebaseFirestore mFirebaseDb;
    private StorageReference mStorageReference;

    AlertDialog.Builder builder;
    AlertDialog progressDialog;

    private static final Long THIRTY_DAY = 2592000000L;
    public static final String TIME_SERVER = "time-a.nist.gov";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_e_pass);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        progressDialog = getBuilder().create();
        progressDialog.setCancelable(false);

        source = findViewById(R.id.source_location);
        destination = findViewById(R.id.destination_location);
        months = findViewById(R.id.months);

        submit = findViewById(R.id.submit);
        selectImage = findViewById(R.id.selectpaymentproof);

        imageView = findViewById(R.id.paymentImage);

        mFirebaseDb = FirebaseFirestore.getInstance();
        mStorageReference = FirebaseStorage.getInstance().getReference();

        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkDetails();
            }
        });


    }

    public void checkDetails() {

        String sourceName = source.getText().toString();
        String destinationName = destination.getText().toString();
        String numberOfMonths = months.getText().toString();


        if (sourceName.equals("") || sourceName.isEmpty()) {
            source.setError("this field is missing");
            Toast.makeText(this, "Please enter all the details", Toast.LENGTH_SHORT).show();
            return;
        }
        if (destinationName.equals("") || destinationName.isEmpty()) {
            destination.setError("this field is missing");
            Toast.makeText(this, "Please enter all the details", Toast.LENGTH_SHORT).show();
            return;
        }
        if (numberOfMonths.equals("") || numberOfMonths.isEmpty()) {
            months.setError("this field is missing");
            Toast.makeText(this, "Please enter all the details", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mImageHolder == null) {
            Toast.makeText(this, "Please select Payment Proof Image/Screenshot", Toast.LENGTH_LONG).show();
            return;
        }

        long currentDate = getCurrentTime();
        long expiryDate = currentDate + Integer.parseInt(numberOfMonths) * THIRTY_DAY;

        progressDialog.show();
        uploadImage(sourceName, destinationName, numberOfMonths, currentDate, expiryDate);

    }


    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mImageHolder = data.getData();
            imageView.setVisibility(View.VISIBLE);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageHolder);
                imageView.setImageBitmap(bitmap);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//                bitmap.recycle();
                //      byteArray = stream.toByteArray();
            } catch (Exception e) {
                Toast.makeText(this, " " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void uploadImage(final String source, final String destination, final String months, final long current, final long expiry) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String uuid = mAuth.getCurrentUser().getUid();
        StorageReference reference = mStorageReference.child("PaymentProof" + "/" + uuid);
        reference.putFile(mImageHolder).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storePassDetails(source, destination, months, current, expiry);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(CreateEPass.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void storePassDetails(String source, String destination, String months, long activationDate, long expiryDate) {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        Map<String, Object> details = new HashMap<>();

        details.put("source", source);
        details.put("destination", destination);
        details.put("months", months);
        details.put("activationDate", activationDate);
        details.put("expiryDate", expiryDate);
        details.put("remark", "No Remarks");
        details.put("verificationStatus", "0");


        mFirebaseDb.collection("ePasses").document(mAuth.getCurrentUser().getUid()).set(details).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(CreateEPass.this, "Successfully Submitted!", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                CreateEPass.this.finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
            }
        });

        mFirebaseDb.collection(StudentDetails.STUDENT_COLLECTION).document(mAuth.getCurrentUser().getUid()).collection("passesHistory").document().set(details).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }


    public AlertDialog.Builder getBuilder() {
        if (builder == null) {
            builder = new AlertDialog.Builder(CreateEPass.this);
            builder.setTitle("Submitting details...");

            final ProgressBar progressBar = new ProgressBar(CreateEPass.this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            progressBar.setLayoutParams(layoutParams);
            builder.setView(progressBar);
        }
        return builder;
    }


    private Long getCurrentTime() {
        NTPUDPClient timeClient = new NTPUDPClient();
        InetAddress inetAddress = null;
        TimeInfo timeInfo = null;
        try {
            inetAddress = InetAddress.getByName(TIME_SERVER);
            timeInfo = timeClient.getTime(inetAddress);
        } catch (IOException e) {
            e.printStackTrace();
        }
        long returnTime = 0;
        if (timeInfo != null) {
            returnTime = timeInfo.getMessage().getReceiveTimeStamp().getTime();
        }
        return returnTime;
    }


}