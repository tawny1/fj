package com.fj.gnss.nmea;


import android.text.TextUtils;

import com.fj.gnss.GnssManager;
import com.fj.gnss.nmea.msg.GGAMessage;
import com.fj.gnss.ntrip.NtripManager;
import com.fj.gnss.ntrip.bean.NtripInfo;

/**
 * @ClassName RTMCManager
 * @Description TODO
 * @Author Jarven.Ding
 * @Date 2021/3/9 20:37
 */
public class NetRTCMManager implements INetRtcmManager {

    private boolean lock = false;
    private long netRTCMTime = 0;
    private String ggaNMEA = "";

    @Override
    public void netRTCM(int rtkMode) {
        if (System.currentTimeMillis() - netRTCMTime < 500 || !GGAMessage.toBeGGA(ggaNMEA)) {
            return;
        }
        netRTCMTime = System.currentTimeMillis();
        if (rtkMode == 1) {
            GnssManager.getInstance().getQxManager().setGGA(ggaNMEA);
            if (!lock) {
                GnssManager.getInstance().getQxManager().startRtcmServer();
            }
            GnssManager.getInstance().getNtripManager().stop();
        } else if (rtkMode == 2) {
            GnssManager.getInstance().getQxManager().stopRtcmServer();
            NtripInfo ntripInfo = GnssManager.getInstance().getNtripManager().getNtripInfo();
            if (ntripInfo == null) {
                return;
            }

            boolean iAutoLink = ntripInfo.isAutoLink();
            boolean isEditing = GnssManager.getInstance().getNtripManager().isNtripEditing();
            NtripManager.NtripState connectState = GnssManager.getInstance().getNtripManager().getNtripConnectState();
            if (connectState == NtripManager.NtripState.LINK_TO_NODE_SUCCESS) {
                GnssManager.getInstance().getNtripManager().sendGGA(ggaNMEA);
            } else if (iAutoLink && !isEditing && !lock) {
                NtripManager.NtripState ntripState = GnssManager.getInstance().getNtripManager().getNtripConnectState();
                if (ntripState != NtripManager.NtripState.LINKING_TO_NODE
                        && ntripState != NtripManager.NtripState.LINK_TO_NODE_SUCCESS
                        && !TextUtils.isEmpty(ntripInfo.getIpAddress()) && ntripInfo.getPort() != 0
                        && !TextUtils.isEmpty(ntripInfo.getSourcePoint())
                        && !TextUtils.isEmpty(ntripInfo.getUsername())
                        && !TextUtils.isEmpty(ntripInfo.getPassword())
                ) {
                    GnssManager.getInstance().getNtripManager().linkSource();
                }
            }
        }
    }

    @Override
    public void stopRTCM() {
        GnssManager.getInstance().getNtripManager().stop();
        GnssManager.getInstance().getQxManager().stopRtcmServer();
    }

    @Override
    public boolean isLock() {
        return lock;
    }

    @Override
    public void setLock(boolean lock) {
        this.lock = lock;
    }

    @Override
    public void setGgaNMEA(String ggaNMEA) {
        this.ggaNMEA = ggaNMEA;
    }
}
