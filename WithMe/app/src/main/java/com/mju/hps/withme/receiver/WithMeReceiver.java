package com.mju.hps.withme.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

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
            Toast.makeText(context, "회원 가입에 성하였습니다", Toast.LENGTH_SHORT).show();
        }
    }
}
