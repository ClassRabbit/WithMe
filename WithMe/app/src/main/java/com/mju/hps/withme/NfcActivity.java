package com.mju.hps.withme;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.kakao.kakaolink.AppActionBuilder;
import com.kakao.kakaolink.AppActionInfoBuilder;
import com.kakao.kakaolink.KakaoLink;
import com.kakao.kakaolink.KakaoTalkLinkMessageBuilder;

import java.nio.charset.Charset;


public class NfcActivity extends AppCompatActivity implements NfcAdapter.CreateNdefMessageCallback{

    private NfcAdapter nfcAdapter;
    private NdefMessage message;
    private String roomId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);

        Intent intent = getIntent();
        roomId = (String)intent.getSerializableExtra("roomId");


        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if(nfcAdapter != null){
            Toast.makeText(this, "nfc 사용가능합니다", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "nfc를 키세요!!!!!!!", Toast.LENGTH_SHORT).show();
        }

        // Ndef 메시지 송신용 콜백을 설정한다
        nfcAdapter.setNdefPushMessageCallback(this, this);
    }

    @Override
    public void onPause() {
        super.onPause();
        // Ndef 메시지 송신을 비활성화한다
        nfcAdapter.disableForegroundNdefPush(this);
    }

        // Ndef 메시지 생성
    public NdefRecord createMimeRecord(String mimeType, byte[] payload) {
        // 받은 MIME 유형의 바이트 배열을 취득한다
        byte[] mimeBytes = mimeType.getBytes(Charset.forName("US-ASCII"));

        // TNF, 포맷, ID, 데이터를 지정하여 Ndef 레코드를 생성
        NdefRecord mimeRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA,
                mimeBytes, new byte[0], payload);
        return mimeRecord;
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent nfcEvent) {
        Log.i("i", "createNdefMessage");
        // 입력된 문자열을 취득한다
        String text = roomId;
        // Ndef 메시지를 생성한다
        NdefMessage msg = new NdefMessage(new NdefRecord[] {
                createMimeRecord("application/com.mju.hps.withme", text.getBytes())
        });
        // 생성한 Ndef 메시지를 반환한다
        // ※반환한 Ndef 메시지를 NFC 어댑터가 송신한다(Push)
        return msg;
    }

    public void sendKakao(View view){
        try{
            final KakaoLink kakaoLink = KakaoLink.getKakaoLink(this);
            final KakaoTalkLinkMessageBuilder kakaoTalkLinkMessageBuilder = kakaoLink.createKakaoTalkLinkMessageBuilder();
            String text = "룸메이트를 구합니다!";
            kakaoTalkLinkMessageBuilder.addText(text);
            Log.e("roomId", roomId);
            kakaoTalkLinkMessageBuilder.addAppLink("WithMe",
                    new AppActionBuilder()
                            .addActionInfo(AppActionInfoBuilder
                                    .createAndroidActionInfoBuilder()
                                    .setExecuteParam("roomId=" + roomId)
                                    .setMarketParam("referrer=kakaotalklink")
                                    .build())
                            .build());
            kakaoLink.sendMessage(kakaoTalkLinkMessageBuilder, this);

        }
        catch (Exception e) {
            Log.e("kakao", e.toString());
        }
    }
}
