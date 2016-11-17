// ITimeServiceCallback.aidl
package com.mju.hps.withme;

// Declare any non-default types here with import statements

interface ITimeServiceCallback {
    oneway void onTimeChanged( String timeInfo);
}
