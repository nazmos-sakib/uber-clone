package com.example.uber_clone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.uber_clone.databinding.ActivityMainBinding;
import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    String TAG = "MainActivity->";
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Objects.requireNonNull(getSupportActionBar()).hide();

        ParseAnalytics.trackAppOpenedInBackground(getIntent());

        //check if a user is logged in
        if (ParseUser.getCurrentUser() == null){
            //login anonymously
            ParseAnonymousUtils.logIn(new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if (e==null){
                        Log.d(TAG, "done: Anonymous login successful");
                    } else {
                        Log.d(TAG, "done: Anonymous login un-successful-> "+e.getMessage());
                    }
                }
            });
        } else {
            if (ParseUser.getCurrentUser().get("rideOrDrive")!=null){
                reDirectActivity();
            }

        }


        binding.btnGetStartedMainActivity.setOnClickListener(View->{
            Log.d(TAG, "onCreate: mode: ".concat(binding.switchRiderDriverMainActivity.isChecked()?"driver":"rider") );
            ParseUser.getCurrentUser().put("rideOrDrive",binding.switchRiderDriverMainActivity.isChecked()?"driver":"rider");
            ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e==null){
                        reDirectActivity();
                    }
                }
            });

        });



    }

    private void reDirectActivity(){
        if (Objects.equals(ParseUser.getCurrentUser().get("rideOrDrive"), "rider")){
            Intent intent = new Intent(getApplicationContext(),RidersActivity.class);
            startActivity(intent);
        } else {
            startActivity(new Intent(getApplicationContext(),ViewRequestActivity.class));
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}