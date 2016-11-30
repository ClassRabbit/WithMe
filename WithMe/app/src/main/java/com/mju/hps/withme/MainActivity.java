package com.mju.hps.withme;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.mju.hps.withme.constants.Constants;
import com.mju.hps.withme.database.DatabaseLab;
import com.mju.hps.withme.model.User;
import com.mju.hps.withme.server.ServerManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int MSG_MAIN_CAN_JOIN = 1;
    private static final int MSG_MAIN_CANNOT_JOIN = 2;
    private static final int MSG_MAIN_ERROR = 3;
    private static final int MSG_MAIN_LOGOUT_SUCCESS = 4;
    private static final int MSG_MAIN_LOGOUT_FAIL = 5;
    private static final int MSG_MAIN_LOGOUT_NULL = 6;
    private static final int MSG_MAIN_ROOMS = 100;

    private FloatingActionButton fab;
    private Handler handler;
    private ListView listview;
    private JSONArray rooms;
    private Toolbar toolbar;
    private RoomListAdapter adapter;
    private boolean isJoin;
    private JSONObject myRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        listview = (ListView)findViewById(R.id.room_list);
        adapter = new RoomListAdapter();
        setSupportActionBar(toolbar);


        //밑의 floating action bar
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RoomCreateActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_MAIN_CAN_JOIN:     // 현재 가입한 방이 없음
                        isJoin = false;
                        fab.setVisibility(View.VISIBLE);
                        break;
                    case MSG_MAIN_CANNOT_JOIN:     // 현재 가입한 방이 있음
                        isJoin = true;
                        fab.setVisibility(View.GONE);
                        break;
                    case MSG_MAIN_ERROR:     // 서버 에러
                        Toast.makeText(MainActivity.this, "서버에 연결되지 않았습니다.", Toast.LENGTH_SHORT).show();
                        break;
                    case MSG_MAIN_LOGOUT_SUCCESS:
//                        // DB비우고
//                        DatabaseLab.getInstance().logoutUser();
//                        //로그인뷰로 이동
//                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//                        startActivity(intent);
//                        MainActivity.this.finish();
                        break;
                    case MSG_MAIN_LOGOUT_FAIL:
                        Toast.makeText(MainActivity.this, "로그아웃에 실패했습니다.", Toast.LENGTH_SHORT).show();
                        break;
                    case MSG_MAIN_LOGOUT_NULL:
                        Toast.makeText(MainActivity.this, "현재 아이디는 삭제된 아이디입니다.", Toast.LENGTH_SHORT).show();
                        break;
                    case MSG_MAIN_ROOMS:
                        rooms = (JSONArray)msg.obj;
                        adapter = new RoomListAdapter();
                        Log.e("MSG_MAIN_ROOMS", "" + rooms.length());
                        try{
                            for(int i=0;i<rooms.length();i++){
                                JSONObject room = rooms.getJSONObject(i);
                                adapter.addRoom(room.getString("id"), room.getString("title"), room.getInt("limit"), room.getString("address"));
                            }
                            listview.setAdapter(adapter);
                        }
                        catch(Exception e) {
                            Log.e("room change", e.toString());
                        }
                        break;
                }
            }
        };



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                // get item
                RoomItem roomItem = (RoomItem) parent.getItemAtPosition(position);
                Intent intent =new Intent(MainActivity.this, RoomViewActivity.class);
                intent.putExtra("roomId", roomItem.getId());
                intent.putExtra("tabLocation", 0);
                MainActivity.this.startActivity(intent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("MainActivity", "onStart 실행");
        final String json = "{" +
                "\"user\" : \"" + User.getInstance().getId() + "\"" +
                "}";
        final Activity activity = this;
        new Thread() {
            public void run() {
                String response = ServerManager.getInstance().post(Constants.SERVER_URL + "/main", json);
                if(response == null){
                    Log.e("onStart", "서버 에러");
                    handler.sendMessage(Message.obtain(handler, MSG_MAIN_ERROR, ""));
                    return;
                }
                Log.e("MainLoad", response);
                try{
                    JSONObject res = new JSONObject(response);
                    //방 등록 했는지 안했는지
                    if(res.getString("result").equals("fail")){
                        Log.e("onStart", "서버 에러");
                        handler.sendMessage(Message.obtain(handler, MSG_MAIN_ERROR, ""));
                    }
                    else {
                        if(res.getBoolean("isJoin") == false){      //등록한 방이 없슴
                            Log.e("isJoin", "등록한 방 없슴");
                            handler.sendMessage(Message.obtain(handler, MSG_MAIN_CAN_JOIN, ""));
                        }
                        else {
                            Log.e("isJoin", "등록한 방 있슴");
                            myRoom = res.getJSONObject("myRoom");
                            handler.sendMessage(Message.obtain(handler, MSG_MAIN_CANNOT_JOIN, ""));
                        }
                        //방 리스트 가져오기
                        JSONArray rooms = res.getJSONArray("rooms");
                        handler.sendMessage(Message.obtain(handler, MSG_MAIN_ROOMS, rooms));
                    }
                }
                catch (Exception e) {
                    Log.e("login", e.toString());
                }

            }
        }.start();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.user_info) {
            Intent intent = new Intent(this, UserInfoActivity.class);
            startActivity(intent);
        } else if(id == R.id.user_home) {
            if(!isJoin){
                Toast.makeText(MainActivity.this, "방에 참여되셔야 이용 가능합니다.", Toast.LENGTH_SHORT).show();
            }
            else {
                Intent intent = new Intent(this, RoomViewActivity.class);
                String myRoomId = null;
                try{
                    myRoomId = myRoom.getString("id");
                }
                catch (Exception e){
                    Log.e("myRoom", e.toString());
                }
                intent.putExtra("roomId", myRoomId);
                intent.putExtra("tabLocation", 0);
                startActivity(intent);
            }
        }
//        else if (id == R.id.room_status) {
//            if(!isJoin){
//                Toast.makeText(MainActivity.this, "방에 참여되셔야 이용 가능합니다.", Toast.LENGTH_SHORT).show();
//            }
//            else {
//                Intent intent = new Intent(this, NfcActivity.class);
//                startActivity(intent);
//            }
//        }
        else if (id == R.id.chatting) {
            if(!isJoin){
                Toast.makeText(MainActivity.this, "방에 참여되셔야 이용 가능합니다.", Toast.LENGTH_SHORT).show();
            }
            else {
                Intent intent = new Intent(this, ChatActivity.class);
                startActivity(intent);
            }

        } else if(id == R.id.log_out) {
            final String json = "{" +
                    "\"id\" : \"" + User.getInstance().getId() + "\"" +
                    "}";
            Log.e("id", User.getInstance().getId());
            final Activity activity = this;
            new Thread() {
                public void run() {
                    String response = ServerManager.getInstance().post(Constants.SERVER_URL + "/user/logout", json);
//                    if(response == null){
//                        Log.e("login", "서버 에러");
//                        handler.sendMessage(Message.obtain(handler, MSG_MAIN_ERROR, ""));
//                        return;
//                    }
//                    Log.e("loginResponse", response);
//                    try{
//                        JSONObject obj = new JSONObject(response);
//                        if(obj.getString("result").equals("success")){
//                            Log.i("login", "로그아웃 성공");
//                            handler.sendMessage(Message.obtain(handler, MSG_MAIN_LOGOUT_SUCCESS, ""));
//                            Intent resultIntent=new Intent(activity, MainActivity.class);
//                            activity.startActivity(resultIntent);
//                        }
//                        else if(obj.getString("result").equals("fail")) {
//                            Log.e("login", "로그아웃 실패");
//                            handler.sendMessage(Message.obtain(handler, MSG_MAIN_LOGOUT_FAIL, ""));
//                        }
//                        else if(obj.getString("result").equals("error")) {
//                            Log.e("login", "로그아웃 서버 에러");
//                            handler.sendMessage(Message.obtain(handler, MSG_MAIN_ERROR, ""));
//                        }
//                        else {
//                            Log.e("login", "NULL 에러");
//                            handler.sendMessage(Message.obtain(handler, MSG_MAIN_LOGOUT_NULL, ""));
//                        }
//                    }
//                    catch (Exception e) {
//                        Log.e("login", e.toString());
//                    }
                }
            }.start();
            // DB비우고
            DatabaseLab.getInstance().logoutUser();
            //로그인뷰로 이동
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            MainActivity.this.finish();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
