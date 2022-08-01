package com.fj.gnss.view.widget;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.fj.construction.tools.utils.NetworkUtil;
import com.fj.construction.ui.dialog.BaseDialog;
import com.fj.construction.ui.toast.MyToast;
import com.fj.gnss.GnssManager;
import com.fj.gnss.R;
import com.fj.gnss.callback.RTKCommunicationCallBack;
import com.fj.gnss.ntrip.NtripManager;
import com.fj.gnss.ntrip.bean.NtripInfo;
import com.fj.gnss.ntrip.bean.NtripSource;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName NtripEditDialog
 * @Description Ntrip编辑界面
 * @Author jarven
 * @Date 2021/9/27
 */
public class NtripEditDialog extends BaseDialog {

    private static final String TAG = NtripEditDialog.class.getSimpleName();
    private final ArrayList<String> mItems = new ArrayList<>();
    private final Handler handler = new Handler(getContext().getMainLooper());
    private final Context mContext;
    private TextView confirmTV;
    private TextView ntripStateTV;
    private EditText hostET;
    private EditText portET;
    private EditText accountET;
    private EditText pwdET;
    private ArrayAdapter<String> spinnerAdapter;
    private NoNavSpinner sourceListSP;
    private RTKCommunicationCallBack rTKCommunicationCallBack;
    private NtripInfo ntripInfo;
    private final NtripManager.NtripListener ntripListener = new NtripManager.NtripListener() {

        @Override
        public void onGetSource(List<NtripSource> ntripSources) {
            mItems.clear();
            for (NtripSource ntripSource : ntripSources) {
                mItems.add(ntripSource.strMountpoint);
            }
            if (mItems.size() > 0 && ntripInfo != null) {
                ntripInfo.setSourcePoint(mItems.get(0));
            }
            handler.post(() -> {
                spinnerAdapter.notifyDataSetChanged();
                sourceListSP.setAdapter(spinnerAdapter);
            });
        }

        @Override
        public void onStateChange(final NtripManager.NtripState ntripState) {
            handler.post(() -> {
                ntripStateTV.setVisibility(View.VISIBLE);
                if (GnssManager.getInstance().getNtripManager().getNtripConnectState() == NtripManager.NtripState.LINK_TO_NODE_SUCCESS) {
                    confirmTV.setText(R.string.ntrip_disconnect);
                    confirmTV.setTextColor(Color.RED);
                } else {
                    confirmTV.setText(R.string.ntrip_connect);
                    confirmTV.setTextColor(Color.GREEN);
                }
                switch (ntripState) {
                    case GETTING_SOURCE:
                        ntripStateTV.setText(R.string.ntrip_get_source);
                        ntripStateTV.setTextColor(Color.GREEN);
                        break;
                    case GET_SOURCE_SUCCESS:
                        ntripStateTV.setText(R.string.ntrip_get_source_success);
                        ntripStateTV.setTextColor(Color.GREEN);
                        break;
                    case GET_SOURCE_FAILED:
                        ntripStateTV.setText(R.string.ntrip_get_source_failed);
                        ntripStateTV.setTextColor(Color.RED);
                        break;
                    case LINKING_TO_NODE:
                        ntripStateTV.setText(R.string.ntrip_link_to_node);
                        ntripStateTV.setTextColor(Color.GREEN);
                        break;
                    case LINK_TO_NODE_SUCCESS:
                        ntripStateTV.setText(R.string.ntrip_link_to_node_success);
                        ntripStateTV.setTextColor(Color.GREEN);
                        handler.postDelayed(() -> {
                            dismiss();
                            MyToast.success(R.string.ntrip_link_to_node_success);
                        }, 500);
                        break;
                    case LINK_TO_NODE_FAILED:
                        ntripStateTV.setText(R.string.ntrip_link_to_node_disconnected);
                        ntripStateTV.setTextColor(Color.RED);
                        break;
                    default:
                        break;
                }
            });
        }

    };

    public NtripEditDialog(@NonNull Context context) {
        super(context, R.style.DialogTheme);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_edit_corentrip);

        // initData
        this.rTKCommunicationCallBack = GnssManager.getInstance().getRTKCommunicationCallBack();
        this.ntripInfo = GnssManager.getInstance().getNtripManager().getNtripInfo();
        if (ntripInfo == null) {
            ntripInfo = new NtripInfo();
        }

        // initView
        hostET = findViewById(R.id.et_ntrip_host);
        portET = findViewById(R.id.et_ntrip_port);
        sourceListSP = findViewById(R.id.sp_source_list);
        ntripStateTV = findViewById(R.id.tv_ntrip_state);
        accountET = findViewById(R.id.et_ntrip_account);
        pwdET = findViewById(R.id.et_ntrip_password);
        confirmTV = findViewById(R.id.btn_dialog_confirm);

        hostET.setText(ntripInfo.getIpAddress());
        portET.setText(ntripInfo.getPort() == 0 ? "" : String.valueOf(ntripInfo.getPort()));
        accountET.setText(ntripInfo.getUsername());
        pwdET.setText(ntripInfo.getPassword());

        if (ntripInfo.getSourcePoint() != null) {
            mItems.add(ntripInfo.getSourcePoint());
        }
        spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, mItems);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sourceListSP.setAdapter(spinnerAdapter);
        sourceListSP.setOnSelectedListener(position -> {
            ntripInfo.setSourcePoint(mItems.get(position));
        });

        if (GnssManager.getInstance().getNtripManager().getNtripConnectState() == NtripManager.NtripState.LINK_TO_NODE_SUCCESS) {
            confirmTV.setText(R.string.ntrip_disconnect);
            confirmTV.setTextColor(Color.RED);
        } else {
            confirmTV.setText(R.string.ntrip_connect);
            confirmTV.setTextColor(Color.GREEN);
        }

        findViewById(R.id.btn_get_source).setOnClickListener(v -> getSource());
        findViewById(R.id.btn_dialog_cancel).setOnClickListener(v -> dismiss());
        confirmTV.setOnClickListener(v -> {
            if (!NetworkUtil.isNetworkAvailable(mContext)) {
                MyToast.error(R.string.network_disconnect);
                return;
            }
            if (GnssManager.getInstance().getNtripManager().getNtripConnectState() == NtripManager.NtripState.LINK_TO_NODE_SUCCESS) {
                disconnect();
            } else {
                connect();
            }
        });
    }

    private void getSource() {
        try {
            if (!NetworkUtil.isNetworkAvailable(mContext)) {
                MyToast.error(R.string.network_disconnect);
                return;
            }
            String host = hostET.getText().toString().trim();
            String portString = portET.getText().toString().trim();
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
     * 重连
     */
    private void connect() {
        if (TextUtils.isEmpty(ntripInfo.getSourcePoint())) {
            MyToast.errorL(R.string.error_source_node_is_empty);
        } else {
            String account = accountET.getText().toString();
            String pwd = pwdET.getText().toString();
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
    private void disconnect() {
        ntripInfo.setAutoLink(false);
        GnssManager.getInstance().getNtripManager().setNtripInfo(ntripInfo);
        GnssManager.getInstance().getNtripManager().stop();

        updateNtripCache();
    }

    private void updateNtripCache() {
        if (rTKCommunicationCallBack != null) {
            this.rTKCommunicationCallBack.saveNtripInfo(ntripInfo);
        }
    }

    @Override
    public void show() {
        super.show();
        GnssManager.getInstance().getNtripManager().addNtripListener(ntripListener);
        GnssManager.getInstance().getNtripManager().setNtripEditing(true);
    }

    @Override
    public void dismiss() {
        try {
            GnssManager.getInstance().getNtripManager().removeNtripListener(ntripListener);
            GnssManager.getInstance().getNtripManager().setNtripEditing(false);
            super.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
