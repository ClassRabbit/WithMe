package com.mju.hps.withme;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class GoogleMapActivity extends FragmentActivity implements OnMapReadyCallback {
    private MapFragment mapFragment;
    private GoogleMap mMap;
    private ImageView pin;
    private TextView pinInfo;

    private ArrayList<String> addressList = new ArrayList();
    private AutoCompleteTextView input;

    Intent sendIntent;

    private static final int REQUEST_CODE_LOCATION = 2;
    private LatLng myLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map);

        Bundle bundle = getIntent().getParcelableExtra("bundle");
        myLocation = bundle.getParcelable("myLocation");

        mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        pin = (ImageView)findViewById(R.id.pin);
        pin.setOnClickListener(new pinListener());

        sendIntent = new Intent(this, RoomCreateActivity.class);

        pinInfo = (TextView)findViewById(R.id.pinInfo);
        pinInfo.setOnClickListener(new pinInfoListener());

        input = (AutoCompleteTextView)findViewById(R.id.searchEditText);
        input.setThreshold(2);

        pinInfo.setVisibility(View.GONE);

//        setupAutoCompleteTextView(input);


//        ArrayAdapter adapter = new ArrayAdapter(this,  android.R.layout.simple_dropdown_item_1line , addressList);
//        android.R.layout.simple_list_item_1
//        input.setAdapter(adapter);
    }



    class pinInfoListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Double selectedLat = mMap.getCameraPosition().target.latitude;
            Double selectedLng = mMap.getCameraPosition().target.longitude;
            Log.i("Get Center", mMap.getCameraPosition().target.toString());

            Bundle bundle = new Bundle();
            bundle.putDouble("lat",selectedLat);
            bundle.putDouble("lng",selectedLng);
            sendIntent.putExtras(bundle);
//            sendIntent.putExtra("isF")
            //
            // 여기서부터 시작
            //
            startActivity(sendIntent);
        } // end onClick
    }

    class pinListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            pinInfo.setVisibility(View.VISIBLE);
//            String selectedLatLng = mMap.getCameraPosition().target.toString();
//            Log.i("Get Center", mMap.getCameraPosition().target.toString());
//            startActivity(sendIntent.putExtra("value", selectedLatLng));
        } // end onClick
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }

        mMap = googleMap;
        /*
        mMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
                pinInfo.setVisibility(View.GONE);
            }
        });
        */
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                pinInfo.setVisibility(View.GONE);
            }
        });

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom( myLocation, 16));
//        mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);

        mMap.setMyLocationEnabled(true);

    }


    public void onMapSearch(View view) {
        //Key Board Hide
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(input.getWindowToken(), 0);

        String location = input.getText().toString();
        input.setText("");
        List<Address> addressList = null;
        if (location != null || !location.equals("")) {
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(location, 1);
                if (addressList.size() > 0){
                    LatLng latLng = new LatLng( addressList.get(0).getLatitude(), addressList.get(0).getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom( latLng, 16));
                }
                else {
                    Toast.makeText(this, "위치 검색 실패", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*
    public void setupAutoCompleteTextView(AutoCompleteTextView autoCompleteTextView) {

        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line , addressList);
//        android.R.layout.simple_list_item_1
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setThreshold(2);
        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                getAddressInfo(GoogleMapActivity.this, s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
    */
    /*
    private void getAddressInfo(Context context, String locationName){
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());

        try {
            List<android.location.Address> a = geocoder.getFromLocationName(locationName, 5);

            for(int i=0;i<a.size();i++){
                String city = a.get(0).getLocality();
                String country = a.get(0).getCountryName();
                String address = a.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                addressList.add(address+", "+city+", "+country);
                Log.i("Address", address+", "+city+", "+country);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    */

}