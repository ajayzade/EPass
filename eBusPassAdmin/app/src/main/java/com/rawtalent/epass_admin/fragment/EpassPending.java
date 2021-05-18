package com.rawtalent.epass_admin.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.airbnb.lottie.LottieAnimationView;
import com.rawtalent.epass_admin.R;
import com.rawtalent.epass_admin.activity.VerifyEpass;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class EpassPending extends Fragment {


    View view;
    RecyclerView mrecylerview;
    private ProgressDialog mProgressDialog;
    private FirebaseFirestore firebaseFirestore;
    ConstraintLayout emptyView;
    private FirestoreRecyclerAdapter firestoreRecyclerAdapter;
    LottieAnimationView animationView;


    public EpassPending() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_epass_pending, container, false);
        animationView=view.findViewById(R.id.animation_view);

        mrecylerview=view.findViewById(R.id.recyclerview);
        firebaseFirestore = FirebaseFirestore.getInstance();
        Query query = firebaseFirestore.collection("ePasses").whereEqualTo("verificationStatus","0");
        // .orderBy("time", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<EpassModel> options = new FirestoreRecyclerOptions.Builder<EpassModel>().setQuery(query, EpassModel.class).build();
        firestoreRecyclerAdapter = new FirestoreRecyclerAdapter<EpassModel, StudentViewHolder>(options) {
            @NonNull
            @Override
            public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.accounts_items, parent, false);
                return new StudentViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final StudentViewHolder holder, final int position, @NonNull final EpassModel model) {

                holder.name.setText(" from: "+model.getSource()+"\n to: "+model.getDestination());
                holder.email.setText("Validity: "+model.getMonths()+" months");
                holder.verifyDetails.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(getActivity(), VerifyEpass.class);
                        intent.putExtra("epass",  model);
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
        mrecylerview.setLayoutManager(new LinearLayoutManager(getActivity()));
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