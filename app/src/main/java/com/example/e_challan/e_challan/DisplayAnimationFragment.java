package com.example.e_challan.e_challan;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;


public class DisplayAnimationFragment extends Fragment {


    RelativeLayout rel;
    AnimationDrawable animationDrawable;


    public DisplayAnimationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_display_animation, container, false);

        getActivity().setTitle("Welcome");

        rel = (RelativeLayout) v.findViewById(R.id.mylayout);
        animationDrawable = (AnimationDrawable) rel.getBackground();
        animationDrawable.setEnterFadeDuration(3500);
        animationDrawable.setExitFadeDuration(3500);
        animationDrawable.start();

        return v;
    }

}
