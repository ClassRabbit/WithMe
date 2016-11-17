package com.mju.hps.withme;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class SubActivity2 extends AppCompatActivity {

    private ITimeService m_remoteTimeSvc = null;
    private ITimeServiceCallback m_remoteCallback = null;

    // for Binding Service
    private ServiceConnection m_TimeSvcConnection=null;

    // UI
    private TextView m_tvTimeInfo= null;
    private TextView m_tvStatus = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub2);

        //
        // basic settings
        m_tvTimeInfo = (TextView) findViewById(R.id.timeInfo);
        m_tvStatus = (TextView) findViewById(R.id.status);
        m_tvStatus.setText("Activity is started ...");

        // #1 Service Connection
        if (m_TimeSvcConnection == null)
        {
            m_TimeSvcConnection = new ServiceConnection ()
            {

                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    m_remoteTimeSvc = ITimeService.Stub.asInterface(service);
                    m_tvStatus.setText("Service is Connected ...");

                    try {
                        if (m_remoteTimeSvc.registerTimeServiceCallback(m_remoteCallback) )
                            m_tvStatus.setText("Callback was registered... ");
                        else
                            m_tvStatus.setText("Registering Callback was failed... ");
                    } catch (RemoteException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    m_remoteTimeSvc = null;
                    m_tvStatus.setText("Service is Disconnected ...");
                }
            };
        }

        // #2. Timer Callback
        m_remoteCallback = new ITimeServiceCallback.Stub ()
        {
            String m_TimeInfo = null;

            @Override
            public void onTimeChanged(String timeInfo) throws RemoteException {
                // TODO Auto-generated method stub
                Log.i("B&U","onTimeChanged"+timeInfo);

                m_TimeInfo = timeInfo;

                Runnable updateUI = new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        m_tvTimeInfo.setText(m_TimeInfo);
                    }
                };

                m_tvTimeInfo.postDelayed(updateUI, 10);

            }
        };

        // #3. Bind Service
        Intent intent = new Intent("com.mju.hps.withme.TimeService");
        intent.setPackage("com.mju.hps.withme");
        bindService( intent, m_TimeSvcConnection, Context.BIND_AUTO_CREATE );
        //
    }
}
