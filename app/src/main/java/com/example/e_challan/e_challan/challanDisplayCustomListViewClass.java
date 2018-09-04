package com.example.e_challan.e_challan;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class challanDisplayCustomListViewClass extends BaseAdapter {

    ArrayList<String> status, fine, challanId, issueDate, challanType;

    TextView issueDateTextView, challanIdTextView, statusTextView, violationTextView, fineTextView;

    Context context;
    LayoutInflater inflater;

    public challanDisplayCustomListViewClass(ArrayList<String> status, ArrayList<String> fine, ArrayList<String> challanId, ArrayList<String> issueDate, ArrayList<String> challanType, Context context) {
        this.status = status;
        this.fine = fine;
        this.challanId = challanId;
        this.issueDate = issueDate;
        this.challanType = challanType;
        this.context = context;
        this.inflater = (LayoutInflater) LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return challanId.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        View vi = view;
        vi = inflater.inflate(R.layout.challan_display_custom_list_view, null);

        issueDateTextView = (TextView) vi.findViewById(R.id.issueDateTextView);
        statusTextView = (TextView) vi.findViewById(R.id.statusTextView);
        challanIdTextView = (TextView) vi.findViewById(R.id.challanIdTextView);
        violationTextView = (TextView) vi.findViewById(R.id.violationTextView);
        fineTextView = (TextView) vi.findViewById(R.id.fineTextView);

        issueDateTextView.setText(issueDate.get(i));
        statusTextView.setText(status.get(i));
        challanIdTextView.setText(challanId.get(i));
        violationTextView.setText(challanType.get(i));
        fineTextView.setText("Rs."+fine.get(i));

        if(status.get(i).equals("Unpaid")){
            statusTextView.setTextColor(Color.RED);
        }
        else {
            statusTextView.setTextColor(Color.GREEN);
        }


        return vi;
    }
}
