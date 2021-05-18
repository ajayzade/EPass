package com.rawtalent.epass_admin.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.rawtalent.epass_admin.R;
import com.rawtalent.epass_admin.fragment.StudentData;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity  {

    private Context mContext;
    TextView name, email, gender, college, roll, accountID;
    ImageView imageView;
    ProgressBar progressBar;

    Button approve,reject;

    ScrollView scrollView;
    AlertDialog.Builder builder;
    AlertDialog progressDialog;

    StudentData studentData;
    String docID;
    String tag;

    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile2);

        mContext =ProfileActivity.this;


        progressDialog = getBuilder().create();
        progressDialog.setCancelable(false);

        name = findViewById(R.id.name);
        college = findViewById(R.id.college);
        gender = findViewById(R.id.gender);
        email = findViewById(R.id.email);
        roll = findViewById(R.id.roll);
        accountID = findViewById(R.id.accountID);
        imageView = findViewById(R.id.idproofimage);
        progressBar=findViewById(R.id.imageloader);


        approve=findViewById(R.id.approve1);
        reject=findViewById(R.id.reject);

        Intent intent=getIntent();
        studentData= (StudentData) intent.getSerializableExtra("studentdata");
        docID=intent.getStringExtra("docid");

        linearLayout=findViewById(R.id.approvalLayout);

        if(studentData.getIsVerified().equals("1")){
            linearLayout.setVisibility(View.GONE);
        }else{
            linearLayout.setVisibility(View.VISIBLE);
        }

        setDetails();



        approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(ProfileActivity.this);
                builder.setTitle("Approve Request").setMessage("Are you sure you want to approve this form?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        approveRequest();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
            }
        });
        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText remark=(EditText) findViewById(R.id.remark);
                final String mRemark=remark.getText().toString();

                if (mRemark.equals("")||mRemark.isEmpty()){
                    remark.setError("Please enter remark");
                    Toast.makeText(mContext, "Please enter remark", Toast.LENGTH_SHORT).show();
                    return;
                }

                AlertDialog.Builder builder=new AlertDialog.Builder(ProfileActivity.this);
                builder.setTitle("Reject Request").setMessage("Are you sure you want to reject this form?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        rejectRequest(mRemark);
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();

            }
        });


      //  getDetails();

    }


    public void setDetails(){

        name.setText(studentData.getFirstName()+" "+studentData.getMiddleName()+" "+studentData.getLastName());
        college.setText(studentData.getCollege());
        gender.setText(studentData.getGender());
        email.setText(studentData.getEmail());
        roll.setText(studentData.getRollNumber());
        accountID.setText(docID);

        loadImageFromDB(docID);

    }

/*

    public void getDetails() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(StudentDetails.STUDENT_COLLECTION).document(mAuth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(@NonNull DocumentSnapshot documentSnapshot) {
                scrollView.setVisibility(View.VISIBLE);
                name.setText("" + documentSnapshot.getString(StudentDetails.FIRST_NAME) + " " + documentSnapshot.getString(StudentDetails.MIDDLE_NAME) + " " +
                        documentSnapshot.getString(StudentDetails.LAST_NAME));
                email.setText("" + documentSnapshot.getString(StudentDetails.USER_EMAIL));
                college.setText("" + documentSnapshot.getString(StudentDetails.COLLEGE_NAME));
                roll.setText("" + documentSnapshot.getString(StudentDetails.COLLEGE_ID));
                accountID.setText("" + documentSnapshot.getId());

                gender.setText("" + documentSnapshot.getString("gender"));
                progressDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(ProfileActivity.this, "" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


 */
    public AlertDialog.Builder getBuilder() {
        if (builder == null) {
            builder = new AlertDialog.Builder(ProfileActivity.this);
            builder.setTitle("Updating status...");

            final ProgressBar progressBar = new ProgressBar(ProfileActivity.this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            progressBar.setLayoutParams(layoutParams);
            builder.setView(progressBar);
        }
        return builder;
    }


    public void loadImageFromDB(String imageName){

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference().child("IdCards").child(imageName);
        storageReference.getBytes(1024 * 1024 * 5).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imageView.setImageBitmap(bitmap);
                progressBar.setVisibility(View.GONE);
            }
        });

    }


    public void approveRequest(){
       uploadImage(docID);
    }

    public void rejectRequest(String remark){
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        progressDialog.show();
        Map<String,Object> data=new HashMap<>();
        data.put("isVerified","-1");
        data.put("remark",remark);
        db.collection("student").document(docID).update(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(mContext, "Successfully Rejected ! ", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                ProfileActivity.this.finish();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(mContext, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    public Bitmap generateQR(String userUUID){

        Bitmap bitmap=null;

         // Whatever you need to encode in the QR code
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(userUUID, BarcodeFormat.QR_CODE,200,200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
             bitmap = barcodeEncoder.createBitmap(bitMatrix);

          //  imageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            Log.d("QRCODE", "onFailure: bitmap: "+e.getMessage());
            e.printStackTrace();
        }
        return bitmap;
    }


    public void uploadImage(String docID){
        progressDialog.show();
        StorageReference storageReference=FirebaseStorage.getInstance().getReference();
        StorageReference reference = storageReference.child("qr_codes" + "/" + docID );
        Bitmap bitmap=generateQR(docID);
        if (bitmap==null){
            Log.d("QRCODE", "onFailure: Bitpmap Null");
            Toast.makeText(mContext, "Some error occured please try again", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();

        }else {

            Log.d("QRCODE", "onSuccess: Bitpmap is NOT Null ,now uploading the image!");

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] data = byteArrayOutputStream.toByteArray();

            reference.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d("QRCODE", "QR is uploaded Successfully!");
                    storeDataToFirestore();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(mContext, "Firebase Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d("QRCODE", "onFailure: " + e.getMessage());
                }
            });
        }

    }

    public void storeDataToFirestore(){
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        db.collection("student").document(docID).update("isVerified","1").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(mContext, "Successfully Approved ! ", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                ProfileActivity.this.finish();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(mContext, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }
}