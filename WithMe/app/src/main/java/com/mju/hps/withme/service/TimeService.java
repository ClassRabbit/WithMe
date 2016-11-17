package com.mju.hps.withme.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import com.mju.hps.withme.ITimeService;
import com.mju.hps.withme.ITimeServiceCallback;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by KMC on 2016. 11. 17..
 */

public class TimeService extends Service {
    Thread m_ThrdUpdateTime = null;
    /**
     *
     */
    public TimeService() {
        // TODO Auto-generated constructor stub
    }

	/* (non-Javadoc)
	 * @see android.app.Service#onBind(android.content.Intent)
	 */

    final RemoteCallbackList<ITimeServiceCallback> m_cbLists = new
            RemoteCallbackList<ITimeServiceCallback>();

    ITimeService.Stub m_binder = new ITimeService.Stub ()
    {

        @Override
        public boolean registerTimeServiceCallback(ITimeServiceCallback callback)
                throws RemoteException {
            Log.i("B&U","m_cbLists.register ");
            m_cbLists.register(callback);
            launchTimeUpdatorThread();
            return true;
        }

        @Override
        public boolean unregisterTimeServiceCallback(ITimeServiceCallback callback)
                throws RemoteException {
            m_cbLists.unregister(callback);
            return false;
        }
    };

    private void launchTimeUpdatorThread()
    {
        m_ThrdUpdateTime = new Thread("Time Updator")
        {
            @Override
            public void run() {
                while (true)
                {
                    Log.i("B&U","Update Time... ");
                    int cnt = m_cbLists.beginBroadcast();
                    for (int idx = 0 ; idx < cnt ; idx++ )
                    {
                        ITimeServiceCallback callback = m_cbLists.getBroadcastItem(idx);
                        //m_cbLists.getBroadcastCookie(index);

                        long curTime = System.currentTimeMillis();
                        SimpleDateFormat tDateFormat = new SimpleDateFormat("yy/MM/dd hh:mm.ss");
                        try {
                            callback.onTimeChanged(tDateFormat.format(new Date(curTime)));
                        } catch (RemoteException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    m_cbLists.finishBroadcast();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        };

        m_ThrdUpdateTime.start();

    }



    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        Log.i("B&U","onBind");

        return m_binder;
    }

    @Override
    public boolean onUnbind(Intent intent)
    {
        return super.onUnbind(intent);
    }
}
