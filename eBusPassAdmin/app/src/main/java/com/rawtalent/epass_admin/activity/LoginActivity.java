package com.rawtalent.epass_admin.activity;

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

import com.rawtalent.epass_admin.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{


    public static final String USER_NAME="busadmin@gmail.com";
    public static final String PASSWORD="busadmin123#";

    EditText email, password;
    Button loginButton;
    TextView alertTV;

    AlertDialog.Builder builder;
    AlertDialog progressDialog;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        progressDialog = getBuilder().create();
        progressDialog.setCancelable(false);


        loginButton = (Button) findViewById(R.id.loginButton);
        email = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        alertTV = (TextView) findViewById(R.id.alertTV);

        email.setText(USER_NAME);
        password.setText(PASSWORD);

        loginButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        alertTV.setVisibility(View.GONE);

        if (v.getId() == R.id.loginButton) {

            String mEmail = email.getText().toString();
            String mPassword = password.getText().toString();



            if (mEmail.equals("") || mEmail.isEmpty()) {
                alertTV.setVisibility(View.VISIBLE);
                alertTV.setText("please enter email");
                return;
            }
            if (mPassword.equals("") || mPassword.isEmpty()) {
                alertTV.setText("please enter password");
                alertTV.setVisibility(View.VISIBLE);
                return;
            }
            progressDialog.show();
            loginWithCredentials(mEmail, mPassword);
        }
    }
    public void loginWithCredentials(String email, String password) {

      /*  if (email.equals(USER_NAME)&&password.equals(PASSWORD)){
            progressDialog.dismiss();
            updateUI();
        }else{
            progressDialog.dismiss();
            Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
        }
        
       */
        mAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                progressDialog.dismiss();
                updateUI();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, "" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


    }

    public AlertDialog.Builder getBuilder() {
        if (builder == null) {
            builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setTitle("Checking details...");

            final ProgressBar progressBar = new ProgressBar(LoginActivity.this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            progressBar.setLayoutParams(layoutParams);
            builder.setView(progressBar);
        }
        return builder;
    }

    public void updateUI() {
        Intent myIntent = new Intent(LoginActivity.this, AccountVerification.class);
        startActivity(myIntent);
        LoginActivity.this.finish();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseAuth auth= FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser()!=null){
            updateUI();
        }
    }
}