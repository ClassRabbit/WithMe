// IMessageServiceCallback.aidl
package com.mju.hps.withme;

// Declare any non-default types here with import statements

interface IMessageServiceCallback {
    oneway void onMessage( String Message);
}
