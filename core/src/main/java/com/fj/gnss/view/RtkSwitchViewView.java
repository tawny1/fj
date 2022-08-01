package com.fj.gnss.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.fj.construction.tools.utils.NetworkUtil;
import com.fj.construction.ui.dialog.BaseDialog;
import com.fj.construction.ui.dialog.InputDialog;
import com.fj.construction.ui.dialog.LoadingDialog;
import com.fj.construction.ui.toast.MyToast;
import com.fj.gnss.GnssManager;
import com.fj.gnss.R;
import com.fj.gnss.bean.RtkMode;
import com.fj.gnss.callback.OnFrequencyPairingListener;
import com.fj.gnss.callback.OnRtkSwitchListener;
import com.fj.gnss.callback.RTKCommunicationCallBack;
import com.fj.gnss.view.widget.NtripEditDialog;


/**
 * Description:RTK设置封装界面
 * Date: 2021/10/22
 * Author: Howard.Zhang
 */
public class RtkSwitchViewView extends LinearLayout implements View.OnClickListener {
    private static final String TAG = RtkSwitchViewView.class.getSimpleName();

    private final Handler mUIHandler = new Handler(Looper.getMainLooper());
    private final RTKCommunicationCallBack rtkCommCallback;
    private final int colorSelected;

    private Context mContext;//上下文

    private InputDialog mParingDialog;//输入对话框
    private LoadingDialog loadingDialog;//等待对话框

    private RelativeLayout rlRtkNet;//内置网络RTK-布局框
    private RelativeLayout rlNtripStation;//Ntrip网络RTK-布局框
    private RelativeLayout rlBaseStation;//基站RTK-布局框

    private TextView tvRtkNet;//内置网络RTK-title
    private TextView tvNtripStation;//Ntrip网络RTK-title
    private TextView tvBaseStation;//基站RTK-title

    private ImageView ivRtkNet;//内置网络RTK-显示图
    private ImageView ivNtripStation;//Ntrip网络RTK-显示图
    private ImageView ivBaseStation;//基站RTK-显示图

    private ImageView ivRtkNetSelect;//内置网络RTK-是否设置按钮
    private ImageView ivRtkNtripSelect;//Ntrip网络RTK-是否设置按钮
    private ImageView ivBaseStationSelect;//基站RTK-是否设置按钮

    private TextView btnNtripRtkLogin;//Ntrip网络RTK-登录按钮
    private TextView btnBaseStationMatch;//基站RTK-对频按钮

    public RtkSwitchViewView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;

        LayoutInflater.from(context).inflate(R.layout.include_rtk_set, this);

        colorSelected = mContext.getResources().getColor(R.color.color_FFC500);
        rtkCommCallback = GnssManager.getInstance().getRTKCommunicationCallBack();

        initView();
    }

    /**
     * 初始view
     */
    public void initView() {
        rlRtkNet = this.findViewById(R.id.rl_rtk_net);
        rlBaseStation = this.findViewById(R.id.rl_base_station);
        rlNtripStation = this.findViewById(R.id.rl_ntrip_station);

        tvRtkNet = this.findViewById(R.id.tv_rtk_net);
        tvBaseStation = this.findViewById(R.id.tv_base_station);
        tvNtripStation = this.findViewById(R.id.tv_ntrip_station);

        ivRtkNet = this.findViewById(R.id.iv_rtk_net);
        ivBaseStation = this.findViewById(R.id.iv_base_station);
        ivNtripStation = this.findViewById(R.id.iv_ntrip_station);

        ivBaseStationSelect = this.findViewById(R.id.iv_base_station_select);
        ivRtkNetSelect = this.findViewById(R.id.iv_rtk_net_select);
        ivRtkNtripSelect = this.findViewById(R.id.iv_rtk_ntrip_select);

        btnNtripRtkLogin = this.findViewById(R.id.tv_ntrip_rtk_login);
        btnBaseStationMatch = this.findViewById(R.id.tv_base_station_match);

        this.rlRtkNet.setOnClickListener(this);
        this.rlBaseStation.setOnClickListener(this);
        this.rlNtripStation.setOnClickListener(this);

        this.btnNtripRtkLogin.setOnClickListener(this);
        this.btnBaseStationMatch.setOnClickListener(this);


        // 隐藏显示QX网络RTK选项
        if (rtkCommCallback == null || rtkCommCallback.isQxNotValid()) {
            rlRtkNet.setVisibility(View.GONE);
            ivRtkNetSelect.setVisibility(View.GONE);
        }

        updateView(GnssManager.getInstance().getNmeaDataParse().getGnssState().getRtkMode());
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rl_base_station) {
            switchRtkMode(0);
        } else if (id == R.id.rl_rtk_net) {
            if (rtkCommCallback == null || rtkCommCallback.isQxNotValid()) {
                MyToast.info(R.string.error_not_support_network_rtk);
                return;
            }
            switchRtkMode(1);
        } else if (id == R.id.rl_ntrip_station) {
            if (GnssManager.getInstance().getNmeaDataParse().getGnssState().supportNtripMode()) {
                switchRtkMode(2);
            } else {
                switchRtkMode(1);
            }
        } else if (id == R.id.tv_ntrip_rtk_login) {
            if (!NetworkUtil.isNetworkAvailable(mContext)) {
                MyToast.error(R.string.network_disconnect);
                return;
            }
            NtripEditDialog ntripEditDialog = new NtripEditDialog(mContext);
            ntripEditDialog.show();
        } else if (id == R.id.tv_base_station_match) {
            if (GnssManager.getInstance().getNmeaDataParse().getGnssState().getRtkMode() == 0) {
                frequencyMatch();
            }
        }

    }

    /**
     * 更新视图
     *
     * @param mode
     */
    private void updateView(int mode) {
        if (null == this.mContext) {
            return;
        }

        // 0 内置电台基站模式；1 网络模式; 2 Ntrip
        if (RtkMode.RTK_MODE_NTRIP.getId() == mode) {
            showBaseStationRtkView(false);
            showQxNetRtkView(false);
            showNtripRtkView(true);
        } else if (RtkMode.RTK_MODE_BASE_STATION.getId() == mode) {
            showBaseStationRtkView(true);
            showQxNetRtkView(false);
            showNtripRtkView(false);
        } else if (RtkMode.RTK_MODE_QX_NET.getId() == mode) {
            showBaseStationRtkView(false);
            showQxNetRtkView(true);
            showNtripRtkView(false);
        }
    }

    /**
     * NtripRtk设置
     *
     * @param isSelected
     */
    private void showNtripRtkView(boolean isSelected) {
        tvNtripStation.setTextColor(isSelected ? colorSelected : Color.BLACK);
        tvNtripStation.setTypeface(Typeface.defaultFromStyle(isSelected ? Typeface.BOLD : Typeface.NORMAL));
        rlNtripStation.setBackgroundResource(isSelected ? R.drawable.bg_5_gradient_gray : R.drawable.bg_6_gray);
        ivNtripStation.setBackgroundResource(isSelected ? R.mipmap.icon_rtk_net_select : R.mipmap.icon_rtk_net_unselect);
        ivRtkNtripSelect.setVisibility(isSelected ? View.VISIBLE : View.INVISIBLE);
        btnNtripRtkLogin.setVisibility(isSelected ? View.VISIBLE : View.INVISIBLE);
    }

    /**
     * 基站设置设置
     *
     * @param isSelected
     */
    private void showBaseStationRtkView(boolean isSelected) {
        tvBaseStation.setTextColor(isSelected ? colorSelected : Color.BLACK);
        tvBaseStation.setTypeface(Typeface.defaultFromStyle(isSelected ? Typeface.BOLD : Typeface.NORMAL));
        rlBaseStation.setBackgroundResource(isSelected ? R.drawable.bg_5_gradient_gray : R.drawable.bg_6_gray);
        ivBaseStation.setBackgroundResource(isSelected ? R.mipmap.icon_base_station_select : R.mipmap.icon_base_station_unselect);
        ivBaseStationSelect.setVisibility(isSelected ? View.VISIBLE : View.INVISIBLE);
        btnBaseStationMatch.setVisibility(isSelected ? View.VISIBLE : View.INVISIBLE);
    }

    /**
     * 网络RTK设置
     *
     * @param isSelected
     */
    private void showQxNetRtkView(boolean isSelected) {
        // 隐藏显示QX网络RTK选项
        if (rtkCommCallback == null || rtkCommCallback.isQxNotValid()) {
            return;
        }
        tvRtkNet.setTextColor(isSelected ? colorSelected : Color.BLACK);
        tvRtkNet.setTypeface(Typeface.defaultFromStyle(isSelected ? Typeface.BOLD : Typeface.NORMAL));
        rlRtkNet.setBackgroundResource(isSelected ? R.drawable.bg_5_gradient_gray : R.drawable.bg_6_gray);
        ivRtkNet.setBackgroundResource(isSelected ? R.mipmap.icon_rtk_net_select : R.mipmap.icon_rtk_net_unselect);
        ivRtkNetSelect.setVisibility(isSelected ? View.VISIBLE : View.INVISIBLE);
    }

    private void switchRtkMode(final int mode) {
        GnssManager.getInstance().switchMode(mode, 20, true, new OnRtkSwitchListener() {
            @Override
            public void onSwitchStart() {
                // 开始切换
                showLoadingDialog(R.string.waiting);
            }

            @Override
            public void onSwitchSuccess(int rtkMode) {
                dismissLoadingDialog();
                MyToast.success(R.string.operation_success);
                updateView(mode);
            }

            @Override
            public void onSwitchFail() {
                dismissLoadingDialog();
                MyToast.error(R.string.operation_failed);
            }
        });
    }

    /**
     * RTK对频
     */
    public void frequencyMatch() {
        if (mParingDialog == null) {
            mParingDialog = new InputDialog(mContext);
        }

        final boolean hasInternalRadio = GnssManager.getInstance().isHasInternalRadio();

        String pairingCode = GnssManager.getInstance().getBaseStationManager().getPairingCode();

        mParingDialog
                .setBackCancelable(false)
                .setOutsideCancelable(false)
                .setTitleText(hasInternalRadio ? R.string.excavator_bs_pairing_internal : R.string.excavator_bs_pairing_external)
                .setInputHint(R.string.excavator_base_code)
                .setInputText(pairingCode)
                .setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL)
                .setMaxInputLength(8)
                .setCancelClickListener(BaseDialog::dismiss)
                .setConfirmButton(R.string.excavator_pairing_btn, diggerDialog -> {
                    String code = mParingDialog.getInputText();
                    if (TextUtils.isEmpty(code)) {
                        return;
                    }

                    showLoadingDialog(R.string.waiting);
                    // 内置电台对频
                    GnssManager.getInstance().getBaseStationManager().pairBaseStation(
                            code,
                            20,
                            hasInternalRadio,
                            true,
                            new OnFrequencyPairingListener() {
                                @Override
                                public void onPairingResult(boolean isSuccess) {
                                    if (isSuccess) {
                                        mParingDialog.dismiss();
                                        dismissLoadingDialog();
                                        MyToast.success(R.string.toast_pair_suc);
                                    } else {
                                        dismissLoadingDialog();
                                        MyToast.error(R.string.toast_pair_failed);
                                    }
                                }

                                @Override
                                public void onReceivePairingCmd() {
                                    MyToast.success(R.string.toast_pairing_success_wait_location);
                                }
                            });
                }).show();
    }

    private void showLoadingDialog(@StringRes int strRes) {
        if (mContext != null) {
            if (loadingDialog == null) {
                loadingDialog = new LoadingDialog(mContext);
            }
            loadingDialog.setMessageText(strRes);
            if (!loadingDialog.isShowing()) {
                mUIHandler.post(() -> loadingDialog.show());
            }
        }
    }

    private void dismissLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            mUIHandler.post(() -> loadingDialog.dismiss());
        }
    }

}
