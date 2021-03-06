package com.example.a1dproject_campustradingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
import java.util.List;

public class AccountSellingActivity extends AppCompatActivity implements AccountSellingAdapter.OnItemClickListener{

    private RecyclerView mRecyclerView;
    private AccountSellingAdapter mAdapter;

    private FirebaseStorage mStorage;
    private DatabaseReference mDatabaseRef;
    private ValueEventListener mDBListener;
    private DatabaseReference mDatabaseRef_user;

    private List<Upload> mUploads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_account);

        //TODO sign up before retrieving the files
        //FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mRecyclerView = findViewById(R.id.account_recycle_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

//        mAdapter.setOnItemClickListener(ImagesActivity.this);


        mUploads = new ArrayList<>();

        mAdapter = new AccountSellingAdapter(AccountSellingActivity.this, mUploads);

        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(AccountSellingActivity.this);

        mStorage = FirebaseStorage.getInstance();

        //mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");
        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference(currentUser);
        //mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");

        mDBListener = mDatabaseRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mUploads.clear();

                //dataSnapshot is a list which represent our data at the mDatabaseRef
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    Upload upload = postSnapshot.getValue(Upload.class);
                    upload.setmKey(postSnapshot.getKey());
                    mUploads.add(upload);
                }

                mAdapter = new AccountSellingAdapter(AccountSellingActivity.this, mUploads);
                //mAdapter.notifyDataSetChanged();

                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //this will be called when there is an error
                Toast.makeText(AccountSellingActivity.this, databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        Toast.makeText(this, "Normal click at position: " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteClick(int position) {
        Toast.makeText(this, "Delete click at position: " + position, Toast.LENGTH_SHORT).show();
        Upload selectedItem = mUploads.get(position);
        final String selectedKey = selectedItem.getmKey();
        StorageReference imageRef = mStorage.getReferenceFromUrl(selectedItem.getmImageUrl());
        //String imageUrl = "https://firebasestorage.googleapis.com/v0/b/project-1d-50001.appspot.com/o/uploads%2F1574168053005.jpg?alt=media&token=82ac181d-674c-4156-a67a-6e3d80fd24fa";
        //StorageReference imageRef = FirebaseStorage.getInstance().getReference().child(imageUrl);
        //StorageReference imageRef = mStorage.getReference().child(imageUrl);
        //mDatabaseRef.child(selectedKey).removeValue();
        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mDatabaseRef.child("uploads").child(selectedKey).removeValue();
                Toast.makeText(AccountSellingActivity.this, "Item deleted", Toast.LENGTH_SHORT).show();
            }
        });
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDatabaseRef.removeEventListener(mDBListener);
    }
}


