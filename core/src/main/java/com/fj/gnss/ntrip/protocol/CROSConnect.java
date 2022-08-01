package com.fj.gnss.ntrip.protocol;

import android.util.Base64;

import com.fj.gnss.ntrip.bean.NtripInfo;
import com.fj.gnss.ntrip.util.UtilByte;

/**
 * Cros链接
 * author:Jarven.ding
 * create:2020/3/28
 */
public class CROSConnect extends DiffTcpOperate {

    public CROSConnect(NtripInfo ntripInfo) {
        super(ntripInfo);
    }

    @Override
    public boolean connect() {
        sendData(getLoginData());
        return super.connect();
    }

    private byte[] getLoginData() {
        StringBuilder command = new StringBuilder();
        command.append("GET /");
        command.append(ntripInfo.getSourcePoint());
        command.append(" HTTP/1.0\r\n").append("User-Agent: NTRIP GNSSInternetRadio/")
                .append("Accept: */*\r\n").append("Connection: Keep-Alive\r\n")
                .append("Authorization: Basic ");

        String usrpwd = ntripInfo.getUsername() + ":" + ntripInfo.getPassword();
        String encode = UtilByte.getString_UTF8(Base64.encode(usrpwd.getBytes(), Base64.DEFAULT));
        encode = encode.replace("\n", "");
        command.append(encode);
        command.append("\r\n\r\n");
        return command.toString().getBytes();
    }

    @Override
    public void disConnect() {
        super.disConnect();
    }

}
