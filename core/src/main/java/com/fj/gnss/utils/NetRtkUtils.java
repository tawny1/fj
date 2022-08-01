package com.fj.gnss.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.fj.construction.tools.utils.DateUtil;
import com.fj.construction.tools.utils.JsonUtil;
import com.fj.construction.tools.utils.NetworkUtil;
import com.fj.construction.ui.toast.MyToast;
import com.fj.gnss.GnssManager;
import com.fj.gnss.R;
import com.fj.gnss.bean.NetRtkMessage;
import com.fj.gnss.bean.NetRtkRequestMessage;
import com.fj.gnss.bean.QxOpenModel;
import com.fj.gnss.callback.RTKCommunicationCallBack;
import com.fj.gnss.ntrip.NtripManager;
import com.fj.gnss.ntrip.bean.NtripInfo;
import com.fj.gnss.ntrip.bean.NtripSource;
import com.fjdynamics.xlog.FJXLog;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.reactivex.subjects.PublishSubject;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * ntrip列表切换的业务单例
 */
public class NetRtkUtils {
    public static final String TAG = "NetRtkUtils";
    /**
     * 选择ntripfragment的显示状态
     */
    public static final Integer SELECT_NTRIP_SHOW = 1;
    /**
     * 选择ntripfragment的隐藏状态
     */
    public static final Integer SELECT_NTRIP_DISMISS = 0;
    /**
     * ntrip链接状态变化值
     */
    private static final PublishSubject<NtripManager.NtripState> ntripSubEnumState = PublishSubject.create();
    /**
     * 选择ntripfragment的观察者对象
     */
    private static final PublishSubject<Integer> selectNtripShow = PublishSubject.create();
    /**
     * app向子模块发消息
     */
    private static final PublishSubject<NetRtkMessage> rtkNetMessage = PublishSubject.create();
    /**
     * 子模块向app发消息
     */
    private static final PublishSubject<NetRtkRequestMessage> rtkNetRequestMessage = PublishSubject.create();

    private Context mContext;

    /**
     * app获取千寻 结果后回调的数据
     *
     * @return
     */
    public PublishSubject<NetRtkMessage> subrtkNetMessage() {
        return rtkNetMessage;
    }

    /**
     * gnss模块点击事件，让app发起千寻请求事件 如果现实的是异常信息，那么app需要获取到请求结果后，通过subrtkNetMessage 回传信息
     *
     * @return
     */
    public PublishSubject<NetRtkRequestMessage> subrtkNetRequestMessage() {
        return rtkNetRequestMessage;
    }

    public void setMcontext(Context mcontext) {
        this.mContext = mcontext;
    }


    private static NetRtkUtils instance;
    /**
     * Ntrip资源
     */
    private List<NtripSource> sources;
    /**
     * 主工程接口实现类
     */
    private RTKCommunicationCallBack rTKCommunicationCallBack;
    /**
     * 操作的ntrip数据
     */
    private NtripInfo ntripInfo;
    /**
     * ntrip状态监听
     */
    private final NtripManager.NtripListener ntripListener = new NtripManager.NtripListener() {

        @Override
        public void onGetSource(List<NtripSource> ntripSources) {
            FJXLog.INSTANCE.e(TAG, "onGetSource" + JsonUtil.getGsonInstance().toJson(ntripSources));
            sources = ntripSources;
        }

        @Override
        public void onStateChange(final NtripManager.NtripState ntripState) {
            FJXLog.INSTANCE.e(TAG, "onStateChange:" + ntripState.name());
            ntripSubEnumState.onNext(ntripState);
            switch (ntripState) {
                case GET_SOURCE_FAILED:
                    MyToast.errorL(R.string.ntrip_get_source_failed);
                    break;
                case LINKING_TO_NODE:
                    MyToast.errorL(R.string.ntrip_link_to_node);
                    break;
                default:
                    break;
            }
        }
    };


    /**
     * 私有构造器
     */
    private NetRtkUtils() {

    }

    /**
     * 单例
     *
     * @return
     */
    public static NetRtkUtils getInstance() {
        if (instance == null) {
            instance = new NetRtkUtils();
        }
        return instance;
    }

    /**
     * 获取资源列表
     *
     * @return
     */
    public List<NtripSource> getSources() {
        return sources;
    }


    /**
     * 获取ntrip事件枚举值
     *
     * @return
     */
    public PublishSubject<NtripManager.NtripState> subEnumNtripState() {
        return ntripSubEnumState;
    }

    /**
     * 获取选择ntrip弹框的显示和隐藏的事件
     *
     * @return
     */
    public PublishSubject<Integer> subSelectNtripFragment() {
        return selectNtripShow;
    }


    /**
     * 连接ntrip rtk
     *
     * @param account 用户名
     * @param pwd     密码
     */
    public void connectNtripRtk(String account, String pwd) {
        Log.e(TAG, "connectNtripRtk: GnssManager.getInstance().getNtripManager().getNtripConnectState()" + GnssManager.getInstance().getNtripManager().getNtripConnectState());
        if (GnssManager.getInstance().getNtripManager().getNtripConnectState() == NtripManager.NtripState.LINK_TO_NODE_SUCCESS) {
            disconnect();
        } else {
            connect(account, pwd);
        }
    }

    /**
     * 初始化ntrip
     */
    public void initNtripInfo() {
        this.rTKCommunicationCallBack = GnssManager.getInstance().getRTKCommunicationCallBack();
        this.ntripInfo = GnssManager.getInstance().getNtripManager().getNtripInfo();
        if (ntripInfo == null) {
            ntripInfo = new NtripInfo();
        }
    }

    /**
     * 设置ntrip
     *
     * @param info
     */
    public void setNtripInfo(NtripInfo info) {
        this.ntripInfo = info;
    }


    /**
     * 连接
     *
     * @param account 账户
     * @param pwd     密码
     */
    private void connect(String account, String pwd) {
        if (TextUtils.isEmpty(ntripInfo.getSourcePoint())) {
            MyToast.errorL(R.string.error_source_node_is_empty);
        } else {
            if (account.isEmpty() || pwd.isEmpty()) {
                MyToast.errorL(R.string.error_account_or_password_is_empty);
                return;
            }
            ntripInfo.setUsername(account);
            ntripInfo.setPassword(pwd);
            ntripInfo.setAutoLink(true);

            GnssManager.getInstance().getNtripManager().setNtripInfo(ntripInfo);
            GnssManager.getInstance().getNtripManager().linkSource();

            updateNtripCache();
        }
    }

    /**
     * 断开
     */
    public void disconnect() {
        ntripInfo.setAutoLink(false);
        // ntripInfo.setUsername("");
        GnssManager.getInstance().getNtripManager().setNtripInfo(ntripInfo);
        GnssManager.getInstance().getNtripManager().stop();
        updateNtripCache();
    }

    /**
     * 更新当前ntrip缓存
     */
    private void updateNtripCache() {
        if (rTKCommunicationCallBack != null) {
            this.rTKCommunicationCallBack.saveNtripInfo(ntripInfo);
        }
    }

    /**
     * 获取源数据
     *
     * @param mContext   上下文
     * @param host       ip地址
     * @param portString 端口
     */
    public void getSource(Context mContext, String host, String portString) {
        try {
            if (!NetworkUtil.isNetworkAvailable(mContext)) {
                MyToast.error(R.string.network_disconnect);
                return;
            }
            if (host.isEmpty() || portString.isEmpty()) {
                MyToast.errorL(R.string.ntrip_host_port_empty);
                return;
            }
            int port = Integer.parseInt(portString);
            ntripInfo.setIpAddress(host);
            ntripInfo.setPort(port);
            GnssManager.getInstance().getNtripManager().setNtripInfo(ntripInfo);
            GnssManager.getInstance().getNtripManager().getSource();
            updateNtripCache();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 添加监听 在selectNtripDialog show的时候调用
     */
    public void addNtripListener() {
        selectNtripShow.onNext(SELECT_NTRIP_SHOW);
        FJXLog.INSTANCE.e(TAG, "addNtripListener");
        GnssManager.getInstance().getNtripManager().addNtripListener(ntripListener);
        GnssManager.getInstance().getNtripManager().setNtripEditing(true);
    }

    /**
     * 移除监听 在selectNtripDialog dismiss的时候调用
     */
    public void removeNtripListener() {
        FJXLog.INSTANCE.e(TAG, "removeNtripListener");
        selectNtripShow.onNext(SELECT_NTRIP_DISMISS);
        try {
            GnssManager.getInstance().getNtripManager().removeNtripListener(ntripListener);
            GnssManager.getInstance().getNtripManager().setNtripEditing(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 直接获取默认信息的调用
     */
    public void getQxTimeDate() {
        HttpUtils.getQxStatus(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                rtkNetMessage.onNext(new NetRtkMessage().applyFail(mContext.getString(R.string.network_exception)));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (response.isSuccessful()) {
                        String result = response.body().string();
                        QxOpenModel model = JsonUtil.getGsonInstance().fromJson(result, QxOpenModel.class);
                        if (model.getCode() == 0) {
                            QxOpenModel.ResultModel resultModel = model.getData();
                            if (resultModel != null) {
                                long expireTime = resultModel.getExpireTime();
                                if (System.currentTimeMillis() > expireTime) {
                                    rtkNetMessage.onNext(new NetRtkMessage().applyFail(mContext.getString(R.string.tetx_rtk_expire_date_over)));
                                } else {
                                    String format = String.format(Locale.US, mContext.getString(R.string.tetx_rtk_expire_date), DateUtil.DATE_FORMAT.format(expireTime));
                                    rtkNetMessage.onNext(new NetRtkMessage().applySuc(format));
                                }
                            } else {
                                //   <string name="msg_sn_not_bind_rtk">当前SN号未开通，请联系当地经销商或拨打客服热线。</string>
                                rtkNetMessage.onNext(new NetRtkMessage().applyFail(mContext.getString(R.string.msg_sn_not_bind_rtk)));
                            }
                        } else {
                            rtkNetMessage.onNext(new NetRtkMessage().applyFail(model.getMessage()));
                        }
                    } else {
                        rtkNetMessage.onNext(new NetRtkMessage().applyFail(mContext.getString(R.string.msg_sn_not_bind_rtk)));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    rtkNetMessage.onNext(new NetRtkMessage().applyFail(mContext.getString(R.string.msg_sn_not_bind_rtk)));
                }
            }
        });
    }


    /**
     * 直接获取默认信息的调用
     */
    public void getQxTimeDate(String url, Map<String, String> map) {

        HttpUtils.getQxStatus(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                rtkNetMessage.onNext(new NetRtkMessage().applyFail(mContext.getString(R.string.network_exception)));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (response.isSuccessful()) {
                        String result = response.body().string();
                        QxOpenModel model = JsonUtil.getGsonInstance().fromJson(result, QxOpenModel.class);
                        if (model.getCode() == 0) {
                            QxOpenModel.ResultModel resultModel = model.getData();
                            if (resultModel != null) {
                                long expireTime = resultModel.getExpireTime();
                                if (System.currentTimeMillis() > expireTime) {
                                    rtkNetMessage.onNext(new NetRtkMessage().applyFail(mContext.getString(R.string.tetx_rtk_expire_date_over)));
                                } else {
                                    String format = String.format(Locale.US, mContext.getString(R.string.tetx_rtk_expire_date), DateUtil.DATE_FORMAT.format(expireTime));
                                    rtkNetMessage.onNext(new NetRtkMessage().applySuc(format));
                                }
                            } else {
                                //   <string name="msg_sn_not_bind_rtk">当前SN号未开通，请联系当地经销商或拨打客服热线。</string>
                                rtkNetMessage.onNext(new NetRtkMessage().applyFail(mContext.getString(R.string.msg_sn_not_bind_rtk)));
                            }
                        } else {
                            rtkNetMessage.onNext(new NetRtkMessage().applyFail(model.getMessage()));
                        }
                    } else {
                        rtkNetMessage.onNext(new NetRtkMessage().applyFail(mContext.getString(R.string.msg_sn_not_bind_rtk)));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    rtkNetMessage.onNext(new NetRtkMessage().applyFail(mContext.getString(R.string.msg_sn_not_bind_rtk)));
                }
            }
        }, map, url);
    }


}
