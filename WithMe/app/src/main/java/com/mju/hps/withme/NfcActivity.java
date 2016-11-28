package com.mju.hps.withme;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.nio.charset.Charset;


public class NfcActivity extends AppCompatActivity implements NfcAdapter.CreateNdefMessageCallback{

    private NfcAdapter nfcAdapter;
    private NdefMessage message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);

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
        String text = "testText";
        // Ndef 메시지를 생성한다
        NdefMessage msg = new NdefMessage(new NdefRecord[] {
                createMimeRecord("application/com.example.minchan.nfc_test2", text.getBytes())
        });
        // 생성한 Ndef 메시지를 반환한다
        // ※반환한 Ndef 메시지를 NFC 어댑터가 송신한다(Push)
        return msg;
    }
}
