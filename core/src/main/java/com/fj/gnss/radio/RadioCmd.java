//package com.fj.gnss.radio;
//
//import com.fj.construction.tools.helper.ThreadExecutor;
//import com.fjdynamics.protocollibrary.transport.TransPort;
//import com.fjdynamics.xlog.FJXLog;
//
///**
// * Create By peter.yang
// * On 2020/12/3
// */
//public class RadioCmd {
//    private String TAG = "RadioCmd";
//    private Thread configWorkFreq, configPublicCmd;
//    private static RadioCmd radioCmd;
//
//    public boolean isIswrite() {
//        return iswrite;
//    }
//
//    public void setIswrite(boolean iswrite) {
//        this.iswrite = iswrite;
//    }
//
//    private boolean iswrite = false;
//
//    public static RadioCmd getInstance() {
//        if (radioCmd == null) {
//            radioCmd = new RadioCmd();
//        }
//        return radioCmd;
//    }
//
//    public void configHXWorkFREQ(TransPort transPort, final String workFreq) {
//        configWorkFreq = new Thread() {
//            @Override
//            public void run() {
//                try {
//                    while (iswrite) {
//                        Thread.sleep(10);
//                    }
//                    iswrite = true;
//                    FJXLog.INSTANCE.d(TAG, "configHXWorkFREQ" + workFreq);
//                    Thread.sleep(1100);
//                    sendData(transPort, "+++");
//                    Thread.sleep(1100);
//                    sendData(transPort, ("ATP0=00 " + workFreq + " " + workFreq + "\r\n"));
//                    Thread.sleep(50);
//                    sendData(transPort, "AT&W\r\n");
//                    Thread.sleep(50);
//                    sendData(transPort, "ATA\r\n");
//                    Thread.sleep(50);
//                    iswrite = false;
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        };
//        ThreadExecutor.getInstance().executor(configWorkFreq);
//    }
//
//    public void configSDRWorkFREQ(TransPort transPort, final String workFreq) {
//        configWorkFreq = new Thread() {
//            @Override
//            public void run() {
//                try {
//                    while (iswrite) {
//                        Thread.sleep(10);
//                    }
//                    iswrite = true;
//                    FJXLog.INSTANCE.d(TAG, "configSDRWorkFREQ" + workFreq);
//                    String tempfreq = workFreq.replace(".", "");
//                    Thread.sleep(1100);
//                    sendData(transPort, "+++");
//                    Thread.sleep(1100);
//                    sendData(transPort, "ATP0=\r");
//                    Thread.sleep(50);
//                    sendData(transPort, "deal%er");
//                    Thread.sleep(50);
//                    sendData(transPort, "0\r");
//                    Thread.sleep(50);
//                    sendData(transPort, (tempfreq + "000\r"));
//                    Thread.sleep(50);
//                    sendData(transPort, "2\r");
//                    Thread.sleep(50);
//                    sendData(transPort, "2\r");
//                    Thread.sleep(50);
//                    sendData(transPort, new byte[]{27});
//                    Thread.sleep(50);
//                    sendData(transPort, "AT&W\r\n");
//                    Thread.sleep(50);
//                    sendData(transPort, "ATA\r\n");
//                    Thread.sleep(50);
//                    iswrite = false;
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        };
//        ThreadExecutor.getInstance().executor(configWorkFreq);
//    }
//
//    private void sendData(TransPort transPort, byte[] data) {
//        synchronized (this) {
//            transPort.SendData(data);
//        }
//    }
//
//    private void sendData(TransPort transPort, String string) {
//        synchronized (this) {
//            FJXLog.INSTANCE.d(TAG, "write config:" + string);
//            transPort.SendData(string.getBytes());
//        }
//    }
//
//    public void configSDRPublicCmd(TransPort transPort, String deviceSn) {
//        FJXLog.INSTANCE.d(TAG, "configSDRPublicCmd" + deviceSn);
//        configPublicCmd = new Thread() {
//            @Override
//            public void run() {
//                try {
//                    while (iswrite) {
//                        Thread.sleep(10);
//                    }
//                    iswrite = true;
//                    FJXLog.INSTANCE.d(TAG, "configSDRPublicCmd");
//                    Thread.sleep(1100);
//                    sendData(transPort, "+++");
//                    Thread.sleep(1100);
//                    sendData(transPort, "ATS128=0\r\n");
//                    Thread.sleep(50);
//                    sendData(transPort, "AT&F54\r\n");
//                    Thread.sleep(50);
//                    sendData(transPort, "ATS131=0\r\n");
//                    Thread.sleep(50);
//                    sendData(transPort, "ATS132=0\r\n");
//                    Thread.sleep(50);
//                    sendData(transPort, "ATS102=1\r\n");
//                    Thread.sleep(50);
//                    sendData(transPort, "ATS103=7\r\n");
//                    Thread.sleep(50);
//                    sendData(transPort, "ATS158=1\r\n");
//                    Thread.sleep(50);
//                    sendData(transPort, "ATS138=0\r\n");
//                    Thread.sleep(50);
//                    sendData(transPort, "ATS101=2\r\n");
//                    Thread.sleep(50);
//                    sendData(transPort, "ATP0=\r");
//                    Thread.sleep(50);
//                    sendData(transPort, "deal%er");
//                    Thread.sleep(50);
//                    sendData(transPort, "0\r");
//                    Thread.sleep(50);
//                    sendData(transPort, "415375000\r");
//                    Thread.sleep(50);
//                    sendData(transPort, "2\r");
//                    Thread.sleep(50);
//                    sendData(transPort, "2\r");
//                    Thread.sleep(50);
//                    sendData(transPort, new byte[]{27});
//                    Thread.sleep(50);
//                    sendData(transPort, "AT&W\r\n");
//                    Thread.sleep(50);
//                    sendData(transPort, "ATA\r\n");
//                    Thread.sleep(50);
//                    iswrite = false;
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        };
//        ThreadExecutor.getInstance().executor(configPublicCmd);
//    }
//
//    public void findRadio(TransPort transPort) {
//        FJXLog.INSTANCE.d(TAG, "findRadio");
//        try {
//            if (!iswrite) {
//                iswrite = true;
//                sendData(transPort, "ATA\r\n");
//                Thread.sleep(1500);
//                sendData(transPort, "+++");
//                Thread.sleep(1500);
//                sendData(transPort, "AT&V\r\n");
//                Thread.sleep(150);
//                sendData(transPort, "ATA\r\n");
//                Thread.sleep(1000);
//                iswrite = false;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void configHXPublicCmd(TransPort transPort, String deviceSn) {
//        configPublicCmd = new Thread() {
//            @Override
//            public void run() {
//                try {
//                    while (iswrite) {
//                        Thread.sleep(10);
//                    }
//                    iswrite = true;
//                    FJXLog.INSTANCE.d(TAG, "configHXPublicCmd");
//                    Thread.sleep(1100);
//                    sendData(transPort, "+++");
//                    Thread.sleep(1100);
//                    sendData(transPort, "AT&F54\r\n");
//                    Thread.sleep(50);
//                    sendData(transPort, "ATS103=4\r\n");
//                    Thread.sleep(50);
//                    sendData(transPort, "ATP0=00 415.375 415.375\r\n");
//                    Thread.sleep(50);
//                    sendData(transPort, "AT&W\r\n");
//                    Thread.sleep(50);
//                    sendData(transPort, "ATA\r\n");
//                    Thread.sleep(50);
//                    iswrite = false;
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        };
//        ThreadExecutor.getInstance().executor(configPublicCmd);
//    }
//}
