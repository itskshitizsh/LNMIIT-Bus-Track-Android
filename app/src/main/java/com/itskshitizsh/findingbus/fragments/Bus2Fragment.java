package com.itskshitizsh.findingbus.fragments;


import android.annotation.SuppressLint;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.itskshitizsh.findingbus.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Bus2Fragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static LatLng ajmeriGate = new LatLng(26.915565, 75.817029);
    static final MarkerOptions ajmeriGateMarker = new MarkerOptions()
            .position(ajmeriGate).title("Ajmeri gate");
    static final CameraPosition target = CameraPosition.builder()
            .zoom(15)
            .target(ajmeriGate)
            .build();
    private boolean mapReady = false;
    private GoogleMap m_map;

    private LatLng currentLatLng;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Marker currentLocation;


    public Bus2Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        FloatingActionButton fab = rootView.findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mapReady) {
                    m_map.animateCamera(CameraUpdateFactory.newCameraPosition(target), 1000, null);
                } else {
                    Toast.makeText(getContext(), "Please Wait, Map is not ready yet!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        FloatingActionButton satelliteFab = rootView.findViewById(R.id.satellite_fab);
        satelliteFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mapReady) {
                    if (m_map.getMapType() == GoogleMap.MAP_TYPE_NORMAL) {
                        m_map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    } else {
                        m_map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    }
                }
            }
        });

        return rootView;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        m_map = googleMap;
        mapReady = true;
        m_map.addMarker(ajmeriGateMarker);

        m_map.setMyLocationEnabled(true);
        googleApiClient = new GoogleApiClient.Builder(getContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        m_map.animateCamera(CameraUpdateFactory.newCameraPosition(target), 1000, null);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (googleApiClient != null) {
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        if (currentLocation != null) {
            currentLocation.remove();
        }
        currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLatLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        currentLocation = m_map.addMarker(markerOptions);

        Toast.makeText(getContext(), "Location Changed", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        @SuppressLint("MissingPermission") Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (mLastLocation != null) {
            //place marker at current position
            m_map.clear();
            currentLatLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(currentLatLng);
            markerOptions.title("Current Position");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            currentLocation = m_map.addMarker(markerOptions);
        }

        locationRequest = new LocationRequest();
        locationRequest.setInterval(5000); //5 seconds
        locationRequest.setFastestInterval(3000); //3 seconds
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
