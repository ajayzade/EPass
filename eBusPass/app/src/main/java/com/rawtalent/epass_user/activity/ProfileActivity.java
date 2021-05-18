package com.rawtalent.epass_user.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.rawtalent.epass_user.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;
    private ChangeNavigationActivities mChangeActivityFromNavigation;
    private Context mContext;
    TextView name, email, gender, college, roll, accountID;
    ScrollView scrollView;
    AlertDialog.Builder builder;
    AlertDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mContext = getApplicationContext();
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mToolbar = findViewById(R.id.toolbar);

        progressDialog = getBuilder().create();
        progressDialog.setCancelable(false);

        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        mChangeActivityFromNavigation = new ChangeNavigationActivities();


        name = findViewById(R.id.name);
        college = findViewById(R.id.college);
        gender = findViewById(R.id.gender);
        email = findViewById(R.id.email);
        roll = findViewById(R.id.roll);
        accountID = findViewById(R.id.accountID);
        scrollView = findViewById(R.id.scrollViewLayout);

        scrollView.setVisibility(View.GONE);

        progressDialog.show();
        getDetails();

    }

    private void setNavigation() {
        mNavigationView = findViewById(R.id.nav_view);
        mNavigationView.getMenu().getItem(1).setChecked(true);
        mNavigationView.setNavigationItemSelectedListener(this);
        final TextView name = mNavigationView.getHeaderView(0).findViewById(R.id.profileName);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        try {
            name.setText("" + mAuth.getCurrentUser().getEmail());
        } catch (Exception e) {

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        setNavigation();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Log.d("PROA", "onNavigationItemSelected: " + id);
        if (id == R.id.nav_profile) {
            //     mDrawerLayout.closeDrawer(GravityCompat.START);
            //    mChangeActivityFromNavigation.startProfileActivity(mContext);
        } else if (id == R.id.nav_home) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            mChangeActivityFromNavigation.startHomeActivity(mContext);
        } else if (id == R.id.nav_new_application) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            mChangeActivityFromNavigation.startUserPassesActivity(mContext);
        } else if (id == R.id.nav_logout) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
            builder.setMessage("Are you sure you want to logout ?").setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }).setNegativeButton("NO", null);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
        return false;
    }


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

    public AlertDialog.Builder getBuilder() {
        if (builder == null) {
            builder = new AlertDialog.Builder(ProfileActivity.this);
            builder.setTitle("fetching details...");

            final ProgressBar progressBar = new ProgressBar(ProfileActivity.this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            progressBar.setLayoutParams(layoutParams);
            builder.setView(progressBar);
        }
        return builder;
    }
}