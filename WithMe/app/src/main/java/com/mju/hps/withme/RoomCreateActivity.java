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
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.iid.FirebaseInstanceId;
import com.mju.hps.withme.constants.Constants;
import com.mju.hps.withme.database.DatabaseLab;
import com.mju.hps.withme.model.User;
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

public class RoomCreateActivity extends AppCompatActivity {

    private static final int MSG_CREATE_ROOM_SUCCESS = 1;
    private static final int MSG_CREATE_ROOM_FAIL = 2;
    private static final int MSG_CREATE_ROOM_ERROR = 3;

    private EditText roomTitle, roomContent;
    private Location currentLocation;
    private Button createRoomButton;
    public Handler handler;

    private TextView selectedLocationTextView;
    private Double selectedLatitude;
    private Double selectedLongitude;

    private RoomCreatePhotoAdapter photoAdapter;
    private ArrayList<String> selectedPhotos = new ArrayList<>();
    private ArrayList<File> photosFileList = new ArrayList<File>();
    private Spinner limitSpinner;

    private LocationManager locationManager;

    private boolean gps_enabled = false;

    private boolean network_enabled = false;

    //error handler
    private AlertDialog.Builder builder;
    private AlertDialog theAlertDialog;

    LocationListener locationListenerGps = new LocationListener() {

        public void onLocationChanged(Location location) {
            currentLocation = location;
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };


    LocationListener locationListenerNetwork = new LocationListener() {

        public void onLocationChanged(Location location) {
            currentLocation = location;
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_create);
        createRoomButton = (Button)findViewById(R.id.room_create_create_button);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);//GPS 이용가능 여부
        network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);//Network 이용가능 여부

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String str;
                switch (msg.what) {
                    case MSG_CREATE_ROOM_SUCCESS:     // 성공
                        str = (String)msg.obj;
                        Toast.makeText(RoomCreateActivity.this, "방 만들기에 성공하였습니다.", Toast.LENGTH_SHORT).show();
                        Intent resultIntent=new Intent(RoomCreateActivity.this, MainActivity.class);
                        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        RoomCreateActivity.this.startActivity(resultIntent);
                        finish();
                        break;
                    case MSG_CREATE_ROOM_FAIL:     // 실패
                        str = (String)msg.obj;
                        Toast.makeText(RoomCreateActivity.this, "방 만들기에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                        break;
                    case MSG_CREATE_ROOM_ERROR:     // 에러
                        str = (String)msg.obj;
                        Toast.makeText(RoomCreateActivity.this, "서버에러 입니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                        break;
                }
                createRoomButton.setClickable(true);
            }
        };

        setupUI(findViewById(R.id.activity_room_create));       //키보드 포커싱 아웃

        if (!gps_enabled && !network_enabled) {
            Log.e("LocationManagerTest", "nothing is enabled"); //모두 사용 불가
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }


        if (gps_enabled) {//GPS를 이용한 측위요청
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListenerGps);// 현재 위치 업데이트
        }


        if(network_enabled) {//Network를 이용한 측위요청
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListenerNetwork);// 현재 위치 업데이트
        }

        //Spiner 추가
        limitSpinner = (Spinner)findViewById(R.id.number_of_roommate);
        ArrayAdapter room_people = ArrayAdapter.createFromResource(this,
                R.array.number_of_people, android.R.layout.simple_spinner_item);
        room_people.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        limitSpinner.setAdapter(room_people);

        builder = new AlertDialog.Builder(this);

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
        //recyclerView.setLayoutManager(new StaggeredGridLayoutManager(4, OrientationHelper.VERTICAL));
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
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

    private boolean errorHandlerRoomCreate(){
        //error 체크 변수 처음엔 false 에러 X.

        boolean errorCheck = false;
        String errMsg = "이 방에 대한 <";
        if(roomTitle.getText().toString().equals("") || roomTitle == null){
            errorCheck = true;
            errMsg = errMsg + " 제목 ";
        }
        if(roomContent.getText().toString().equals("") || roomContent == null){
            errorCheck = true;
            errMsg = errMsg + " 내용 ";
        }
        if(selectedLatitude == null && selectedLongitude == null){
            errorCheck = true;
            errMsg = errMsg + " 위치 ";
        }
        if(selectedPhotos == null || selectedPhotos.size() <= 0){
            errorCheck = true;
            errMsg = errMsg + " 사진 ";
        }
        if(errorCheck){
            errMsg = errMsg + "> 정보를 입력 해주세요.";
            showAlert("방에 대한 설명 부족", errMsg);
        }
        return errorCheck;
    }

    public void showAlert(String title, String message) {

        // Set alert title
        builder.setTitle(title);

        // The message
        builder.setMessage(message);
        builder.setPositiveButton("확인", null);
        // Create the alert dialog and display it
        theAlertDialog = builder.create();
        theAlertDialog.show();
    }

    public void roomInfoInit(){
        roomTitle.setText("");
        roomContent.setText("");
        selectedLatitude = null;
        selectedLongitude = null;

    }
    public void create_room(View view) {
        if(errorHandlerRoomCreate()){
            Log.e("방 만들기 Error", "Room Create Error");
        }
        else {
            createRoomButton.setClickable(false);
            final String json = "{" +
                    "\"user\" : \""  + User.getInstance().getId() + "\", " +
                    "\"title\" : \""  + roomTitle.getText().toString() + "\", " +
                    "\"content\" : \""  + roomContent.getText().toString() + "\", " +
                    "\"latitude\" : \""  + selectedLatitude.toString() + "\", " +
                    "\"longitude\" : \""  + selectedLongitude.toString() + "\", " +
                    "\"address\" : \""  + selectedLocationTextView.getText().toString() + "\", " +
                    "\"limit\" : \""  + limitSpinner.getSelectedItem().toString()+ "\"" +
                    "}";

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
                        if(responseStr == null){
                            handler.sendMessage(Message.obtain(handler, MSG_CREATE_ROOM_ERROR, ""));
                            return;
                        }
                        Log.d("createRoomResult", responseStr);
                        try {
                            JSONObject response = new JSONObject(responseStr);
                            String result = response.getString("result");
                            if(result.equals("fail")){
                                handler.sendMessage(Message.obtain(handler, MSG_CREATE_ROOM_FAIL, ""));
                            }
                            else {
//                            //Init


                                handler.sendMessage(Message.obtain(handler, MSG_CREATE_ROOM_SUCCESS, ""));
                                roomInfoInit();
                            }

                        } catch (Throwable t) {
                            Log.e("createUser", t.toString());
                        }
                    }
                      }
            }.start();

        }
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




    //
    // 키보드 숨김 함수
    //
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    public void setupUI(View view) {
        //Set up touch listener for non-text box views to hide keyboard.
        if(!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(RoomCreateActivity.this);
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }
}
