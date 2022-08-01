package com.fj.gnss.nmea;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.fj.gnss.GnssManager;
import com.fj.gnss.bean.GnssState;
import com.fj.gnss.callback.RTKCommunicationCallBack;
import com.fj.gnss.nmea.msg.BestPos2Msg;
import com.fj.gnss.nmea.msg.GGAMessage;
import com.fj.gnss.nmea.msg.HDTMessage;
import com.fj.gnss.nmea.msg.SettingMessage;
import com.fj.gnss.nmea.msg.VTGMessage;

/**
 * NMEA协议解析类
 *
 * @author jarven
 */
public class NMEADataParse implements INMEADataParse {

    private static final String TAG = NMEADataParse.class.getSimpleName();

    private final GnssState gnssState = new GnssState();
    private RTKCommunicationCallBack mRtkCommCallback;

    /**
     * nmea缓存
     */
    private final StringBuffer nmea = new StringBuffer();
    private int step = 0;

    private synchronized void parseData(String nmea) {
        if (TextUtils.isEmpty(nmea)) {
            return;
        }
        if (mRtkCommCallback != null) {
            mRtkCommCallback.onParsedNmea(nmea);
        }
        try {
            if (GGAMessage.toBeGGA(nmea)) {
                GGAMessage ggaMessage = new GGAMessage();
                nmea += "\r\n";
                if (ggaMessage.parse(nmea)) {
                    gnssState.setReceiveTime(System.currentTimeMillis());

                    if (mRtkCommCallback != null) {
                        mRtkCommCallback.onParsedGGA(nmea);
                        GnssManager.getInstance().getNetRtcmManager().setGgaNMEA(nmea);
                    }
                    gnssState.setHeight(ggaMessage.getHeight());
                    gnssState.setAltitude(ggaMessage.getAltitude());
                    gnssState.setGpsState(ggaMessage.getGpsState());
                    gnssState.setLat(ggaMessage.getLatitude());
                    gnssState.setLon(ggaMessage.getLongitude());
                    gnssState.setSatelliteNum(ggaMessage.getSatelliteNum());
                    gnssState.setNorthEarth(ggaMessage.isNorthEarth());
                    gnssState.setEastEarth(ggaMessage.isEastEarth());
                    gnssState.setValid(true);
                } else if (gnssState.isTimeOut()) {
                    gnssState.setHeight(0);
                    gnssState.setAltitude(0);
                    gnssState.setGpsState(0);
                    gnssState.setLat(0);
                    gnssState.setLon(0);
                    gnssState.setSatelliteNum(0);
                    gnssState.setValid(false);
                }
            } else if (BestPos2Msg.toBeBestPos2(nmea)) {
                BestPos2Msg bestPos2Msg = new BestPos2Msg();
                if (bestPos2Msg.parse(nmea)) {
                    gnssState.setLat2(bestPos2Msg.getLat());
                    gnssState.setLon2(bestPos2Msg.getLon());
                    gnssState.setAltitude2(bestPos2Msg.getAltitude());
                    gnssState.setHeight2(bestPos2Msg.getHeight());
                    gnssState.setGpsState2(bestPos2Msg.getGpsState());
                } else {
                    gnssState.setLat2(0);
                    gnssState.setLon2(0);
                    gnssState.setAltitude2(0);
                    gnssState.setHeight2(0);
                    gnssState.setGpsState2(0);
                }
            } else if (VTGMessage.toBeVTG(nmea)) {
                VTGMessage vtgMessage = new VTGMessage();
                if (vtgMessage.parse(nmea)) {
                    gnssState.setSpeed(vtgMessage.getSpeed());
                } else {
                    gnssState.setSpeed(0);
                }
            } else if (HDTMessage.toBeHDT(nmea)) {
                HDTMessage hdtMessage = new HDTMessage();
                if (hdtMessage.parse(nmea)) {
                    gnssState.setHeading(hdtMessage.getHeading());
                } else {
                    gnssState.setHeading(0);
                }
            } else if (SettingMessage.toBeSetting(nmea)) {
                SettingMessage settingMessage = new SettingMessage();
                if (settingMessage.parse(nmea)) {
                    gnssState.setRtkMode(settingMessage.getRtkMode());
                    gnssState.setRtkType(settingMessage.getRtkType());
                    gnssState.setRtkHasRadio(settingMessage.getRtkHasRadio());
                    gnssState.setRtkVersion(settingMessage.getRtkVersion());
                    gnssState.setRtkCore(settingMessage.getRtkCore());
                }
                if (mRtkCommCallback != null) {
                    mRtkCommCallback.onGnssStateUpdate(gnssState);
                    GnssManager.getInstance().setAutoLink(gnssState);
                }
            }
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

    @Override
    public void setRTKCommunicationCallBack(RTKCommunicationCallBack callBack) {
        this.mRtkCommCallback = callBack;
    }

    /**
     * nmea解析
     *
     * @param data data
     */
    @Override
    public synchronized void parseNMEA(byte[] data) {
        String dataBuff = new String(data).trim();
        char start1 = '$';
        char start2 = '#';
        char end1 = '*';
        char end2 = '\n';
        for (int i = 0; i < dataBuff.length(); i++) {
            char cur = dataBuff.charAt(i);
            if (cur == start1 || cur == start2) {
                if (nmea.length() > 0) {
                    parseData(nmea.toString());
                    nmea.setLength(0);
                }
                nmea.append(cur);
                step = 1;
            } else if (step == 1) {
                nmea.append(cur);
                if (cur == end1) {
                    step = 2;
                }
            } else if (step == 2) {
                nmea.append(cur);
                if (cur == end2) {
                    parseData(nmea.toString());
                    nmea.setLength(0);
                    step = 0;
                }
            }
        }
    }

    @NonNull
    @Override
    public GnssState getGnssState() {
        return gnssState;
    }
}
