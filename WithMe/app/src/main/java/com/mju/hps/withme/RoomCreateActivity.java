package com.mju.hps.withme;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class RoomCreateActivity extends AppCompatActivity {

    TextView room_title, room_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_create);

        Spinner monthSpinner = (Spinner)findViewById(R.id.number_of_roommate);
        ArrayAdapter room_people = ArrayAdapter.createFromResource(this,
                R.array.number_of_people, android.R.layout.simple_spinner_item);
        room_people.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(room_people);
    }

    public void create_room() {
        //텍스트뷰 & 맵 & 사진 다 서버로 넘겨주면 될듯?
    }

    public void input_map() {
        //map 넣고
    }

    public void select_photo() {
        //사진 고르고
    }
}
