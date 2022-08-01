package com.fj.gnss.basestation;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.fj.gnss.GnssManager;
import com.fj.gnss.bean.GnssState;
import com.fj.gnss.callback.OnFrequencyPairingListener;
import com.fj.gnss.callback.RTKCommunicationCallBack;
import com.fjdynamics.xlog.FJXLog;

/**
 * @ClassName BaseStationManager
 * @Description 基站对频管理类
 * @Author Howard Zhang
 * @Date 2021/12/28
 */
public class BaseStationManager implements IBaseStationManager {
    private static final String TAG = BaseStationManager.class.getSimpleName();

    private final Handler mHandler = new Handler(Looper.getMainLooper());
    /**
     * 对频码
     */
    private String mPairingCode;
    /**
     * 外置电台对频码 mHexCode
     */
    private String mHexCode;
    /**
     * 基站对频状态
     */
    private BaseStationState mBaseStationState;

    private RTKCommunicationCallBack mRtkCommCallBack;

    private OnFrequencyPairingListener mPairingListener;

    /**
     * 允许对频
     */
    private boolean isPairing;

    /**
     * 获取对频码
     *
     * @return
     */
    @Override
    public String getPairingCode() {
        return mPairingCode;
    }

    /**
     * 设置对频码
     *
     * @param mPairingCode
     */
    @Override
    public void setPairingCode(String mPairingCode) {
        this.mPairingCode = mPairingCode;
    }

    @Override
    public void setPairingOtherCode(String hexCode) {
        this.mHexCode = hexCode;
    }

    @Override
    public void setRTKCommunicationCallBack(RTKCommunicationCallBack callBack) {
        this.mRtkCommCallBack = callBack;
    }

    @Override
    public void setReceivePairingCmd() {
        if (mPairingListener != null) {
            mPairingListener.onReceivePairingCmd();
        }
    }


    @Override
    public void setOtherPairingCode(String hexCode) {
        this.mHexCode = hexCode;
    }

    @Override
    public void pairBaseOtherStation(String hexCode, int timeOutNumber, boolean hasInternalRadio, boolean isFirst, OnFrequencyPairingListener listener) {
        if (TextUtils.isEmpty(hexCode)) {
            return;
        }
        if (this.mPairingListener == null) {
            this.mPairingListener = listener;
        }

        if (isFirst) {
            isPairing = true;
            setBaseStationState(BaseStationState.PAIRING);
            if (hasInternalRadio) {
                boolean isOtherRtk = hexCode.length() == 12;
                if (mRtkCommCallBack != null) {
                    //开始其他基站回调
                    mRtkCommCallBack.setOtherBaseStation();
                }
                try {
                    Thread.sleep(200);
                } catch (Exception e) {

                }
                mRtkCommCallBack.pairOtherBaseStation(hexCode);
            } else {
                // 外置电台对频 暂时不用
                //GnssManager.getInstance().getExternalRadioManager().configPublicCmd(code);
            }
        }
        //两秒后检查状态
        mHandler.postDelayed(() -> {
            GnssState gnssState = GnssManager.getInstance().getNmeaDataParse().getGnssState();
            if (gnssState.getRtkMode() == 0
                    && (gnssState.getGpsState() == 2 || gnssState.getGpsState() == 4 || gnssState.getGpsState() == 5)) {
                // 对频+获取定位成功
                setBaseStationState(BaseStationState.PAIR_SUCCESS);
                setPairingOtherCode(hexCode);
                if (listener != null) {
                    listener.onPairingResult(true);
                }
                if (mRtkCommCallBack != null) {
                    mRtkCommCallBack.saveOtherPairingCode(hexCode);
                }
                isPairing = false;
                BaseStationManager.this.mPairingListener = null;
            } else if (timeOutNumber > 1 && this.isPairing) {
                setBaseStationState(BaseStationState.PAIRING);
                pairBaseOtherStation(hexCode, timeOutNumber - 1, hasInternalRadio, false, listener);
            } else {

                // 对频超时失败
                setBaseStationState(BaseStationState.PAIR_FAILED);

                if (listener != null) {
                    listener.onPairingResult(false);
                }
                isPairing = false;
                BaseStationManager.this.mPairingListener = null;
            }
        }, 5000);
    }

    /**
     * 获取当前对频状态
     *
     * @return
     */
    public BaseStationState getBaseStationState() {
        return mBaseStationState;
    }

    /**
     * 设置当前对频状态
     *
     * @param mBaseStationState
     */
    public void setBaseStationState(BaseStationState mBaseStationState) {
        this.mBaseStationState = mBaseStationState;
    }

    /**
     * 对频
     *
     * @param code
     * @param timeOutNumber
     * @param hasInternalRadio
     * @param isFirst
     * @param listener
     */
    @Override
    public synchronized void pairBaseStation(final String code, final int timeOutNumber, boolean hasInternalRadio, final boolean isFirst,
                                             final OnFrequencyPairingListener listener) {
        if (TextUtils.isEmpty(code)) {
            return;
        }
        if (this.mPairingListener == null) {
            this.mPairingListener = listener;
        }

        if (isFirst) {
            isPairing = true;
            setBaseStationState(BaseStationState.PAIRING);
            if (hasInternalRadio) {
                // 内置电台对频
                boolean isNewRTK = code != null && code.startsWith("BS") && code.length() == 8;
                if (mRtkCommCallBack != null) {
                    mRtkCommCallBack.setBaseStationType(isNewRTK);
                }
                try {
                    Thread.sleep(200);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String codeNew = code;
                if (isNewRTK) {
                    codeNew = code.substring(3);
                }
                if (mRtkCommCallBack != null) {
                    mRtkCommCallBack.pairBaseStation(isNewRTK, codeNew);
                }
            } else {
                // 外置电台对频
                GnssManager.getInstance().getExternalRadioManager().configPublicCmd(code);
            }
        }
        //两秒后检查状态
        mHandler.postDelayed(() -> {
            GnssState gnssState = GnssManager.getInstance().getNmeaDataParse().getGnssState();
            if (gnssState.getRtkMode() == 0
                    && (gnssState.getGpsState() == 2 || gnssState.getGpsState() == 4 || gnssState.getGpsState() == 5)) {
                // 对频+获取定位成功
                setBaseStationState(BaseStationState.PAIR_SUCCESS);
                setPairingCode(code);
                if (listener != null) {
                    listener.onPairingResult(true);
                }
                if (mRtkCommCallBack != null) {
                    mRtkCommCallBack.savePairingCode(code);
                }
                isPairing = false;
                BaseStationManager.this.mPairingListener = null;
            } else if (timeOutNumber > 1 && this.isPairing) {
                setBaseStationState(BaseStationState.PAIRING);
                pairBaseStation(code, timeOutNumber - 1, hasInternalRadio, false, listener);
            } else {

                // 对频超时失败
                setBaseStationState(BaseStationState.PAIR_FAILED);

                if (listener != null) {
                    listener.onPairingResult(false);
                }
                isPairing = false;
                BaseStationManager.this.mPairingListener = null;
            }
        }, 5000);
    }

    /**
     * 自动对频
     *
     * @param gpsState
     */
    @Override
    public synchronized void autoPairing(int gpsState) {
        if (gpsState <= 1 && !GnssManager.getInstance().getNetRtcmManager().isLock() &&
                mBaseStationState != BaseStationState.PAIRING && mBaseStationState != BaseStationState.PAIR_SUCCESS) {
            FJXLog.INSTANCE.d(TAG, "autoPairing: 基站自动对频");

            // Howard.Zhang on 2021/6/29 重试次数从1次增加到10次，避免偶现的自动对频失败
            pairBaseStation(mPairingCode, 10, GnssManager.getInstance().isHasInternalRadio(), true, new OnFrequencyPairingListener() {
                @Override
                public void onPairingResult(boolean isSuccess) {
                    FJXLog.INSTANCE.d(TAG, "autoPairing: 自动对频 = " + isSuccess);
                }

                @Override
                public void onReceivePairingCmd() {
                    FJXLog.INSTANCE.d(TAG, "autoPairing: 对频码设置成功");
                }
            });
        }
    }

    public boolean isPairing() {
        return isPairing;
    }


    @Override
    public void stopPair() {
        this.isPairing = false;
    }


    /**
     * 对频状态
     */
    public enum BaseStationState {
        DISABLED,       // 未对频
        PAIRING,        // 对频中
        PAIR_SUCCESS,   // 对频成功
        PAIR_FAILED     // 对频失败
    }

}
