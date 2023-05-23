package com.mutshinya.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import Adapter.PlaceAutoSuggestAdapter;
import Requests.DRequests;

public class ChooseLocationActivity extends AppCompatActivity {

    AutoCompleteTextView editPickUp;
    AutoCompleteTextView editDestination;
    TextView userName;
    String validateLocation = "New York";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_location);

        //find view by ids
        editPickUp = findViewById(R.id.editPickUp);
        editDestination = findViewById(R.id.editDestination);
        userName = findViewById(R.id.username);

        //get location from previous page from textView to editPickUp
        String currentLocation = getIntent().getStringExtra("keyLocation");
        editPickUp.setText(currentLocation);

        getUserName();


        // Show location suggestion
        editPickUp.setAdapter(new PlaceAutoSuggestAdapter(ChooseLocationActivity.this, android.R.layout.simple_list_item_1));
        editDestination.setAdapter(new PlaceAutoSuggestAdapter(ChooseLocationActivity.this, android.R.layout.simple_list_item_1));


        // Show location suggestion
        editPickUp.setAdapter(new PlaceAutoSuggestAdapter(ChooseLocationActivity.this, android.R.layout.simple_list_item_1));
        editDestination.setAdapter(new PlaceAutoSuggestAdapter(ChooseLocationActivity.this, android.R.layout.simple_list_item_1));

        editPickUp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                handleLocationSelection(editPickUp.getText().toString(), editDestination.getText().toString());
            }
        });

        editDestination.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                handleLocationSelection(editPickUp.getText().toString(), editDestination.getText().toString());
            }
        });

    }
        /**
         * convert address to coordinates
         */

    private LatLng getLatLngFromAddress(String address){

        Geocoder geocoder=new Geocoder(ChooseLocationActivity.this);
        List<Address> addressList;

        try {
            addressList = geocoder.getFromLocationName(address, 1);
            if(addressList!=null){
                Address single_address=addressList.get(0);
                LatLng latLng=new LatLng(single_address.getLatitude(),single_address.getLongitude());
                return latLng;
            }
            else{
                Toast.makeText(this, "Location field empty", Toast.LENGTH_SHORT).show();
                return null;
            }
        }
        catch (Exception e){

            return getLatLngFromAddress(validateLocation);
        }

    }

    public void handleLocationSelection (String pickUpLocation, String destinationLocation){
        if (pickUpLocation.equals("")) {
            Toast.makeText(ChooseLocationActivity.this, "Starting location is empty", Toast.LENGTH_SHORT).show();
        }else if (destinationLocation.equals("") ||destinationLocation.equals("Your Destination")) {
            Toast.makeText(ChooseLocationActivity.this, "Destination location is empty", Toast.LENGTH_SHORT).show();
        } else if (!pickUpLocation.equals("") && !destinationLocation.equals("")) {
            LatLng latLng = getLatLngFromAddress(pickUpLocation);
            String validP = latLng.toString();

            LatLng latLng1 = getLatLngFromAddress(destinationLocation);
            String validD = latLng1.toString();

            if (!latLng.equals(getLatLngFromAddress(validateLocation)) && !latLng1.equals(getLatLngFromAddress(validateLocation))) {
                DRequests.addString(0, pickUpLocation);
                DRequests.addString(1, destinationLocation);
                DRequests.addString(2, validP);
                DRequests.addString(3, validD);
                Intent intent = new Intent(ChooseLocationActivity.this, RouteActivity.class);
                intent.putExtra("pLocation", pickUpLocation);
                intent.putExtra("dLocation", destinationLocation);
                startActivity(intent);
            } else if (latLng.equals(getLatLngFromAddress(validateLocation)) && latLng1.equals(getLatLngFromAddress(validateLocation))) {
                Toast.makeText(ChooseLocationActivity.this, "Starting and destination location don't exist", Toast.LENGTH_SHORT).show();
            } else if (latLng1.equals(getLatLngFromAddress(validateLocation)) && !latLng.equals(getLatLngFromAddress(validateLocation))) {
                Toast.makeText(ChooseLocationActivity.this, "Destination location doesn't exist", Toast.LENGTH_SHORT).show();
            } else if (latLng.equals(getLatLngFromAddress(validateLocation)) && !latLng1.equals(getLatLngFromAddress(validateLocation))) {
                Toast.makeText(ChooseLocationActivity.this, "Starting location doesn't exist", Toast.LENGTH_SHORT).show();
            }
        }   else {
            Toast.makeText(ChooseLocationActivity.this, "Fields are empty", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Convert coordinates to Address
     */
    private Address getAddressFromLatLng(LatLng latLng){
        Geocoder geocoder=new Geocoder(ChooseLocationActivity.this);
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 5);
            if(addresses!=null){
                Address address=addresses.get(0);
                return address;
            }
            else{
                return null;
            }
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    /**
     * Get UserName the user name of a current customer
     * greet the user with their UserName
     */

    private void getUserName(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();

            userName.setText("Hey "+name);
        }
    }

}