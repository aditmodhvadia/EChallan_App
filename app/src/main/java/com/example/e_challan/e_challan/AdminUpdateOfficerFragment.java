package com.example.e_challan.e_challan;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
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


public class AdminUpdateOfficerFragment extends android.support.v4.app.Fragment {

    EditText updateOfficerAadhaarNoEditText;
    EditText officerFNameEditText,officerLNameEditText,officerPhoneNoEditText,officerDOBEditText,officerAddressEditText;

    TextView update_officer_display;
    Button officerUpdateButton;
    Button searchOfficerButton;
    ProgressDialog progressDialog;
    private String officerEmailID,officerAadhaarNo ;
    Calendar calendar;
    DatePickerDialog dpd;

    private FirebaseAuth auth;
    private FirebaseUser currOfficer;
    private DatabaseReference dbcurrofficer;

    public AdminUpdateOfficerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_update_officer, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Update Officer Details");

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        auth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(getContext());

        update_officer_display = (TextView) view.findViewById(R.id.update_officer_display);

        updateOfficerAadhaarNoEditText = (EditText) view.findViewById(R.id.updateOfficerAadhaarNoEditText);
        searchOfficerButton = (Button) view.findViewById(R.id.searchOfficerButton);
        officerFNameEditText = (EditText) view.findViewById(R.id.officerFNameEditText);
        officerLNameEditText = (EditText) view.findViewById(R.id.officerLNameEditText);
        officerPhoneNoEditText = (EditText) view.findViewById(R.id.officerPhoneNoEditText);
        officerDOBEditText = (EditText) view.findViewById(R.id.officerDOBEditText);
        officerAddressEditText = (EditText) view.findViewById(R.id.officerAddressEditText);
        officerUpdateButton = (Button) view.findViewById(R.id.officerUpdateButton);


        officerDOBEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getActionMasked() == MotionEvent.ACTION_DOWN){
                    calendar= Calendar.getInstance();
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

        searchOfficerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                officerAadhaarNo = updateOfficerAadhaarNoEditText.getText().toString().trim();

                if(officerAadhaarNo.isEmpty())
                {
                    updateOfficerAadhaarNoEditText.setError("Aadhaar Number Required");
                    updateOfficerAadhaarNoEditText.requestFocus();
                    return;
                }
                if(officerAadhaarNo.length()!=12)
                {
                    updateOfficerAadhaarNoEditText.setError("Aadhaar Number should be of 12 digits");
                    updateOfficerAadhaarNoEditText.requestFocus();
                    return;
                }

                progressDialog.setMessage("Searching Officer");
                progressDialog.show();
                progressDialog.setCanceledOnTouchOutside(false);

                dbcurrofficer = FirebaseDatabase.getInstance().getReference().child("User");

                dbcurrofficer.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.child(officerAadhaarNo).exists() && dataSnapshot.child(officerAadhaarNo).child("UserType").getValue().toString().equals("Officer"))
                        {
                            //officer with aadhaar no exists. load fragment to take new values
                            progressDialog.hide();
                            update_officer_display.setVisibility(View.VISIBLE);
                            officerAddressEditText.setVisibility(View.VISIBLE);
                            officerDOBEditText.setVisibility(View.VISIBLE);
                            officerFNameEditText.setVisibility(View.VISIBLE);
                            officerLNameEditText.setVisibility(View.VISIBLE);
                            officerPhoneNoEditText.setVisibility(View.VISIBLE);
                            officerUpdateButton.setVisibility(View.VISIBLE);

                            //Setting all the values for easy reference
                            officerAddressEditText.setText(dataSnapshot.child(officerAadhaarNo).child("Address").getValue().toString());
                            officerDOBEditText.setText(dataSnapshot.child(officerAadhaarNo).child("DOB").getValue().toString());
                            officerFNameEditText.setText(dataSnapshot.child(officerAadhaarNo).child("FirstName").getValue().toString());
                            officerLNameEditText.setText(dataSnapshot.child(officerAadhaarNo).child("LastName").getValue().toString());
                            officerPhoneNoEditText.setText(dataSnapshot.child(officerAadhaarNo).child("PhoneNo").getValue().toString());
                            officerEmailID=dataSnapshot.child(officerAadhaarNo).child("EmailId").getValue().toString();

                            officerUpdateButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    checkValues();
                                }
                            });

                        }
                        else
                        {
                            //Officer with aadhaar no does not exist
                            progressDialog.hide();
                            update_officer_display.setVisibility(View.GONE);
                            officerAddressEditText.setVisibility(View.GONE);
                            officerDOBEditText.setVisibility(View.GONE);
                            officerFNameEditText.setVisibility(View.GONE);
                            officerLNameEditText.setVisibility(View.GONE);
                            officerPhoneNoEditText.setVisibility(View.GONE);
                            officerUpdateButton.setVisibility(View.GONE);
                            updateOfficerAadhaarNoEditText.setError("Officer does not exist");
                            updateOfficerAadhaarNoEditText.requestFocus();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });


    }

    private void checkValues() {
        final String officerFName,officerLName,officerPhoneNo,officerDOB,officerAddress;

        officerFName = officerFNameEditText.getText().toString().trim();
        officerLName = officerLNameEditText.getText().toString().trim();
        officerPhoneNo = officerPhoneNoEditText.getText().toString().trim();
        officerDOB = officerDOBEditText.getText().toString().trim();
        officerAddress = officerAddressEditText.getText().toString().trim();
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

        //now add these new values to the database

        progressDialog.setMessage("Adding Officer");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);

        dbcurrofficer.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(officerAadhaarNo).exists() )
                {

                    dbcurrofficer.child(officerAadhaarNo).child("FirstName").setValue(officerFName);
                    dbcurrofficer.child(officerAadhaarNo).child("Address").setValue(officerAddress);
                    dbcurrofficer.child(officerAadhaarNo).child("DOB").setValue(officerDOB);
                    dbcurrofficer.child(officerAadhaarNo).child("EmailId").setValue(officerEmailID);
                    dbcurrofficer.child(officerAadhaarNo).child("LastName").setValue(officerLName);
                    dbcurrofficer.child(officerAadhaarNo).child("PhoneNo").setValue(officerPhoneNo);
                    dbcurrofficer.child(officerAadhaarNo).child("UserType").setValue("Officer");

                    String temp = officerEmailID.replace("@","");
                    temp = temp.replace(".","");
                    dbcurrofficer.child("Number").child(temp).setValue(officerPhoneNo);

                    progressDialog.hide();

                    updateOfficerAadhaarNoEditText.setText("");
                    officerAddressEditText.setText("");
                    officerDOBEditText.setText("");
                    officerFNameEditText.setText("");
                    officerLNameEditText.setText("");
                    officerPhoneNoEditText.setText("");

                    update_officer_display.setVisibility(View.GONE);
                    officerAddressEditText.setVisibility(View.GONE);
                    officerDOBEditText.setVisibility(View.GONE);
                    officerFNameEditText.setVisibility(View.GONE);
                    officerLNameEditText.setVisibility(View.GONE);
                    officerPhoneNoEditText.setVisibility(View.GONE);
                    officerUpdateButton.setVisibility(View.GONE);

                    Toast.makeText(getContext(),"Officer Details Updated Successfuly",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getContext(), "Some Error occurred. Try again", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
}
