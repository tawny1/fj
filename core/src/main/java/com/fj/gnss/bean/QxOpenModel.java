package com.fj.gnss.bean;

/**
 * Everyday is another day, keep going.
 * Created by ramo.wu
 * email:   ramo.wu@fjdynamics.com
 * date:    2020/11/23 17:03
 * desc:
 *
 * @author ramo.wu
 */
public class QxOpenModel {

    /**
     * 0	    调用成功，调用结果符合预期
     * 10001	参数为空
     * 10002	参数错误
     * 39998	系统繁忙
     * 39999	内部错误
     * 20004	配额不足，没有可用的设备服务号
     * 21004	设备服务号在该sik下只能手动绑定
     * 21005	SIK无效
     * 25001	设备服务号不存在
     * 25002	设备服务号已被绑定过
     * 25013	该设备服务号已被关联，不能再绑定。
     * 25015	该设备服务号关联了其他的DSK，不能再绑定。
     * 25019	该设备服务号正在被其他用户操作中
     * 70001	SIK没有权限
     */
    private int code;
    /**
    * description 信息
    * author tony.tang
    * date 2021/11/1 10:35
    */
    private String message;
    /**
    * description 对应的实体对象
    * author tony.tang
    * date 2021/11/1 10:35
    */
    private ResultModel data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ResultModel getData() {
        return data;
    }

    public void setData(ResultModel data) {
        this.data = data;
    }
    /**
    * description 实体对象
    * author tony.tang
    * date 2021/11/1 10:35
    */
    public static class ResultModel {


        // {"expireTime":1614441599999,"createTime":1576231420207,"dsk":"D2c4880pj44odm","deviceType":"fjdaidrive",
        // "currentStatus":2,"deviceId":"NSTR01VMCZX01909220100102","activateTime":1582785202248}
        /**
         * 设备服务号
         */
        private String dsk;
        /**
         * 设备ID
         */
        private String deviceId;
        /**
         * 设备类型
         */
        private String deviceType;

        /**
         * 当前生命周期状态
         * <p>
         * 1表示未激活，
         * 2表示服务中，
         * 3表示过期，
         * 4表示暂停，
         * 5表示仍在服务中，但是即将过期。
         */
        private int currentStatus;

        /**
         * 创建时间
         */
        private long createTime;
        /**
         * 激活时间
         */
        private long activateTime;
        /**
         * 过期时间
         */
        private long expireTime;

        public long getExpireTime() {
            return expireTime;
        }

        public void setExpireTime(long expireTime) {
            this.expireTime = expireTime;
        }

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }

        public String getDsk() {
            return dsk;
        }

        public void setDsk(String dsk) {
            this.dsk = dsk;
        }

        public String getDeviceType() {
            return deviceType;
        }

        public void setDeviceType(String deviceType) {
            this.deviceType = deviceType;
        }

        public int getCurrentStatus() {
            return currentStatus;
        }

        public void setCurrentStatus(int currentStatus) {
            this.currentStatus = currentStatus;
        }

        public String getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        public long getActivateTime() {
            return activateTime;
        }

        public void setActivateTime(long activateTime) {
            this.activateTime = activateTime;
        }
    }
}
