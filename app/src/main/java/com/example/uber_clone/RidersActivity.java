package com.example.uber_clone;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.uber_clone.interfaces.Callback;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.uber_clone.databinding.ActivityRidersBinding;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class RidersActivity extends FragmentActivity implements OnMapReadyCallback,LocationListener {
    private final String TAG = "RidersActivity->";

    private GoogleMap mMap;
    private ActivityRidersBinding binding;

    private LocationManager locationManager;
    private boolean isUberRequestActive = false;
    private boolean isDriverActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRidersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_riderActivity);
        mapFragment.getMapAsync(this);

        //check if the user has already requested a request
        findActiveRide(returnObjects->{
            Log.d(TAG, "onCreate: findActiveRide-> active request found");
            binding.btnCallAnUberRiderActivity.setText(getString(R.string.cancel_uber));
            isUberRequestActive = true;
            fetchDriverLocationContinuously();
        });

        //set button click call an uber
        binding.btnCallAnUberRiderActivity.setOnClickListener(View->{
            if (!isUberRequestActive){
                //create a parse object that will take user location
                Location lastKnownLocation = getLocation();
                if (lastKnownLocation != null){
                    //ParseUser.getCurrentUser().put("location",new );
                    requestARide(lastKnownLocation,
                            nullArg->{
                            binding.btnCallAnUberRiderActivity.setText(getString(R.string.cancel_uber));
                            isUberRequestActive = true;
                            fetchDriverLocationContinuously();
                    });
                } else {
                    Toast.makeText(this,"Couldnt find location. Please try again later",Toast.LENGTH_SHORT).show();
                }
            } else {
                findActiveRide(returnObjects->{

                    for (ParseObject obj: returnObjects){
                        obj.deleteInBackground();
                    }

                    binding.btnCallAnUberRiderActivity.setText(getString(R.string.get_an_uber));
                    isUberRequestActive = false;
                });
            }

        }); // end button click

        //log out button click
        binding.btnLogOutRiderActivity.setOnClickListener(View->{
            driverLocationTimer = null;
            ParseUser.logOut();
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
        });

        //fab current location
        binding.fabCurrentLocationRidersActivity.setOnClickListener(View->{
            updateMap(getLocation());
        });
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
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("ServiceCast")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        Log.d("TAG", "onMapReady: ");
        mMap = googleMap;

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        } else {
            Location lastKnownLocation = getLocation();
            if (lastKnownLocation != null){
                updateMap(lastKnownLocation);
            }
        }
    }

    private void updateMap(Location location) {
        if (!isDriverActive){
            Log.d("TAG", "updateMap: "+location.toString());
            LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.clear();
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,13));
            mMap.addMarker(new MarkerOptions().position(userLocation).title("Your location"));
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Location lastKnownLocation = getLocation();
                if (lastKnownLocation != null){
                    updateMap(lastKnownLocation);
                }
            }
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        updateMap(location);
    }

    private Location getLocation(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocation!=null){
                return lastKnownLocation;
            } else {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
                lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (lastKnownLocation!=null){
                     return lastKnownLocation;
                }
            }
        }
        return null;
    }

    private void requestARide(Location location, Callback<Void> callback){
        ParseObject req = new ParseObject("Request");
        req.put("username", ParseUser.getCurrentUser().getUsername());
        req.put("location",new ParseGeoPoint(location.getLatitude(),location.getLongitude()));
        req.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e==null){
                    callback.onCallback(null);
                }
            }
        });
    }

    private void findActiveRide(Callback<List<ParseObject>> callback){
        ParseQuery<ParseObject> query = new ParseQuery<>("Request");
        query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e==null){
                    if (objects.size()>0){
                        callback.onCallback(objects);
                    }
                }
            }
        });
    }

    Timer driverLocationTimer;
    private void fetchDriverLocationContinuously(){
        driverLocationTimer = new Timer();
        driverLocationTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkForUpdate();
            }
        },0,2*1000); //9 second
    }

    private void checkForUpdate(){
        Log.d(TAG, "checkForUpdate: checking for update");
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Request");
        query.whereEqualTo("username",ParseUser.getCurrentUser().getUsername());
        query.whereExists("driversUsername");
        query.findInBackground((listObj,e)->{
            if (e==null){
                if (listObj.size()>0){
                    Log.d(TAG, "checkForUpdate: someone has excepted you request");
                    isDriverActive = true;
                    getDriverLocation(listObj.get(0));
                }
            }
        });
    }

    private void getDriverLocation(ParseObject obj){
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username",obj.getString("driversUsername"));
        query.findInBackground((listObj,e)->{
            if (e==null){
                if (listObj.size()>0){
                    ParseGeoPoint driverLocation = listObj.get(0).getParseGeoPoint("location");
                    Location userLocation = getLocation();
                    if (userLocation!=null && driverLocation != null){
                        double distanceInKilometers = driverLocation.distanceInKilometersTo(new ParseGeoPoint(userLocation.getLatitude(),userLocation.getLongitude()));
                        double distanceOneDP = (double) Math.round(distanceInKilometers * 10)/10;

                        if (distanceInKilometers<0.01){
                            //show text
                            binding.tvInfoRiderActivity.setVisibility(View.VISIBLE);
                            binding.tvInfoRiderActivity.setText(R.string.your_driver_is_here);

                            //after 800ms
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    deleteActiveRequest(nullCallback->{
                                        // do stuff
                                        //show text
                                        binding.tvInfoRiderActivity.setVisibility(View.INVISIBLE);
                                        binding.btnCallAnUberRiderActivity.setVisibility(View.VISIBLE);
                                        binding.btnCallAnUberRiderActivity.setText(R.string.get_an_uber);
                                        isUberRequestActive = false;
                                        isDriverActive = false;

                                        driverLocationTimer = null;
                                    });
                                }
                            }, 8*1000);



                        } else {
                            //show text
                            binding.tvInfoRiderActivity.setVisibility(View.VISIBLE);
                            binding.tvInfoRiderActivity.setText(getString(R.string.your_driver_is).concat(String.valueOf(distanceOneDP).concat(getString(R.string.kilometer_way))));
                            binding.btnCallAnUberRiderActivity.setVisibility(View.INVISIBLE);

                            updateMarker(driverLocation,new ParseGeoPoint(userLocation.getLatitude(),userLocation.getLongitude()));
                        }



                    }
                }
            }
        });
    }

    private void updateMarker(ParseGeoPoint driverLocation, ParseGeoPoint userLocation){
        //update marker
        LatLng driversLocation_ = new LatLng(driverLocation.getLatitude(), driverLocation.getLongitude());
        LatLng userLocation_ = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());


        ArrayList<Marker> markers = new ArrayList<>();
        mMap.clear();
        markers.add(mMap.addMarker(new MarkerOptions()
                .position(userLocation_)
                .title("Your location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))));

        markers.add(mMap.addMarker(new MarkerOptions().position(driversLocation_).title("Driver location")));

        //
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker:markers){
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();

        int padding = 30;
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,padding);
        mMap.animateCamera(cu);
    }

    private void deleteActiveRequest(Callback<Void> callback){
        ParseQuery<ParseObject> query = new ParseQuery<>("Request");
        query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        for (ParseObject object:objects){
                            object.deleteInBackground();
                        }
                        callback.onCallback(null);
                    }
                }
            }
        });
    }


    private void saveUserCurrentLocation(Location location){
        ParseUser.getCurrentUser().put("location",new ParseGeoPoint(location.getLatitude(),location.getLongitude()));
        ParseUser.getCurrentUser().saveInBackground();
    }


}