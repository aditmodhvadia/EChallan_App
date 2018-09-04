package com.example.e_challan.e_challan;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;


public class AdminAddOfficerFragment extends android.support.v4.app.Fragment {

    EditText officerFNameEditText,officerLNameEditText,officerPhoneNoEditText,officerEmailEditText,officerDOBEditText,officerAddressEditText;
    EditText officerAadhaarNoEditText;
    Button officerAddButton;
    ProgressDialog progressDialog;
    int flag;

    Calendar calendar;
    DatePickerDialog dpd;

    private FirebaseAuth auth;
    private FirebaseUser newOfficer;
    private DatabaseReference dbnewofficer;

    public AdminAddOfficerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_add_officer, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Add Officer");

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        auth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(getContext());

        officerFNameEditText = (EditText) view.findViewById(R.id.officerFNameEditText);
        officerLNameEditText = (EditText) view.findViewById(R.id.officerLNameEditText);
        officerPhoneNoEditText = (EditText) view.findViewById(R.id.officerPhoneNoEditText);
        officerEmailEditText = (EditText) view.findViewById(R.id.officerLoginEmailEditText);
        officerDOBEditText = (EditText) view.findViewById(R.id.officerDOBEditText);
        officerAddressEditText = (EditText) view.findViewById(R.id.officerAddressEditText);
        officerAadhaarNoEditText = (EditText) view.findViewById(R.id.officerAadhaarNoEditText);

        officerDOBEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getActionMasked() == MotionEvent.ACTION_DOWN){
                    calendar=Calendar.getInstance();
                    final int day = calendar.get(DAY_OF_MONTH);
                    final int month = calendar.get(MONTH);
                    final int year = calendar.get(YEAR);

                    dpd = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int mYear, int mMonth, int mDay) {
                            if((year-mYear)<25)
                            {
                                //put toast over here for updating dob of officer again. and add this code o update officer details

                                Toast.makeText(getContext(), "Officer should be atleast 25 years old", Toast.LENGTH_SHORT).show();
                                //officerDOBEditText.requestFocus();
                                dpd.show();
                                return;
                            }
                            if((year-mYear==25&&(month==mMonth&&day<mDay))||(year-mYear==25&&(month<mMonth)))
                            {
                                Toast.makeText(getContext(), "Officer should be atleast 25 years old", Toast.LENGTH_SHORT).show();
                                //officerDOBEditText.requestFocus();
                                dpd.show();
                                return;
                            }

                            officerDOBEditText.setText(mDay+"/"+(mMonth+1)+"/"+mYear);
                        }
                    } ,year,month,day);

                    dpd.show();
                }

                return false;
            }
        });


        officerAddButton = (Button) view.findViewById(R.id.officerAddButton);

        officerAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkValues();
            }
        });

    }

    private void checkValues() {
        final String officerFName,officerLName,officerPhoneNo,officerEmail,officerDOB,officerAddress,officerAadhaarNo;

        officerFName = officerFNameEditText.getText().toString().trim();
        officerLName = officerLNameEditText.getText().toString().trim();
        officerPhoneNo = officerPhoneNoEditText.getText().toString().trim();
        officerEmail = officerEmailEditText.getText().toString().trim().toLowerCase();
        officerDOB = officerDOBEditText.getText().toString().trim();
        officerAddress = officerAddressEditText.getText().toString().trim();
        officerAadhaarNo = officerAadhaarNoEditText.getText().toString().trim();

        //Validating all entries First

        if(officerFName.isEmpty())
        {
            officerFNameEditText.setError("First Name Required");
            officerFNameEditText.requestFocus();
            return;
        }
        if(officerLName.isEmpty())
        {
            officerLNameEditText.setError("Last Name Required");
            officerLNameEditText.requestFocus();
            return;
        }
        if(officerAadhaarNo.isEmpty())
        {
            officerAadhaarNoEditText.setError("Aadhaar Number Required");
            officerAadhaarNoEditText.requestFocus();
            return;
        }
        if(officerAadhaarNo.length()!=12)
        {
            officerAadhaarNoEditText.setError("Aadhaar Number should be of 12 digits");
            officerAadhaarNoEditText.requestFocus();
            return;
        }
        if(officerPhoneNo.isEmpty())
        {
            officerPhoneNoEditText.setError("Phone Number Required");
            officerPhoneNoEditText.requestFocus();
            return;
        }
        if(officerPhoneNo.length()!=10)
        {
            officerPhoneNoEditText.setError("Phone Number should be of 10 digits");
            officerPhoneNoEditText.requestFocus();
            return;
        }
        if(officerEmail.isEmpty())
        {
            officerEmailEditText.setError("Email ID Required");
            officerEmailEditText.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(officerEmail).matches()) {
            officerEmailEditText.setError("Enter valid Email Address");
            officerEmailEditText.requestFocus();
            return;
        }
        if(officerDOB.isEmpty())
        {
            officerDOBEditText.setError("Date Of Birth Required");
            officerDOBEditText.requestFocus();
            return;
        }
        if(officerAddress.isEmpty())
        {
            officerAddressEditText.setError("Address Required");
            officerAddressEditText.requestFocus();
            return;
        }


        progressDialog.setMessage("Adding Officer");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
        //Now check if the entry for corresponding aadhaar no is present in realtime database or not

        flag=1;

        dbnewofficer = FirebaseDatabase.getInstance().getReference("User");
        dbnewofficer.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.child(officerAadhaarNo).exists())
                {
                    progressDialog.hide();
                    officerAadhaarNoEditText.setError("Officer with this Aadhaar No already exists");
                    officerAadhaarNoEditText.requestFocus();
                    flag=0;
                }
                else {
                    auth.createUserWithEmailAndPassword(officerEmail,officerAadhaarNo).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                dbnewofficer.child(officerAadhaarNo).child("FirstName").setValue(officerFName);
                                dbnewofficer.child(officerAadhaarNo).child("Address").setValue(officerAddress);
                                dbnewofficer.child(officerAadhaarNo).child("DOB").setValue(officerDOB);
                                dbnewofficer.child(officerAadhaarNo).child("EmailId").setValue(officerEmail);
                                dbnewofficer.child(officerAadhaarNo).child("LastName").setValue(officerLName);
                                dbnewofficer.child(officerAadhaarNo).child("PhoneNo").setValue(officerPhoneNo);
                                dbnewofficer.child(officerAadhaarNo).child("UserType").setValue("Officer");
                                String temp = officerEmail.replace("@","");
                                temp = temp.replace(".","");
                                dbnewofficer.child("Number").child(temp).setValue(officerPhoneNo);

                                progressDialog.hide();

                                officerAadhaarNoEditText.setText("");
                                officerAddressEditText.setText("");
                                officerDOBEditText.setText("");
                                officerEmailEditText.setText("");
                                officerFNameEditText.setText("");
                                officerLNameEditText.setText("");
                                officerPhoneNoEditText.setText("");

                                Toast.makeText(getContext(),"Officer Added Successfuly",Toast.LENGTH_SHORT).show();



                            }
                            else
                            {
                                progressDialog.hide();
                                if (task.getException() instanceof FirebaseNetworkException)
                                {
                                    Toast.makeText(getContext(), "Internet connectivity required", Toast.LENGTH_SHORT).show();
                                }
                                else if (task.getException() instanceof FirebaseAuthUserCollisionException)
                                {
                                    officerEmailEditText.setError("Email ID is already in use");
                                    officerEmailEditText.requestFocus();
                                }
                                else
                                {
                                    Toast.makeText(getContext(), "Some error occurred. Try again", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if (flag==0)
        {
            return;
        }

    }
}
