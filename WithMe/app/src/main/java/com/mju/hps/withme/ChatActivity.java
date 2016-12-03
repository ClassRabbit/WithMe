package com.mju.hps.withme;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.mju.hps.withme.constants.Constants;
import com.mju.hps.withme.model.User;
import com.mju.hps.withme.server.ServerManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatActivity extends ActionBarActivity {

    public static final int MSG_CHAT_SUCCESS = 1;
    public static final int MSG_CHAT_FAIL = 2;
    public static final int MSG_CHAT_ERROR = 3;
    public static final int MSG_CHAT_GET_SUCCESS = 4;

    private EditText messageET;
    private ListView messagesContainer;
    private Button sendBtn;
    private ChatAdapter adapter;
    private ArrayList<ChatMessage> chatHistory;
    private JSONArray chats;

    static public Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initControls();

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                ChatMessage chatMessage;
                switch (msg.what) {
                    case MSG_CHAT_SUCCESS:     // 성공
                        chatMessage = (ChatMessage)msg.obj;
                        displayMessage(chatMessage);
                        break;
                    case MSG_CHAT_FAIL:     // 실패
                        Log.i("chatHandler", "Fail");
                        break;
                    case MSG_CHAT_ERROR:     // 에러
                        Log.i("chatHandler", "Error");
                        break;
                    case MSG_CHAT_GET_SUCCESS:
                        Log.i("chatHandler", "GetSuccess");
                        chats = (JSONArray)msg.obj;
                        Log.i("chats.length", "" + chats.length());
                        try{
                            for(int i=0;i<chats.length();i++){
                                JSONObject chat = chats.getJSONObject(i);
                                chatMessage = new ChatMessage();
                                chatMessage.setMessage(chat.getString("name") + " : " + chat.getString("text"));
                                chatMessage.setDate(chat.getString("chatAt"));
                                if(chat.getString("user").equals(User.getInstance().getId())){
                                    chatMessage.setMe(true);
                                }
                                displayMessage(chatMessage);
                            }
                        }
                        catch(Exception e){
                            Log.e("chatGet", e.toString());
                        }
                        break;
                }
            }
        };


    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("ChatActivity", "onStart 실행");
        final String json = "{" +
                "\"user\" : \"" + User.getInstance().getId() + "\"" +
                "}";
        final Activity activity = this;
        new Thread() {
            public void run() {
                String response = ServerManager.getInstance().post(Constants.SERVER_URL + "/fcm/all", json);
                if(response == null){
                    Log.e("login", "서버 에러");
                    handler.sendMessage(Message.obtain(handler, MSG_CHAT_ERROR, ""));
                    return;
                }
                Log.e("loginResponse", response);
                try{
                    JSONObject res = new JSONObject(response);
                    JSONArray chats = res.getJSONArray("chats");
                    handler.sendMessage(Message.obtain(handler, ChatActivity.MSG_CHAT_GET_SUCCESS, chats));
                }
                catch (Exception e) {
                    Log.e("login", e.toString());
                }

            }
        }.start();
    }



//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_chat, menu);
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

    private void initControls() {
        messagesContainer = (ListView) findViewById(R.id.messagesContainer);
        messageET = (EditText) findViewById(R.id.messageEdit);
        sendBtn = (Button) findViewById(R.id.chatSendButton);

        RelativeLayout container = (RelativeLayout) findViewById(R.id.container);

        loadDummyHistory();

        //
        // 채팅 클릭 이벤트 함수
        //
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(messageET.getText().equals("")){
                    return;
                }
                String messageText = messageET.getText().toString();
                final String json = "{" +
                            "\"user\" : \"" + User.getInstance().getId() + "\", " +
                            "\"name\" : \"" + User.getInstance().getName() + "\", " +
                            "\"time\" : \"" + DateFormat.getDateTimeInstance().format(new Date()) + "\"," +
                            "\"text\" : \"" + messageText + "\"" +
                        "}";
                new Thread() {
                    public void run() {
                        String response = ServerManager.getInstance().post(Constants.SERVER_URL + "/fcm/chat", json);
                        if(response == null){
                            Log.e("login", "서버 에러");
                            return;
                        }
                    }
                }.start();
                messageET.setText("");
            }
        });


    }

    public void displayMessage(ChatMessage message) {
        adapter.add(message);
        adapter.notifyDataSetChanged();
        scroll();
    }

    private void scroll() {
        messagesContainer.setSelection(messagesContainer.getCount() - 1);
    }

    private void loadDummyHistory(){

        chatHistory = new ArrayList<ChatMessage>();

        adapter = new ChatAdapter(ChatActivity.this, new ArrayList<ChatMessage>());
        messagesContainer.setAdapter(adapter);

        for(int i=0; i<chatHistory.size(); i++) {
            ChatMessage message = chatHistory.get(i);
            displayMessage(message);
        }

    }


    public Handler getHandler() {
        return handler;
    }

}