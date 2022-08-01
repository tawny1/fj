package com.fj.gnss.radio;

import com.fj.gnss.callback.RTKCommunicationCallBack;

/**
 * Create By peter.yang
 * On 2020/10/12
 * 外置电台管理类
 */
public class JZRTKManager extends Thread implements IExternalRadioManager {
    private static final String TAG = JZRTKManager.class.getSimpleName();

//    TransPort transPort;
//    private String jz_chanal;
//    private String jz_workFreq;
//    private String jz_sn;
//    private String user_sn;
//    private boolean isConfig = false;
//    private boolean isConfiged = false;
//    private int radioType = 0;//0:华信电台；1：华盛电台
//    private String HX5002_FREQ = "420.375,421.875,422.375,423.875,424.375,425.875,426.375,427.875,428.375,429.875,430.375,431.875,432.375,433.875,434.375,435.875,436.375,437" +
//            ".875,438.375,439.875,440.375,441.875,442.375,443.875,444.375,445.875,446.375,447.875,448.375,449.875,450.375,451.875";
//    private Thread configPublicCmd, configWorkFreq, findRadio;
//    private boolean isFindRadio = false;
//    private boolean findRadioState = false;
//    private boolean state = true;
//    private boolean haseData = false;
//    private int readRtcmSleepTime = 950;
//    private String lastCode = "";
//
//    private RTKCommunicationCallBack mRtkCommCallBack;
//
//    private boolean isPairSuccess = false;
//
//    private ByteBuffer byteBuffer;
//    private ByteBuffer rtcmBuffer;
//    private boolean haseR = false;
//    private boolean haseN = false;
//    private boolean rtcmhaseR = false;
//    private boolean rtcmhaseN = false;

    public JZRTKManager() {
//        this.lastCode = GnssManager.getInstance().getBaseStationManager().getPairingCode();
//        initConnect();
//        this.start();
    }

//    public void setLastCode(String lastCode) {
//        this.lastCode = lastCode;
//    }
//
//    public boolean isConnected() {
//        return false;
//    }
//
//    public void startConnect() {
//        initConnect();
//        this.start();
//    }
//
//    private void initConnect() {
//        if (transPort != null) {
//            transPort.Stop();
//        }
//        isConfiged = false;
//        isConfig = false;
//        transPort = null;
//        byteBuffer = ByteBuffer.allocate(1024 * 10);
//        rtcmBuffer = ByteBuffer.allocate(1024 * 10);
//        transPort = CmdManager.getInstance().startSeirListen("/dev/ttyS3", 115200, new RecDataCallBack() {
//            @Override
//            public void onRecData(String s, byte[] bytes) {
//                try {
//                    FJXLog.INSTANCE.d(TAG, "len:" + bytes.length + DataUtil.byte2hex(bytes));
//                    if (bytes.length > 2) {
//                        haseData = true;
//                    }
//                    String data = new String(bytes);
//                    FJXLog.INSTANCE.d(TAG, data);
//                    if (data.contains("HX-")) {
//                        isFindRadio = true;
//                        radioType = 0;
//                        isConfig = false;
//                    }
//                    if (data.contains("SDR4")) {
//                        isFindRadio = true;
//                        radioType = 1;
//                        isConfig = false;
//                    }
//                    if (bytes.length > 2 && user_sn != null && user_sn.equalsIgnoreCase(lastCode)) {
//                        for (int i = 0; i < bytes.length - 1; i++) {
//                            if (bytes[i] == (byte) 0xd3 && bytes[i + 1] == 0) {
//                                transPort.setSleepTime(readRtcmSleepTime);
//                                isFindRadio = true;
//                                isConfig = true;
//                                isConfiged = true;
//                                break;
//                            }
//                        }
//
//                    }
//                    FJXLog.INSTANCE.d(TAG, "isfindradio " + isFindRadio + user_sn + isConfig);
//                    if (isFindRadio && user_sn != null && !isConfig) {
//                        FJXLog.INSTANCE.d(TAG, "config radioType:" + radioType);
//                        switch (radioType) {
//                            case 0:
//                                RadioCmd.getInstance().configHXPublicCmd(transPort, user_sn);
//                                isConfig = true;
//                                break;
//                            case 1:
//                                RadioCmd.getInstance().configSDRPublicCmd(transPort, user_sn);
//                                isConfig = true;
//                                break;
//                            default:
//                                break;
//                        }
//                    }
//                    if (!isConfiged) {
//                        for (byte bt : bytes) {
//                            if (bt == 13) {
//                                haseR = true;
//                                continue;
//                            }
//                            if (bt == 10) {
//                                haseN = true;
//                            }
//                            if (haseR && haseN) {
//                                haseN = false;
//                                haseR = false;
//                                byte[] temp = new byte[byteBuffer.position()];
//                                byteBuffer.position(0);
//                                byteBuffer.get(temp);
//                                handleData(new String(temp));
//                                byteBuffer.clear();
//                                continue;
//                            }
//                            byteBuffer.put(bt);
//                        }
//                    } else {
//                        if ((data.contains("FREQ") && data.startsWith("FREQ")) || (data.contains("$FREQ") && data.startsWith("$FREQ"))) {
//                            isPairSuccess = false;
//                            handleData(data);
//                        } else {
//                            isPairSuccess = true;
//                            FJXLog.INSTANCE.d(TAG, "rtcm size:" + bytes.length);
//                            GnssManager.getInstance().setRtcmSendTime(RtkMode.RTK_MODE_BASE_STATION, System.currentTimeMillis());
//                            mRtkCommCallBack.onGetDiffRtcm(RtkMode.RTK_MODE_BASE_STATION, bytes);
//                        }
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        transPort.setSleepTime(readRtcmSleepTime);
//    }
//
//    public void close() {
//        try {
//            isConfiged = false;
//            transPort.Stop();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void handleData(String string) {
//        //$FREQ,16,BS100002,436.375
//        FJXLog.INSTANCE.d(TAG, "read string:" + string + "pair code:" + user_sn);
//        if ((string.contains("FREQ") && string.startsWith("FREQ")) || (string.contains("$FREQ") && string.startsWith("$FREQ"))) {
//            String[] strs = string.split(",");
//            if (strs.length == 4) {
//                jz_chanal = strs[1];
//                jz_sn = strs[2];
//                jz_workFreq = strs[3].replace("\r\n", "");
//                if (jz_sn.equalsIgnoreCase(user_sn) && HX5002_FREQ.contains(jz_workFreq)) {
//                    configWorkFREQ(jz_workFreq);
//                }
//            }
//        }
//    }
//
//    private void configWorkFREQ(final String workFreq) {
//        FJXLog.INSTANCE.d(TAG, "configWorkFREQ" + workFreq);
//        isConfiged = false;
//        switch (radioType) {
//            case 0:
//                RadioCmd.getInstance().configHXWorkFREQ(transPort, workFreq);
//                isConfiged = true;
//                break;
//            case 1:
//                RadioCmd.getInstance().configSDRWorkFREQ(transPort, workFreq);
//                isConfiged = true;
//                break;
//            default:
//                break;
//        }
//        if (transPort != null) {
//            transPort.setSleepTime(readRtcmSleepTime);
//        }
//    }
//
//    @Override
//    public void run() {
//        super.run();
//        while (state) {
//            try {
//                if (!haseData) {
//                    if (GnssManager.getInstance().isHasExternalRadio()) {
//                        sendOutRadioEvent(false);
//                    }
//                } else {
//                    if (!GnssManager.getInstance().isHasExternalRadio()) {
//                        sendOutRadioEvent(true);
//                    }
//                }
//                haseData = false;
//                Thread.sleep(5000);
//                if (!haseData) {
//                    RadioCmd.getInstance().findRadio(transPort);
//                    Thread.sleep(3000);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    public void quitConfig() {
//        transPort.SendData("AT&W\r\n".getBytes());
//        transPort.SendData("ATA\r\n".getBytes());
//    }
//
//    public void send(byte[] bytes) {
//        transPort.SendData(bytes);
//    }
//
//    private void findRadioType() {
//        if (!findRadioState) {
//            findRadio = new Thread() {
//                @Override
//                public void run() {
//                    while (!isFindRadio) {
//                        findRadioState = true;
//                        FJXLog.INSTANCE.d(TAG, "findRadio");
//                        RadioCmd.getInstance().findRadio(transPort);
//                    }
//                    findRadioState = false;
//                }
//            };
//            ThreadExecutor.getInstance().executor(findRadio);
//        }
//    }
//
//    public void sendOutRadioEvent(boolean isIn) {
//        FJXLog.INSTANCE.d(TAG, "sendOutRadioEvent 外置电台是否接入:" + isIn);
//        GnssManager.getInstance().setHasExternalRadio(isIn);
//
//        // 给rtk发送外置电台连接状态
//        if (GnssManager.getInstance().is105Exist()) {
//            RadioMessageManager.getInstance().sendRadioState(isIn);
//        }
//
//        if (!isIn) {
//            isPairSuccess = false;
//            isFindRadio = false;
//            // 外置电台拔出，停止对频
//            GnssManager.getInstance().getBaseStationManager().stopPair();
//        }
//    }

    @Override
    public void configPublicCmd(String deviceSn) {
//        FJXLog.INSTANCE.d(TAG, "configPublicCmd" + deviceSn);
//        isConfig = false;
//        isConfiged = false;
//        user_sn = deviceSn;
//        isFindRadio = false;
//        if (transPort != null) {
//            transPort.setSleepTime(100);
//        }
//        findRadioType();
    }

    @Override
    public void setRTKCommunicationCallBack(RTKCommunicationCallBack callBack) {
//        this.mRtkCommCallBack = callBack;
    }
}
