package com.mju.hps.withme;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.ListView;
import android.widget.Toast;
import com.mju.hps.withme.constants.Constants;
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
    private static final int MSG_MAIN_ROOMS = 100;

    private FloatingActionButton fab;
    private Handler handler;
    private ListView listview;
    private JSONArray rooms;
    private Toolbar toolbar;
    private RoomListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        listview = (ListView)findViewById(R.id.room_list);
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
                String str;
                switch (msg.what) {
                    case MSG_MAIN_CAN_JOIN:     // 현재 가입한 방이 없음
                        str = (String)msg.obj;
                        fab.setVisibility(View.VISIBLE);
                        break;
                    case MSG_MAIN_CANNOT_JOIN:     // 현재 가입한 방이 있음
                        str = (String)msg.obj;
                        fab.setVisibility(View.GONE);
                        break;
                    case MSG_MAIN_ERROR:     // 현재 가입한 방이 있음
                        str = (String)msg.obj;
                        Toast.makeText(MainActivity.this, "서버에 연결하지 못했습니다.", Toast.LENGTH_SHORT).show();
                        break;
                    case MSG_MAIN_ROOMS:
                        rooms = (JSONArray)msg.obj;
                        Log.e("MSG_MAIN_ROOMS", "" + rooms.length());
                        try{
                            for(int i=0;i<rooms.length();i++){
                                JSONObject room = rooms.getJSONObject(i);
                                adapter = new RoomListAdapter();
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


//        roomitem room1 = new roomitem("Room1");
//        roomitem room2 = new roomitem("Room2");
//        roomitem room3 = new roomitem("Room3");
//        data.add(room1);
//        data.add(room2);
//        data.add(room3);

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
                    Log.e("login", "서버 에러");
                    handler.sendMessage(Message.obtain(handler, MSG_MAIN_ERROR, ""));
                    return;
                }
                Log.e("loginResponse", response);
                try{
                    JSONObject res = new JSONObject(response);
                    //방 등록 했는지 안했는지
                    if(res.getBoolean("isJoin") == false){      //등록한 방이 없슴
                        Log.e("isJoin", "등록한 방 없슴");
                        handler.sendMessage(Message.obtain(handler, MSG_MAIN_CAN_JOIN, ""));
                    }
                    else {
                        Log.e("isJoin", "등록한 방 있슴");
                        handler.sendMessage(Message.obtain(handler, MSG_MAIN_CANNOT_JOIN, ""));
                    }

                    //방 리스트 가져오기
                    JSONArray rooms = res.getJSONArray("rooms");
                    handler.sendMessage(Message.obtain(handler, MSG_MAIN_ROOMS, rooms));
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
        } else if (id == R.id.room_status) {

        } else if (id == R.id.chatting) {
            Intent intent = new Intent(this, ChatActivity.class);
            startActivity(intent);
        } else if(id == R.id.log_out) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
