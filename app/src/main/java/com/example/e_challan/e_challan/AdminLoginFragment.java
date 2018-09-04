package com.example.e_challan.e_challan;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class AdminLoginFragment extends android.support.v4.app.Fragment {

    Button adminLoginButton;
    EditText adminEmailEditText, adminPasswordEditText;
    CheckBox showPassword;

    ProgressDialog progressdialog;

    private FirebaseAuth mAuth;

    //declarations for encryption
    public static byte[] input;
    public static byte[] keyBytes = "12345678".getBytes();
    public static byte[] ivBytes = "input123".getBytes();
    public static SecretKeySpec key = new SecretKeySpec(keyBytes, "DES");
    public static IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
    public static Cipher cipher;
    public static byte[] cipherText;
    public static int ctLength;
    public static String encrypted;
    public static String decrypted;
    public String encryptedpin;
    public String decryptedpin;


    public AdminLoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mAuth = FirebaseAuth.getInstance();

        progressdialog = new ProgressDialog(getContext());

        View v = inflater.inflate(R.layout.fragment_admin_login, container, false);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        adminEmailEditText = (EditText) v.findViewById(R.id.adminEmailEditText);
        adminPasswordEditText = (EditText) v.findViewById(R.id.adminPasswordEditText);
        adminLoginButton = (Button) v.findViewById(R.id.adminLoginButton);
        adminLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginAdmin();
            }
        });

        showPassword = (CheckBox) v.findViewById(R.id.showPassword);

        showPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked)
                {
                    adminPasswordEditText.setTransformationMethod(null);
                }
                else
                {
                    adminPasswordEditText.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        });

        return v;
    }

    private void loginAdmin() {
        String adminEmail = adminEmailEditText.getText().toString().trim();
        String adminPassword = adminPasswordEditText.getText().toString().trim();

        if (adminEmail.isEmpty()) {
            adminEmailEditText.setError("Email is required");
            adminEmailEditText.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(adminEmail).matches() || !adminEmail.equals("adit.modhvadia@gmail.com")) {
            adminEmailEditText.setError("Enter valid Email Address");
            adminEmailEditText.requestFocus();
            return;
        }

        if (adminPassword.isEmpty()) {
            adminPasswordEditText.setError("Password is required");
            adminPasswordEditText.requestFocus();
            return;
        }

        if (adminPassword.length() < 6) {
            adminPasswordEditText.setError("Minimum length of password is 6");
            adminPasswordEditText.requestFocus();
            return;
        }

        progressdialog.setMessage("Logging In");
        progressdialog.setCanceledOnTouchOutside(false);
        progressdialog.show();

        mAuth.signInWithEmailAndPassword(adminEmail, adminPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressdialog.hide();
                if (task.isSuccessful()) {
                    progressdialog.hide();
                    adminPasswordEditText.setText("");
                    adminEmailEditText.setText("");
                    Intent i = new Intent(getContext(), AdminFunctionActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    getActivity().overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                } else {
                    if (task.getException() instanceof FirebaseNetworkException)
                    {
                        Toast.makeText(getContext(), "Internet connectivity required", Toast.LENGTH_SHORT).show();
                    }
                    else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException)
                    {
                        adminPasswordEditText.setError("Incorrect Password");
                        adminPasswordEditText.requestFocus();
                        adminPasswordEditText.setText("");
                    }
                    else if (task.getException() instanceof FirebaseAuthInvalidUserException)
                    {
                        adminEmailEditText.setError("Email ID not registered");
                        adminEmailEditText.requestFocus();
                    }
                    else
                    {
                        Toast.makeText(getContext(), "Some error occurred. Try again", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();
        if(mAuth.getCurrentUser()!=null && mAuth.getCurrentUser().getEmail().equals("adit.modhvadia@gmail.com"))
        {
            startActivity(new Intent(getContext(),AdminFunctionActivity.class));
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        /*if(mAuth.getCurrentUser()!=null && mAuth.getCurrentUser().getEmail().equals("adit.modhvadia@gmail.com"))
        {
            startActivity(new Intent(getContext(),AdminFunctionActivity.class));
        }*/
    }

    //Functions for encryption
    public static String encrypt(String inp) {

        try {
            //Security.addProvider(new org.spongycastle.jce.provider.BouncyCastleProvider());
            input = inp.getBytes();
            key = new SecretKeySpec(keyBytes, "DES");
            ivSpec = new IvParameterSpec(ivBytes);
            cipher = Cipher.getInstance("DES/CTR/NoPadding", "BC");

            cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);

            cipherText = new byte[cipher.getOutputSize(input.length)];

            ctLength = cipher.update(input, 0, input.length, cipherText, 0);

            ctLength += cipher.doFinal(cipherText, ctLength);

            encrypted = new String(cipherText);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return encrypted;
    }

    public static String decrypt(String inp) {
        try {

            input = inp.getBytes();

            cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);

            byte[] plainText = new byte[cipher.getOutputSize(ctLength)];

            int ptLength = cipher.update(cipherText, 0, ctLength, plainText, 0);

            ptLength += cipher.doFinal(plainText, ptLength);

            decrypted = new String(plainText);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return decrypted;
    }

}
