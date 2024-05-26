package com.example;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.uber_clone.R;
import com.example.uber_clone.databinding.ActivityDriverLocationBinding;
import com.example.uber_clone.interfaces.Callback;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;

public class DriverLocationActivity extends FragmentActivity implements OnMapReadyCallback {
    private final String TAG = "DriverLocationActivity->";
    private GoogleMap mMap;
    private ActivityDriverLocationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDriverLocationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_driverLocationActivity);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    Intent intent;
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        intent = getIntent();

        LatLng driversLocation = new LatLng(intent.getDoubleExtra("driverLatitude",0), intent.getDoubleExtra("driverLongitude",0));
        LatLng requestLocation = new LatLng(intent.getDoubleExtra("requestLatitude",0), intent.getDoubleExtra("requestLongitude",0));


        ArrayList<Marker> markers = new ArrayList<>();
        markers.add(mMap.addMarker(new MarkerOptions()
                                    .position(driversLocation)
                                    .title("Your location")
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))));

        markers.add(mMap.addMarker(new MarkerOptions().position(requestLocation).title("Request location")));

        //
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker:markers){
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();

        int padding = 30;
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,padding);
        mMap.animateCamera(cu);


        //button
        binding.btnAcceptRequestDriversLocationActivity.setOnClickListener(View->{
            binding.progressBarDriverLocation.setVisibility(android.view.View.VISIBLE);
            acceptRequest();
        });
    }


    private void acceptRequest(){
        saveDriverName(nullResponse->{
            Intent directionIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?saddr="
                            +intent.getDoubleExtra("driverLatitude",0) +","
                            +intent.getDoubleExtra("driverLongitude",0)
                            +"&daddr="
                            +intent.getDoubleExtra("requestLatitude",0) +","
                            +intent.getDoubleExtra("requestLongitude",0)  ));

            binding.progressBarDriverLocation.setVisibility(View.GONE);

            startActivity(directionIntent);

        });
    }

    private void saveDriverName(Callback<Void> callback){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Request");
        query.whereEqualTo("username",intent.getStringExtra("username"));

        query.findInBackground((listObj,e)->{
            if (e==null){
                if (listObj.size()>0){
                    Log.d(TAG, "saveDriverName: request found");
                    for (ParseObject obj:listObj){
                        obj.put("driversUsername", ParseUser.getCurrentUser().getUsername());
                        obj.saveInBackground(saveError->{
                            if (saveError==null){
                                Log.d(TAG, "saveDriverName: driver name save successful");
                                callback.onCallback(null);
                            }
                        });
                    }
                }
            }
        });
    }
}