package com.fj.gnss.ntrip;

import android.text.TextUtils;
import android.util.Log;

import com.fj.gnss.GnssManager;
import com.fj.gnss.bean.RtkMode;
import com.fj.gnss.callback.RTKCommunicationCallBack;
import com.fj.gnss.ntrip.bean.NtripInfo;
import com.fj.gnss.ntrip.bean.NtripSource;
import com.fj.gnss.ntrip.protocol.CROSConnect;
import com.fj.gnss.ntrip.protocol.GetSourceThread;
import com.fj.gnss.ntrip.util.GeoUtil;
import com.fjdynamics.xlog.FJXLog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Ntrip管理类
 * author:Jarven.ding
 * create:2020/4/1
 */
public class NtripManager implements IFJNtripManager {

    private static final String TAG = NtripManager.class.getSimpleName();

    private final List<NtripSource> ntripRecordList = new ArrayList<>();
    private final List<NtripListener> ntripListeners = new ArrayList<>();

    private RTKCommunicationCallBack rTKCommunicationCallBack;
    private NtripState ntripConnectState = NtripState.LINK_NULL;
    private NtripState ntripSourceState = NtripState.NO_SOURCE;
    private CROSConnect crosConnect;

    private NtripInfo ntripInfo;
    private boolean isNtripEditing = false;


    @Override
    public void setRTKCommunicationCallBack(RTKCommunicationCallBack rTKCommunicationCallBack) {
        this.rTKCommunicationCallBack = rTKCommunicationCallBack;
    }

    @Override
    public synchronized void getSource() {
        setNtripSourceState(NtripState.GETTING_SOURCE);
        GetSourceThread getSourceThread = new GetSourceThread(ntripInfo, getLinkSourceData(), ntripRecordList);
        getSourceThread.setGetSourceListener(new GetSourceThread.GetSourceListener() {
            @Override
            public void sourceGetSuccess() {
                Log.i("Ntrip", "getSource Success");
                for (NtripListener ntripListener : new ArrayList<>(ntripListeners)) {
                    ntripListener.onGetSource(ntripRecordList);
                }
                setNtripSourceState(NtripState.GET_SOURCE_SUCCESS);
            }

            @Override
            public void sourceGetFailed(int errorCode, String errorMessage) {
                Log.e("Ntrip", "getSource Failed " + errorMessage);
                setNtripSourceState(NtripState.GET_SOURCE_FAILED);
            }
        });
        getSourceThread.start();
    }

    @Override
    public synchronized void linkSource() {
        if (TextUtils.isEmpty(ntripInfo.getSourcePoint()) || ntripConnectState == NtripState.LINKING_TO_NODE) {
            return;
        }
        if (crosConnect != null) {
            crosConnect.disConnect();
        }
        FJXLog.INSTANCE.d(TAG, "linkSource: ");
        setNtripConnectState(NtripState.LINKING_TO_NODE);
        crosConnect = new CROSConnect(ntripInfo);
        crosConnect.connect();
    }

    public synchronized void sortSourceBySite(double lat, double lon) {
        for (NtripSource ntripSource : ntripRecordList) {
            try {
                double nlat = Double.parseDouble(ntripSource.strLatitude);
                double nlon = Double.parseDouble(ntripSource.strLongitude);
                ntripSource.distance = GeoUtil.getDistance(lat, lon, nlat, nlon);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Collections.sort(ntripRecordList, (o1, o2) -> (int) (o1.distance - o2.distance));
    }

    private synchronized void onSourceStateChange() {
        for (NtripListener ntripListener : new ArrayList<>(ntripListeners)) {
            ntripListener.onStateChange(ntripSourceState);
        }
    }

    private synchronized void onConnectStateChange() {
        for (NtripListener ntripListener : new ArrayList<>(ntripListeners)) {
            ntripListener.onStateChange(ntripConnectState);
        }
    }

    private String getLinkSourceData() {
        return "GET / HTTP/1.0\r\n" +
                "User-Agent: NTRIP NtripServerCMD/"
                + 1.0 + "\r\n" +
                "Accept: */*\r\n" + "Connection: close\r\n" +
                "Authorization: Basic Og==\r\n\r\n";
    }

    @Override
    public NtripInfo getNtripInfo() {
        if (ntripInfo == null) {
            ntripInfo = new NtripInfo();
        }
        return ntripInfo;
    }

    @Override
    public void setNtripInfo(NtripInfo ntripInfo) {
        if (ntripInfo == null) {
            ntripInfo = new NtripInfo();
        }
        this.ntripInfo = ntripInfo;
    }

    @Override
    public void sendGGA(String gga) {
        if (ntripConnectState == NtripState.LINK_TO_NODE_SUCCESS && crosConnect != null) {
            crosConnect.sendData(gga.getBytes());
        }
    }

    @Override
    public synchronized void setDiffRtcm(byte[] bt) {
        GnssManager.getInstance().setRtcmSendTime(RtkMode.RTK_MODE_NTRIP, System.currentTimeMillis());
        if (null != this.rTKCommunicationCallBack) {
            this.rTKCommunicationCallBack.onGetDiffRtcm(RtkMode.RTK_MODE_NTRIP, bt);
        }
    }

    @Override
    public synchronized void stop() {
        if (crosConnect != null) {
            crosConnect.disConnect();
            crosConnect = null;
        }
        setNtripConnectState(NtripState.DISABLE);
        setNtripSourceState(NtripState.DISABLE);
    }

    @Override
    public NtripState getNtripConnectState() {
        return ntripConnectState;
    }

    @Override
    public void setNtripConnectState(NtripState ntripConnectState) {
        this.ntripConnectState = ntripConnectState;
        onConnectStateChange();
    }

    public NtripState getNtripSourceState() {
        return ntripSourceState;
    }

    public void setNtripSourceState(NtripState ntripSourceState) {
        this.ntripSourceState = ntripSourceState;
        onSourceStateChange();
    }


    @Override
    public synchronized void addNtripListener(NtripListener ntripListener) {
        if (!this.ntripListeners.contains(ntripListener)) {
            this.ntripListeners.add(ntripListener);
        }
    }

    @Override
    public synchronized void removeNtripListener(NtripListener ntripListener) {
        this.ntripListeners.remove(ntripListener);
    }

    @Override
    public boolean isNtripEditing() {
        return isNtripEditing;
    }

    @Override
    public void setNtripEditing(boolean ntripEditing) {
        isNtripEditing = ntripEditing;
    }


    public interface NtripListener {

        void onGetSource(List<NtripSource> ntripSources);

        void onStateChange(NtripState ntripState);

    }

    public enum NtripState {
        DISABLE,
        NO_SOURCE,
        GETTING_SOURCE,
        GET_SOURCE_SUCCESS,
        GET_SOURCE_FAILED,
        LINK_NULL,
        LINKING_TO_NODE,
        LINK_TO_NODE_SUCCESS,
        LINK_TO_NODE_FAILED;
    }
}
