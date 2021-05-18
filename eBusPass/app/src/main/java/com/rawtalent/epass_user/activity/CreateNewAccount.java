package com.rawtalent.epass_user.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.rawtalent.epass_user.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class CreateNewAccount extends AppCompatActivity {


    AlertDialog.Builder builder;
    AlertDialog progressDialog;
    Button signup;
    EditText email, password;
    TextView alertTV;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_account);

        mAuth = FirebaseAuth.getInstance();
        progressDialog = getBuilder().create();
        progressDialog.setCancelable(false);

        signup = findViewById(R.id.signupButton);
        email = findViewById(R.id.username);
        password = findViewById(R.id.password);
        alertTV = findViewById(R.id.alertTV);


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mEmail = email.getText().toString();
                String mPassword = password.getText().toString();

                if (mEmail.equals("") || mEmail.isEmpty()) {
                    alertTV.setText("please enter email");
                    alertTV.setVisibility(View.VISIBLE);
                    return;
                }
                if (mPassword.equals("") || mPassword.isEmpty()) {
                    alertTV.setText("please enter password");
                    alertTV.setVisibility(View.VISIBLE);
                    return;
                }
                if (mPassword.length() < 9) {
                    alertTV.setText("password must be longer than 8 characters");
                    alertTV.setVisibility(View.VISIBLE);
                    return;
                }

                signupWithCredentials(mEmail, mPassword);

            }
        });


    }

    public void signupWithCredentials(@NonNull String email, @NonNull String password) {
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Intent intent = new Intent(CreateNewAccount.this, StudentDetails.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                progressDialog.dismiss();
                CreateNewAccount.this.finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(CreateNewAccount.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public AlertDialog.Builder getBuilder() {
        if (builder == null) {
            builder = new AlertDialog.Builder(CreateNewAccount.this);
            builder.setTitle("Creating Account");

            final ProgressBar progressBar = new ProgressBar(CreateNewAccount.this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            progressBar.setLayoutParams(layoutParams);
            builder.setView(progressBar);
        }
        return builder;
    }


}