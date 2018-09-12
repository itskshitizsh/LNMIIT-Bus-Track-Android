package com.itskshitizsh.findingbus.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.itskshitizsh.findingbus.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Bus3Fragment extends Fragment implements OnMapReadyCallback {

    private static LatLng rajaPark = new LatLng(26.897476, 75.831578);
    static final MarkerOptions rajaParkMarker = new MarkerOptions()
            .position(rajaPark).title("Raja Park");
    static final CameraPosition target = CameraPosition.builder()
            .zoom(17)
            .target(rajaPark)
            .build();
    private boolean mapReady = false;
    private GoogleMap m_map;
    public Bus3Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        m_map = googleMap;
        mapReady = true;
        m_map.addMarker(rajaParkMarker);
        m_map.animateCamera(CameraUpdateFactory.newCameraPosition(target), 5000, null);

    }
}
