package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.CellSignalStrength;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import java.util.HashMap;
import java.util.Map;

import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.TelephonyManager;

import androidx.core.app.ActivityCompat;

import java.util.List;
import java.util.Objects;

public class GetOperators {
    private Context context;

    // Базовый метод для создания экземляра класса + передача контекста
    public GetOperators(Context context) {
        this.context = context;
    }

    // Метод для получения названия оператора связи
    public String getOperatorName() {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getNetworkOperatorName();
    }


    // Карта, которая содержит соответствие операторов и их ID
    private static final Map<String, String> operatorMap = new HashMap<String, String>() {{
        put("25001", "MTS");
        put("2500101", "MGTS");
        put("25002", "MegaFon");
        put("2500204", "VK Mobile");
        put("25006", "Danycom");
        put("25007", "Gals Telecom");
        put("25008", "VainahTelecom");
        put("25011", "Yota");
        put("25013", "Lycamobile");
        put("25014", "Skynet");
        put("25016", "MiATel");
        put("25018", "12.ru");
        put("25020", "Tele2");
        put("2502001", "Rostelecom");
        put("2502003", "TTK Mobile");
        put("2502007", "ER Telecom");
        put("2502008", "Virgin connect");
        put("2502012", "Central Telegraph");
        put("2502014", "MS Spetstelecom");
        put("25021", "Globaltel");
        put("25024", "Quantek");
        put("25026", "VTB Mobile");
        put("25027", "Letai");
        put("25032", "Win Mobile");
        put("25033", "Sevmobile");
        put("25034", "Krymtelekom");
        put("25035", "Motiv");
        put("25037", "MCN Telecom");
        put("25040", "Osnova Telecom");
        put("25041", "Gazprom Telecom");
        put("25042", "MTT");
        put("2504201", "Avia Mobile");
        put("2504202", "Almatel");
        put("25043", "EZ MOBILE");
        put("25044", "Allo Incognito");
        put("2504401", "SunSIM");
        put("2504402", "NEXT Mobile");
        put("25045", "Gazprombank Mobile");
        put("25047", "SIM SIM");
        put("25048", "V-Tell");
        put("25049", "Plusofon");
        put("25050", "SberMobile");
        put("25051", "Center2m");
        put("25052", "Easy4");
        put("25057", "Matrix Mobile");
        put("25058", "Transneft Telecom");
        put("25059", "WiFire");
        put("25060", "Volna Mobile");
        put("25061", "Intertelecom");
        put("25062", "T-Mobile");
        put("25077", "Glonass");
        put("25094", "MirTelecom");
        put("25099", "Beeline");
        put("2509901", "Atlas");
        put("2509903", "WhyFly");
        put("2509904", "Bezlimit");
        put("2509906", "Teletai");
        put("2509908", "Vipabonent");
        put("2509909", "ComfortWay");
    }};

    // Метод для получения названия оператора по его ID
    public String getOperatorIDtoName() {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String operatorID = telephonyManager.getSimOperator(); // Получаем ID оператора

        // Ищем в карте название оператора по его ID
        String operatorName = operatorMap.get(operatorID);

        // Возвращаем результат или "Не найдено" если ID оператора нет в карте
        return operatorName != null ? operatorName : "Не найдено";
    }

    // Метод для получения уровня сигнала связи
    public String getlvl() throws SecurityException {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String strength = null;
        List<CellInfo> cellInfos = telephonyManager.getAllCellInfo();   //This will give info of all sims present inside your mobile
        if(cellInfos != null) {
            for (int i = 0 ; i < cellInfos.size() ; i++) {
                if (cellInfos.get(i).isRegistered()) {
                    if (cellInfos.get(i) instanceof CellInfoWcdma) {
                        CellInfoWcdma cellInfoWcdma = (CellInfoWcdma) cellInfos.get(i);
                        CellSignalStrengthWcdma cellSignalStrengthWcdma = cellInfoWcdma.getCellSignalStrength();
                        strength = String.valueOf(cellSignalStrengthWcdma.getDbm());
                    } else if (cellInfos.get(i) instanceof CellInfoGsm) {
                        CellInfoGsm cellInfogsm = (CellInfoGsm) cellInfos.get(i);
                        CellSignalStrengthGsm cellSignalStrengthGsm = cellInfogsm.getCellSignalStrength();
                        strength = String.valueOf(cellSignalStrengthGsm.getDbm());
                    } else if (cellInfos.get(i) instanceof CellInfoLte) {
                        CellInfoLte cellInfoLte = (CellInfoLte) cellInfos.get(i);
                        CellSignalStrengthLte cellSignalStrengthLte = cellInfoLte.getCellSignalStrength();
                        strength = String.valueOf(cellSignalStrengthLte.getDbm());
                    } else if (cellInfos.get(i) instanceof CellInfoCdma) {
                        CellInfoCdma cellInfoCdma = (CellInfoCdma) cellInfos.get(i);
                        CellSignalStrengthCdma cellSignalStrengthCdma = cellInfoCdma.getCellSignalStrength();
                        strength = String.valueOf(cellSignalStrengthCdma.getDbm());
                    }
                }
            }
        }
        return strength;
    }
    public String getlvl_type() throws SecurityException {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String strength = null;
        List<CellInfo> cellInfos = telephonyManager.getAllCellInfo();   //This will give info of all sims present inside your mobile
        if(cellInfos != null) {
            for (int i = 0 ; i < cellInfos.size() ; i++) {
                if (cellInfos.get(i).isRegistered()) {
                    if (cellInfos.get(i) instanceof CellInfoWcdma) {
                        CellInfoWcdma cellInfoWcdma = (CellInfoWcdma) cellInfos.get(i);
                        CellSignalStrengthWcdma cellSignalStrengthWcdma = cellInfoWcdma.getCellSignalStrength();
                        strength = "3G";
                    } else if (cellInfos.get(i) instanceof CellInfoGsm) {
                        CellInfoGsm cellInfogsm = (CellInfoGsm) cellInfos.get(i);
                        CellSignalStrengthGsm cellSignalStrengthGsm = cellInfogsm.getCellSignalStrength();
                        strength = "2G";
                    } else if (cellInfos.get(i) instanceof CellInfoLte) {
                        CellInfoLte cellInfoLte = (CellInfoLte) cellInfos.get(i);
                        CellSignalStrengthLte cellSignalStrengthLte = cellInfoLte.getCellSignalStrength();
                        strength = "LTE";
                    } else if (cellInfos.get(i) instanceof CellInfoCdma) {
                        CellInfoCdma cellInfoCdma = (CellInfoCdma) cellInfos.get(i);
                        CellSignalStrengthCdma cellSignalStrengthCdma = cellInfoCdma.getCellSignalStrength();
                        strength = "3G";
                    }
                }
            }
        }
        return strength;
    }
}
