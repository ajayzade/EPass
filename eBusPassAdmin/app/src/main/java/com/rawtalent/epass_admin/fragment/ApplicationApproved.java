package com.rawtalent.epass_admin.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.airbnb.lottie.LottieAnimationView;
import com.rawtalent.epass_admin.R;
import com.rawtalent.epass_admin.activity.ProfileActivity;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class ApplicationApproved extends Fragment {


    View view;
    RecyclerView mrecylerview;
    private ProgressDialog mProgressDialog;
    private FirebaseFirestore firebaseFirestore;
    ConstraintLayout emptyView;
    private FirestoreRecyclerAdapter firestoreRecyclerAdapter;
    LottieAnimationView animationView;

    Context mContext;
    public ApplicationApproved() {
        // Required empty public constructor
    }

    public ApplicationApproved(Context mContext) {
        // Required empty public constructor
        this.mContext=mContext;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_application_approved, container, false);
        mrecylerview=view.findViewById(R.id.recyclerview);
        animationView=view.findViewById(R.id.animation_view);

        firebaseFirestore = FirebaseFirestore.getInstance();
        Query query = firebaseFirestore.collection("student").whereEqualTo("isVerified","1");
        // .orderBy("time", Query.Direction.ASCENDING);

        Log.d("DCH", "onCreateView: Query set");
        FirestoreRecyclerOptions<StudentData> options = new FirestoreRecyclerOptions.Builder<StudentData>().setQuery(query, StudentData.class).build();
        firestoreRecyclerAdapter = new FirestoreRecyclerAdapter<StudentData, StudentViewHolder>(options) {
            @NonNull
            @Override
            public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.accounts_items, parent, false);
                Log.d("DCH", "onCreateView: view set");
                return new StudentViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final StudentViewHolder holder, final int position, @NonNull final StudentData model) {

                holder.name.setText(""+model.getFirstName()+" "+model.getLastName());
                holder.email.setText(""+model.getEmail());
                holder.verifyDetails.setText("View Details");
                holder.verifyDetails.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent=new Intent(getActivity(), ProfileActivity.class);
                        intent.putExtra("studentdata",model);
                        intent.putExtra("docid",getSnapshots().getSnapshot(position).getId());
                        getActivity().startActivity(intent);

                    }
                });



            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
                if (firestoreRecyclerAdapter.getItemCount()==0){
                    animationView.setVisibility(View.VISIBLE);

                }else{
                    animationView.setVisibility(View.GONE);
                    //  emptyView.setVisibility(View.GONE);
                }
            }
        };
        mrecylerview.setHasFixedSize(true);
        mrecylerview.setLayoutManager(new LinearLayoutManager(mContext));
        mrecylerview.setAdapter(firestoreRecyclerAdapter);
        return view;
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
    @Override
    public void onStop() {
        super.onStop();
        firestoreRecyclerAdapter.stopListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        firestoreRecyclerAdapter.startListening();
    }

}