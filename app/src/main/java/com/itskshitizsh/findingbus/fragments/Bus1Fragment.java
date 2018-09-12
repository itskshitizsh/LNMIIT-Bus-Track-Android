package com.itskshitizsh.findingbus.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.itskshitizsh.findingbus.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Bus1Fragment extends Fragment {


    public Bus1Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        TextView labelTextView = rootView.findViewById(R.id.section_label);
        labelTextView.setText("This is bus 1 fragment");
        return rootView;
    }

}
