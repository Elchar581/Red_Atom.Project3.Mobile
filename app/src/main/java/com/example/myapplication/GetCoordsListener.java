package com.example.myapplication;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import androidx.annotation.NonNull;

public class GetCoordsListener implements LocationListener {

    private com.example.myapplication.LocListenerInterface LocListenerInterface;
    @Override
    public void onLocationChanged(@NonNull Location location) {
        LocListenerInterface.OnLocationChange(location);
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        LocationListener.super.onStatusChanged(provider, status, extras);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        LocationListener.super.onProviderDisabled(provider);
    }

    public void setLocListenerInterface(com.example.myapplication.LocListenerInterface locListenerInterface) {
        LocListenerInterface = locListenerInterface;
    }
}
