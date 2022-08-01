package com.fj.gnss.ntrip.protocol;

import com.fj.gnss.ntrip.bean.NtripInfo;
import com.fj.gnss.ntrip.bean.NtripSource;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * 获取源
 * author:Jarven.ding
 * create:2020/3/28
 */
public class GetSourceThread extends Thread {

    private Socket socket = null;
    private PrintWriter printOut = null;
    private BufferedReader bufferedIn = null;
    private boolean isRun = false;

    private GetSourceListener getSourceListener;

    private String sendData;
    private List<NtripSource> sourceList;
    private NtripInfo ntripInfo;

    public GetSourceThread(NtripInfo ntripInfo, String sendData,
                           List<NtripSource> sourceList) {
        this.ntripInfo = ntripInfo;
        this.sendData = sendData;
        this.sourceList = sourceList;
    }


    public void setGetSourceListener(GetSourceListener getSourceListener) {
        this.getSourceListener = getSourceListener;
    }

    public void removeTableNetworkSourceListGet() {
        this.getSourceListener = null;
    }

    @Override
    public void run() {
        super.run();
        try {
            closeNet();
            isRun = true;
            if (connectFailed()) {
                return;
            }
            if (bufferFailed()) {
                return;
            }
            this.printOut.print(this.sendData);
            this.printOut.flush();
            ArrayList<String> recData = new ArrayList<>();
            //Loop reads data
            loopReceivedData(recData);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void loopReceivedData(ArrayList<String> recData) {
        try {
            while (isRun) {
                if (socket != null && !socket.isClosed() && socket.isConnected() && !socket
                        .isInputShutdown()) {
                    String rec = bufferedIn.readLine();
                    if (rec != null) {
                        recData.add(rec);
                        if (rec.contains("ENDSOURCETABLE")) {
                            parseSourcePoints(recData);
                            if (getSourceListener != null) {
                                getSourceListener.sourceGetSuccess();
                            }
                            break;
                        }
                    }
                } else {
                    if (getSourceListener != null) {
                        getSourceListener.sourceGetFailed(0, "socket is null！");
                    }
                    break;
                }
            }
            closeNet();
        } catch (IOException ex) {
            if (getSourceListener != null) {
                getSourceListener.sourceGetFailed(0, GetSourceThread.class.getName() + "readLine IOException！");
            }
        }
    }

    private boolean bufferFailed() {
        try {
            this.bufferedIn =
                    new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.printOut = new PrintWriter(
                    new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
        } catch (IOException ex) {
            if (getSourceListener != null) {
                getSourceListener.sourceGetFailed(0, "get OutputStreamWriter IOException！");
            }
            return true;
        }
        return false;
    }

    private boolean connectFailed() {
        try {
            socket = new Socket();
            SocketAddress socAddress = new InetSocketAddress(ntripInfo.getIpAddress(), ntripInfo.getPort());
            socket.connect(socAddress, 5000);
        } catch (IOException ex) {
            if (getSourceListener != null) {
                getSourceListener.sourceGetFailed(0, "socket Exception！");
            }
            return true;
        }
        return false;
    }

    private void closeNet() {
        isRun = false;
        try {
            if (this.socket != null) {
                this.socket.close();
            }
            if (this.printOut != null) {
                this.printOut.close();
            }
            if (this.bufferedIn != null) {
                this.bufferedIn.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseSourcePoints(ArrayList<String> strList) {
        this.sourceList.clear();
        for (int i = 0; i < strList.size(); i++) {
            if (strList.get(i).contains("STR")) {
                parseNtripRecord(strList.get(i));
            }
        }
    }

    private void parseNtripRecord(String strRecord) {
        String[] strs = strRecord.split(";");
        NtripSource record = new NtripSource();
        int nCount;
        nCount = strs.length;
        if (nCount >= 2) {
            record.strMountpoint = strs[1];
        }
        if (nCount >= 3) {
            record.strIdentifier = strs[2];
        }
        if (nCount >= 4) {
            record.strFormat = strs[3];
        }
        if (nCount >= 5) {
            record.strFormatDetails = strs[4];
        }
        if (nCount >= 6) {
            record.strCarrier = strs[5];
        }
        if (nCount >= 7) {
            record.strNavSystem = strs[6];
        }
        if (nCount >= 8) {
            record.strNetwork = strs[7];
        }
        if (nCount >= 9) {
            record.strCountry = strs[8];
        }
        if (nCount >= 10) {
            record.strLatitude = strs[9];
        }
        if (nCount >= 11) {
            record.strLongitude = strs[10];
        }
        if (nCount >= 12) {
            record.strSendNMEA = strs[11];
        }
        if (nCount >= 13) {
            record.strSolution = strs[12];
        }
        if (nCount >= 14) {
            record.strGeneraror = strs[13];
        }
        if (nCount >= 15) {
            record.strCompression = strs[14];
        }
        if (nCount >= 16) {
            record.strAuthertication = strs[15];
        }
        if (nCount >= 17) {
            record.strFee = strs[16];
        }
        if (nCount >= 18) {
            record.strBitrate = strs[17];
        }
        if (nCount >= 19) {
            record.strMisc = strs[18];
        }

        this.sourceList.add(record);
    }

    public interface GetSourceListener {
        void sourceGetSuccess();

        void sourceGetFailed(int errorCode, String errorMessage);
    }

}


