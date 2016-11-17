// ITimeService.aidl
package com.mju.hps.withme;

// Declare any non-default types here with import statements
import com.mju.hps.withme.ITimeServiceCallback;

interface ITimeService {
    boolean registerTimeServiceCallback( ITimeServiceCallback callback );
    boolean unregisterTimeServiceCallback( ITimeServiceCallback callback );
}
