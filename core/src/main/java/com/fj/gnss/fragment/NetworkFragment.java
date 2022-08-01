package com.fj.gnss.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.fj.construction.tools.utils.DisplayUtils;
import com.fj.construction.ui.dialog.LoadingDialog;
import com.fj.construction.ui.toast.MyToast;
import com.fj.gnss.GnssManager;
import com.fj.gnss.R;
import com.fj.gnss.bean.NetRtkMessage;
import com.fj.gnss.bean.RtkMode;
import com.fj.gnss.callback.OnRtkSwitchListener;
import com.fj.gnss.callback.RTKCommunicationCallBack;
import com.fj.gnss.databinding.FragmentRtkNetworkBinding;
import com.fj.gnss.ntrip.NtripManager;
import com.fj.gnss.utils.NetRtkUtils;
import com.fj.gnss.utils.SchedulersTransformer;
import com.fjdynamics.xlog.FJXLog;

/**
 * @author Wentao.Hu
 * @description 网络信息 选择ntrip
 */
public class NetworkFragment extends Fragment implements View.OnClickListener {
    public static final String TAG = "NetworkFragment";
    private static final int RtkNet = 1;
    private static final int BaseRtkNet = 0;
    private static final int NtripRtkNet = 2;
    private FragmentRtkNetworkBinding binding;
    /**
     * 加载
     */
    private LoadingDialog loadingDialog;//等待对话框

    private int currentmode;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRtkNetworkBinding.inflate(inflater, container, false);
        initView();
        return binding.getRoot();
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onEvent(RtkAccountEvent event){
//        binding.tvNtirp2.setText(event.getAccountName());
//        binding.tvEnd2.setText(R.string.text_switch_account);
//    }

    @SuppressLint("CheckResult")
    private void initView() {
        binding.rlRtkNet.setOnClickListener(this);
        binding.rlRtkNtrip.setOnClickListener(this);
        binding.rlRtkStation.setOnClickListener(this);
        binding.tvEnd.setOnClickListener(this);
        binding.tvEnd2.setOnClickListener(this);
        binding.tvEnd3.setOnClickListener(this);

        setSelect(true, false, false, -1, 0);
        if (GnssManager.getInstance().getNtripManager().getNtripConnectState() == NtripManager.NtripState.LINK_TO_NODE_SUCCESS) {

            binding.tvEnd2.setText(R.string.text_change_account);
        } else {
            binding.tvEnd2.setText(R.string.cnn_apply);
        }
        updateView(GnssManager.getInstance().getNmeaDataParse().getGnssState().getRtkMode());
        //观察选择ntripfragment 消失的事件
        NetRtkUtils.getInstance().subSelectNtripFragment().compose(new SchedulersTransformer<Integer>())
                .subscribe(
                        state -> {
                            if (NetRtkUtils.SELECT_NTRIP_DISMISS.equals(state)) {
                                restNtrip();
                            }
                        },
                        throwable -> {
                            FJXLog.INSTANCE.e(TAG, "" + throwable.getMessage());
                            throwable.printStackTrace();
                        }
                );
        NetRtkUtils.getInstance().subrtkNetMessage().compose(new SchedulersTransformer<NetRtkMessage>())
                .subscribe(
                        state -> {
                            FJXLog.INSTANCE.e(TAG, "message:" + state);
                            binding.tvNet2.setText(state.isNormal() ? state.getTimeMessage() : state.getErrMessage());
                            if (RtkNet == currentmode) {
                                binding.tvEnd.setVisibility(state.isNormal() ? View.VISIBLE : View.GONE);
                            }
                        },
                        throwable -> {
                            FJXLog.INSTANCE.e(TAG, "" + throwable.getMessage());
                            throwable.printStackTrace();
                        }
                );

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.rl_rtk_net) {
            RTKCommunicationCallBack rtkCommCallback = GnssManager.getInstance().getRTKCommunicationCallBack();
            if (rtkCommCallback == null || rtkCommCallback.isQxNotValid()) {
                MyToast.info(R.string.error_not_support_network_rtk);
                return;
            }

            //  NetRtkUtils.getInstance().subrtkNetRequestMessage().onNext(new NetRtkRequestMessage(true));
            switchRtkMode(RtkNet);

        } else if (view.getId() == R.id.rl_rtk_ntrip) {
            Log.e(TAG,
                    "onClick: GnssManager.getInstance().getNmeaDataParse().getGnssState().supportNtripMode()" + GnssManager.getInstance().getNmeaDataParse().getGnssState().supportNtripMode());
            if (GnssManager.getInstance().getNmeaDataParse().getGnssState().supportNtripMode()) {
                switchRtkMode(NtripRtkNet);
            } else {
                switchRtkMode(RtkNet);
            }
            //   setSelect(false, true, false, NtripRtkNet, R.id.rl_rtk_ntrip);
        } else if (view.getId() == R.id.rl_rtk_station) {
            switchRtkMode(BaseRtkNet);
            //  setSelect(false, false, true, BaseRtkNet, R.id.rl_rtk_station);
        } else if (view.getId() == R.id.tv_end) {
            //延长有效期
            Log.e(TAG, "onClick:延长有效期 ");
        } else if (view.getId() == R.id.tv_end2) {
            Log.e(TAG, "onClick:ntrip连接使用2 ");
            SelectNtripDialog.show(getActivity().getSupportFragmentManager());

        } else if (view.getId() == R.id.tv_end3) {
            Log.e(TAG, "onClick:基站对频 ");
            FrequencyCodeDialog.show(getChildFragmentManager());
        }
    }

    private void setSelect(boolean b, boolean b2, boolean b3, int mode, int viewId) {
        binding.rlRtkNet.setSelected(b);
        binding.rlRtkNtrip.setSelected(b2);
        binding.rlRtkStation.setSelected(b3);

        binding.tvNet.setSelected(b);
        binding.tvNtirp.setSelected(b2);
        binding.tvRtk.setSelected(b3);

        binding.ivNet.setSelected(b);
        binding.ivNtrip.setSelected(b2);
        binding.ivRtk.setSelected(b3);

        setVisibility(b, View.VISIBLE, View.GONE, View.GONE);
        setVisibility(b2, View.GONE, View.VISIBLE, View.GONE);
        setVisibility(b3, View.GONE, View.GONE, View.VISIBLE);

        if (mode != -1) {
            //  NetRtkUtils.getInstance().switchRtkMode(getContext(), mode, viewId);
        }
    }

    private void setVisibility(boolean b3, int gone, int gone2, int visible) {
        if (b3) {
            binding.tvEnd.setVisibility(gone);
            binding.tvEnd2.setVisibility(gone2);
            binding.tvEnd3.setVisibility(visible);
            binding.tvNet2.setVisibility(gone);
            binding.tvNtirp2.setVisibility(gone2);
            binding.tvRtk2.setVisibility(visible);
        }
    }

    private void getQxStatusFailed(String msg) {
        requireActivity().runOnUiThread(() -> {
            binding.tvNet2.setText(msg);
        });
    }

    /**
     * 切换模式
     *
     * @param mode
     */
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
     * 显示dialog
     *
     * @param strRes 字符串int
     */
    private void showLoadingDialog(@StringRes int strRes) {
        if (getContext() != null) {
            if (loadingDialog == null) {
                loadingDialog = new LoadingDialog(getContext());
            }
            loadingDialog.setMessageText(strRes);
            if (!loadingDialog.isShowing()) {
                getActivity().runOnUiThread(() -> loadingDialog.show());
            }
        }
    }

    private void dismissLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            getActivity().runOnUiThread(() -> loadingDialog.dismiss());
        }
    }

    /**
     * 更新视图
     *
     * @param mode
     */
    private void updateView(int mode) {
        if (null == getContext()) {
            return;
        }
        currentmode = mode;
        // 0 内置电台基站模式；1 网络模式; 2 Ntrip
        if (RtkMode.RTK_MODE_NTRIP.getId() == mode) {
            setSelect(false, true, false, NtripRtkNet, R.id.rl_rtk_ntrip);
        } else if (RtkMode.RTK_MODE_BASE_STATION.getId() == mode) {
            setSelect(false, false, true, BaseRtkNet, R.id.rl_rtk_station);
        } else if (RtkMode.RTK_MODE_QX_NET.getId() == mode) {
            setSelect(true, false, false, RtkNet, R.id.rl_rtk_net);
        }
        restNtrip();
    }

    @Override
    public void onResume() {
        super.onResume();
        FJXLog.INSTANCE.e(TAG, "onResume");
        restNtrip();
        FragmentActivity activity = getActivity();
        if (activity != null) {
            DisplayUtils.fullScreen(activity);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        FJXLog.INSTANCE.e(TAG, "onStart");
        GnssManager.getInstance().setRtkSwitchLock(true);
    }

    @Override
    public void onStop() {
        super.onStop();
        FJXLog.INSTANCE.e(TAG, "onStop");

    }

    /**
     * 重新显示ntrip 账号信息
     */
    private void restNtrip() {
        FJXLog.INSTANCE.e(TAG, "restNtrip:currentmode2:" + currentmode);
        if (NtripRtkNet == currentmode) {
            String username = GnssManager.getInstance().getNtripManager().getNtripInfo().getUsername();
            FJXLog.INSTANCE.e(TAG, "restNtrip:username" + username);
            binding.tvNtirp2.setText(TextUtils.isEmpty(username) ? "" : username);
            binding.tvEnd2.setText(R.string.text_change_account);
        }
    }


}
