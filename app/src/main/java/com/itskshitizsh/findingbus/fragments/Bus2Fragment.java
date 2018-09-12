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
public class Bus2Fragment extends Fragment implements OnMapReadyCallback {

    private static LatLng ajmeriGate = new LatLng(26.915565, 75.817029);
    static final MarkerOptions ajmeriGateMarker = new MarkerOptions()
            .position(ajmeriGate).title("Ajmeri gate");
    static final CameraPosition target = CameraPosition.builder()
            .zoom(17)
            .target(ajmeriGate)
            .build();
    private boolean mapReady = false;
    private GoogleMap m_map;


    public Bus2Fragment() {
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
        m_map.addMarker(ajmeriGateMarker);
        m_map.animateCamera(CameraUpdateFactory.newCameraPosition(target), 5000, null);
    }
}
