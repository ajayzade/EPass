package com.rawtalent.epass_admin.fragment;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rawtalent.epass_admin.R;

public class StudentViewHolder extends RecyclerView.ViewHolder{

    TextView name,email;
    Button verifyDetails;

    public StudentViewHolder(@NonNull View itemView) {
        super(itemView);

        name=itemView.findViewById(R.id.studentName);
        email=itemView.findViewById(R.id.studentEmail);
        verifyDetails=itemView.findViewById(R.id.verifyDetails);

    }
}