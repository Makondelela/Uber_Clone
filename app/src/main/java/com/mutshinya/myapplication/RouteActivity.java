package com.mutshinya.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.mutshinya.myapplication.databinding.ActivityRouteBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Models.ApiInterface;

import Models.Result;
import Requests.DRequests;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RouteActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityRouteBinding binding;
    private TextView txtRoute;
    private CardView cardView;
    private TextView txtuberGo,txtuber,txtuberX,txtcomfort,txtuberVan;
    private ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = ActivityRouteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map2);
        mapFragment.getMapAsync(this);


        txtuberGo = findViewById(R.id.price1);
        txtuber= findViewById(R.id.price2);
        txtuberX = findViewById(R.id.price3);
        txtcomfort = findViewById(R.id.price4);
        txtuberVan = findViewById(R.id.price5);

        setTextViews();
        takePrice();

    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        direction();
        takePrice();


    }

    /**
     * Manipulates the map once available.
     * get directions from user location to destination
     * draw the route
     */
    private void direction(){

        //get current and destination locations from previous intent
        String pickUpLocation = getIntent().getStringExtra("pLocation");
        String destinationLocation = getIntent().getStringExtra("dLocation");

        //convert current and destination locations to coordinates
        LatLng latLngP = getLatLngFromAddress(pickUpLocation);
        LatLng latLngD = getLatLngFromAddress(destinationLocation);

        //get latitude and longitude co-ordinates for both pickup and drop locations
        Double plat = latLngP.latitude;
        Double dlat = latLngD.latitude;
        Double plng = latLngP.longitude;
        Double dlng = latLngD.longitude;

        //create a string using the coordinates so that we can input them on our API
        String destination = dlat+","+dlng;
        String origin = plat+","+plng;


        //Consuming JSON API for route
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = Uri.parse("https://maps.googleapis.com/maps/api/directions/json")
                .buildUpon()
                .appendQueryParameter("destination",destination)
                .appendQueryParameter("origin",origin)
                .appendQueryParameter("mode","driving")
                .appendQueryParameter("key","[Map_API_KEY]")
                .toString();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    //get API response
                    String status = response.getString("status");
                    //if API response is fine ..
                    if(status.equals("OK")){
                        JSONArray routes = response.getJSONArray("routes");

                        ArrayList<LatLng> points;
                        PolylineOptions polylineOptions = null;

                        for (int i=0; i<routes.length(); i++){
                            points = new ArrayList<>();
                            polylineOptions = new PolylineOptions();
                            JSONArray legs = routes.getJSONObject(i).getJSONArray("legs");

                            for (int j=0; j<legs.length(); j++){
                                JSONArray steps = legs.getJSONObject(j).getJSONArray("steps");

                                for (int k=0; k<steps.length(); k++){
                                    String polyline = steps.getJSONObject(k).getJSONObject("polyline").getString("points");
                                    List<LatLng> list = decodePoly(polyline);

                                    for (int l=0; l<list.size(); l++){
                                        LatLng position = new LatLng((list.get(l)).latitude, (list.get(l)).longitude);
                                        points.add(position);
                                    }
                                }
                            }
                            polylineOptions.addAll(points);
                            polylineOptions.width(10);
                            polylineOptions.color(ContextCompat.getColor(RouteActivity.this, R.color.route));
                            polylineOptions.geodesic(true);

                        }
                        mMap.addPolyline(polylineOptions);
                        mMap.addMarker(new MarkerOptions().position(new LatLng(latLngP.latitude,latLngP.longitude)).title("Marker 1")
                                .icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_baseline_local_shipping_24)));
                        mMap.addMarker(new MarkerOptions().position(new LatLng(latLngD.latitude,latLngD.longitude)).title("Marker 2")
                                .icon(BitmapFromVector(getApplicationContext(), R.drawable.startlocation)));

                        LatLngBounds bounds = new LatLngBounds.Builder()
                                .include(new LatLng(latLngP.latitude,latLngP.longitude))
                                .include(new LatLng(latLngD.latitude,latLngD.longitude)).build();
                        Point point = new Point();
                        getWindowManager().getDefaultDisplay().getSize(point);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds,point.x,1500,30));


                    }
                }catch (JSONException ex){
                    ex.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RetryPolicy retryPolicy = new DefaultRetryPolicy(3000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(retryPolicy);
        requestQueue.add(jsonObjectRequest);
    }

    private List<LatLng> decodePoly(String encoded){
        List<LatLng> poly = new ArrayList<>();
        int index =0, len = encoded.length();
        int lat=0, lng = 0;

        while (index<len){
            int b, shift = 0, result = 0;
            do{
                b=encoded.charAt(index++)-63;
                result |=(b & 0x1f) << shift;
                shift += 5;

            }while (b >= 0x20);
            int dlat = ((result & 1) !=0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;

            do{
                b=encoded.charAt(index++)-63;
                result |=(b & 0x1f) << shift;
                shift += 5;

            }while (b >= 0x20);
            int dlng = ((result & 1) !=0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat/1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }
        return  poly;
    }

    /**
     * convert address to coordinates
     */

    private LatLng getLatLngFromAddress(String address){

        Geocoder geocoder=new Geocoder(RouteActivity.this);
        List<Address> addressList;

        try {
            addressList = geocoder.getFromLocationName(address, 1);
            if(addressList!=null){
                Address single_address=addressList.get(0);
                LatLng latLng=new LatLng(single_address.getLatitude(),single_address.getLongitude());
                return latLng;
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

    private BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {
        // below line is use to generate a drawable.
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);

        // below line is use to set bounds to our vector drawable.
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());

        // below line is use to create a bitmap for our
        // drawable which we have added.
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        // below line is use to add bitmap in our canvas.
        Canvas canvas = new Canvas(bitmap);

        // below line is use to draw our
        // vector drawable in canvas.
        vectorDrawable.draw(canvas);

        // after generating our bitmap we are returning our bitmap.
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    /**
     * using the distance amd relevant values to calculate the price
     */
    private void priceFromDistance(double pricev,double distance,double value){

        //price for Mini Car
        int priceMini;
        double price_Mini = pricev + distance*value;
        int xD = (int) price_Mini;
        //Price conversions
        if(price_Mini-xD< 0.1) {
            priceMini = xD;
        }else {
            priceMini = (int)Math.ceil(price_Mini);

        }
        txtuberGo.setText("R"+Integer.toString(priceMini));


        //price for uber
        int priceuber;
        double price_uber = (pricev + distance*value)*1.15;
        int b = (int) price_uber;
        //Price conversions
        if(price_uber-b< 0.1) {
            priceuber = b;
        }else {
            priceuber = (int)Math.ceil(price_uber);
        }
        txtuber.setText("R"+Integer.toString(priceuber));

        //price for uber
        int priceuberX;
        double price_uberX = (pricev + distance*value)*1.2;
        int c = (int) price_uberX;
        //Price conversions
        if(price_uberX-c< 0.1) {
            priceuberX = c;
        }else {
            priceuberX = (int)Math.ceil(price_uberX);
        }
        txtuberX.setText("R"+Integer.toString(priceuberX));


        //price for uber
        int pricecomfort;
        double price_comfort = (pricev + distance*value)*1.4;
        int d = (int) price_comfort;
        //Price conversions
        if(price_comfort-d< 0.1) {
            pricecomfort = d;
        }else {
            pricecomfort = (int)Math.ceil(price_comfort);
        }
        txtcomfort.setText("R"+Integer.toString(pricecomfort));



        //price for uber
        int priceuberVan;
        double price_uberVan = (pricev + distance*value)*2.0;
        int e = (int) price_uberVan;
        //Price conversions
        if(price_uberVan-e< 0.1) {
            priceuberVan = e;
        }else {
            priceuberVan = (int)Math.ceil(price_uberVan);
        }
        txtuberVan.setText("R"+Integer.toString(priceuberVan));

    }

    /**
     * Get the distance from source to destination
     * @param origin
     * @param destination
     * @param callback
     */
    public void getDistance(String origin, String destination, final DistanceCallback callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/maps/api/distancematrix/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiInterface service = retrofit.create(ApiInterface.class);

        Call<Result> call = service.getDistance(origin, destination, "[YourApiKey]");
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, retrofit2.Response<Result> response) {
                if (response.isSuccessful()) {
                    Result distanceMatrixResponse = response.body();
                    String distanceText = distanceMatrixResponse.getRows().get(0).getElements().get(0).getDistance().getText();
                    String[] parts = distanceText.split(" ");
                    double distance = Double.parseDouble(parts[0].replace(",", ""));
                    callback.onSuccess(distance);
                } else {
                    // Handle the error
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                // Handle the failure
            }
        });
    }

    public interface DistanceCallback {
        void onSuccess(double distance);
    }

    /**
     * Take the relevant source and destination of the delivery
     * and set the textviews
     */
    public void setTextViews(){
        String pickUpLocation = getIntent().getStringExtra("pLocation");
        String destinationLocation = getIntent().getStringExtra("dLocation");

        //convert current and destination locations to coordinates
        LatLng latLngP = getLatLngFromAddress(pickUpLocation);
        LatLng latLngD = getLatLngFromAddress(destinationLocation);
        //Display from and to adress to the user on a textView
        String route = pickUpLocation+"\n To \n"+destinationLocation;
        txtRoute = findViewById(R.id.txtRoute);

        txtRoute.setText(route);

        //get latitude and longitude co-ordinates for both pickup and drop locations
        Double plat = latLngP.latitude;
        Double dlat = latLngD.latitude;
        Double plng = latLngP.longitude;
        Double dlng = latLngD.longitude;

        //create a string using the coordinates so that we can input them on our API
        String destination = dlat+","+dlng;
        String origin = plat+","+plng;

        /*
         * on calling the getDistance method call the priceFromDistance method
         * in it's Interface onSuccess
         */
        getDistance(destination, origin, new DistanceCallback() {

            public void onSuccess(double distance) {

                priceFromDistance(10,distance,5);
            }

        });

    }
    /*
     * Handle the amounts and moving to the next window
     */
    public void takePrice(){

        txtuberGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String price = txtuberGo.getText().toString();
                Intent intent =new Intent(RouteActivity.this,PaymentActivity.class);
                intent.putExtra("price",price);
                startActivity(intent);
                DRequests.addString(4,price);
                DRequests.addString(5,"Mini uber");
            }
        });

        txtuber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String price = txtuber.getText().toString();
                Intent intent =new Intent(RouteActivity.this,PaymentActivity.class);
                intent.putExtra("price",price);
                startActivity(intent);
                DRequests.addString(4,price);
                DRequests.addString(5,"uber");
            }
        });


        txtuberX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String price = txtuberX.getText().toString();
                Intent intent =new Intent(RouteActivity.this,PaymentActivity.class);
                intent.putExtra("price",price);
                startActivity(intent);
                DRequests.addString(4,price);
                DRequests.addString(5,"Mini comfort");
            }
        });

        txtcomfort.setOnClickListener(v -> {
            String price = txtcomfort.getText().toString();
            Intent intent =new Intent(RouteActivity.this,PaymentActivity.class);
            intent.putExtra("price",price);
            startActivity(intent);
            DRequests.addString(4,price);
            DRequests.addString(5,"comfort");
        });

        txtuberVan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String price = txtuberVan.getText().toString();
                Intent intent =new Intent(RouteActivity.this,PaymentActivity.class);
                intent.putExtra("price",price);
                startActivity(intent);
                DRequests.addString(4,price);
                DRequests.addString(5,"Mega comfort");
            }
        });

    }
}
