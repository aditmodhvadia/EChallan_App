package com.example.e_challan.e_challan;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Pattern;


public class CivilianLoginFragment extends android.support.v4.app.Fragment {

    Button civilianCheckButton;
    TextView civilianRegNoEditText;

    private DatabaseReference rootVehicles;

    ProgressDialog progressDialog;

    public CivilianLoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_civilian_login, container, false);



        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        civilianCheckButton = (Button) v.findViewById(R.id.civilianCheckButton);
        civilianRegNoEditText = (TextView) v.findViewById(R.id.civilianRegNoEditText);
        progressDialog = new ProgressDialog(getContext());

        civilianCheckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check if regno is valid or not...
                progressDialog.setTitle("Please Wait..");
                progressDialog.setMessage("Getting details");
                progressDialog.show();

                final String regNo = civilianRegNoEditText.getText().toString();

                if (!Pattern.matches("^[A-Z]{2}[0-9]{2}[A-Z]{2}[0-9]{4}$", regNo)) {
                    civilianRegNoEditText.setError("Enter Valid Registration Number");
                    civilianRegNoEditText.requestFocus();
                    progressDialog.hide();
                    return;
                }


                //then check if there is any challan for the car or not

                rootVehicles = FirebaseDatabase.getInstance().getReference().child("Challan");

                rootVehicles.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child(regNo).exists()){
                            progressDialog.hide();
                            Intent challan = new Intent(getContext(),ViewChallanActivity.class);
                            challan.putExtra("RegNo",regNo);

                            startActivity(challan);
                            getActivity().overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                        }
                        else {
                            progressDialog.hide();
                            Toast.makeText(getContext(), "No Challans found", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        });



        return v;
    }

}
