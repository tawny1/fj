package com.fj.gnss.qx;

import android.text.TextUtils;
import android.util.Log;

import com.fj.gnss.GnssManager;
import com.fj.gnss.bean.RtkMode;
import com.fj.gnss.callback.IQXRequestInterface;
import com.fj.gnss.callback.RTKCommunicationCallBack;
import com.fjdynamics.xlog.FJXLog;
import com.qxwz.sdk.configs.AccountInfo;
import com.qxwz.sdk.configs.OssConfig;
import com.qxwz.sdk.configs.SDKConfig;
import com.qxwz.sdk.core.CapInfo;
import com.qxwz.sdk.core.IRtcmSDKCallback;
import com.qxwz.sdk.core.RtcmSDKManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.qxwz.sdk.core.Constants.QXWZ_SDK_CAP_ID_NOSR;
import static com.qxwz.sdk.core.Constants.QXWZ_SDK_STAT_AUTH_SUCC;
import static com.qxwz.sdk.core.Constants.QXWZ_SDK_STAT_CAP_START_SUCC;
import static com.qxwz.sdk.types.KeyType.QXWZ_SDK_KEY_TYPE_AK;
import static com.qxwz.sdk.types.KeyType.QXWZ_SDK_KEY_TYPE_DSK;

/**
 * Create By peter.yang
 * On 2019/12/12
 *
 */
public class FJQxManager implements IRtcmSDKCallback, IFJQxManager {
    private static final String TAG = FJQxManager.class.getSimpleName();

    private String GGA;
    private Long lastRtcmChangedTime = 0L;
    private boolean mStart = false; //千寻服务是否开启
    private boolean isStopping = false;
    private Thread broadThread = null;
    private RTKCommunicationCallBack mRtkCommCallback;
    private String deviceSn;

    private IQXRequestInterface iqxRequestInterface;

    /**
     * DGK和DGS分别为设备组账号和设备组密钥
     * 目前采用固定一个设备组的方式，所有的DSK都在一个DGK下面
     */
//    public static final String DGK = "D104880ea55lop";
//    public static final String DGS = "89c0f8abdf268e419685c18e7c4fdbb2eefb36c4312d18f0ae86c1d5331944d8";
    public static final String DGK = "G48c2c8hsgsn";
    public static final String DGS = "722f429e54fd5f63";
    public static String DSK = "";
    public static String DSS = "";

    /**
     * 当前鉴权的是否是年包，年包则再次重试日包，已是日包则不重试
     * 避免单次年包认证失败后一直重试日包而不重试年包
     */
    private boolean isCurrentYearlyAuth = false;

    @Override
    public long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }

    private long expireTime = 0;

    public void setIqxRequestInterface(IQXRequestInterface iqxRequestInterface) {
        this.iqxRequestInterface = iqxRequestInterface;
    }

    public String getDGK() {
        return DGK;
    }

    public String getDGS() {
        return DGS;
    }

    public void setDSK(String DSK) {
        FJQxManager.DSK = DSK;
    }

    public void setDSS(String DSS) {
        FJQxManager.DSS = DSS;
    }

    @Override
    public void setRTKCommunicationCallBack(RTKCommunicationCallBack rtkCommunicationCallBack) {
        this.mRtkCommCallback = rtkCommunicationCallBack;
    }

    @Override
    public synchronized void startRtcmServer() {
        try {
            if (deviceSn == null) {
                Log.d(TAG, "No_SN");
                return;
            }
            if (!mStart && !"".equalsIgnoreCase(deviceSn)) {
                FJXLog.INSTANCE.d(TAG, "startRtcmServer mStart ; " + mStart + " , deviceSn : " + deviceSn);
                mStart = true;
//                if (NetConstant.QXNETYEAR.equals(this.netType)) {
                connectYear(deviceSn);
//                } else {
//                connectDaily(deviceSn);
//                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "startRtcmServer exception: " + e.getMessage());
        }
    }

    /**
     * 年包
     *
     * @param deviceSn
     */
    private void connectYear(String deviceSn) {
        Log.d(TAG, "开始年包连接");
        //"请输入deviceType";
        String DEVICE_TYPE = "fjdaidrive";
        //"请输入appSecret"；
        String APP_SECRET = "8dad94a7ac1a64a8df4dc4b5c2a4cabcd5a82283a1fece2efa3fa525e99fba8f";
        //"请输入appKey";
        String APP_KEY = "681047";

        OssConfig ossConfig = OssConfig.builder()
                .setHeartbeatInterval(30)//设置⼼跳间隔，单位秒
                .setRetryInterval(20)//设置重连间隔，单位秒
                .build();

        SDKConfig sdkConfig = SDKConfig.builder()
                .setAccountInfo(
                        AccountInfo.builder()
                                .setKeyType(QXWZ_SDK_KEY_TYPE_AK)
                                .setKey(APP_KEY)
                                .setSecret(APP_SECRET)
                                .setDeviceId(deviceSn)
                                .setDeviceType(DEVICE_TYPE)
                                .build())
                .setRtcmSDKCallback(this)
                .setOssConfig(ossConfig)
                .build();
        // step1: 初始化
        RtcmSDKManager.getInstance().init(sdkConfig);
        // step2: 鉴权
        RtcmSDKManager.getInstance().auth();
        isCurrentYearlyAuth = true;
        Log.d(TAG, "开始年包鉴权");
    }

    /**
     * 日包
     *
     * @param deviceSn
     */
    private void connectDaily(String deviceSn) {
        Log.d(TAG, "开始日包连接");
        //"请输入deviceType";
        String DEVICE_TYPE = "fjdaidrive";
        OssConfig ossConfig = OssConfig.builder()
                .setHeartbeatInterval(30)//设置⼼跳间隔，单位秒
                .setRetryInterval(20)//设置重连间隔，单位秒
                .build();
        if (TextUtils.isEmpty(DSK) || TextUtils.isEmpty(DSS)) {
            // DSK DSS的获取交予app module
            if (this.iqxRequestInterface != null) {
                this.iqxRequestInterface.requestDskDss();
            }
            Log.d(TAG, "千寻初始化失败");
            mStart = false;
            isCurrentYearlyAuth = false;
            return;
        }

        SDKConfig sdkConfig = SDKConfig.builder()
                .setAccountInfo(
                        AccountInfo.builder()
                                .setKeyType(QXWZ_SDK_KEY_TYPE_DSK)
                                .setKey(DSK)
                                .setSecret(DSS)
                                .setDeviceId(deviceSn)
//                                .setDeviceId("FJLQ10121302681ZC")
                                .setDeviceType(DEVICE_TYPE)
                                .build())
                .setRtcmSDKCallback(this)
                .setOssConfig(ossConfig)
                .build();
        // step1: 初始化
        RtcmSDKManager.getInstance().init(sdkConfig);
        // step2: 鉴权
        RtcmSDKManager.getInstance().auth();
        isCurrentYearlyAuth = false;
        mStart = true;
        FJXLog.INSTANCE.hd(TAG, "开始日包鉴权");
    }

    @Override
    public void onAuth(int i, List<CapInfo> caps) {
        Log.d(TAG, "onAuth is: " + i);
        if (i == QXWZ_SDK_STAT_AUTH_SUCC) {
            new Thread() {
                @Override
                public void run() {
                    // step3: 鉴权成功，启动能力
                    RtcmSDKManager.getInstance().start(QXWZ_SDK_CAP_ID_NOSR);
                }
            }.start();
            for (CapInfo capInfo : caps) {
                if (capInfo.getState() == 0) {
                    expireTime = capInfo.getExpireTime();
                } else {
                    if (isCurrentYearlyAuth) {
                        stopRtcmServerAndRestartDay();
                    } else {
                        stopRtcmServer();
                    }
                }
                Log.e(TAG, "onAuth, capInfo:" + capInfo.toString() + " expireTime:" + getStringDate(capInfo.getExpireTime() * 1000));
            }
        } else {
            // step4: 鉴权失败，重试
            // RtcmSDKManager.getInstance().auth();
            for (CapInfo capInfo : caps) {
                Log.e(TAG, "onAuth, capInfo:" + capInfo.toString());
            }

            if (isCurrentYearlyAuth) {
                stopRtcmServerAndRestartDay();
            } else {
                stopRtcmServer();
            }

        }
    }

    /**
     * 获取现在时间
     *
     * @return返回字符串格式 yyyy-MM-dd HH:mm:ss
     */
    private static String getStringDate(long time) {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        String dateString = formatter.format(time);
        return dateString;
    }

    @Override
    public void onStart(int code, int capId) {
        Log.d(TAG, "onStart code: " + code + "--->capId: " + capId);
        if (code == QXWZ_SDK_STAT_CAP_START_SUCC) {
            // step5: 成功播发GGA
            broadcastGGA();
        } else {
            // step6: 能力启动失败则重试，关闭鉴权，然后重试
            // RtcmSDKManager.getInstance().start(QXWZ_SDK_CAP_ID_NOSR);
            stopRtcmServer();
        }
    }

    private void broadcastGGA() {
        try {
            if (broadThread != null) {
                broadThread.interrupt();
            }
            broadThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (mStart) {
                        if (GGA != null && GGA.length() != 0) {
//                            if (LogCallBack.RTCM_DEBUG) {
//                                Log.d(TAG, "broadcastGGA：" + GGA);
//                            }
                            RtcmSDKManager.getInstance().sendGga(GGA);
                            GGA = null;
                        } else {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
            broadThread.start();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "broadcastGGA exception: " + e.getMessage());
        }
    }

    @Override
    public void stopRtcmServer() {
        if (mStart && !isStopping) {
            isStopping = true;
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "stopRtcmServer: 停止千寻");
                    try {
                        RtcmSDKManager.getInstance().stop(QXWZ_SDK_CAP_ID_NOSR);
                        RtcmSDKManager.getInstance().cleanup();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, "stopRtcmServer exception: " + e.getMessage());
                    }
                    mStart = false;
                    isStopping = false;
                }
            };
            new Thread(runnable).start();
        }
    }

    public void stopRtcmServerAndRestartDay() {
        if (mStart && !isStopping) {
            isStopping = true;
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "stopRtcmServerAndRestartDay: 停止千寻");
                    try {
                        RtcmSDKManager.getInstance().stop(QXWZ_SDK_CAP_ID_NOSR);
                        RtcmSDKManager.getInstance().cleanup();

                        String deviceSn = FJQxManager.this.deviceSn;
                        if (!"".equalsIgnoreCase(deviceSn)) {
                            Log.d(TAG, "startRtcmServer mStart ; " + mStart + " , deviceSn : " + deviceSn);
                            connectDaily(deviceSn);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, "stopRtcmServer exception: " + e.getMessage());
                    }
//                    mStart = false;
                    isStopping = false;
                }
            };
            new Thread(runnable).start();
        }
    }

    @Override
    public void onData(int i, byte[] bytes) {
        // 在此处将rtcm写入串口
        try {
            if (!mStart) {
                return;
            }
            if (lastRtcmChangedTime == 0) {
                lastRtcmChangedTime = System.currentTimeMillis();
            }
            if (System.currentTimeMillis() - lastRtcmChangedTime < 50) {
                Thread.sleep(100);
            }
            GnssManager.getInstance().setRtcmSendTime(RtkMode.RTK_MODE_QX_NET, System.currentTimeMillis());
            if (mRtkCommCallback != null) {
                mRtkCommCallback.onGetDiffRtcm(RtkMode.RTK_MODE_QX_NET, bytes);
                Log.i(TAG, "DiffGet");
            }
            lastRtcmChangedTime = System.currentTimeMillis();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "onData exception: " + e.getMessage());
        }
    }

    @Override
    public void onStatus(int i) {
        Log.d(TAG, "onStatus is: " + i);
    }

    @Override
    public void setDeviceSn(String deviceSn) {
        this.deviceSn = deviceSn;
    }

    @Override
    public void setGGA(String gga) {
        this.GGA = gga;
    }
}
