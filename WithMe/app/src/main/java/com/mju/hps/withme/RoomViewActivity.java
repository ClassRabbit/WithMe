package com.mju.hps.withme;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mju.hps.withme.constants.Constants;
import com.mju.hps.withme.model.RoomData;
import com.mju.hps.withme.model.User;
import com.mju.hps.withme.model.UserData;
import com.mju.hps.withme.server.ServerManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import static com.mju.hps.withme.R.id.map;

public class RoomViewActivity extends AppCompatActivity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {

    public static final int MSG_ROOM_VIEW_ERROR = 1;
    public static final int MSG_ROOM_VIEW_CAN_JOIN = 2;
    public static final int MSG_ROOM_VIEW_CANNOT_JOIN = 3;
    public static final int MSG_ROOM_VIEW_SUCCESS = 4;
    public static final int MSG_ROOM_VIEW_WAITING_ACK = 5;
    public static final int MSG_ROOM_VIEW_WAITING_REFUCE = 6;
    public static final int MSG_ROOM_VIEW_JOIN_SUCCESS = 7;
    public static final int MSG_ROOM_VIEW_JOIN_FULL = 8;
    public static final int MSG_ROOM_VIEW_SECESSION_SUCCESS = 9;
    public static final int MSG_ROOM_VIEW_JOINCANCLE_SUCCESS = 10;
    public static final int MSG_ROOM_VIEW_DESTROY_SUCCESS = 11;
    public static final int MSG_ROOM_VIEW_NULL = 12;
    public static final int MSG_ROOM_VIEW_USER_NULL = 13;

    private static String roomId;
    public static Handler handler;
    private static JSONObject owner;
    private static JSONObject room;
    private boolean isJoin = false;
    private JSONObject myRoom;
    private static JSONArray joins;         //이 방에 조인내역
    private static JSONArray users;         //이 방에 조인한 유저들
    private static int constitutorCnt = 0;         //방에 join한 유저 수
    private static WaitingListAdapter waitingAdapter;
    private static ConstitutorListAdapter constitutorAdapter;
    private static View thirdView;
    private int tabLocation = 0;
    private Intent refreshIntent;
    private static Context context;


    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    //Slide Show
    static SliderLayout demoSlider;

//    private MapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_view);

//        //slide show
//        DemoSlider = (SliderLayout)findViewById(R.id.slider);
//        mapFragment.getMapAsync(this);

        //정보 받기
        final Intent intent = getIntent();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            Log.e("roomViewonStart", "nfc");
            // 받은 인텐트에서 Ndef 메시지를 취득한다
            Parcelable[] rawMsgs = intent
                    .getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

            // Android Beam에서는 한 번에 한 개의 메시지만 송수신 가능
            NdefMessage msg = (NdefMessage) rawMsgs[0];

            // 첫 번째 레코드에 MIME데이터가 포함된다
            roomId = new String(msg.getRecords()[0].getPayload());
            tabLocation = 0;
        }
        else {
            Log.e("roomViewonStart", "else");
            roomId = (String)intent.getSerializableExtra("roomId");
            if(roomId == null){
                Uri uri = intent.getData();
                roomId = uri.getQueryParameter("roomId");
            }
            tabLocation = intent.getIntExtra("tabLocation", 0);
        }



        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String str;
                TabLayout tabLayout;
                FloatingActionButton fab;
                myRoom = null;
                Intent intent;
                switch (msg.what) {
                    case MSG_ROOM_VIEW_ERROR:
                        str = (String)msg.obj;
                        Toast.makeText(RoomViewActivity.this, "서버에 연결되지 않았습니다.", Toast.LENGTH_SHORT).show();
                        break;
                    case MSG_ROOM_VIEW_CAN_JOIN:     // 현재 가입한 방이 없음
                        Log.e("isJoin", "false");
                        isJoin = false;
                        break;
                    case MSG_ROOM_VIEW_CANNOT_JOIN:     // 현재 가입한 방이 있음
                        Log.e("isJoin", "true");
                        isJoin = true;
                        break;
                    case MSG_ROOM_VIEW_SUCCESS:
                        waitingAdapter = new WaitingListAdapter();
                        constitutorAdapter = new ConstitutorListAdapter();
                        JSONArray data = (JSONArray)msg.obj;
                        try{        //1번 요소는 방, 2번 요소는 방장, 3번요소는 이방에 join한 모든내역, 4번요소는 이방에 조인한 유저목록
                            room = data.getJSONObject(0);
                            owner = data.getJSONObject(1);
                            joins = data.getJSONArray(2);
                            users = data.getJSONArray(3);
                            constitutorCnt = 0;
                            for(int i = 0; i<joins.length(); i++){
                                if(User.getInstance().getId().equals(joins.getJSONObject(i).getString("user"))){
                                    myRoom = joins.getJSONObject(i);
                                }
                                if(!joins.getJSONObject(i).getString("position").equals("waiting")){        // 아방에 방장이랑 참가자수 계산
                                    constitutorCnt++;
                                }
                            }
                            // 아래와 같은 형식으로 참고해서 관리하면 될듯
                            // owner = new UserData(roomJson.getString("title"), roomJson.getString("content"), roomJson.getInt("limit"), roomJson.getJSONArray())

                        }
                        catch(Exception e){
                            Log.e("RoomView", e.toString());
                        }
                        if(isJoin == false){                              //현재 방에 참가하지않아 이방에 참가할 수있는 사람의 경우(visitor)
                            mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), 3, "visitor");
                        }
                        else  if(isJoin == true && myRoom == null) {      //방에 속한 사람이며, 이방에 속할 가능성 없음 따라서 탭 2개만 보여줌("")
                            mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), 2, "");
                        }
                        else if(isJoin == true && myRoom != null){       //방에 속한 사람이며, 이방에 속한 사람의 경우
                            String position = null;
                            try{
                                position = myRoom.getString("position");    //주인이면 (owner), 구성자면 (constitutor), 신청대기자면 (waiting)
                            }
                            catch(Exception e){
                                Log.e("position parse", e.toString());
                            }
                            Log.i("position", position);
                            mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), 3, position);     //속한 사람의 경우 3번째 텝 보여줌
                        }

                        mViewPager = (ViewPager) findViewById(R.id.container);
                        mViewPager.setAdapter(mSectionsPagerAdapter);
                        mViewPager.setCurrentItem(tabLocation);
                        tabLocation = 0;
                        tabLayout = (TabLayout) findViewById(R.id.tabs);
                        tabLayout.setupWithViewPager(mViewPager);

                        break;
                    case MSG_ROOM_VIEW_JOIN_FULL:
                        refreshIntent = new Intent(RoomViewActivity.this, RoomViewActivity.class);
                        refreshIntent.putExtra("roomId", roomId);
                        refreshIntent.putExtra("tabLocation", 2);
                        refreshIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        refreshIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        Toast.makeText(RoomViewActivity.this, "이미 방에 참여인원이 꽉찼습니다.", Toast.LENGTH_SHORT).show();
                        startActivity(refreshIntent);
                        break;
                    case MSG_ROOM_VIEW_WAITING_ACK:
                    case MSG_ROOM_VIEW_WAITING_REFUCE:
                    case MSG_ROOM_VIEW_JOIN_SUCCESS:
                    case MSG_ROOM_VIEW_SECESSION_SUCCESS:
                    case MSG_ROOM_VIEW_JOINCANCLE_SUCCESS:
                        refreshIntent = new Intent(RoomViewActivity.this, RoomViewActivity.class);
                        refreshIntent.putExtra("roomId", roomId);
                        refreshIntent.putExtra("tabLocation", 2);
                        refreshIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        refreshIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(refreshIntent);
                        break;
                    case MSG_ROOM_VIEW_NULL:
                        Toast.makeText(RoomViewActivity.this, "존재하지 않는 방입니다.", Toast.LENGTH_SHORT).show();
                        intent =new Intent(RoomViewActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        RoomViewActivity.this.startActivity(intent);
                        break;
                    case MSG_ROOM_VIEW_USER_NULL:
                        refreshIntent = new Intent(RoomViewActivity.this, RoomViewActivity.class);
                        refreshIntent.putExtra("roomId", roomId);
                        refreshIntent.putExtra("tabLocation", 2);
                        refreshIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        refreshIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        Toast.makeText(RoomViewActivity.this, "이미 참여를 취소한 인원입니다.", Toast.LENGTH_SHORT).show();
                        startActivity(refreshIntent);
                        break;
                    case MSG_ROOM_VIEW_DESTROY_SUCCESS:
                        Toast.makeText(RoomViewActivity.this, "방을 삭제하였습니다.", Toast.LENGTH_SHORT).show();
                        intent =new Intent(RoomViewActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        RoomViewActivity.this.startActivity(intent);
                        break;
                }
            }
        };


        reloadView();



    }

    public void reloadView(){
        context = this.getBaseContext();
        final String json = "{" +
                "\"user\" : \"" + User.getInstance().getId() + "\", " +
                "\"roomId\" : \"" +  roomId + "\"" +
                "}";
        new Thread() {
            public void run() {
                String response = ServerManager.getInstance().post(Constants.SERVER_URL + "/room/view", json);
                if(response == null){
                    Log.e("reloadView", "서버 에러");
                    handler.sendMessage(Message.obtain(handler, MSG_ROOM_VIEW_ERROR, ""));
                    return;
                }
                Log.e("reloadViewResponse", response);
                try{
                    JSONObject res = new JSONObject(response);
                    //방 등록 했는지 안했는지

                    if(res.getString("result").equals("fail")){
                        handler.sendMessage(Message.obtain(handler, MSG_ROOM_VIEW_ERROR, ""));

                    }
                    else if(res.getString("result").equals("null")){
                        handler.sendMessage(Message.obtain(handler, MSG_ROOM_VIEW_NULL, ""));
                    }
                    else{
                        if(res.getBoolean("isJoin") == false){      //등록한 방이 없슴
                            Log.e("isJoin", "등록한 방 없슴");
                            handler.sendMessage(Message.obtain(handler, MSG_ROOM_VIEW_CAN_JOIN, ""));
                        }
                        else {
                            Log.e("isJoin", "등록한 방 있슴");
                            handler.sendMessage(Message.obtain(handler, MSG_ROOM_VIEW_CANNOT_JOIN, ""));
                        }
                        //[방 정보, 만든이 정보, 이방에 조인한 정보] 형태의 JsonArray 가져옴
                        JSONArray data = res.getJSONArray("data");
                        handler.sendMessage(Message.obtain(handler, MSG_ROOM_VIEW_SUCCESS, data));
                    }

                }
                catch (Exception e) {
                    Log.e("reloadView", e.toString());
                }

            }
        }.start();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onStop() {
        demoSlider.stopAutoCycle();
        super.onStop();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements OnMapReadyCallback{
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static final String ARG_JOIN_POSITION = "join_position";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber, String joinPosition) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            Log.i("newInstance", joinPosition);
            args.putString(ARG_JOIN_POSITION, joinPosition);
            fragment.setArguments(args);
            return fragment;
        }

        private GoogleMap mMap;
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
        }

        //
        // 실제 뷰 그리는 곳
        //
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {


            if(getArguments().getInt(ARG_SECTION_NUMBER) == 1){
                Log.i("onCreateView", "1");                                                                 //방 정보일때
                View rootView = inflater.inflate(R.layout.fragment_room_view_1, container, false);

                // Slideshow
                int numberOfImages = 3; // default 3
                String title = "";


                // google map
                MapView mapView;
                mapView = (MapView) rootView.findViewById(R.id.room_view_mapView);
                Bundle newBundle = new Bundle();
                mapView.onCreate(newBundle);
//                mapView.onCreate(savedInstanceState);
                // Gets to GoogleMap from the MapView and does initialization stuff
                try {
                    MapsInitializer.initialize(this.getActivity());

                } catch (Exception e) {
                    Log.e("Map Error", e.toString());
                }

                mMap = mapView.getMap();
                mMap.getUiSettings();

                Double Lat = 0.0;
                Double Lng = 0.0;
                String address = "";
                //여기서 객체를 받고
                TextView roomTitleInfo = (TextView)rootView.findViewById(R.id.room_title_info);
                TextView roomContent = (TextView)rootView.findViewById(R.id.room_content_info);
                TextView roomAddress = (TextView)rootView.findViewById(R.id.room_address_info);
                TextView roomRecentPeople = (TextView)rootView.findViewById(R.id.room_recent_people);
                TextView roomLimitPeople = (TextView)rootView.findViewById(R.id.room_people_all);
                try{
                    //트라이 안에서 값 파싱해서 넣기
                    roomTitleInfo.setText(room.getString("title"));
                    roomContent.setText(room.getString("content"));
                    roomAddress.setText(room.getString("address"));
                    roomRecentPeople.setText("" + constitutorCnt);
                    roomLimitPeople.setText("" + (room.getInt("limit") + 1));
                    title = room.getString("title");

                    numberOfImages = Integer.parseInt(room.getString("numberOfImages"));

                    Lat = Double.parseDouble(room.getString("latitude"));
                    Lng = Double.parseDouble(room.getString("longitude"));
                    address = room.getString("address");
                }
                catch (Exception e){
                    Log.e("onCreateView", e.toString());
                }
//Slider init
                demoSlider = (SliderLayout)rootView.findViewById(R.id.slider);

                for(int i = 0; i < numberOfImages; i++){
                    Log.e(String.valueOf(i+1), Constants.SERVER_URL + "/images/room/" + roomId + "/" + i + ".png");
                    TextSliderView textSliderView = new TextSliderView(getContext());
                    // initialize a SliderLayout
                    textSliderView
                            .description(title + " - " + String.valueOf(i+1))
                            .image(Constants.SERVER_URL + "/images/room/" + roomId + "/" + i + ".png")
                            .setScaleType(BaseSliderView.ScaleType.Fit);
//                            .setOnSliderClickListener(new );

                    //add your extra information
                    Bundle bundle = new Bundle();
                    textSliderView.bundle(bundle);
                    textSliderView.getBundle()
                            .putString("extra", title + " - " + String.valueOf(i+1));

                    demoSlider.addSlider(textSliderView);
                }
                demoSlider.setPresetTransformer(SliderLayout.Transformer.Default);
                demoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
                demoSlider.setCustomAnimation(new DescriptionAnimation());
                demoSlider.setDuration(4000);
//                mDemoSlider.addOnPageChangeListener(this);

                //google map
                Log.e("Lat", Lat.toString());
                Log.e("Lng", Lng.toString());
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(Lat, Lng), 12);
                mMap.animateCamera(cameraUpdate);

                MarkerOptions marker = new MarkerOptions();
                marker.position(new LatLng(Lat, Lng));// 위도  경도
                marker.title(title);// 제목 미리보기
                marker.snippet(address);
                mMap.addMarker(marker).showInfoWindow();

                mapView.onResume();
                mapView.getMapAsync(this);
                return rootView;
            }
            else if(getArguments().getInt(ARG_SECTION_NUMBER) == 2){
                Log.i("onCreateView", "2");                                                                 //유저 정보 페이지일때
                View rootView = inflater.inflate(R.layout.fragment_room_view_2, container, false);
                ListView constitutorList = (ListView)rootView.findViewById(R.id.room_view_listview_constitutor); //룸메이트

                try{
                    for(int i=0; i<joins.length();i++) {
                        JSONObject join = joins.getJSONObject(i);
                        if(join.getString("position").equals("constitutor") || join.getString("position").equals("owner")){
                            for(int j=0;j<users.length();j++){
                                JSONObject user = users.getJSONObject(j);
                                if(join.getString("user").equals(user.getString("id"))){
                                    constitutorAdapter.addRoommate(join.getString("id"), user.getString("mail"),
                                            user.getString("name"), user.getString("gender"), user.getString("birth"));
                                }
                            }
                        }
                    }
                } catch(Exception e) {
                    Log.e("roommate list", e.toString());
                }
                constitutorList.setAdapter(constitutorAdapter);
                return rootView;
            }
            else if(getArguments().getInt(ARG_SECTION_NUMBER) == 3){
                Log.i("onCreateView", "3");
                View rootView;
                if(getArguments().getString(ARG_JOIN_POSITION).equals("owner")){                            //방장일때
                    rootView = inflater.inflate(R.layout.fragment_room_view_3, container, false);
                    thirdView = rootView;
                    ListView waitingListView = (ListView) rootView.findViewById((R.id.room_view_listview_waiting));
                    waitingAdapter = new WaitingListAdapter();
                    try{
                        for(int i=0;i<joins.length();i++){
                            JSONObject join = joins.getJSONObject(i);
                            if(join.getString("position").equals("waiting")){
                                for(int j=0;j<users.length();j++){
                                    JSONObject user = users.getJSONObject(j);
                                    if(join.getString("user").equals(user.getString("id"))){
                                        waitingAdapter.addWaiting(join.getString("id"), user.getString("mail"), user.getString("name"),
                                                user.getString("birth"), user.getString("gender"), user.getString("phone"));
                                    }
                                }
                            }
                        }
                    }
                    catch(Exception e) {
                        Log.e("room change", e.toString());
                    }
                    waitingListView.setAdapter(waitingAdapter);

                    //NFC 버튼
                    final Button nfcButton = (Button)rootView.findViewById((R.id.room_view_button_nfc));
                    nfcButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
//                            fixButton.setClickable(false);

                            Intent intent = new Intent(context, NfcActivity.class);
                            String roomId = null;
                            try{
                                roomId = room.getString("id");
                            }
                            catch (Exception e){
                                Log.e("nfc", e.toString());
                            }
                            intent.putExtra("roomId", roomId);
                            startActivity(intent);
                        }
                    });

                    //방 수정 버튼
                    final Button fixButton = (Button)rootView.findViewById((R.id.room_view_button_fix));
                    fixButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
//                            fixButton.setClickable(false);

                            Intent intent = new Intent(context, RoomCreateActivity.class);
                            intent.putExtra("isFix", true);
                            intent.putExtra("isMap", false);
                            try{
                                intent.putExtra("roomId", room.getString("id"));
                                intent.putExtra("title", room.getString("title"));
                                intent.putExtra("content", room.getString("content"));
                                intent.putExtra("latitude", room.getDouble("latitude"));
                                intent.putExtra("longitude", room.getDouble("longitude"));
                                intent.putExtra("address", room.getString("address"));
                                intent.putExtra("limit", room.getInt("limit"));
                            }
                            catch(Exception e){
                                Log.e("fixRoom", e.toString());
                            }
                            startActivity(intent);
                        }
                    });

                    //방 파괴 버튼
                    final Button destroyButton = (Button)rootView.findViewById((R.id.room_view_button_destroy));
                    destroyButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            destroyButton.setClickable(false);
                            String roomId = null;
                            try{
                                roomId = room.getString("id");
                            }
                            catch(Exception e){
                                Log.e("onCreateView3", e.toString());
                                return;
                            }
                            final String json = "{" +
                                    "\"room\" : \"" + roomId + "\"" +
                                    "}";

                            new Thread() {
                                public void run() {
                                    String response = ServerManager.getInstance().post(Constants.SERVER_URL + "/room/destroy", json);
                                    if(response == null){
                                        Log.e("onCreateView3", "서버 에러");
                                        return;
                                    }
                                    Log.e("secession", response);
                                    try{
                                        JSONObject res = new JSONObject(response);
                                        if(res.getString("result").equals("fail")){
                                            handler.sendMessage(Message.obtain(handler, MSG_ROOM_VIEW_ERROR, ""));
                                            return;
                                        }
                                        else {
                                            handler.sendMessage(Message.obtain(handler, MSG_ROOM_VIEW_DESTROY_SUCCESS, ""));
                                        }

                                    }
                                    catch (Exception e) {
                                        Log.e("onCreateView3", e.toString());
                                    }
                                }
                            }.start();
                        }
                    });

//                    //Chatting button
//                    final Button chatButton = (Button)rootView.findViewById((R.id.room_view_button_chat));
//                    chatButton.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
////                            fixButton.setClickable(false);
//
//                            Intent intent = new Intent(context, ChatActivity.class);
//                            String roomId = null;
//                            try{
//                                roomId = room.getString("id");
//                            }
//                            catch (Exception e){
//                                Log.e("chat", e.toString());
//                            }
//                            intent.putExtra("roomId", roomId);
//                            startActivity(intent);
//                        }
//                    });
                }
                else if (getArguments().getString(ARG_JOIN_POSITION).equals("constitutor")){               //이방의 구성자일때
                    Log.i("onCreateView", "4");
                    rootView = inflater.inflate(R.layout.fragment_room_view_4, container, false);
                    final Button secessionButton = (Button)rootView.findViewById((R.id.room_view_button_secession));
                    secessionButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            secessionButton.setClickable(false);
                            String roomId = null;
                            try{
                                roomId = room.getString("id");
                            }
                            catch(Exception e){
                                Log.e("onCreateView3", e.toString());
                                return;
                            }
                            final String json = "{" +
                                    "\"user\" : \"" + User.getInstance().getId() + "\", " +
                                    "\"userName\" : \"" + User.getInstance().getName() + "\", " +
                                    "\"room\" : \"" + roomId + "\"" +
                                    "}";
                            new Thread() {
                                public void run() {
                                    String response = ServerManager.getInstance().post(Constants.SERVER_URL + "/room/secession", json);
                                    if(response == null){
                                        Log.e("onCreateView4", "서버 에러");
                                        return;
                                    }
                                    Log.e("secession", response);
                                    try{
                                        JSONObject res = new JSONObject(response);
                                        if(res.getString("result").equals("fail")){
                                            handler.sendMessage(Message.obtain(handler, MSG_ROOM_VIEW_ERROR, ""));
                                            return;
                                        }
                                        handler.sendMessage(Message.obtain(handler, MSG_ROOM_VIEW_SECESSION_SUCCESS, ""));
                                    }
                                    catch (Exception e) {
                                        Log.e("onCreateView4", e.toString());
                                    }
                                }
                            }.start();
                        }
                    });

                    //NFC 버튼 of constitutor
                    final Button nfcButton = (Button)rootView.findViewById((R.id.nfc_join1));
                    nfcButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
//                            fixButton.setClickable(false);

                            Intent intent = new Intent(context, NfcActivity.class);
                            String roomId = null;
                            try{
                                roomId = room.getString("id");
                            }
                            catch (Exception e){
                                Log.e("nfc", e.toString());
                            }
                            intent.putExtra("roomId", roomId);
                            startActivity(intent);
                        }
                    });

                    //Chatting button
                    final Button chatButton = (Button)rootView.findViewById((R.id.chat_join1));
                    chatButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
//                            fixButton.setClickable(false);

                            Intent intent = new Intent(context, ChatActivity.class);
                            String roomId = null;
                            try{
                                roomId = room.getString("id");
                            }
                            catch (Exception e){
                                Log.e("chat", e.toString());
                            }
                            intent.putExtra("roomId", roomId);
                            startActivity(intent);
                        }
                    });
                }
                else if (getArguments().getString(ARG_JOIN_POSITION).equals("waiting")) {                  //대기중일때
                    Log.i("onCreateView", "5");
                    rootView = inflater.inflate(R.layout.fragment_room_view_5, container, false);
                    final Button joinCancleButton = (Button)rootView.findViewById((R.id.room_view_button_joinCancle));
                    joinCancleButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            joinCancleButton.setClickable(false);
                            String roomId = null;
                            try{
                                roomId = room.getString("id");
                            }
                            catch(Exception e){
                                Log.e("onCreateView3", e.toString());
                                return;
                            }
                            final String json = "{" +
                                    "\"user\" : \"" + User.getInstance().getId() + "\", " +
                                    "\"room\" : \"" + roomId + "\"" +
                                    "}";
                            new Thread() {
                                public void run() {
                                    String response = ServerManager.getInstance().post(Constants.SERVER_URL + "/room/joinCancle", json);
                                    if(response == null){
                                        Log.e("onCreateView5", "서버 에러");
                                        return;
                                    }
                                    Log.e("joincancle", response);
                                    try{
                                        JSONObject res = new JSONObject(response);
                                        if(res.getString("result").equals("fail")){
                                            handler.sendMessage(Message.obtain(handler, MSG_ROOM_VIEW_ERROR, ""));
                                            return;
                                        }
                                        handler.sendMessage(Message.obtain(handler, MSG_ROOM_VIEW_JOINCANCLE_SUCCESS, ""));
                                    }
                                    catch (Exception e) {
                                        Log.e("onCreateView5", e.toString());
                                    }
                                }
                            }.start();
                        }
                    });

//                    //Chatting button
//                    final Button chatButton = (Button)rootView.findViewById((R.id.chat_join2));
//                    chatButton.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
////                            fixButton.setClickable(false);
//
//                            Intent intent = new Intent(context, ChatActivity.class);
//                            String roomId = null;
//                            try{
//                                roomId = room.getString("id");
//                            }
//                            catch (Exception e){
//                                Log.e("chat", e.toString());
//                            }
//                            intent.putExtra("roomId", roomId);
//                            startActivity(intent);
//                        }
//                    });
                }
                else {                                                                                    //방문자
                    Log.i("onCreateView", "6");
                    rootView = inflater.inflate(R.layout.fragment_room_view_6, container, false);
                    final Button joinButton = (Button)rootView.findViewById((R.id.room_view_button_join));
                    try{
                        if((room.getInt("limit")+1) <= joins.length()){
//                            joinButton.setText("신청불가");
//                            joinButton.setVisibility(View.GONE);
                            joinButton.setEnabled(false);
                        }
                    }
                    catch(Exception e){
                        Log.e("onCreateView6", e.toString());
                    }
                    joinButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            joinButton.setClickable(false);
                            final String json = "{" +
                                    "\"user\" : \"" + User.getInstance().getId() + "\", " +
                                    "\"room\" : \"" + roomId + "\"" +
                                    "}";
                            new Thread() {
                                public void run() {
                                    String response = ServerManager.getInstance().post(Constants.SERVER_URL + "/room/join", json);
                                    if(response == null){
                                        Log.e("onCreateView6", "서버 에러");
                                        return;
                                    }
                                    Log.e("onCreateView6", response);
                                    try{
                                        JSONObject res = new JSONObject(response);
                                        if(res.getString("result").equals("full")){
                                            handler.sendMessage(Message.obtain(handler, MSG_ROOM_VIEW_JOIN_FULL, ""));
                                            return;
                                        }
                                        else if(res.getString("result").equals("null")){      //등록한 방이 없슴
                                            handler.sendMessage(Message.obtain(handler, MSG_ROOM_VIEW_NULL, ""));
                                            return;
                                        }
                                        else if(res.getString("result").equals("fail")){
                                            handler.sendMessage(Message.obtain(handler, MSG_ROOM_VIEW_ERROR, ""));
                                            return;
                                        }
                                        else {
                                            handler.sendMessage(Message.obtain(handler, MSG_ROOM_VIEW_JOIN_SUCCESS, ""));
                                        }

                                    }
                                    catch (Exception e) {
                                        Log.e("onCreateView6", e.toString());
                                    }
                                }
                            }.start();
                        }
                    });

                }
                return rootView;
            }
            else {
                View rootView = inflater.inflate(R.layout.fragment_room_view, container, false);
                TextView textView = (TextView) rootView.findViewById(R.id.section_label);
                textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
                return rootView;
            }
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private int tabCount;
        private String positionStr;

        public SectionsPagerAdapter(FragmentManager fm, int tabCount, String position) {
            super(fm);
            this.tabCount = tabCount;
            this.positionStr = position;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            Log.i("SectionsPagerAdapter", positionStr);
            return PlaceholderFragment.newInstance(position + 1, positionStr);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return tabCount;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "방 정보";
                case 1:
                    return "룸메이트 정보";
                case 2:
                    if(positionStr.equals("owner")){
                        return "관리";
                    }
                    else if(positionStr.equals("constitutor")) {
                        return "모집현황";
                    }
                    else if(positionStr.equals("waiting")){
                        return "모집현황";
                    }
                    else {
                        return "신청하기";
                    }

            }
            return null;
        }
    }



    @Override
    public void onSliderClick(BaseSliderView slider) {
        Toast.makeText(this,slider.getBundle().get("extra") + "",Toast.LENGTH_SHORT).show();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override
    public void onPageSelected(int position) {
        Log.d("Slider Demo", "Page Changed: " + position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {}
}
