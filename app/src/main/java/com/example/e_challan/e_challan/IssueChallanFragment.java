package com.example.e_challan.e_challan;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.regex.Pattern;

import static android.app.Activity.RESULT_OK;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;


public class IssueChallanFragment extends Fragment {

    EditText regNoEditText, challanDateEditText, challanTimeEditText, challanAmountEditText;
    Button issueChallanButton, choosePicButton, clickPicButton;
    Spinner placeSpinner;
    ImageView picImageView;
    ProgressDialog progressDialog;
    Calendar calendar;
    DatePickerDialog dpd;
    TimePickerDialog tpd;
    int CHOOSE_IMAGE = 100, CLICK_IMAGE = 200;

    String ChallanID, regNo, Date, Time, Amount, place, officerEmailId, STATUS_CHALLAN;

    Uri choosePicUri;
    String challanPicUri;
    File file;
    Uri fileUri, UploadUri;


    private FirebaseUser officer;
    private DatabaseReference rootVehicles, rootChallan;

    private String ownerAadhaarNo;

    public IssueChallanFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_issue_challan, container, false);

        getActivity().setTitle("Issue Challan");

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        progressDialog = new ProgressDialog(getContext());

        regNoEditText = (EditText) v.findViewById(R.id.regNoEditText);
        challanDateEditText = (EditText) v.findViewById(R.id.challanDateEditText);
        challanTimeEditText = (EditText) v.findViewById(R.id.challanTimeEditText);
        challanAmountEditText = (EditText) v.findViewById(R.id.challanAmountEditText);
        placeSpinner = (Spinner) v.findViewById(R.id.placeSpinner);

        picImageView = (ImageView) v.findViewById(R.id.picImageView);

        issueChallanButton = (Button) v.findViewById(R.id.issueChallanButton);
        choosePicButton = (Button) v.findViewById(R.id.choosePicButton);
        clickPicButton = (Button) v.findViewById(R.id.clickPicButton);

        challanDateEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    calendar = Calendar.getInstance();
                    final int day = calendar.get(DAY_OF_MONTH);
                    final int month = calendar.get(MONTH);
                    final int year = calendar.get(YEAR);

                    dpd = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int mYear, int mMonth, int mDay) {
                            //Edit this so that the officer can only choose a date which is smaller than today's date
                            if (mYear>year) {
                                //put toast over here for updating dob of officer again. and add this code o update officer details

                                Toast.makeText(getContext(), "Incorrect date selected", Toast.LENGTH_SHORT).show();
                                //officerDOBEditText.requestFocus();
                                dpd.show();
                                return;
                            }
                            if ((year - mYear == 0 && (month == mMonth && day < mDay)) || (year - mYear == 0 && (month < mMonth))) {
                                Toast.makeText(getContext(), "Incorrect date selected", Toast.LENGTH_SHORT).show();
                                //officerDOBEditText.requestFocus();
                                dpd.show();
                                return;
                            }

                            challanDateEditText.setText(mDay + "/" + (mMonth + 1) + "/" + mYear);
                        }
                    }, year, month, day);

                    dpd.show();
                }

                return false;
            }
        });

        challanTimeEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    calendar = Calendar.getInstance();
                    final int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    final int minute = calendar.get(Calendar.MINUTE);

                    tpd = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int mHour, int mMinute) {

                            challanTimeEditText.setText(mHour + ":" + mMinute);

                        }
                    }, hour, minute, true);


                    tpd.show();
                }

                return false;
            }
        });


        clickPicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                file = new File(getActivity().getExternalCacheDir(),
                        String.valueOf(System.currentTimeMillis()) + ".jpg");
                fileUri = Uri.fromFile(file);
                i2.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(i2, CLICK_IMAGE);


            }
        });

        choosePicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i1 = new Intent();
                i1.setType("image/*");
                i1.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(i1, "Select Challan Image"), CHOOSE_IMAGE);
            }
        });


        issueChallanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog.setTitle("Processing...");
                progressDialog.setMessage("Validating and Issuing Challan");
                progressDialog.show();
                issueChallan();
            }
        });


        return v;
    }

    //for choosing image from gallery and clicking an image as well

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            choosePicUri = data.getData();
            UploadUri = choosePicUri;

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), choosePicUri);
                picImageView.setImageBitmap(bitmap);


            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        if (requestCode == CLICK_IMAGE && resultCode == RESULT_OK && data != null) {

            UploadUri = fileUri;

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), UploadUri);
                picImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }




        }
    }

    //upload image first and then save all the other details...

    private void uploadimagetofirebasestorage(String regNo, String challanId) {

        StorageReference challanStorage = FirebaseStorage.getInstance().getReference("ChallanImages/" + regNo + "/" + challanId + ".jpg");

        if (UploadUri != null) {
            challanStorage.putFile(UploadUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //hide progress bar

                    progressDialog.dismiss();

                    challanPicUri = taskSnapshot.getDownloadUrl().toString();

                    Toast.makeText(getContext(), "Uploaded", Toast.LENGTH_SHORT).show();

                    //regNoEditText.setText("");


                    /*FragmentManager fm = getFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction().replace(R.id.content_officer_function_display, new DisplayAnimationFragment());
                    ft.commit();*/

                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction().replace(R.id.content_officer_function_display, new DisplayAnimationFragment());
                    ft.commit();



                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    //hide progress bar

                    progressDialog.dismiss();

                    Toast.makeText(getContext(), e.getMessage().toString(), Toast.LENGTH_SHORT).show();


                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    progressDialog.setMessage((int) progress + "% Uploaded");
                }
            });
        } else {
            Toast.makeText(getContext(), "Uri is Null", Toast.LENGTH_SHORT).show();
        }

    }

    private void issueChallan() {

        officer = FirebaseAuth.getInstance().getCurrentUser();

        regNo = regNoEditText.getText().toString().trim().toUpperCase();
        Date = challanDateEditText.getText().toString().trim();
        Time = challanTimeEditText.getText().toString().trim();
        Amount = challanAmountEditText.getText().toString().trim();
        place = placeSpinner.getSelectedItem().toString().trim();
        officerEmailId = officer.getEmail();
        STATUS_CHALLAN = "Unpaid";


        if (!Pattern.matches("^[A-Z]{2}[0-9]{2}[A-Z]{2}[0-9]{4}$", regNo)) {
            regNoEditText.setError("Enter Valid Registration Number");
            regNoEditText.requestFocus();
            progressDialog.hide();
            return;
        }

        if (Date.isEmpty()) {
            challanDateEditText.setError("Required");
            challanDateEditText.requestFocus();
            progressDialog.hide();
            return;
        }
        if (Time.isEmpty()) {
            challanTimeEditText.setError("Required");
            challanTimeEditText.requestFocus();
            progressDialog.hide();
            return;
        }
        if (Amount.isEmpty()) {
            challanAmountEditText.setError("Required");
            challanAmountEditText.requestFocus();
            progressDialog.hide();
            return;
        }



        rootVehicles = FirebaseDatabase.getInstance().getReference().child("Vehicles");

        rootVehicles.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(regNo).exists()) {
                    //ownerAadhaarNo.add(dataSnapshot.child(regNo).child("AadhaarNo").getValue().toString());
                    ownerAadhaarNo = dataSnapshot.child(regNo).child("AadhaarNo").getValue().toString();
                    progressDialog.hide();
                    findChallanID(regNo);
                } else {
                    progressDialog.hide();
                    regNoEditText.setError("Vehicle with thie Number Plate does not exist");
                    regNoEditText.requestFocus();
                    return;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void findChallanID(final String regno) {

        rootVehicles = FirebaseDatabase.getInstance().getReference().child("Challan");

        rootVehicles.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(regno).exists()) {

                    String id = "Challan_";
                    int i =(int)dataSnapshot.child(regno).getChildrenCount()+1;

                    ChallanID = id + i;

                    rootVehicles.child(regno).child(ChallanID).child("AadhaarNo").setValue(ownerAadhaarNo);
                    rootVehicles.child(regno).child(ChallanID).child("Date").setValue(Date);
                    rootVehicles.child(regno).child(ChallanID).child("Fine").setValue(Amount);
                    rootVehicles.child(regno).child(ChallanID).child("OfficerEmailId").setValue(officerEmailId);
                    rootVehicles.child(regno).child(ChallanID).child("Place").setValue(place);
                    rootVehicles.child(regno).child(ChallanID).child("Status").setValue(STATUS_CHALLAN);
                    rootVehicles.child(regno).child(ChallanID).child("Time").setValue(Time);
                    rootVehicles.child(regno).child(ChallanID).child("Type").setValue("No Helmet");

                    progressDialog.setTitle("Uploading Image...");
                    progressDialog.show();
                    progressDialog.setCanceledOnTouchOutside(false);

                    uploadimagetofirebasestorage(regNo,ChallanID);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
