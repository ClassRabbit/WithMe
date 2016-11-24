// IMessageService.aidl
package com.mju.hps.withme;

// Declare any non-default types here with import statements
import com.mju.hps.withme.IMessageServiceCallback;

interface IMessageService {
    boolean registerMessageServiceCallback( IMessageServiceCallback callback );
    boolean unregisterMessageServiceCallback( IMessageServiceCallback callback );
}
