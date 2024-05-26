package com.example.uber_clone;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseUser;

import android.app.Application;

public class AppStarter extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        //Enable local datastore
        Parse.enableLocalDatastore(this);

        //Initializing connection at onCreate
        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                .applicationId(getString(R.string.back4app_app_id))
                .clientKey(getString(R.string.back4app_client_key))
                .server(getString(R.string.back4app_server_url))
                .build());

        //ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new  ParseACL();
        defaultACL.setPublicReadAccess(true);
        defaultACL.setPublicWriteAccess(true);
        ParseACL.setDefaultACL(defaultACL,true);

    }
}
