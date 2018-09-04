package com.example.e_challan.e_challan;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    Button btn;
    EditText text;
    DatabaseReference dbr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn = (Button) findViewById(R.id.btn);
        text = (EditText) findViewById(R.id.text);
        dbr= FirebaseDatabase.getInstance().getReference().child("Challan").child("GJ01HK6173");

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //dbr.child("User_01").child("Name").setValue(text.getText().toString());
                //dbr.child("User_01").child("Age").setValue("20");
                //dbr.child("User_02").child("Name").setValue("Avani Shitole");
                //dbr.child("User_02").child("Age").setValue("21");
                dbr.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String id = "Challan_0";
                        int i = 1;
                        while(dataSnapshot.child(id+i).exists())
                        {
                            String value =  dataSnapshot.child(id+i).child("Fine").getValue().toString();
                            text.setText(text.getText().toString()+value);
                            i++;
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }
}
