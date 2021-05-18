package com.rawtalent.epass_user.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.rawtalent.epass_user.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class RedirectActivity extends AppCompatActivity {


    TextView status,remark;



    FirebaseFirestore db;
    static FirebaseAuth mAuth;

    AlertDialog.Builder builder;
    AlertDialog progressDialog;

    Button resubmit;

    ConstraintLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redirect);

        status=findViewById(R.id.textView4);
        remark=findViewById(R.id.textView5);

        layout=findViewById(R.id.layout);
        layout.setVisibility(View.INVISIBLE);

        resubmit=findViewById(R.id.resubmit);
        progressDialog = getBuilder().create();
        progressDialog.setCancelable(false);

        db=FirebaseFirestore.getInstance();
        mAuth=FirebaseAuth.getInstance();

        resubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RedirectActivity.this, StudentDetails.class);
                startActivity(intent);
                RedirectActivity.this.finish();
            }
        });

        progressDialog.show();
        db.collection(StudentDetails.STUDENT_COLLECTION).document(mAuth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                try {
                    int statusCode = Integer.parseInt(documentSnapshot.getString(StudentDetails.USER_VERIFIED));
                    if (statusCode == 1) {
                        Intent intent=new Intent(RedirectActivity.this,MainActivity.class);
                        progressDialog.dismiss();
                        startActivity(intent);
                        RedirectActivity.this.finish();
                    } else if (statusCode == -1) {
                       resubmit.setVisibility(View.VISIBLE);
                       status.setText("Hello "+documentSnapshot.getString(StudentDetails.FIRST_NAME)+", your account request is REJECTED by the verification officer!");
                       remark.setText("Please check these remarks given by verification officer and resubmit the form with correct details \nRemarks:"+documentSnapshot.getString("remark"));
                        layout.setVisibility(View.VISIBLE);
                    } else {
                        status.setText("Hello "+documentSnapshot.getString(StudentDetails.FIRST_NAME)+", your account is under verification process");

                        layout.setVisibility(View.VISIBLE);
                        //nothing
                    }
                    progressDialog.dismiss();
                  //  layout.setVisibility(View.VISIBLE);

                } catch (Exception e) {
                    Intent intent = new Intent(RedirectActivity.this, StudentDetails.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    progressDialog.dismiss();
                    startActivity(intent);
                    RedirectActivity.this.finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RedirectActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });




    }

    public AlertDialog.Builder getBuilder() {
        if (builder == null) {
            builder = new AlertDialog.Builder(RedirectActivity.this);
            builder.setTitle("Checking details...");

            final ProgressBar progressBar = new ProgressBar(RedirectActivity.this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            progressBar.setLayoutParams(layoutParams);
            builder.setView(progressBar);
        }
        return builder;
    }
}