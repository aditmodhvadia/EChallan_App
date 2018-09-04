package com.example.e_challan.e_challan;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class AdminDeleteOfficerFragment extends Fragment {

    EditText deleteOfficerAadhaarNoEditText;
    Button deleteOfficerButton;
    ProgressDialog progressDialog;

    private FirebaseAuth auth;
    private FirebaseUser newOfficer;
    private DatabaseReference dbnewofficer;

    public AdminDeleteOfficerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_admin_delete_officer, container, false);

        getActivity().setTitle("Delete Officer");

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        auth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(getContext());

        deleteOfficerAadhaarNoEditText = v.findViewById(R.id.deleteOfficerAadhaarNoEditText);
        deleteOfficerButton = (Button) v.findViewById(R.id.deleteOfficerButton);

        deleteOfficerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteOfficer();
            }
        });

        return v;
    }

    private void deleteOfficer() {

        final String officerAadhaar = deleteOfficerAadhaarNoEditText.getText().toString().trim();

        if (officerAadhaar.isEmpty()) {
            deleteOfficerAadhaarNoEditText.setError("Enter Aadhaar Number");
            deleteOfficerAadhaarNoEditText.requestFocus();
            return;
        }

        if (officerAadhaar.length() != 12) {
            deleteOfficerAadhaarNoEditText.setError("Aadhaar Number should be of 12 digits");
            deleteOfficerAadhaarNoEditText.requestFocus();
            return;
        }

        progressDialog.setMessage("Deleting Officer");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);

        dbnewofficer = FirebaseDatabase.getInstance().getReference("User");
        dbnewofficer.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.hide();
                String officerEmailId;
                if (dataSnapshot.child(officerAadhaar).exists()) {

                    //Delete officer here
                    if (dataSnapshot.child(officerAadhaar).child("UserType").getValue().toString().equals("Officer")) {

                        officerEmailId = dataSnapshot.child(officerAadhaar).child("EmailId").getValue().toString();

                        //newOfficer = FirebaseUser.

                        newOfficer.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    //delete officer and also check for internet connection


                                } else {
                                    if (task.getException() instanceof FirebaseNetworkException)
                                    {
                                        Toast.makeText(getContext(), "Internet connectivity required", Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        Toast.makeText(getContext(), "Some error occurred. Try again", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });

                        //delete officer and also check for internet connection


                        dataSnapshot.child(officerAadhaar).getRef().removeValue(new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            }
                        });

                        //remove the officer with the email id from authentication list. this should happen before deleting all the values

                    } else {
                        //User with aadhaar no is not an officer
                        deleteOfficerAadhaarNoEditText.setError("User is not an officer");
                        deleteOfficerAadhaarNoEditText.requestFocus();
                    }
                } else {
                    //No such officer exists
                    deleteOfficerAadhaarNoEditText.setError("Officer does not exist");
                    deleteOfficerAadhaarNoEditText.requestFocus();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
