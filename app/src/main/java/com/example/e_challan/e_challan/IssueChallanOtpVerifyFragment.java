package com.example.e_challan.e_challan;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;


public class IssueChallanOtpVerifyFragment extends Fragment {

    EditText codeEditText;
    Button resendCodeButton, verifyCodeButton;

    String phoneNo;
    String emailid;


    private PhoneAuthProvider.OnVerificationStateChangedCallbacks verificationCallbacks;
    private FirebaseAuth auth;
    private DatabaseReference rootVehicles;
    private FirebaseUser officer;
    private PhoneAuthProvider.ForceResendingToken resendToken;

    private String phoneVerificationId;

    private DatabaseReference dbcurrofficer;


    public IssueChallanOtpVerifyFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_issue_challan_otp_verify, container, false);

        codeEditText = (EditText) v.findViewById(R.id.codeEditText);
        resendCodeButton = (Button) v.findViewById(R.id.resendCodeButton);
        verifyCodeButton = (Button) v.findViewById(R.id.verifyCodeButton);

        auth = FirebaseAuth.getInstance();
        officer = auth.getCurrentUser();
        emailid = officer.getEmail();

        getPhoneNo();









        return v;
    }

    private void setUpVerificationCallbacks() {

        verificationCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

                Toast.makeText(getContext(), "Mobile Verification Successful", Toast.LENGTH_SHORT).show();

                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction().replace(R.id.content_officer_function_display, new IssueChallanFragment());
                ft.commit();

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

                if( e instanceof FirebaseAuthInvalidCredentialsException){
                    Toast.makeText(getContext(), "Invalid Code", Toast.LENGTH_SHORT).show();
                    codeEditText.setError("Invalid Code");
                }
                else{
                    Toast.makeText(getContext(), "Error Occurred Try again!!", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {

                phoneVerificationId = verificationId;
                resendToken = token;

            }



        };

        verifyCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = codeEditText.getText().toString();

                PhoneAuthCredential credential =
                        PhoneAuthProvider.getCredential(phoneVerificationId, code);
            }
        });

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNo,
                120,
                TimeUnit.SECONDS,
                getActivity(),
                verificationCallbacks
        );

        Toast.makeText(getContext(), "Verification Code sent to your Mobile", Toast.LENGTH_LONG).show();


    }

    public void getPhoneNo(){

        emailid = emailid.replace(".","");
        emailid = emailid.replace("@","");

        dbcurrofficer = FirebaseDatabase.getInstance().getReference().child("User").child("Number").child(emailid);

        dbcurrofficer.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    phoneNo = dataSnapshot.getValue().toString();

                    setUpVerificationCallbacks();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
