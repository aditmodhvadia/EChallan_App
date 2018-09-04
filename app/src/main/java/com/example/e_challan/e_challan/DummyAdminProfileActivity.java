package com.example.e_challan.e_challan;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DummyAdminProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    TextView tempDisp;
    int exitCount;
    long currTime,prevTime;

    Button tempButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dummy_admin_profile);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        exitCount=0;
        tempDisp = (TextView) findViewById(R.id.tempDisp);
        tempDisp.setText("Welcome "+user.getEmail());

        tempButton = (Button) findViewById(R.id.tempButton);
        tempButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityForResult(Intent.createChooser(intent,"Select Image"),7);
            }
        });


    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();

        exitCount++;
        if(exitCount==1)
        {
            Toast.makeText(getApplicationContext(),"Press back once more to logout and exit",Toast.LENGTH_SHORT).show();
            prevTime = System.currentTimeMillis();
        }
        if (exitCount==2)
        {
            currTime = System.currentTimeMillis();
            if(currTime-prevTime>2000)
            {
                Toast.makeText(getApplicationContext(),"Press back once more to logout and exit",Toast.LENGTH_SHORT).show();
                prevTime = System.currentTimeMillis();
                exitCount=1;
            }
            else
            {
                FirebaseAuth.getInstance().signOut();
                finish();/*
                Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                homeIntent.addCategory( Intent.CATEGORY_HOME );
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
                finish();*/
            }

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==7 && resultCode== RESULT_OK)
        {

        }
    }
}
