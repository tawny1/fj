package com.fj.gnss.ntrip.protocol;

import com.fj.gnss.ntrip.bean.NtripInfo;

import java.util.Vector;

/**
 * 差分操作
 * author:Jarven.ding
 * create:2020/3/28
 */
public abstract class DiffOperate {

    NtripInfo ntripInfo;
    final Vector<byte[]> datas = new Vector<>();

    boolean isRead = false;
    boolean isWrite = false;

    DiffOperate(NtripInfo ntripInfo) {
        this.ntripInfo = ntripInfo;
    }

    public void sendData(byte[] data) {
        synchronized (datas) {
            if (datas.size() > 0) {
                datas.clear();
            }
            datas.add(data);
        }
    }
}
