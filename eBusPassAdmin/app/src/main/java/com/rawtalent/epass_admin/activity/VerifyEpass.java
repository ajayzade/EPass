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
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.rawtalent.epass_admin.R;
import com.rawtalent.epass_admin.fragment.EpassModel;
import com.rawtalent.epass_admin.fragment.StudentData;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class VerifyEpass extends AppCompatActivity {


    TextView source,destination,validity,months,status;
    EditText remark;
    ImageView paymentProof;
    Button approve,reject,viewProfile;

    Context mContext;

    AlertDialog.Builder builder;
    AlertDialog progressDialog;

    ProgressBar progressBar;

    EpassModel epassModel;
    String docID;

    StudentData data;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_epass);

        mContext=VerifyEpass.this;
        progressDialog = getBuilder().create();
        progressDialog.setCancelable(false);

        source=findViewById(R.id.source);
        destination=findViewById(R.id.destination);
        validity=findViewById(R.id.validity);
        months=findViewById(R.id.months);
        status=findViewById(R.id.status);

        remark=findViewById(R.id.remark);
        paymentProof=findViewById(R.id.idproofimage);

        approve=findViewById(R.id.approve);
        progressBar=findViewById(R.id.imageloader);

        reject=findViewById(R.id.reject);
        viewProfile=findViewById(R.id.viewProfile);

        Intent intent=getIntent();
        epassModel= (EpassModel) intent.getSerializableExtra("epass");
        docID=intent.getStringExtra("docid");

        if (epassModel.getVerificationStatus().equals("1")){
            LinearLayout linearLayout=findViewById(R.id.remarkLayout);
            linearLayout.setVisibility(View.GONE);
        }

        viewProfile.setClickable(false);
        setdetails();
        fetchData();

        viewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(VerifyEpass.this, ProfileActivity.class);
                intent.putExtra("studentdata",data);
                intent.putExtra("docid",docID);
                startActivity(intent);
            }
        });


        approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(VerifyEpass.this);
                builder.setTitle("Approve ePass").setMessage("Are you sure you want to approve this ePass?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
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
                    Toast.makeText(VerifyEpass.this, "Please enter remark", Toast.LENGTH_SHORT).show();
                    return;
                }

                AlertDialog.Builder builder=new AlertDialog.Builder(VerifyEpass.this);
                builder.setTitle("Reject ePass").setMessage("Are you sure you want to reject this ePass?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
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
    }


    public void approveRequest(){
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        progressDialog.show();
        db.collection("ePasses").document(docID).update("verificationStatus","1").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(mContext, "Successfully Approved ! ", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                VerifyEpass.this.finish();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(mContext, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    public void rejectRequest(String remark){
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        progressDialog.show();
        Map<String,Object> data=new HashMap<>();
        data.put("verificationStatus","-1");
        data.put("remark",remark);
        db.collection("ePasses").document(docID).update(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(mContext, "Successfully Rejected ! ", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                VerifyEpass.this.finish();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(mContext, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }
    public AlertDialog.Builder getBuilder() {
        if (builder == null) {
            builder = new AlertDialog.Builder(VerifyEpass.this);
            builder.setTitle("Updating status...");

            final ProgressBar progressBar = new ProgressBar(VerifyEpass.this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            progressBar.setLayoutParams(layoutParams);
            builder.setView(progressBar);
        }
        return builder;
    }


    public void loadImageFromDB(String imageName){

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference().child("PaymentProof").child(imageName);
        storageReference.getBytes(1024 * 1024 * 5).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                paymentProof.setImageBitmap(bitmap);
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    public void fetchData(){
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        db.collection("student").document(docID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                data=documentSnapshot.toObject(StudentData.class);
                viewProfile.setClickable(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    public String convertDate(long time) {
        String date = new SimpleDateFormat("dd/MM/yyyy").format(new Date(time));
        return date;
    }


    public void setdetails(){
        source.setText(""+epassModel.getSource());
        destination.setText(""+epassModel.getDestination());
        months.setText(""+epassModel.getMonths());
        validity.setText(""+convertDate(epassModel.getExpiryDate()));

        String mStatus=epassModel.getVerificationStatus();
        if (mStatus.equals("1")){
            status.setText("Approved");
        }else{
            status.setText("Pending");
        }

        loadImageFromDB(docID);


    }
}