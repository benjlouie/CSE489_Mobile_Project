package karouie.theftdetect;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.*;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.ArrayList;

public class BackgroundService extends Service {

    private static BackgroundThread bg_operations;
    private LocationManager locationManager;
    private String provider;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10F;

    private class LocationListener implements android.location.LocationListener {
        Location lastLocation;

        public LocationListener(String provider)
        {
            Log.d("LocationListener", "LocationListener " + provider);
            lastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.d("LocationListener", "onLocationChanged: " + location);
            lastLocation.set(location);
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d("LocationListener", "onStatusChanged: " + provider);
        }
        @Override
        public void onProviderEnabled(String provider) {
            Log.d("LocationListener", "onProviderEnabled: " + provider);
        }
        @Override
        public void onProviderDisabled(String provider) {
            Log.d("LocationListener", "onProviderDisabled: " + provider);
        }
    }

    LocationListener[] locationListeners = new LocationListener[] {
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    @Override
    public void onCreate() {
        if (locationManager == null) {
            locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }


        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //do stuff here

        if (bg_operations == null) {
            Log.i("BackGorundService.start", "created new bg_thread");
            bg_operations = new BackgroundThread(this, 10); //10 minutes for testing purposes
        }
        if (!bg_operations.isAlive()) {
            Log.i("BackGorundService.start", "starting bg_thread");
            bg_operations.updateContext(this);
            bg_operations.start();
        } else {
            Log.i("BackGorundService.start", "thread already going");
        }

        return super.onStartCommand(intent, flags, startId);
    }

    public void startListeners() {
        //Taken from stack overflow
        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE, locationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.e("BackgroundService", "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.e("BackgroundService", "network provider does not exist, " + ex.getMessage());
        }
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE, locationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.e("BackgroundService", "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.e("BackgroundService", "gps provider does not exist " + ex.getMessage());
        }
    }

    public void stopListeners() {
        for(LocationListener listener : locationListeners) {
            locationManager.removeUpdates(listener);
        }
    }

    public Location getLocation() {
        Location location;
        location = locationListeners[0].lastLocation;
        if(location == null) {
            location = locationListeners[1].lastLocation;
            if(location == null) {
                return null;
            }
        }
        return location;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //restart service if it's stopped
        if(locationManager != null) {
            for(int i = 0; i < locationListeners.length; i++) {
                try{
                    locationManager.removeUpdates(locationListeners[i]);
                } catch (SecurityException e) {
                    Log.i("BackGorundService.start", "fail to remove location listners, ignore", e);
                }
            }
        }

        sendBroadcast(new Intent("Restart_BackgroundService"));
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");

        return null;
    }

}


class BackgroundThread extends Thread {
    private Context context;
    private Location previousLocation;
    private long sleepTime;
    private final long LOCATION_TEST_INTERVAL = 5000; //5 sec
    private final long TRIAL_PERIOD_TIME = 3600000;
    private final double MAX_LAT_DIFF = 0.1;
    private final double MAX_LNG_DIFF = 0.1;

    BackgroundThread(Context context) {
        this(context, 10);
    }

    BackgroundThread(Context context, long sleepTime_mins) {
        this.context = context;
        sleepTime = sleepTime_mins * 60000;
        //sleepTime = 5000; //5 sec
    }

    public void updateContext(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        ProfileDb db = new ProfileDb(context);
        boolean duringTrial = db.getTrialRun();

        //collect data and test for theft here
        while (true) {
            Location location = getLocation();
            long trialStartTime = db.getTrialTime();
            long curTime = System.currentTimeMillis();

            if(duringTrial) {
                addToTrial(location);
                if (curTime - trialStartTime > TRIAL_PERIOD_TIME) {
                    duringTrial = false;
                    transferModel();
                    db.setTrialRun(false);
                }
            } else {
                runModel(location);
            }

            serviceMain();
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void addToTrial(Location location) {
        ProfileDb db = new ProfileDb(context);
        boolean success = db.insertGpsOutside(new GpsData(location.getLatitude(), location.getLongitude(), location.getTime()));
        if(!success) {
            Log.e("backgroundService", "addToTrial(): failed to insert gps location ");
        }
    }

    public void transferModel() {
        ProfileDb db = new ProfileDb(context);
        ArrayList<GpsData> locs = db.getAllGpsOutside();
        db.deleteAllGpsOutside(); //remove from outside-table

        //get centroid
        double latAvg = 0.0;
        double lngAvg = 0.0;
        for(GpsData loc : locs) {
            latAvg += loc.latitude;
            lngAvg += loc.longitude;
        }
        latAvg /= locs.size();
        lngAvg /= locs.size();

        //get avg distance from centroid
        double avgRadius = 0.0;
        for(GpsData loc : locs) {
            double latDist = Math.abs(loc.latitude - latAvg);
            double lngDist = Math.abs(loc.longitude - lngAvg);
            avgRadius += Math.sqrt(latDist * latDist + lngDist * lngDist);
        }
        avgRadius /= locs.size();

        //TODO: determine what size should be put as radius for all points

        //transfer locations to other db table
        for(GpsData loc : locs) {
            loc.radius = avgRadius;
            db.insertGps(loc);
        }
    }

    public void runModel(Location location) {
        ProfileDb db = new ProfileDb(context);
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        //get nearby locations
        ArrayList<GpsData> nearbyLocs = db.getGPSWithin(lat - MAX_LAT_DIFF, lat + MAX_LAT_DIFF,
                lng - MAX_LNG_DIFF, lng + MAX_LNG_DIFF);

        //test if cur location is within any of the nearby points
        boolean withinMap = false;
        for(GpsData point : nearbyLocs) {
            double latDist = Math.abs(location.getLatitude() - point.latitude);
            double lngDist = Math.abs(location.getLongitude() - point.longitude);
            double distance = Math.sqrt(latDist * latDist + lngDist * lngDist);
            if(distance < point.radius) {
                withinMap = true;
            }
        }
        if(withinMap) {
            //TODO: within nearby points, do something
        } else {
            //outside nearby points, or nothing nearby
            //add to gpsOutside
            db.insertGpsOutside(new GpsData(location.getLatitude(), location.getLongitude(), location.getTime()));
        }
    }

    public Location getLocation() {
        BackgroundService bg = (BackgroundService) context;
        Location location;

        bg.startListeners();
        location = bg.getLocation();
        while(location == null
                || (location.getLatitude() == 0.0 && location.getLongitude() == 0.0)
                || (previousLocation != null && location.getTime() == previousLocation.getTime())) {
            try {
                Thread.sleep(LOCATION_TEST_INTERVAL); //sleep for a bit then try again
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            location = bg.getLocation();
        }
        bg.stopListeners();

        return location;
    }

    //TODO: set up everything to handle the gps algorithm stuff
    public void serviceMain() {
        BackgroundService bg = (BackgroundService) context;
        Location location = bg.getLocation();
        ProfileDb db = new ProfileDb(context);

        //check if location has changed
        if(location == null) {
            Log.d("ServiceMain", "null location");
            return;
        }

        double lat = location.getLatitude();
        double lng = location.getLongitude();
        if (previousLocation == null) {
            previousLocation = new Location(location);
            if(location.getTime() != 0) {
                db.insertGps(new GpsData(lat, lng, location.getTime()));
            }
        } else {
            if(lat == previousLocation.getLatitude()
                    && lng == previousLocation.getLongitude()) {
                //equivalent coordinates, ignore current result
                Log.d("ServiceMain", "no change to location: " + lat + " " + lng);
            } else {
                previousLocation.set(location);
                if(location.getTime() != 0) {
                    db.insertGps(new GpsData(lat, lng, location.getTime()));
                }
            }
        }
    }

}