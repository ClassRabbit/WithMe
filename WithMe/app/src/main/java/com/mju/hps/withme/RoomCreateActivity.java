package com.mju.hps.withme;

import android.*;
import android.app.Activity;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.iid.FirebaseInstanceId;
import com.mju.hps.withme.constants.Constants;
import com.mju.hps.withme.database.DatabaseLab;
import com.mju.hps.withme.room.RoomCreatePhotoAdapter;
import com.mju.hps.withme.server.ServerManager;
import com.yongbeam.y_photopicker.util.photopicker.PhotoPickerActivity;
import com.yongbeam.y_photopicker.util.photopicker.utils.YPhotoPickerIntent;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import me.iwf.photopicker.PhotoPreview;

import static com.mju.hps.withme.constants.Constants.REQUEST_CODE_PHOTO_PICKER;
import static com.mju.hps.withme.model.User.user;

public class RoomCreateActivity extends AppCompatActivity {

    private EditText roomTitle, roomContent;
    private Location currentLocation;

    private TextView selectedLocationTextView;
    private Double selectedLatitude;
    private Double selectedLongitude;

    private RoomCreatePhotoAdapter photoAdapter;
    private ArrayList<String> selectedPhotos = new ArrayList<>();
    private ArrayList<File> photosFileList = new ArrayList<File>();
    private Spinner limitSpinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_create);

        //Spiner 추가
        limitSpinner = (Spinner)findViewById(R.id.number_of_roommate);
        ArrayAdapter room_people = ArrayAdapter.createFromResource(this,
                R.array.number_of_people, android.R.layout.simple_spinner_item);
        room_people.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        limitSpinner.setAdapter(room_people);


        roomTitle = (EditText) findViewById(R.id.room_create_input_title);
        roomContent = (EditText) findViewById(R.id.room_create_input_content);
        selectedLocationTextView = (TextView) findViewById(R.id.selectedLocation);
        // 첫번째 인자는 키, 두번째 인자는 키에 대한 데이터가 존재하지 않을 경우의 디폴트값
        SharedPreferences prefs = getSharedPreferences("RoomInfo", MODE_PRIVATE);
        String title = prefs.getString("title", "");
        String content = prefs.getString("content", "");
        Double lat = Double.parseDouble(prefs.getString("lat", "0"));
        Double lng = Double.parseDouble(prefs.getString("lng", "0"));
        if(lat != 0 && lng != 0){
            selectedLatitude = lat;
            selectedLongitude = lng;
            selectedLocationTextView.setText(getAddress(selectedLatitude, selectedLongitude));
        }
        roomTitle.setText(title);
        roomContent.setText(content);

        //Photos 관련
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.photos_recycler_view);
        photoAdapter = new RoomCreatePhotoAdapter(this, selectedPhotos);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(4, OrientationHelper.VERTICAL));
        recyclerView.setAdapter(photoAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                PhotoPreview.builder()
                        .setPhotos(selectedPhotos)
                        .setCurrentItem(position)
                        .start(RoomCreateActivity.this);
            }
        }));

        //Google Map 관련
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
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Google Map 관련
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
        if(currentLocation == null){
            Log.e("currentLocation", "위치를 받아오지 못함");
        }
//        Log.e("currentLocation", currentLocation.toString());
    }

    @Override
    protected void onStop() {
        super.onStop();

        String title = roomTitle.getText().toString();
        String content = roomContent.getText().toString();
        SharedPreferences prefs = getSharedPreferences("RoomInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("title", title);
        editor.putString("content", content);

        if(selectedLatitude != null && selectedLongitude != null){
            editor.putString("lat", selectedLatitude.toString());
            editor.putString("lng", selectedLongitude.toString());
        }

        editor.commit();
    }


    public void create_room(View view) {
        final String json = "{" +
                "\"user\" : \""  + user.getId() + "\", " +
                "\"title\" : \""  + roomTitle.getText().toString() + "\", " +
                "\"content\" : \""  + roomContent.getText().toString() + "\", " +
                "\"latitude\" : \""  + selectedLatitude.toString() + "\", " +
                "\"longitude\" : \""  + selectedLongitude.toString() + "\", " +
                "\"limit\" : \""  + limitSpinner.getSelectedItem().toString()+ "\"}";

        for (int i = 0; i < selectedPhotos.size(); i ++){
//            String realPath = getRealPathFromString(selectedPhotos.get(i));
            photosFileList.add(new File(selectedPhotos.get(i)));
        }

        // 사진은 필수로 적용함 selectedPhotos
        final Activity activity = this;
        new Thread() {
            public void run() {
                if (selectedPhotos != null) {
                    String responseStr = ServerManager.getInstance().roomCreate(Constants.SERVER_URL + "/room/create", json, photosFileList);
                    Log.d("createRoomResult", responseStr);
                    try {
                        JSONObject response = new JSONObject(responseStr);
                        String result = response.getString("result");
                        if(result.equals("fail")){
                            activity.sendBroadcast(new Intent("com.mju.hps.withme.reciver.createUserFail"));
                        }
                        else {
                            //Init
                            roomTitle.setText("");
                            roomContent.setText("");
                            selectedLatitude = null;
                            selectedLongitude = null;
                            activity.sendBroadcast(new Intent("com.mju.hps.withme.reciver.createUserSuccess"));
                        }

                    } catch (Throwable t) {
                        Log.e("createUser", t.toString());
                    }
                }

            }
        }.start();

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
        YPhotoPickerIntent intent = new YPhotoPickerIntent(this);
        intent.setMaxSelectCount(4);
        intent.setShowCamera(true);
        intent.setShowGif(false);
        intent.setSelectCheckBox(false);
        intent.setMaxGrideItemCount(4);
        startActivityForResult(intent, REQUEST_CODE_PHOTO_PICKER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<String> photos = null;
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_PHOTO_PICKER) {
            if (data != null) {
                photos = data.getStringArrayListExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS);
            }
            // 기존선택된것들 다 지움
            selectedPhotos.clear();
            if (photos != null) {
                selectedPhotos.addAll(photos);
                for(int i=0; i < selectedPhotos.size(); i++){
                    Log.e("selectedPhotos", selectedPhotos.get(i).toString());
                }
            }
            photoAdapter.notifyDataSetChanged();
        }
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

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };

        CursorLoader cursorLoader = new CursorLoader(this, contentUri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
    public String getRealPathFromString(String contentStr) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Uri contentUri = Uri.parse(contentStr);
        CursorLoader cursorLoader = new CursorLoader(this, contentUri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
}
