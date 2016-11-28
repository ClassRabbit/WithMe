package com.mju.hps.withme;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.mju.hps.withme.constants.Constants;
import com.mju.hps.withme.model.RoomData;
import com.mju.hps.withme.model.User;
import com.mju.hps.withme.model.UserData;
import com.mju.hps.withme.server.ServerManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

public class RoomViewActivity extends AppCompatActivity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener{

    public static final int MSG_ROOM_VIEW_ERROR = 1;
    public static final int MSG_ROOM_VIEW_CAN_JOIN = 2;
    public static final int MSG_ROOM_VIEW_CANNOT_JOIN = 3;
    public static final int MSG_ROOM_VIEW_SUCCESS = 4;
    public static final int MSG_ROOM_VIEW_WAITING_ACK = 5;
    public static final int MSG_ROOM_VIEW_WAITING_REFUCE = 6;
    public static final int MSG_ROOM_VIEW_JOIN_SUCCESS = 7;
    //    public static final int MSG_ROOM_VIEW_NULL = 7;

    private static String roomId;
    public static Handler handler;
    private static JSONObject owner;
    private static JSONObject room;
    private boolean isJoin = false;
    private JSONObject myRoom;
    private static JSONArray joins;         //이 방에 조인내역
    private static JSONArray users;         //이 방에 조인한 유저들
    private static int constitutorCnt = 0;         //방에 join한 유저 수
    private static RoomViewWatingAdapter waitingAdapter;
    private static RoommateListAdapter roommateAdapter;
    private static View thirdView;
    private int tabLocation = 0;
    private Intent refreshIntent;


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
    SliderLayout DemoSlider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_view);

        //slide show
        DemoSlider = (SliderLayout)findViewById(R.id.slider);

        //정보 받기
        final Intent intent = getIntent();
        roomId = (String)intent.getSerializableExtra("roomId");
        tabLocation = (int)intent.getSerializableExtra("tabLocation");


        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String str;
                TabLayout tabLayout;
                FloatingActionButton fab;
                myRoom = null;
                switch (msg.what) {
                    case MSG_ROOM_VIEW_ERROR:
                        str = (String)msg.obj;
                        Toast.makeText(RoomViewActivity.this, "서버에 연결하지 못했습니다.", Toast.LENGTH_SHORT).show();
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
                        waitingAdapter = new RoomViewWatingAdapter();
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
                            //                            owner = new UserData(roomJson.getString("title"), roomJson.getString("content"), roomJson.getInt("limit"), roomJson.getJSONArray())

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
                    case MSG_ROOM_VIEW_WAITING_ACK:
                        //                        tabLocation = 2;
                        //                        reloadView();
                        refreshIntent = new Intent(RoomViewActivity.this, RoomViewActivity.class);
                        refreshIntent.putExtra("roomId", roomId);
                        refreshIntent.putExtra("tabLocation", 2);
                        refreshIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        refreshIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(refreshIntent);
                        break;
                    case MSG_ROOM_VIEW_WAITING_REFUCE:
                        //                        tabLocation = 2;
                        //                        reloadView();
                        refreshIntent = new Intent(RoomViewActivity.this, RoomViewActivity.class);
                        refreshIntent.putExtra("roomId", roomId);
                        refreshIntent.putExtra("tabLocation", 2);
                        refreshIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        refreshIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(refreshIntent);
                        break;
                    case MSG_ROOM_VIEW_JOIN_SUCCESS:
                        refreshIntent = new Intent(RoomViewActivity.this, RoomViewActivity.class);
                        refreshIntent.putExtra("roomId", roomId);
                        refreshIntent.putExtra("tabLocation", 2);
                        refreshIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        refreshIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(refreshIntent);
                }
            }
        };


        reloadView();



    }

    public void reloadView(){
        final String json = "{" +
                "\"user\" : \"" + User.getInstance().getId() + "\", " +
                "\"roomId\" : \"" +  roomId + "\"" +
                "}";
        new Thread() {
            public void run() {
                String response = ServerManager.getInstance().post(Constants.SERVER_URL + "/room/view", json);
                if(response == null){
                    Log.e("login", "서버 에러");
                    handler.sendMessage(Message.obtain(handler, MSG_ROOM_VIEW_ERROR, ""));
                    return;
                }
                Log.e("loginResponse", response);
                try{
                    JSONObject res = new JSONObject(response);
                    //방 등록 했는지 안했는지
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
                catch (Exception e) {
                    Log.e("login", e.toString());
                }

            }
        }.start();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onStop() {
//        DemoSlider.stopAutoCycle();
        super.onStop();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
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
                    Log.e("constitutorCnt", "" + constitutorCnt);
                    roomRecentPeople.setText("" + constitutorCnt);
                    roomLimitPeople.setText("" + (room.getInt("limit") + 1));
                    title = room.getString("title");

                    numberOfImages = Integer.parseInt(room.getString("numberOfImages"));
                }
                catch (Exception e){
                    Log.e("onCreateView", e.toString());
                }
//Slider init
                SliderLayout mDemoSlider = (SliderLayout)rootView.findViewById(R.id.slider);

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

                    mDemoSlider.addSlider(textSliderView);
                }
                mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Default);
                mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
                mDemoSlider.setCustomAnimation(new DescriptionAnimation());
                mDemoSlider.setDuration(4000);
//                mDemoSlider.addOnPageChangeListener(this);
                //
                return rootView;
            }
            else if(getArguments().getInt(ARG_SECTION_NUMBER) == 2){
                Log.i("onCreateView", "2");                                                                 //유저 정보 페이지일때
                View rootView = inflater.inflate(R.layout.fragment_room_view_2, container, false);
                //ListView listview1 = (ListView)rootView.findViewById(R.id.room_master); //방장
                ListView listview2 = (ListView)rootView.findViewById(R.id.roommate); //룸메이트

                try{
                    for(int i=0; i<joins.length();i++) {
                        JSONObject join = joins.getJSONObject(i);
                        if(join.getString("position").equals("constitutor")){
                            for(int j=0;j<users.length();j++){
                                JSONObject user = users.getJSONObject(j);
                                if(join.getString("user").equals(user.getString("id"))){
                                    roommateAdapter.addRoommate(join.getString("id"), user.getString("name"), user.getString("birth"));
                                }
                            }
                        }
                    }
                } catch(Exception e) {
                    Log.e("roommate list", e.toString());
                }
                listview2.setAdapter(roommateAdapter);
                return rootView;
            }
            else if(getArguments().getInt(ARG_SECTION_NUMBER) == 3){
                Log.i("onCreateView", "3");
                View rootView;
                if(getArguments().getString(ARG_JOIN_POSITION).equals("owner")){                            //방장일때
                    rootView = inflater.inflate(R.layout.fragment_room_view_3, container, false);
                    thirdView = rootView;
                    ListView waitingListView = (ListView) rootView.findViewById((R.id.room_view_listview_waiting));
                    try{
                        for(int i=0;i<joins.length();i++){
                            JSONObject join = joins.getJSONObject(i);
                            if(join.getString("position").equals("waiting")){
                                for(int j=0;j<users.length();j++){
                                    JSONObject user = users.getJSONObject(j);
                                    if(join.getString("user").equals(user.getString("id"))){
                                        waitingAdapter.addWaiting(join.getString("id"), user.getString("name"), user.getString("birth"));
                                    }
                                }
                            }
                        }
                    }
                    catch(Exception e) {
                        Log.e("room change", e.toString());
                    }
                    waitingListView.setAdapter(waitingAdapter);
                }
                else if (getArguments().getString(ARG_JOIN_POSITION).equals("constitutor")){               //이방의 구성자일때
                    Log.i("onCreateView", "4");
                    rootView = inflater.inflate(R.layout.fragment_room_view_4, container, false);
                }
                else if (getArguments().getString(ARG_JOIN_POSITION).equals("waiting")) {                  //대기중일때
                    Log.i("onCreateView", "5");
                    rootView = inflater.inflate(R.layout.fragment_room_view_5, container, false);
                }
                else {                                                                                    //방문자
                    Log.i("onCreateView", "6");
                    rootView = inflater.inflate(R.layout.fragment_room_view_6, container, false);
                    Button joinButton = (Button)rootView.findViewById((R.id.room_view_button_join));
                    joinButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final String json = "{" +
                                    "\"user\" : \"" + User.getInstance().getId() + "\", " +
                                    "\"room\" : \"" + roomId + "\"" +
                                    "}";
                            new Thread() {
                                public void run() {
                                    String response = ServerManager.getInstance().post(Constants.SERVER_URL + "/room/join", json);
                                    if(response == null){
                                        Log.e("login", "서버 에러");
                                        return;
                                    }
                                    Log.e("loginResponse", response);
                                    try{
                                        JSONObject res = new JSONObject(response);
                                        if(res.getString("result").equals("fail")){      //등록한 방이 없슴
                                            handler.sendMessage(Message.obtain(handler, MSG_ROOM_VIEW_ERROR, ""));
                                            return;
                                        }
                                        handler.sendMessage(Message.obtain(handler, MSG_ROOM_VIEW_JOIN_SUCCESS, ""));
                                    }
                                    catch (Exception e) {
                                        Log.e("login", e.toString());
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
                        return "'모집현황'";
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
