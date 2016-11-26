package com.mju.hps.withme;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.mju.hps.withme.constants.Constants;
import com.mju.hps.withme.model.RoomData;
import com.mju.hps.withme.model.User;
import com.mju.hps.withme.model.UserData;
import com.mju.hps.withme.server.ServerManager;

import org.json.JSONArray;
import org.json.JSONObject;

public class RoomViewActivity extends AppCompatActivity {

    private static final int MSG_ROOM_VIEW_CAN_JOIN = 1;
    private static final int MSG_ROOM_VIEW_CANNOT_JOIN = 2;
    private static final int MSG_ROOM_VIEW_ERROR = 3;
    private static final int MSG_ROOM_VIEW_SUCCESS = 4;
    private static final int MSG_ROOM_VIEW_NULL = 5;

    private String roomId;
    private Handler handler;
    private UserData owner;
    private RoomData room;

    private Button joinButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_view);

        Intent intent = getIntent();
        roomId = (String)intent.getSerializableExtra("roomId");
        joinButton = (Button)findViewById(R.id.room_view_button_join);


        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String str;
                switch (msg.what) {
                    case MSG_ROOM_VIEW_CAN_JOIN:     // 현재 가입한 방이 없음
                        str = (String)msg.obj;
                        joinButton.setVisibility(View.VISIBLE);
                        break;
                    case MSG_ROOM_VIEW_CANNOT_JOIN:     // 현재 가입한 방이 있음
                        str = (String)msg.obj;
                        joinButton.setVisibility(View.GONE);
                        break;
                    case MSG_ROOM_VIEW_ERROR:
                        str = (String)msg.obj;
                        Toast.makeText(RoomViewActivity.this, "서버에 연결하지 못했습니다.", Toast.LENGTH_SHORT).show();
                        break;
                    case MSG_ROOM_VIEW_SUCCESS:
                        JSONArray data = (JSONArray)msg.obj;
                        try{        //1번 요소는 방, 2번 요소는 유저, 3번요소는 이방에 join한 인원수
                            JSONObject roomJson = data.getJSONObject(0);
                            JSONObject userJson = data.getJSONObject(1);
                            int joinCnt = data.getInt(2);
                            // 아래와 같은 형식으로 참고해서 관리하면 될듯
//                            owner = new UserData(roomJson.getString("title"), roomJson.getString("content"), roomJson.getInt("limit"), roomJson.getJSONArray())
                            Log.e("RoomView 값받기 테스트", "join 수 : " + joinCnt);

                            //
                            // 데이터를 받고나서 여기서부터 뷰를 그려야함
                            // 여기 아래에 뷰를 꾸미는 코드 시작
                            //
                        }
                        catch(Exception e){
                            Log.e("RoomView", e.toString());
                        }
                        break;
                }
            }
        };


        final String json = "{" +
                "\"user\" : \"" + User.getInstance().getId() + "\", " +
                "\"roomId\" : \"" +  roomId + "\"" +
                "}";
        final Activity activity = this;
        new Thread() {
            public void run() {
                String response = ServerManager.getInstance().post(Constants.SERVER_URL + "/main/view", json);
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

                    //[방 정보, 만든이 정보] 형태의 JsonArray 가져옴
                    JSONArray data = res.getJSONArray("data");
                    handler.sendMessage(Message.obtain(handler, MSG_ROOM_VIEW_SUCCESS, data));
                }
                catch (Exception e) {
                    Log.e("login", e.toString());
                }

            }
        }.start();
    }
}
