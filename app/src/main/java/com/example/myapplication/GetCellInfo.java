package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.TelephonyManager;

import java.util.Objects;


import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;

import androidx.core.content.ContextCompat;

import java.util.List;

public class GetCellInfo {
    private Context context;
    private String sCellInfo;
    public String Reuslt;
    // Базовый метод для создания экземляра класса + передача контекста
    public GetCellInfo(Context context) {
        this.context = context;
    }

    // Метод для получения названия вышки связи
    public String getCellID() {
        if (ContextCompat.checkSelfPermission(context,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return "-1";
        }else{
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            List<CellInfo> cis = telephonyManager.getAllCellInfo();
            for(CellInfo sCellInfo: cis){
                if(sCellInfo.isRegistered()){
                    if (sCellInfo instanceof CellInfoCdma) {
                        CellInfoCdma cic = (CellInfoCdma) sCellInfo;
                        //return cic.getCellIdentity().getNetworkId();
                    }
                    if (sCellInfo instanceof CellInfoGsm) {
                        CellInfoGsm cic = (CellInfoGsm) sCellInfo;
                        //return cic.getCellIdentity().getCid();
                    }
                    if (sCellInfo instanceof CellInfoLte) {
                        CellInfoLte cic = (CellInfoLte) sCellInfo;
                        //return cic.getCellIdentity().getCi();
                    }
                    if (sCellInfo instanceof CellInfoWcdma) {
                        CellInfoWcdma cic = (CellInfoWcdma) sCellInfo;
                        //return cic.getCellIdentity().getCid();
                    }
                }
            }
            return "0";
        }

    }

    // Метод для получения уровня сигнала связи
    public int getCellInfo() {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            return Objects.requireNonNull(telephonyManager.getSignalStrength()).getLevel();
        }
        else {
            return -1;
        }
    }
}