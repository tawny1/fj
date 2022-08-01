package com.fj.gnss.board;

import android.util.Log;

import com.fj.construction.tools.helper.ThreadExecutor;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Description:
 * Date: 2021/10/26
 * Author: Howard.Zhang
 */
public class BoardManager implements IBoardManager {
    private static final String TAG = BoardManager.class.getSimpleName();

    /**
     * 板卡重启支持的标识
     */
    private final String SYSTEM_VERSION_STRING = "[ro.fj.system.version]";

    /**
     * Gnss管理对象
     */
    private static BoardManager instance;


    /**
     * 线程对象
     */
    private boolean runBool;

    /**
     * 板卡消息回调
     */
    private IBoardCallBack mIBoardCallBack;

    /**
     * 得到管理对象
     *
     * @return
     */
    public static BoardManager getInstance() {
        if (null == instance) {
            synchronized (BoardManager.class) {
                if (null == instance) {
                    instance = new BoardManager();
                }
            }
        }
        return instance;
    }

    /**
     * 设置版本号查询回调
     *
     * @param iBoardCallBack
     */
    @Override
    public void setIBoardCallBack(IBoardCallBack iBoardCallBack) {
        this.mIBoardCallBack = iBoardCallBack;
    }


    /**
     * 检测板卡是否能重启
     */
    @Override
    public synchronized void checkIsCanRestart() {
        runBool = true;
        try {
            new Thread(() -> {
                while (runBool) {
                    try {
                        Process p = Runtime.getRuntime().exec("getprop");
                        p.waitFor();
                        BufferedReader stdInput = new BufferedReader(new InputStreamReader(
                                p.getInputStream()));
                        String temp = "";
                        while ((temp = stdInput.readLine()) != null) {
                            Log.d(TAG, "收到-" + temp);
                            if (temp.contains(SYSTEM_VERSION_STRING)) {

                                runBool = false;
                                temp = temp.replaceAll(" ", "");
                                String[] tempVersions = temp.split(":");
                                if (tempVersions.length > 1) {
                                    temp = temp.split(":")[1];
                                    int index = temp.indexOf(".");
                                    if (index > 0 && temp.length() > 0) {
                                        temp = temp.substring(1, index);
                                    } else {
                                        temp = "0";
                                    }

                                    if (mIBoardCallBack != null) {
                                        mIBoardCallBack.onSystemVersionFound(str2int(temp));
                                        mIBoardCallBack = null;
                                    }
                                }
                                return;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        runBool = false;
                        if (mIBoardCallBack != null) {
                            mIBoardCallBack.onSystemVersionFound(0);
                            mIBoardCallBack = null;
                        }
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void reStartBoard() {
        setIBoardCallBack(sysVersion -> {
            if (sysVersion > 2) {
                rebootRtk();
            }
        });
        checkIsCanRestart();
    }

    /**
     * 板卡重启
     */
    @Override
    public synchronized void rebootRtk() {
        ThreadExecutor.getInstance().executor((new Runnable() {
            @Override
            public void run() {
                try {
                    ShellUtils.execCommand("gpio 77 0", true);
                    Thread.sleep(2000);
                    ShellUtils.execCommand("gpio 77 1", true);
                    Thread.sleep(5000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }));
    }

    private static int str2int(String str) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;

    }

}
