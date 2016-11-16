package com.mju.hps.withme.service;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by KMC on 2016. 11. 16..
 */

public class FcmInstanceIdService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        // 보안때문에 일정기간 간격으로 기기 토큰 재생성이 되는듯
        // 이게 실행될때 사용자의 기기 토큰을 교체해줘야 할듯
        Log.e("FcmInsIdService", "FcmInsIdService 실행됨");
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        sendRegistrationToServer(refreshedToken);
    }
    private void sendRegistrationToServer(String refreshedToken) {

    }
}
