package com.example.diabetes.Exercises.Muscle;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.diabetes.R;


public class TricepsExtensionstart extends Fragment {

    Button buttonfinish;

    public static TricepsExtensionstart newInstance() {
        TricepsExtensionstart fragment = new TricepsExtensionstart();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_triceps_extensionstart, container, false);
        buttonfinish = (Button) v.findViewById(R.id.btnfinish);

        buttonfinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStackImmediate();

            }

        });


        return v;
    }
}