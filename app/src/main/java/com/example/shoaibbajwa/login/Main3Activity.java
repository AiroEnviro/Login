package com.example.shoaibbajwa.login;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class Main3Activity extends AppCompatActivity implements LocationListener {
    final long MIN_TIME_INTERVAL = 60 * 1000L;
    private boolean isGPSEnabled = false;
    private boolean isNetworkEnabled = false;
    private Location location;          //  location.getLatitude() & location.getLongitude() will give you what you need...
    private LocationManager locationManager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);


        requestForUpdates();

        showLocation();
    }

    public void showLocation(){
        double lat = location.getLatitude();
        double lng = location.getLongitude();

        String lati = String.valueOf(lat);
        String lngi = String.valueOf(lng);

        Toast.makeText(Main3Activity.this, lati+lngi , Toast.LENGTH_SHORT).show();
        return;
    }

    public void requestForUpdates() {

        if (locationManager == null) {
            return;
        }

        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (isNetworkEnabled) {
            if (ActivityCompat.checkSelfPermission(Main3Activity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, Main3Activity.this);
            if (locationManager != null) {
                Location tempLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (tempLocation != null && isBetterLocation(tempLocation,location))
                    location = tempLocation;
            }
        }
        if (isGPSEnabled) {
            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, Main3Activity.this, null);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0, Main3Activity.this);
            if (locationManager != null) {
                Location tempLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (tempLocation != null  && isBetterLocation(tempLocation,location))
                    location = tempLocation;
            }
        }
    }

    public void stopLocationUpdates(){
        if(locationManager!=null){
            locationManager.removeUpdates(Main3Activity.this);
        }
    }

    private boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > MIN_TIME_INTERVAL;
        boolean isSignificantlyOlder = timeDelta < -MIN_TIME_INTERVAL;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate  && isFromSameProvider) {
            return true;
        }
        return false;
    }

    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    @Override
    public void onLocationChanged(Location loc) {
        if (loc != null  && isBetterLocation(loc, location)) {
            location = loc;
        }
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
}