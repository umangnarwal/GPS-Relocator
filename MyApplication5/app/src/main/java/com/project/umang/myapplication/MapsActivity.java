package com.project.umang.myapplication;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.media.RingtoneManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;



import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MarkerOptions;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback{

    private GoogleMap mMap;
   private LocationManager lm;
    Double lat;
    Double lon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
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
                public void onLocationChanged(Location location) {
                }
            });}
        else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
        }
#android


        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Select Mock Location App");
        alertDialogBuilder.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS));
                    }
                });

        alertDialogBuilder.setNegativeButton("Already Selected ",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        Button but = (Button)findViewById(R.id.set);
        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lat!=null&&lon!=null)
                {
                setMockLocation(lat,lon,500);}

            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        lat= mMap.getCameraPosition().target.latitude;
        lon= mMap.getCameraPosition().target.longitude;
        mMap.moveCamera(CameraUpdateFactory.newLatLng(mMap.getCameraPosition().target));
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                if(mMap!=null)
                {
                    mMap.clear();
                }
                lat= mMap.getCameraPosition().target.latitude;
                lon= mMap.getCameraPosition().target.longitude;

            }
        });

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 2) {
                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    }

                }
                else {
                    Toast.makeText(this, "not possible", Toast.LENGTH_SHORT).show();
                }
            }

    private void setMockLocation(double latitude, double longitude, float accuracy) {
        final String TEST_PROVIDER =  LocationManager.GPS_PROVIDER;    // "gps"
        if (lm.getProvider(TEST_PROVIDER) != null) {
            lm.removeTestProvider(TEST_PROVIDER);
        }

        lm.addTestProvider
                (
                        LocationManager.GPS_PROVIDER,
                        "requiresNetwork" == "",
                        "requiresSatellite" == "",
                        "requiresCell" == "",
                        "hasMonetaryCost" == "",
                        "supportsAltitude" == "",
                        "supportsSpeed" == "",
                        "supportsBearing" == "",

                        Criteria.POWER_HIGH,
                        Criteria.ACCURACY_HIGH
                );

        Location newLocation = new Location(LocationManager.GPS_PROVIDER);

        newLocation.setLatitude (latitude);
        newLocation.setLongitude(longitude);
        newLocation.setAccuracy(accuracy);
        newLocation.setTime(System.currentTimeMillis());
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            newLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
        }
        newLocation.setAccuracy(500);


        lm.setTestProviderEnabled
                (
                        LocationManager.GPS_PROVIDER,
                        true
                );

       lm.setTestProviderStatus
                (
                        LocationManager.GPS_PROVIDER,
                        LocationProvider.AVAILABLE,
                        null,
                        System.currentTimeMillis()
                );

       lm.setTestProviderLocation
                (
                        LocationManager.GPS_PROVIDER,
                        newLocation
                );

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.common_full_open_on_phone)
                        .setContentTitle("New Location :")
                        .setContentText("Latitude : "+String.valueOf(lat)+"\nLatitude : "+String.valueOf(lon) );
        mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, mBuilder.build());
        Toast.makeText(this, "New Location is Set", Toast.LENGTH_SHORT).show();


    }

}
