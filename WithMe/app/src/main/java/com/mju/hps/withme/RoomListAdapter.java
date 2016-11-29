package com.mju.hps.withme;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mju.hps.withme.constants.Constants;
import com.mju.hps.withme.database.DatabaseLab;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by MinChan on 2016-11-26.
 */

public class RoomListAdapter extends BaseAdapter{
    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<RoomItem> roomList = new ArrayList<RoomItem>() ;

    // RoomListAdapter 생성자
    public RoomListAdapter() {

    }

    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return roomList.size() ;
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_mainview, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        ImageView imageView = (ImageView) convertView.findViewById(R.id.item_main_image_view) ;
        TextView titleTextView = (TextView) convertView.findViewById(R.id.item_main_text_view) ;
        TextView addressTextView = (TextView) convertView.findViewById(R.id.item_main_text_view2) ;
//        TextView descTextView = (TextView) convertView.findViewById(R.id.textView2) ;

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        RoomItem roomItem = roomList.get(position);

        // 아이템 내 각 위젯에 데이터 반영
        titleTextView.setText(roomItem.getTitle());
        addressTextView.setText(roomItem.getAddress());
//        descTextView.setText(roomItem.getAddress());
        getRoomImage(roomItem.getId(), imageView);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        return convertView;
    }

    private Bitmap profileImage;
    private Boolean profileImageUploadCheck = false;
    public void getRoomImage(String id, ImageView imageView){
        final String baseShoppingURL = Constants.SERVER_URL + "/images/room/" + id +"/0.png";
        Log.e("ImageURL", baseShoppingURL);
        Thread mThread = new Thread() {
            @Override
            public void run() {
                try {
                    URL url = new URL(baseShoppingURL); // URL 주소를 이용해서 URL 객체 생성
                    //  아래 코드는 웹에서 이미지를 가져온 뒤
                    //  이미지 뷰에 지정할 Bitmap을 생성하는   
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    profileImage = BitmapFactory.decodeStream(is);
                    profileImageUploadCheck = true;
                }
                catch(IOException ex) {
                    profileImageUploadCheck = false;
                    Log.e("사진 읽어오기 실패", ex.toString());
                }
            }
        };
        mThread.start(); // 웹에서 이미지를 가져오는 작업 스레드 실행.
        try {
            mThread.join();
            if(profileImageUploadCheck){
                imageView.setImageBitmap(profileImage);
            }
            else {
                imageView.setImageResource(R.drawable.user_information);
            }
        } catch (InterruptedException e) {

        }
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position ;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) {
        return roomList.get(position) ;
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addRoom(String id, String title, int limit, String address) {
        RoomItem item = new RoomItem(id, title, limit, address);
        roomList.add(item);
    }
}
