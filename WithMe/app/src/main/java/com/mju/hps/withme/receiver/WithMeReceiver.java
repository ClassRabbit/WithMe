package com.mju.hps.withme.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.mju.hps.withme.MainActivity;
import com.mju.hps.withme.SubActivity;

/**
 * Created by KMC on 2016. 11. 17..
 */

public class WithMeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.e("action", action);
        if(action.equals("com.mju.hps.withme.sendreciver.createUserFail")){
            Toast.makeText(context, "회원 가입에 실패하였습니다", Toast.LENGTH_SHORT).show();
        }
        else if(action.equals("com.mju.hps.withme.sendreciver.createUserSuccess")){

            Intent intent2=new Intent(context, MainActivity.class);
            intent2.putExtra("text","data");
            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent2);
            Toast.makeText(context, "회원 가입에 성공하였습니다", Toast.LENGTH_SHORT).show();
        }
    }
}
