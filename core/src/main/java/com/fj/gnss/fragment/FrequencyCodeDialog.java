package com.fj.gnss.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.fj.construction.tools.utils.DensityUtil;
import com.fj.construction.tools.utils.DisplayUtils;
import com.fj.construction.ui.dialog.LoadingDialog;
import com.fj.construction.ui.toast.MyToast;
import com.fj.gnss.GnssManager;
import com.fj.gnss.R;
import com.fj.gnss.callback.OnFrequencyPairingListener;
import com.fj.gnss.databinding.DialogFrequencyCodeBinding;

/**
 * 对频码dialog实现
 */
public class FrequencyCodeDialog extends DialogFragment {

    public static final String TAG = "FrequencyCodeDialog";

    private DialogFrequencyCodeBinding binding;
    /**
     * 加载
     */
    private LoadingDialog loadingDialog;//等待对话框

    public static void show(FragmentManager fmgr) {
        FrequencyCodeDialog dialog = new FrequencyCodeDialog();
        dialog.show(fmgr, TAG);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.ActDialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogFrequencyCodeBinding.inflate(inflater, container, false);
        initView();
        return binding.getRoot();
    }

    private void initView() {
        binding.tvCancel.setOnClickListener(view -> dismiss());
        binding.tvConfirm.setOnClickListener(view -> confireClick());
        String pairingCode = GnssManager.getInstance().getBaseStationManager().getPairingCode();
        binding.etCode.setText(TextUtils.isEmpty(pairingCode) ? "" : pairingCode);
        setCancelable(false);
    }

    private void setWindowParams() {
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            int uiOptions = 0x00400000 | 0x00200000 | 0x01000000 | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;

            window.getDecorView().setSystemUiVisibility(uiOptions);
            WindowManager.LayoutParams params = window.getAttributes();
            if (params != null) {
                params.width = DensityUtil.dp2Px(getContext(), 472);
                params.height = DensityUtil.dp2Px(getContext(), 281);
                window.setAttributes(params);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        setWindowParams();
    }

    private void fullscreen() {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            DisplayUtils.fullScreen(activity);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        fullscreen();
    }

    /**
     * 确认按钮点击
     */
    private void confireClick() {
        String code = binding.etCode.getText().toString();
        if (TextUtils.isEmpty(code)) {
            MyToast.error(R.string.text_input_frequency_code);
            return;
        }
        final boolean hasInternalRadio = GnssManager.getInstance().isHasInternalRadio();
        showLoadingDialog(R.string.waiting);
        GnssManager.getInstance().getBaseStationManager().pairBaseStation(
                code,
                20,
                hasInternalRadio,
                true,
                new OnFrequencyPairingListener() {
                    @Override
                    public void onPairingResult(boolean isSuccess) {
                        if (isSuccess) {
                            dismissLoadingDialog();
                            dismiss();
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
    }

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
}
