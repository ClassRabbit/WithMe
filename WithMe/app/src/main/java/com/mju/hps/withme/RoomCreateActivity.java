package com.mju.hps.withme;

import android.*;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class RoomCreateActivity extends AppCompatActivity {

    TextView room_title, room_content;

    private Location currentLocation;

    private TextView selectedLocationTextView;
    private Double selectedLatitude;
    private Double selectedLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_create);

        Spinner monthSpinner = (Spinner)findViewById(R.id.number_of_roommate);
        ArrayAdapter room_people = ArrayAdapter.createFromResource(this,
                R.array.number_of_people, android.R.layout.simple_spinner_item);
        room_people.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(room_people);

        selectedLocationTextView = (TextView) findViewById(R.id.selectedLocation);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return ;
        }
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return ;
        }

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);
        currentLocation = locationManager.getLastKnownLocation(provider);

        Intent getLocation = getIntent();
        if (getLocation != null){
            Bundle bundle = getLocation.getExtras();

            if( bundle != null){

                selectedLatitude = bundle.getDouble("lat");
                selectedLongitude =  bundle.getDouble("lng");

                Log.e("Lat", selectedLatitude.toString());
                Log.e("Lng", selectedLongitude.toString());
            }
//
            if(selectedLatitude != null && selectedLongitude != null){
                selectedLocationTextView.setText(getAddress(selectedLatitude, selectedLongitude));
            }
        }

//            String lng = getLocation.getStringExtra("lng");
//
//             = Double.parseDouble(lat);
//            selectedLongitude = Double.parseDouble(lng);
//


    }

    public void create_room(View view) {
        //텍스트뷰 & 맵 & 사진 다 서버로 넘겨주면 될듯?
    }

    public void input_map(View view) {
        Intent googleIntent = new Intent(this, GoogleMapActivity.class);
        LatLng myLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        Bundle args = new Bundle();
        args.putParcelable("myLocation", myLocation);
        googleIntent.putExtra("bundle", args);
        startActivity(googleIntent);
    }

    public void select_photo(View view) {
        //사진 고르고
    }

    private String getAddress(double latitude, double longitude) {
        StringBuilder result = new StringBuilder();
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
//                address.getAddressLine()
                result.append(address.getAdminArea()).append(" ");
                result.append(address.getLocality()).append(" ");
                result.append(address.getFeatureName());
//                result.append(address.getCountryName());
            }
        } catch (IOException e) {
            Log.e("tag", e.getMessage());
        }
        Log.e("Address", result.toString());
        return result.toString();
    }
}
