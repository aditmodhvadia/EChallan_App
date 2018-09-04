package com.example.e_challan.e_challan;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewChallanActivity extends AppCompatActivity {

    ArrayList<String> status, fine, challanId, issueDate, challanType;

    private DatabaseReference rootVehicles;

    ProgressDialog progressDialog;

    String regNo;
    ListView challanDisplayListView;
    challanDisplayCustomListViewClass displayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_challan);

        progressDialog = new ProgressDialog(ViewChallanActivity.this);

        progressDialog.setTitle("Please Wait..");
        progressDialog.setMessage("Getting details");
        progressDialog.show();

        Intent data = getIntent();

        regNo = data.getExtras().getString("RegNo");

        rootVehicles = FirebaseDatabase.getInstance().getReference().child("Challan");

        status = new ArrayList<String>();
        fine = new ArrayList<String>();
        challanId = new ArrayList<String>();
        issueDate = new ArrayList<String>();
        challanType = new ArrayList<String>();


        rootVehicles.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int child = (int) dataSnapshot.child(regNo).getChildrenCount();
                String ID = "Challan_";
                String temp;
                for (int i = 1; i <= child; i++) {
                    temp = ID + i;
                    challanId.add(dataSnapshot.child(regNo).child(temp).getKey().toString());
                    status.add(dataSnapshot.child(regNo).child(temp).child("Status").getValue().toString());
                    fine.add(dataSnapshot.child(regNo).child(temp).child("Fine").getValue().toString());
                    issueDate.add(dataSnapshot.child(regNo).child(temp).child("Date").getValue().toString());
                    challanType.add(dataSnapshot.child(regNo).child(temp).child("Type").getValue().toString());


                }
                if (challanId.size() == child) {
                    //call the listview over here my buoy
                    challanDisplayListView = (ListView) findViewById(R.id.challanDisplayListView);

                    displayAdapter = new challanDisplayCustomListViewClass(status, fine, challanId, issueDate, challanType, getApplicationContext());

                    challanDisplayListView.setAdapter(displayAdapter);
                    progressDialog.hide();

                    challanDisplayListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {

                            rootVehicles.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    Intent fullDisplay = new Intent(ViewChallanActivity.this, ViewFullChallanActivity.class);
                                    fullDisplay.putExtra("ID", challanId.get(i));
                                    fullDisplay.putExtra("Status", status.get(i));
                                    fullDisplay.putExtra("Fine", fine.get(i));
                                    fullDisplay.putExtra("RegNo",regNo);
                                    fullDisplay.putExtra("IssueDate", issueDate.get(i));
                                    fullDisplay.putExtra("Type",challanType.get(i));
                                    fullDisplay.putExtra("ChallanType", challanType.get(i));
                                    fullDisplay.putExtra("Time", dataSnapshot.child(regNo).child(challanId.get(i)).child("Time").getValue().toString());
                                    fullDisplay.putExtra("Place", dataSnapshot.child(regNo).child(challanId.get(i)).child("Place").getValue().toString());
                                    fullDisplay.putExtra("AadhaarNo", dataSnapshot.child(regNo).child(challanId.get(i)).child("AadhaarNo").getValue().toString());
                                    fullDisplay.putExtra("OfficerEmail",dataSnapshot.child(regNo).child(challanId.get(i)).child("OfficerEmailId").getValue().toString());

                                    startActivity(fullDisplay);
                                    overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });


                        }
                    });

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
