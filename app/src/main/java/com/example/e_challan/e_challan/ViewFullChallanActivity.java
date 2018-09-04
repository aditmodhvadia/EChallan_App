package com.example.e_challan.e_challan;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class ViewFullChallanActivity extends AppCompatActivity {

    String challanId, issueTime, place, fine, issueDate, status, aadhaarNo, regNo, officerEmail, displayMessage, challanType;
    ImageView challanImageView;

    TextView challanIdTextView, violationTypeTextView, dateTextView, timeTextView, placeTextView, fineTextView, statusTextView , regNoTextView;
    TextView aadhaarNoTextView;

    Button contactOfficerButton;

    StorageReference challanStorage;

    ProgressDialog pd;

    String Subject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_full_challan);

        pd = new ProgressDialog(ViewFullChallanActivity.this);
        pd.setMessage("Loading");
        pd.setCancelable(false);
        pd.show();
        challanImageView = (ImageView) findViewById(R.id.challanImageView);
        challanIdTextView = (TextView) findViewById(R.id.challanIdTextView);
        violationTypeTextView = (TextView) findViewById(R.id.violationTypeTextView);
        dateTextView = (TextView) findViewById(R.id.dateTextView);
        timeTextView = (TextView) findViewById(R.id.timeTextView);
        placeTextView = (TextView) findViewById(R.id.placeTextView);
        fineTextView = (TextView) findViewById(R.id.fineTextView);
        statusTextView = (TextView) findViewById(R.id.statusTextView);
        regNoTextView = (TextView) findViewById(R.id.regNoTextView);
        aadhaarNoTextView = (TextView) findViewById(R.id.aadhaarNoTextView);
        contactOfficerButton = (Button) findViewById(R.id.contactOfficerButton);

        Intent data = getIntent();

        challanId = data.getExtras().getString("ID");
        issueTime = data.getExtras().getString("Time");
        place = data.getExtras().getString("Place");
        fine = data.getExtras().getString("Fine");
        challanType = data.getExtras().getString("Type");
        issueDate = data.getExtras().getString("IssueDate");
        status = data.getExtras().getString("Status");
        aadhaarNo = data.getExtras().getString("AadhaarNo");
        regNo = data.getExtras().getString("RegNo");
        officerEmail = data.getExtras().getString("OfficerEmail");

        //Setting data
        challanIdTextView.setText(challanIdTextView.getText().toString()+": "+challanId);
        violationTypeTextView.setText(violationTypeTextView.getText().toString()+": "+challanType);
        dateTextView.setText(dateTextView.getText().toString()+": "+issueDate);
        timeTextView.setText(timeTextView.getText().toString()+": "+issueTime);
        placeTextView.setText(placeTextView.getText().toString()+": "+place);
        fineTextView.setText(fineTextView.getText().toString()+": "+fine);
        statusTextView.setText(statusTextView.getText().toString()+": "+status);
        regNoTextView.setText(regNoTextView.getText().toString()+": "+regNo);
        aadhaarNoTextView.setText(aadhaarNoTextView.getText().toString()+": "+aadhaarNo);


        challanStorage = FirebaseStorage.getInstance().getReference("ChallanImages/" + regNo + "/" + challanId + ".jpg");


        final File localFile;
        try {
            localFile = File.createTempFile("images", "jpg");

            challanStorage.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            // Successfully downloaded data to local file
                            // ...
                            Bitmap bitmap = null;
                            try {
                                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), android.net.Uri.parse(localFile.toURI().toString()));
                                pd.hide();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            challanImageView.setImageBitmap(bitmap);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle failed download
                    // ...
                    if(exception instanceof FirebaseNetworkException){
                        Toast.makeText(ViewFullChallanActivity.this, "Internet connectivity required", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(ViewFullChallanActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

        contactOfficerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Subject = "Query for Challan of "+regNo+" "+challanId;
                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{ officerEmail});
                email.putExtra(Intent.EXTRA_CC, new String[]{ "adit.modhvadia@gmail.com"});
                email.putExtra(Intent.EXTRA_SUBJECT, Subject);

                //need this to prompts email client only
                email.setType("message/rfc822");

                startActivity(Intent.createChooser(email, "Choose an Email client :"));
            }
        });

    }

    @Override
    public  void finish()
    {
        super.finish();
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
    }
}
