package com.mutshinya.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;

import java.util.List;
import java.util.Locale;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;
    TextView pickUpAddress ;
    TextView destination;
    ImageView menuView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        //get view by ID
        pickUpAddress = findViewById(R.id.textPickUp);
        destination = findViewById(R.id.textDestination);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        //
        menuView = findViewById(R.id.menu);
        menuView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(MapsActivity.this,SettingActivity.class));
            }
        });
        fetchLocation();

        toNextScreen();


    }

    /**
     * Get user current location
     */
    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }

        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {
            if (location != null) {
                currentLocation = location;
                Toast.makeText(getApplicationContext(), currentLocation.getLatitude() + "" + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                assert supportMapFragment != null;
                supportMapFragment.getMapAsync(MapsActivity.this);
            }
        });
    }

    /**
     * What to do when the map is ready
     */
    @SuppressLint({"MissingPermission", "SetTextI18n"})
    @Override
    public void onMapReady(GoogleMap googleMap) {

        // How we move to the next screen
        toNextScreen();
        //Set longitude and latitude
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
        //display location icon
        googleMap.setMyLocationEnabled(true);
        //get location address using coordinates
        String address = getCompleteAddressString(currentLocation.getLatitude(),currentLocation.getLongitude());
        //show address on current location TextView
        pickUpAddress.setText(address);

        //if we can't retrive user current location? else:
        if(address.equals("")){

            //Alert user about their network connection
            Toast.makeText(this,"We can't retrieve your physical Address" , Toast.LENGTH_SHORT).show();
            Toast.makeText(this,"Check your internet connection and try again or " +
                    "set location manually" , Toast.LENGTH_SHORT).show();
            //show the following text on current location TextView
            pickUpAddress.setText("Set your location Manually here");
        }else{
            Toast.makeText(this,address , Toast.LENGTH_SHORT).show();
        }


    }

    /**
     * Request Location permisson
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE:
                // if location granted get user location
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchLocation();
                    // else open setting for user to grant location manually
                }else if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), "");
                    intent.setData(uri);
                    startActivity(intent);
                }
                break;


        }
    }

    /**
     * use coordinates to get user current address
     */
    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder();

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("My Current loction address", strReturnedAddress.toString());
                //Toast.makeText(this, strReturnedAddress.toString(), Toast.LENGTH_SHORT).show();
            } else {
                Log.w("My Current loction address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current location address", "Can not get Address!");
        }
        return strAdd;
    }

    //what data to send to the next class when we move
    public void toNextScreen(){

        pickUpAddress.setOnClickListener(view -> {
            String currentLocation = pickUpAddress.getText().toString();
            Intent intent = new Intent(MapsActivity.this, ChooseLocationActivity.class);
            intent.putExtra("keyLocation",currentLocation);
            startActivity(intent);
        });

        destination.setOnClickListener(view -> {
            String currentLocation = pickUpAddress.getText().toString();
            Intent intent = new Intent(MapsActivity.this, ChooseLocationActivity.class);
            intent.putExtra("keyLocation",currentLocation);
            startActivity(intent);
        });
    }
}