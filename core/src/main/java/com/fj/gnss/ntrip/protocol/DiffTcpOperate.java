package com.fj.gnss.ntrip.protocol;

import android.util.Log;

import com.fj.gnss.GnssManager;
import com.fj.gnss.ntrip.NtripManager;
import com.fj.gnss.ntrip.bean.NtripInfo;
import com.fj.gnss.ntrip.util.UtilByte;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * 差分操作
 * author:Jarven.ding
 * create:2020/3/28
 */
public class DiffTcpOperate extends DiffOperate {

    private Socket socket = null;
    private DataInputStream mIn;
    private DataOutputStream mOut;


    DiffTcpOperate(NtripInfo ntripInfo) {
        super(ntripInfo);
    }

    public boolean connect() {
        closeNet();
        //Start Login Thread
        ThreadLoginServer threadLoginServer = new ThreadLoginServer();
        threadLoginServer.start();
        return true;
    }

    public void disConnect() {
        closeNet();
    }

    class ThreadLoginServer extends Thread {
        @Override
        public void run() {
            super.run();
            Thread.currentThread().setName("DiffTcpOperateThreadLoginServer");
            try {
                socket = new Socket();
                SocketAddress socAddress = new InetSocketAddress(ntripInfo.getIpAddress(), ntripInfo.getPort());
                socket.connect(socAddress, 5000);
                mIn = new DataInputStream(socket.getInputStream());
                mOut = new DataOutputStream(socket.getOutputStream());
            } catch (Exception e) {
                e.printStackTrace();
                closeNet();
                GnssManager.getInstance().getNtripManager().setNtripConnectState(NtripManager.NtripState.LINK_TO_NODE_FAILED);
                return;
            }
            startReadWrite();
        }
    }

    private void closeNet() {
        try {
            if (this.socket != null) {
                this.socket.close();
                this.socket = null;
            }
            if (mOut != null) {
                mOut.close();
                mOut = null;
            }
            if (mIn != null) {
                mIn.close();
                mIn = null;
            }
            stopReadWrite();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startReadWrite() {
        try {
            isRead = true;
            isWrite = true;
            ThreadRead threadRead = new ThreadRead();
            threadRead.start();
            ThreadWrite threadWrite = new ThreadWrite();
            threadWrite.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopReadWrite() {
        isRead = false;
        isWrite = false;
    }

    class ThreadRead extends Thread {
        byte[] bufferBytes = new byte[4096];

        @Override
        public void run() {
            while (isRead) {
                Thread.currentThread().setName("DiffTcpOperateThreadRead");
                try {
                    if (socket != null && !socket.isClosed() && socket.isConnected() && !socket
                            .isInputShutdown()) {
                        int len = mIn.read(bufferBytes);
                        if (len > 0) {
                            byte[] bts = UtilByte.get(bufferBytes, 0, len);
                            postData(bts);
                            Thread.sleep(50);
                        } else {
                            closeNet();
                            GnssManager.getInstance().getNtripManager().setNtripConnectState(NtripManager.NtripState.LINK_TO_NODE_FAILED);
                        }
                    } else {
                        closeNet();
                        GnssManager.getInstance().getNtripManager().setNtripConnectState(NtripManager.NtripState.LINK_TO_NODE_FAILED);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    closeNet();
                    GnssManager.getInstance().getNtripManager().setNtripConnectState(NtripManager.NtripState.LINK_TO_NODE_FAILED);
                }
            }
        }


        private void postData(byte[] bt) {
            String rec = new String(bt);
            Log.i("Ntrip", rec.length() + "");
            String NC_ICY_200_OK = "ICY 200 OK";
            String NC_401_UNAUTHORIZED = "401 Unauthorized";
            if (rec.trim().contains(NC_ICY_200_OK)) {
                Log.d("Ntrip", "NC_ICY_200_OK");
                GnssManager.getInstance().getNtripManager().setNtripConnectState(NtripManager.NtripState.LINK_TO_NODE_SUCCESS);
            } else if (rec.trim().contains(NC_401_UNAUTHORIZED)) {
                Log.d("Ntrip", "NC_401_UNAUTHORIZED");
                closeNet();
                GnssManager.getInstance().getNtripManager().setNtripConnectState(NtripManager.NtripState.LINK_TO_NODE_FAILED);
            } else {
                GnssManager.getInstance().getNtripManager().setDiffRtcm(bt);
            }
        }
    }

    class ThreadWrite extends Thread {
        @Override
        public void run() {
            Thread.currentThread().setName("DiffTcpOperateThreadWrite");
            while (isWrite) {
                if (socket == null || mOut == null) {
                    return;
                }
                if (datas.size() <= 0) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        continue;
                    }
                    continue;
                }
                byte[] buffer = null;
                synchronized (datas) {
                    if (datas.size() > 0) {
                        buffer = datas.remove(0);// get a data
                        if (buffer == null || buffer.length < 1) {
                            continue;
                        }
                    } else {
                        continue;
                    }
                }
                if (isWrite) {
                    writes(buffer);
                } else {
                    return;
                }
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

        void writes(byte[] data) {
            if (mOut != null) {
                try {
                    mOut.write(data);
                    mOut.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}