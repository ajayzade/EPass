package com.rawtalent.epass_user.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.rawtalent.epass_user.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class StudentDetails extends AppCompatActivity {

    public static final String FIRST_NAME = "firstName";
    public static final String MIDDLE_NAME = "middleName";
    public static final String LAST_NAME = "lastName";
    public static final String COLLEGE_NAME = "college";
    public static final String COLLEGE_ID = "rollNumber";
    public static final String ID_CARD_IMAGE = "idcard";
    public static final String SOURCE_LOCATION = "source";
    public static final String DESTINATION_LOCATION = "destination";
    public static final String USER_VERIFIED = "isVerified";
    public static final String USER_EMAIL = "email";
    public static final String STUDENT_COLLECTION = "student";

    EditText name, middlename, surname, college, rollnumber, source, destination;
    private ImageView idCardImage;
    private byte[] byteArray;
    @Nullable
    private Uri mImageHolder;
    Button selectImageBtn, submit;
    RadioGroup radioGroup;

    private FirebaseFirestore mFirebaseDb;
    private StorageReference mStorageReference;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_details);

        mProgressDialog = new ProgressDialog(this);

        name = findViewById(R.id.firstName);
        middlename = findViewById(R.id.middleName);
        surname = findViewById(R.id.lastName);
        college = findViewById(R.id.collegeName);
        rollnumber = findViewById(R.id.collegeID);
        source = findViewById(R.id.source);
        destination = findViewById(R.id.destination);

        radioGroup = findViewById(R.id.radioGroup);
        selectImageBtn = findViewById(R.id.selectImage);
        submit = findViewById(R.id.submitrequest);
        idCardImage = findViewById(R.id.idcardImage);

        mFirebaseDb = FirebaseFirestore.getInstance();
        mStorageReference = FirebaseStorage.getInstance().getReference();

        selectImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAllFields();
            }
        });
    }

    public void checkAllFields() {

        String mName = name.getText().toString();
        String mMiddle = middlename.getText().toString();
        String mLast = surname.getText().toString();

        String mCollege = college.getText().toString();
        String mRoll = rollnumber.getText().toString();

        String mSource = source.getText().toString();
        String mDestination = destination.getText().toString();

        int radioButtonId = radioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = findViewById(radioButtonId);
        String gender = radioButton.getText().toString();

        if (mName.equals("") || mName.isEmpty()) {
            name.setError("this field is missing");
            Toast.makeText(this, "Please enter all the details", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mMiddle.equals("") || mMiddle.isEmpty()) {
            middlename.setError("this field is missing");
            Toast.makeText(this, "Please enter all the details", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mLast.equals("") || mLast.isEmpty()) {
            surname.setError("this field is missing");
            Toast.makeText(this, "Please enter all the details", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mCollege.equals("") || mCollege.isEmpty()) {
            college.setError("this field is missing");
            Toast.makeText(this, "Please enter all the details", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mRoll.equals("") || mRoll.isEmpty()) {
            rollnumber.setError("this field is missing");
            Toast.makeText(this, "Please enter all the details", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mSource.equals("") || mSource.isEmpty()) {
            source.setError("this field is missing");
            Toast.makeText(this, "Please enter all the details", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mDestination.equals("") || mDestination.isEmpty()) {
            destination.setError("this field is missing");
            Toast.makeText(this, "Please enter all the details", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mImageHolder == null) {
            Toast.makeText(this, "Please enter you ID PROOF IMAGE", Toast.LENGTH_SHORT).show();
            return;
        }

        storeImage(mName, mLast, mMiddle, mCollege, mRoll, mSource, mDestination, gender);
    }


    public void uploadDetails(String name, String surname, String middleName, String college, String id, String source, String destination, String gender) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String uuid = mAuth.getCurrentUser().getUid();
        String email = mAuth.getCurrentUser().getEmail();

        Map<String, Object> details = new HashMap<>();
        details.put(FIRST_NAME, name);
        details.put(LAST_NAME, surname);
        details.put(MIDDLE_NAME, middleName);
        details.put(COLLEGE_NAME, college);
        details.put(COLLEGE_ID, id);
        details.put(SOURCE_LOCATION, source);
        details.put(DESTINATION_LOCATION, destination);
        details.put(USER_VERIFIED, "0");
        details.put(USER_EMAIL, email);
        details.put("remark", "No Remarks");
        details.put("gender", gender);

        mFirebaseDb.collection(STUDENT_COLLECTION).document(uuid).set(details).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                hideProgressDialogWithTitle();
                updateUI();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideProgressDialogWithTitle();
                Toast.makeText(StudentDetails.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
            idCardImage.setVisibility(View.VISIBLE);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageHolder);
                idCardImage.setImageBitmap(bitmap);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//                bitmap.recycle();
                byteArray = stream.toByteArray();
            } catch (Exception e) {
                Toast.makeText(this, " " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void storeImage(final String name, final String surname, final String middleName, final String college, final String id, final String source, final String destination, final String gender) {

        showProgressDialogWithTitle("Submitting your details...");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String uuid = mAuth.getCurrentUser().getUid();
        StorageReference reference = mStorageReference.child("IdCards" + "/" + uuid);
        reference.putFile(mImageHolder).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                uploadDetails(name, surname, middleName, college, id, source, destination, gender);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideProgressDialogWithTitle();
                Toast.makeText(StudentDetails.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void updateUI() {
        Intent myIntent = new Intent(StudentDetails.this, RedirectActivity.class);
        startActivity(myIntent);
        StudentDetails.this.finish();
    }


    private void showProgressDialogWithTitle(String substring) {
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage(substring);
        mProgressDialog.show();
    }

    private void hideProgressDialogWithTitle() {
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.dismiss();
    }

}