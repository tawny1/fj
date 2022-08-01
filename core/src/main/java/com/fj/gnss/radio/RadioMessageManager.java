package com.fj.gnss.radio;

import com.fj.gnss.GnssManager;
import com.fj.gnss.callback.RTKCommunicationCallBack;
import com.fjdynamics.xlog.FJXLog;

/**
 * Create By peter.yang
 * On 2020/12/16
 */
public class RadioMessageManager {
    private static final String TAG = RadioMessageManager.class.getSimpleName();

    private static RadioMessageManager instance;

    private boolean getAck = false;

    public static RadioMessageManager getInstance() {
        if (instance == null) {
            synchronized (RadioMessageManager.class) {
                if (instance == null) {
                    instance = new RadioMessageManager();
                }
            }
        }
        return instance;
    }

    public boolean isGetAck() {
        return getAck;
    }

    /**
     * rtk收到外置电台连接状态回包
     *
     * @param getAck
     */
    public void setGetAck(boolean getAck) {
        this.getAck = getAck;
    }

    /**
     * 给rtk发送外置电台连接状态
     *
     * @param in
     */
    public void sendRadioState(final boolean in) {
        new Thread(() -> {
            getAck = false;
            while (!getAck) {
                try {
                    FJXLog.INSTANCE.d(TAG, "send radio is in:" + in);
                    RTKCommunicationCallBack rtkCommCallBack = GnssManager.getInstance().getRTKCommunicationCallBack();
                    if (rtkCommCallBack != null) {
                        rtkCommCallBack.sendExternalRadioState(
                                new byte[]{in ? (byte) 0x01 : (byte) 0x00}
                        );
                    }
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            FJXLog.INSTANCE.d(TAG, "send radio state success");
        }).start();
    }

}
