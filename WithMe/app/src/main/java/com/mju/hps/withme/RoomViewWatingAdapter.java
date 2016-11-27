package com.mju.hps.withme;

import android.content.Context;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mju.hps.withme.constants.Constants;
import com.mju.hps.withme.model.User;
import com.mju.hps.withme.server.ServerManager;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by MinChan on 2016-11-27.
 */

public class RoomViewWatingAdapter extends BaseAdapter {

    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<WaitingItem> waitingList = new ArrayList<WaitingItem>() ;

    // RoomListAdapter 생성자
    public RoomViewWatingAdapter() {

    }

    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return waitingList.size() ;
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.room_view_waiting_list_item, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        ImageView iconImageView = (ImageView) convertView.findViewById(R.id.imageView1) ;
        TextView titleTextView = (TextView) convertView.findViewById(R.id.textView1) ;
        Button AckButton = (Button)  convertView.findViewById(R.id.button1) ;
        Button refuseButton = (Button)  convertView.findViewById(R.id.button2) ;



        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        final WaitingItem waitingItem = waitingList.get(position);

        // 아이템 내 각 위젯에 데이터 반영
//        iconImageView.setImageDrawable(roomItem.getIcon());
        titleTextView.setText(waitingItem.getName() + " (" + waitingItem.getBirth() + ")");
        AckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String json = "{" +
                        "\"joinId\" : \"" + waitingItem.getId() + "\"" +
                        "}";
                new Thread() {
                    public void run() {
                        String response = ServerManager.getInstance().post(Constants.SERVER_URL + "/room/ack", json);
                        if(RoomViewActivity.handler == null){
                            Log.e("RVhandler", "null");
                            return;
                        }
                        if(response == null){
                            Log.e("login", "서버 에러");
                            RoomViewActivity.handler.sendMessage(Message.obtain(RoomViewActivity.handler, RoomViewActivity.MSG_ROOM_VIEW_ERROR, ""));
                            return;
                        }
                        waitingList.remove(position);
                        RoomViewActivity.handler.sendMessage(Message.obtain(RoomViewActivity.handler, RoomViewActivity.MSG_ROOM_VIEW_WAITING_ACK, ""));
                    }
                }.start();
            }
        });

        refuseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String json = "{" +
                        "\"joinId\" : \"" + waitingItem.getId() + "\"" +
                        "}";
                new Thread() {
                    public void run() {
                        String response = ServerManager.getInstance().post(Constants.SERVER_URL + "/room/refuce", json);
                        if(RoomViewActivity.handler == null){
                            Log.e("RVhandler", "null");
                            return;
                        }
                        if(response == null){
                            Log.e("login", "서버 에러");
                            RoomViewActivity.handler.sendMessage(Message.obtain(RoomViewActivity.handler, RoomViewActivity.MSG_ROOM_VIEW_ERROR, ""));
                            return;
                        }
                        RoomViewActivity.handler.sendMessage(Message.obtain(RoomViewActivity.handler, RoomViewActivity.MSG_ROOM_VIEW_WAITING_REFUCE, ""));
                    }
                }.start();
            }
        });

        return convertView;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position ;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) {
        return waitingList.get(position) ;
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addWaiting(String id, String name, String birth) {
        WaitingItem item = new WaitingItem(id, name, birth);
        waitingList.add(item);
    }
}
