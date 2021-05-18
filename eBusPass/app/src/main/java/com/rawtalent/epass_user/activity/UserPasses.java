package com.rawtalent.epass_user.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.rawtalent.epass_user.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;

public class UserPasses extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;
    private ChangeNavigationActivities mChangeActivityFromNavigation;
    private Context mContext;

    AlertDialog.Builder builder;
    AlertDialog progressDialog;

    LinearLayout remarkLayout;

    public static final String REMARK = "Please create a new pass from Home Section\n";

    TextView source, destination, validity, months, remark, status;
    Button createNewPass,createPass2;
    CardView cardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_passes);

        mContext = getApplicationContext();
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mToolbar = findViewById(R.id.toolbar);

        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        mChangeActivityFromNavigation = new ChangeNavigationActivities();

        progressDialog = getBuilder().create();
        progressDialog.setCancelable(false);

        status = findViewById(R.id.status);
        source = findViewById(R.id.source);
        destination = findViewById(R.id.destination);
        validity = findViewById(R.id.validity);
        months = findViewById(R.id.months);
        remark = findViewById(R.id.remark);
        remarkLayout = findViewById(R.id.remarkLayout);
        createNewPass = findViewById(R.id.createbtn);
        createPass2 = findViewById(R.id.createPass);

        cardView = findViewById(R.id.cardView);
        cardView.setVisibility(View.GONE);


        getData();

        createNewPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserPasses.this, CreateEPass.class);
                startActivity(intent);
            }
        });
        createPass2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserPasses.this, CreateEPass.class);
                startActivity(intent);
            }
        });
    }

    private void setNavigation() {
        mNavigationView = findViewById(R.id.nav_view);
        mNavigationView.getMenu().getItem(2).setChecked(true);
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
            mDrawerLayout.closeDrawer(GravityCompat.START);
            mChangeActivityFromNavigation.startProfileActivity(mContext);
        } else if (id == R.id.nav_home) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            mChangeActivityFromNavigation.startHomeActivity(mContext);
        } else if (id == R.id.nav_new_application) {
            //  mDrawerLayout.closeDrawer(GravityCompat.START);
            //  mChangeActivityFromNavigation.startUserPassesActivity(mContext);
        } else if (id == R.id.nav_logout) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            AlertDialog.Builder builder = new AlertDialog.Builder(UserPasses.this);
            builder.setMessage("Are you sure you want to logout ?").setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(UserPasses.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }).setNegativeButton("NO", null);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
        return false;
    }

    public void getData() {
        progressDialog.show();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        db.collection("ePasses").document(mAuth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(@NonNull DocumentSnapshot documentSnapshot) {

                try {
                    source.setText("" + documentSnapshot.getString("source"));
                    destination.setText("" + documentSnapshot.getString("destination"));
                    months.setText("" + documentSnapshot.getString("months"));
                    validity.setText("" + convertDate(documentSnapshot.getLong("expiryDate")));

                    int mstatus = Integer.parseInt(documentSnapshot.getString("verificationStatus"));
                    if (mstatus == 1) {

                        status.setText("Approved");
                    } else if (mstatus == -1) {
                        status.setText("Rejected");
                        remarkLayout.setVisibility(View.VISIBLE);
                        remark.setText(REMARK + documentSnapshot.getString("remark"));
                    } else {
                        status.setText("Pending");
                    }

                    cardView.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    Toast.makeText(mContext, "You dont have any ePass", Toast.LENGTH_LONG).show();
                }
                progressDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(mContext, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    @NonNull
    public String convertDate(long time) {
        String date = new SimpleDateFormat("dd/MM/yyyy").format(new Date(time));
        return date;
    }

    public AlertDialog.Builder getBuilder() {
        if (builder == null) {
            builder = new AlertDialog.Builder(UserPasses.this);
            builder.setTitle("fetching details...");

            final ProgressBar progressBar = new ProgressBar(UserPasses.this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            progressBar.setLayoutParams(layoutParams);
            builder.setView(progressBar);
        }
        return builder;
    }

}