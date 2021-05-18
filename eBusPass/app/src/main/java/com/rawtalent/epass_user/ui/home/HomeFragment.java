package com.rawtalent.epass_user.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.rawtalent.epass_user.R;
import com.rawtalent.epass_user.activity.StudentDetails;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeFragment extends Fragment {


    public static final String Remark1 = " Your account details is being verified by the scrutiny officer \n ";
    public static final String Remark2 = " YOUR ACCOUNT IS VERIFIED! \n you can now create e-Passes for your travel";
    public static final String Remark3 = " YOUR REQUEST IS REJECTED! \n Please correct your details and resubmit your form \n Remark by verification officer:\n ";

    TextView remark, status;
    Button createPass, resubmit;
    FirebaseFirestore db;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        remark = root.findViewById(R.id.verificationRemark);
        resubmit = root.findViewById(R.id.resubmit);
        createPass = root.findViewById(R.id.createpass);
        status = root.findViewById(R.id.verificationStatus);
        db = FirebaseFirestore.getInstance();

        checkStatus();

        resubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        createPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        return root;
    }


    public void checkStatus() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        db.collection("students").document(mAuth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(@NonNull DocumentSnapshot documentSnapshot) {
                int statusCode = Integer.parseInt(documentSnapshot.getString(StudentDetails.USER_VERIFIED));
                if (statusCode == -1) {
                    approved();
                } else if (statusCode == 1) {
                    rejected(documentSnapshot.getString("remark"));
                } else {
                    pending();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    public void approved() {
        status.setText("Approved");
        remark.setText(Remark2);
        remark.setBackgroundResource(R.drawable.remark_green);
        createPass.setVisibility(View.VISIBLE);
        resubmit.setVisibility(View.GONE);
    }

    public void rejected(String reason) {
        status.setText("Rejected");
        remark.setText(Remark3 + reason);
        remark.setBackgroundResource(R.drawable.remark_red);
        createPass.setVisibility(View.GONE);
        resubmit.setVisibility(View.VISIBLE);
    }

    public void pending() {
        status.setText("Pending");
        remark.setText(Remark1);
        remark.setBackgroundResource(R.drawable.remark_orange);
        createPass.setVisibility(View.GONE);
        resubmit.setVisibility(View.GONE);
    }
}