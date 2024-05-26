package com.example.uber_clone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.DriverLocationActivity;
import com.example.uber_clone.databinding.ActivityRidersBinding;
import com.example.uber_clone.databinding.ActivityViewRequestBinding;
import com.example.uber_clone.interfaces.Callback;
import com.example.uber_clone.interfaces.RecyclerViewClickListener;
import com.parse.Parse;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class ViewRequestActivity extends AppCompatActivity implements RecyclerViewClickListener, LocationListener {
    ActivityViewRequestBinding binding;

    private LocationManager locationManager;
    private RecyclerAdapter recAdapter;

    ArrayList<Double> requestLatitude = new ArrayList<>();
    ArrayList<Double> requestLongitude = new ArrayList<>();
    ArrayList<String> usernames = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewRequestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        initRecView();


    }

    ArrayList<String> recData = new ArrayList<>();
    @SuppressLint("NotifyDataSetChanged")
    private void initRecView() {
        recAdapter = new RecyclerAdapter(this);
        binding.recViewViewRequestActivity.setAdapter(recAdapter);
        binding.recViewViewRequestActivity.setLayoutManager(new LinearLayoutManager(this));
        recData.add("Getting nearby request");
        recAdapter.setAdapterData(recData);
        recAdapter.notifyDataSetChanged();

        checkPermission();
    }

    private void checkPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        } else {
            Location lastKnownLocation = getLocation();
            if (lastKnownLocation != null){
                updateRecView(lastKnownLocation);
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateRecView(Location location){
        //ArrayList<String> distances = new ArrayList<>();
        recData.clear();
        requestLatitude.clear();
        requestLongitude.clear();

        fetchRequestData(location,
                (objS)->{
                    objS.forEach(parseObject -> {
                        //get the distance from user to customer
                        //location need to me in ParseGeoPoint and then call kilometer convert function
                        double distanceInKilometers = new ParseGeoPoint(location.getLatitude(),location.getLongitude()).distanceInKilometersTo(parseObject.getParseGeoPoint("location"));
                        double distanceOneDP = (double) Math.round(distanceInKilometers * 10)/10;
                        recData.add(String.valueOf(distanceOneDP).concat(" kilometer"));
                        Log.d("TAG", "updateRecView: "+String.valueOf(distanceOneDP).concat(" kilometer"));

                        requestLatitude.add(Objects.requireNonNull(parseObject.getParseGeoPoint("location")).getLatitude());
                        requestLongitude.add(Objects.requireNonNull(parseObject.getParseGeoPoint("location")).getLongitude());
                        usernames.add(parseObject.getString("username"));

                    });
                    recAdapter.setAdapterData(recData);
                    recAdapter.notifyDataSetChanged();
                    Log.d("TAG", "updateRecView: "+recAdapter.getItemCount());
        });
    }

    @Override
    public void onRecViewItemClick(int position) {
        Location driversLocation = getLocation();
        if (requestLatitude.size()>position && driversLocation!=null){
            Intent intent = new Intent(this, DriverLocationActivity.class);
            intent.putExtra("requestLatitude",requestLatitude.get(position));
            intent.putExtra("requestLongitude",requestLongitude.get(position));
            intent.putExtra("driverLatitude",driversLocation.getLatitude());
            intent.putExtra("driverLongitude",driversLocation.getLongitude());
            intent.putExtra("username",usernames.get(position));

            startActivity(intent);
        }
    }

    private void fetchRequestData(Location location, Callback<List<ParseObject>> callback){
        if (location!=null){
            binding.progressBarViewRequestActivity.setVisibility(View.VISIBLE);
            ParseQuery<ParseObject> query1 = ParseQuery.getQuery("Request");
            query1.whereNear("location",new ParseGeoPoint(location.getLatitude(),location.getLongitude()));
            query1.whereDoesNotExist("driversUsername");
            query1.orderByDescending("createdAt");
            query1.setLimit(10);


            ParseQuery<ParseObject> query2 = ParseQuery.getQuery("Request");
            query2.whereNear("location",new ParseGeoPoint(location.getLatitude(),location.getLongitude()));
            query2.whereEqualTo("driversUsername", "");

            // Combine queries using ParseQuery.or
            List<ParseQuery<ParseObject>> queries = new ArrayList<>();
            queries.add(query1);
            queries.add(query2);

            //ParseQuery<ParseObject> combinedQuery = ParseQuery.or(queries);
            // Execute the combined query
            //combinedQuery.orderByDescending("createdAt");
            //combinedQuery.setLimit(10);

            query1.findInBackground((objects, e) -> {
                binding.progressBarViewRequestActivity.setVisibility(View.INVISIBLE);
                if (e == null) {
                    //passing object list to other function like adapter
                    //initTodoList(objects);
                    Log.d("TAG", "fetchRequestData: "+objects.size());

                    callback.onCallback(objects);
                } else {
                    Log.d("combinedQuery.findInBackground", "fetchRequestData: "+e.getMessage());
                }
            });
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Location lastKnownLocation = getLocation();
                if (lastKnownLocation != null){
                    updateRecView(lastKnownLocation);
                }
            }
        }
    }
    @Override
    public void onLocationChanged(@NonNull Location location) {
        updateRecView(location);

        ParseUser.getCurrentUser().put("location",new ParseGeoPoint(location.getLatitude(),location.getLongitude()));
        ParseUser.getCurrentUser().saveInBackground();
    }


    private Location getLocation(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocation!=null){
                return lastKnownLocation;
            } else {
                lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (lastKnownLocation!=null){
                    return lastKnownLocation;
                }
            }
        }
        return null;
    }

}