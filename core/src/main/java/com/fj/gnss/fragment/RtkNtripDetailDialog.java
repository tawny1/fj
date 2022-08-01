package com.fj.gnss.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.fj.construction.tools.utils.DisplayUtils;
import com.fj.construction.tools.utils.JsonUtil;
import com.fj.construction.ui.toast.MyToast;
import com.fj.gnss.BuildConfig;
import com.fj.gnss.GnssManager;
import com.fj.gnss.R;
import com.fj.gnss.databinding.DialogRtkNtripAddBinding;
import com.fj.gnss.ntrip.NtripManager;
import com.fj.gnss.ntrip.bean.NtripInfo;
import com.fj.gnss.ntrip.bean.NtripSource;
import com.fj.gnss.utils.NetRtkUtils;
import com.fj.gnss.utils.SchedulersTransformer;
import com.fj.gnss.view.NetNtripAdapter;
import com.fjdynamics.xlog.FJXLog;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import io.reactivex.disposables.Disposable;

/**
 * 新增和详情的rtk数据 详细
 */
public class RtkNtripDetailDialog extends DialogFragment {

    public static final String TAG = "RtkNtripDetailDialog";
    private final static Integer LEAVE_NTRIP_ANDJOIN = 0;
    private final static Integer JOIN_NTRIP = 1;
    private final static Integer DELETE_NTRIP = 2;
    private final static Integer DIS_CNN = 3;
    private final static Integer NTRIP_CNN = 4;
    private final int EDITTEXT_AMOUNT = 5;
    private final ArrayList<String> mItems = new ArrayList<>();
    private final boolean isDetail;
    private boolean isactive = false;
    //队列 用来放操作步骤 如果加入的时候
    private final LinkedBlockingQueue<Integer> queue = new LinkedBlockingQueue<>();
    //连接的队列
    private final LinkedBlockingQueue<Integer> cnnQueue = new LinkedBlockingQueue<>();
    private final NtripInfo accountData;
    private final NetNtripAdapter adapter;
    private final boolean isConnect;
    private NtripInfo ntripInfo;
    private DialogRtkNtripAddBinding binding;
    private int mEditTextHaveInputCount = 0;
    private boolean isShowPassword = true;
    private ArrayAdapter<String> spinnerAdapter;
    //  private MMKV mmkv; 资源  这个是从别处拿到的。
    private String sourceCode;
    private static RtkNtripDetailDialog dialog;
    Disposable subscribe;

    public RtkNtripDetailDialog(boolean isDetail, boolean isConnect, NtripInfo accountData, NetNtripAdapter adapter) {
        this.isDetail = isDetail;
        this.isConnect = isConnect;
        this.accountData = accountData;
        this.adapter = adapter;
    }

    public static void show(FragmentManager fmg, boolean isDetail, boolean isConnect, NtripInfo accountData, NetNtripAdapter adapter) {
        dialog = new RtkNtripDetailDialog(isDetail, isConnect, accountData, adapter);
        dialog.show(fmg, TAG);

    }

    @Override
    public int show(@NonNull FragmentTransaction transaction, @Nullable String tag) {
        isactive = true;
        return super.show(transaction, tag);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.ActDialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogRtkNtripAddBinding.inflate(inflater, container, false);
        initView();
        Log.e(TAG, "onCreateView: ");
        return binding.getRoot();
    }

    /**
     * 初始化view
     */
    private void initView() {
        //mmkv = MMKV.defaultMMKV();
        NetRtkUtils.getInstance().initNtripInfo();
        initData();
        initListener();
        this.ntripInfo = GnssManager.getInstance().getNtripManager().getNtripInfo();
        if (isDetail && ntripInfo.getSourcePoint() != null) {
            mItems.clear();
            mItems.add(ntripInfo.getSourcePoint());
        }

        spinnerAdapter = new ArrayAdapter<>(getContext(), R.layout.simple_spinner_item, mItems);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        binding.etSourceNode.setAdapter(spinnerAdapter);
        binding.etSourceNode.setOnSelectedListener(position -> {
//            ntripInfo.setSourcePoint(mItems.get(position));
            ntripInfo.setSourcePoint(mItems.get(position));
            sourceCode = mItems.get(position);
        });
        rxNtripEnumState();
    }

    /**
     * 观察ntrip的状态
     */
    private void rxNtripEnumState() {
        subscribe = NetRtkUtils.getInstance().subEnumNtripState()
                .compose(new SchedulersTransformer<>())
                .subscribe(
                        state -> {
                            dispatchEvent(state);
                        },
                        throwable -> {
                            FJXLog.INSTANCE.e(TAG, "" + throwable.getMessage());
                            throwable.printStackTrace();
                        }
                );
    }

    /**
     * 消费状态改变的事件
     *
     * @param state ntrip的状态 主要功能
     *              主要有 连接，断开 获取源数据，判断是否在激活的状态
     *              连接分为已连接下的其他ntrip连接
     */
    private void dispatchEvent(NtripManager.NtripState state) {
        FJXLog.INSTANCE.e(TAG, "dispatchEvent：isactive->" + isactive);
        if (!isactive) {
            return;
        }
        FJXLog.INSTANCE.e(TAG, "NtripManager.NtripState:" + state);
        switch (state) {

            case GET_SOURCE_SUCCESS:
                //获取源成功
                List<NtripSource> ntripSources = NetRtkUtils.getInstance().getSources();
                mItems.clear();
                FJXLog.INSTANCE.e(TAG, "ntripSources" + JsonUtil.getGsonInstance().toJson(ntripSources));
                if (ntripSources != null) {
                    for (NtripSource ntripSource : ntripSources) {
                        mItems.add(ntripSource.strMountpoint);
                    }
                }
                if (mItems.size() > 0 && accountData != null) {
                    ntripInfo.setSourcePoint(mItems.get(0));
                }
                sourceCode = mItems.get(0);
                spinnerAdapter.notifyDataSetChanged();
                binding.etSourceNode.setAdapter(spinnerAdapter);
                break;
            case GET_SOURCE_FAILED:
                MyToast.errorL(R.string.ntrip_get_source_failed);
                break;
            case LINKING_TO_NODE:
                MyToast.errorL(R.string.ntrip_link_to_node);
                break;
            case LINK_TO_NODE_SUCCESS:
                //连接成功
                //如果是详情
                Integer cnn = cnnQueue.poll();
                FJXLog.INSTANCE.e(TAG, "cnn queue：" + cnn);
                if (NTRIP_CNN.equals(cnn)) {
                    MyToast.success(R.string.ntrip_link_to_node_success);
                    mmkvAddData(true);
                    dismiss();
                }
                //连接成功后，dismiss
                break;
            case LINK_TO_NODE_FAILED:
                Integer poll = queue.poll();
                FJXLog.INSTANCE.e("poll", "队列数据是：" + poll);
                if (LEAVE_NTRIP_ANDJOIN.equals(poll)) {
                    connectRtk();
                } else if (DELETE_NTRIP.equals(poll)) {
                    mmkvRemoveData(accountData);
                    //删除完成，就要结束这个
                }
                break;
            default:
                break;
        }
    }


    @Override
    public void show(@NonNull FragmentManager manager, @Nullable String tag) {
        super.show(manager, tag);
        //   GnssManager.getInstance().getNtripManager().addNtripListener(ntripListener);
        // setRtkSwitchLock
        GnssManager.getInstance().getNtripManager().setNtripEditing(true);

    }


    @Override
    public void onDestroyView() {
        Log.e(TAG, "onDestroyView: ");
        super.onDestroyView();
    }

    private void initListener() {
        binding.etCompanyName.addTextChangedListener(new AccountEditTextWatcher());
        binding.etAddress.addTextChangedListener(new AccountEditTextWatcher());
//        binding.etSourceNode.addTextChangedListener(new AccountEditTextWatcher());
        binding.etAccountName.addTextChangedListener(new AccountEditTextWatcher());
        binding.etPort.addTextChangedListener(new AccountEditTextWatcher());
        binding.etPassword.addTextChangedListener(new AccountEditTextWatcher());
        binding.tvCancel.setOnClickListener(view -> dismiss());
        binding.ivBack.setOnClickListener(view -> dismiss());
        binding.tvSave.setOnClickListener(view -> {//保存
            if (judgeSave()) {
                return;
            }
            mmkvAddData(isConnect);
            dismiss();
        });
        binding.tvDelete.setOnClickListener(view -> {//删除-断开rtk
            //先断开连接，再进行删除
            FJXLog.INSTANCE.e("tvDelete",
                    "GnssManager.getInstance().getNtripManager().getNtripConnectState()" + GnssManager.getInstance().getNtripManager().getNtripConnectState());
            if (GnssManager.getInstance().getNtripManager().getNtripConnectState() == NtripManager.NtripState.LINK_TO_NODE_SUCCESS) {
                //正在连接，而且
                FJXLog.INSTANCE.e("tvDelete", "LINK_TO_NODE_SUCCESS");
                if (isEquals(ntripInfo, accountData)) {
                    FJXLog.INSTANCE.e("tvDelete", "ntripInfo==accountData");
                    queue.offer(DELETE_NTRIP);
                    connectRtk();
                } else {
                    mmkvRemoveData(accountData);
                }
            } else {
                mmkvRemoveData(accountData);
            }
            //  connectRtk();
        });
        binding.tvConnect.setOnClickListener(view -> {//连接rtk
//            mmkvAddData(true);
//            NetRtkUtils.getInstance().setNtripInfo(accountData);
//            NetRtkUtils.getInstance().connectNtripRtk(binding.etAccountName.getText().toString(), binding.etPassword.getText().toString());
//            dismiss();
            //  NetRtkUtils.getInstance().setDialogAction(4);
            if (judgeSave()) {
                return;
            }
            if (GnssManager.getInstance().getNtripManager().getNtripConnectState() == NtripManager.NtripState.LINK_TO_NODE_SUCCESS) {
                //如果有其他连接
                queue.offer(LEAVE_NTRIP_ANDJOIN);
            }
            cnnQueue.offer(NTRIP_CNN);
            connectRtk();
        });
        binding.tvConfirm.setOnClickListener(view -> {//连接or断开 rtk

            FJXLog.INSTANCE.e(TAG, "" + binding.tvConfirm.getText());
            FJXLog.INSTANCE.e(TAG, "getString(R.string.ntrip_connect)" + getString(R.string.ntrip_connect));
            if (binding.tvConfirm.getText().toString().equals(getString(R.string.ntrip_connect))) {
                if (judgeSave()) {
                    return;
                }
                //再连接上rtk
                if (GnssManager.getInstance().getNtripManager().getNtripConnectState() == NtripManager.NtripState.LINK_TO_NODE_SUCCESS) {
                    //如果有其他连接
                    queue.offer(LEAVE_NTRIP_ANDJOIN);
                    //queue.offer(JOIN_NTRIP);
                } else {
                    //如果没有其他连接
                    //queue.offer(JOIN_NTRIP);
                }
                cnnQueue.offer(NTRIP_CNN);
                connectRtk();
            } else {
                //  NetRtkUtils.getInstance().setDialogAction(5);
                disConnect();
            }
        });
        binding.ivPassword.setOnClickListener(view -> {
            if (isShowPassword) {
                isShowPassword = false;
                binding.ivPassword.setImageResource(R.mipmap.ic_pwd_show);
                binding.etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                isShowPassword = true;
                binding.ivPassword.setImageResource(R.mipmap.ic_pwd_hide);
                binding.etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });
        binding.etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (TextUtils.isEmpty(binding.etPassword.getText().toString())) {
                    binding.ivPassword.setVisibility(View.GONE);
                } else {
                    binding.ivPassword.setVisibility(View.VISIBLE);
                }
            }
        });
        //源列表
        binding.tvSourceList.setOnClickListener(v -> {
            NetRtkUtils.getInstance().getSource(getContext(), binding.etAddress.getText().toString(), binding.etPort.getText().toString());
        });
        if (BuildConfig.DEBUG && !isDetail) {
            binding.etAddress.setText("lbs.fjdac.com");
            binding.etPort.setText("60127");
            binding.etAccountName.setText("fjlbs00000020");
            binding.etPassword.setText("7hr842");
            binding.etCompanyName.setText("fj");
        }
    }

    /**
     * 校验
     *
     * @return
     */
    private boolean judgeSave() {
        if (TextUtils.isEmpty(binding.etCompanyName.getText().toString())) {
            MyToast.error(getString(R.string.text_input_company_name));
            //getString(R.string.text_input_company_name)
            return true;
        }
        if (TextUtils.isEmpty(binding.etAccountName.getText().toString())) {
            //  MyToast.error(getString(R.string.text_input_account_name));
            MyToast.error(getString(R.string.text_input_ntrip_name));
            return true;
        }
        if (TextUtils.isEmpty(binding.etPassword.getText().toString())) {
            //   MyToast.error(getString(R.string.text_input_password));
            MyToast.error(getString(R.string.text_input_ntrip_pwd));
            return true;
        }
        if (TextUtils.isEmpty(sourceCode)) {
            MyToast.error(getString(R.string.error_source_node_is_empty));
            return true;
        }
        if (TextUtils.isEmpty(binding.etAddress.getText().toString()) || TextUtils.isEmpty(binding.etPort.getText().toString())) {
            MyToast.error(getString(R.string.ntrip_host_port_empty));
            return true;
        }
        return false;
    }

    /**
     * 断开连接
     */
    private void disConnect() {
        List<NtripInfo> list = getDataList();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                list.get(i).setSelected(false);
                list.get(i).setConnected(false);
            }
        }
        GnssManager.getInstance().getRTKCommunicationCallBack().saveNtripList(list);
        NetRtkUtils.getInstance().disconnect();
        adapter.refresh(list);
        dismiss();
    }

    /**
     * 数据准备
     */
    private void initData() {
        if (isDetail) {
            binding.tvTitle.setText(R.string.text_ntrip_detail);
            binding.tvCancel.setVisibility(View.GONE);
            binding.ivBack.setVisibility(View.VISIBLE);
            binding.tvConnect.setVisibility(View.GONE);
            binding.layoutDetail.setVisibility(View.VISIBLE);
            if (isConnect) {
                binding.tvConfirm.setText(R.string.ntrip_disconnect);
            }
            if (accountData != null) {
                binding.etCompanyName.setText(accountData.getCompanyName());
                binding.etAddress.setText(accountData.getIpAddress());
                binding.etPort.setText("" + accountData.getPort());
                //   binding.etSourceNode.setText(accountData.getSourceCode());
                if (!TextUtils.isEmpty(accountData.getSourcePoint())) {
                    mItems.clear();
                    mItems.add(accountData.getSourcePoint());
                }
                binding.etAccountName.setText(accountData.getUsername());
                binding.etPassword.setText(accountData.getPassword());
                binding.ivPassword.setVisibility(TextUtils.isEmpty(accountData.getPassword()) ? View.GONE : View.VISIBLE);
            }
        }

    }

    /**
     * 删除mmkv中某列的数据
     *
     * @param ntripInfo
     */
    private void mmkvRemoveData(NtripInfo ntripInfo) {
        List<NtripInfo> list = getDataList();
        assert list != null;
        int position = list.size();
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                if (isEquals(ntripInfo, list.get(i))) {
                    position = i;
                }
            }
            //position为0 会触发异常
            list.remove(position);
        }
        GnssManager.getInstance().getRTKCommunicationCallBack().deleteNtripInfo(ntripInfo);
        adapter.refresh(list);
        dismiss();
    }

    /**
     * 用来判断两个ntrip账号是否相同
     *
     * @param data1
     * @param data2
     * @return
     */
    private boolean isEquals(NtripInfo data1, NtripInfo data2) {
        FJXLog.INSTANCE.e("data1", "" + data1.eqHashCode());
        FJXLog.INSTANCE.e("data2", "" + data2.eqHashCode());
        return data1.eqHashCode().equals(data2.eqHashCode());
    }

    /**
     * 获取app接口的ntriplist
     *
     * @return
     */
    @Nullable
    private List<NtripInfo> getDataList() {
        List<NtripInfo> list = GnssManager.getInstance().getRTKCommunicationCallBack().getNtripList();
//        String net_rtk = mmkv.decodeString(CacheKeys.MMKV_KV_NET_RTK);
//        if (!TextUtils.isEmpty(net_rtk)) {
//            list = new Gson().fromJson(net_rtk, new TypeToken<List<AccountData>>() {}.getType());
//        }
        return list;
    }

    /**
     * 数据保存
     *
     * @param isConnect
     */
    private void mmkvAddData(boolean isConnect) {
        List<NtripInfo> list = getDataList();
        NtripInfo ntripInfo = new NtripInfo();
        ntripInfo.setCompanyName(binding.etCompanyName.getText().toString());
        ntripInfo.setIpAddress(binding.etAddress.getText().toString());
        ntripInfo.setUsername(binding.etAccountName.getText().toString());
        ntripInfo.setPassword(binding.etPassword.getText().toString());
        ntripInfo.setPort(Integer.parseInt(binding.etPort.getText().toString()));
        ntripInfo.setSourcePoint(sourceCode);

        if (isConnect) {
            ntripInfo.setConnected(true);
            ntripInfo.setSelected(true);
        } else {
            ntripInfo.setConnected(false);
            ntripInfo.setSelected(false);
        }
        assert list != null;
        int position = list.size();
        if (isDetail) {
            if (list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    list.get(i).setSelected(false);
                    list.get(i).setConnected(false);
                    if (isEquals(accountData, list.get(i))) {
                        position = i;
                        FJXLog.INSTANCE.e("mmkvAddData", "详情position:" + position);
                    }
                }
                if (list.size() != position) {
                    list.remove(position);
                }
            }
        } else {
            if (list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    list.get(i).setSelected(false);
                    list.get(i).setConnected(false);
                    if (isEquals(ntripInfo, list.get(i))) {
                        position = i;
                        FJXLog.INSTANCE.e("mmkvAddData", "新增position:" + position);
                    }
                }
                //下标不是数组长度，才去移除，如果是下标，就不给移除
                if (list.size() != position) {
                    list.remove(position);
                }
            }
        }
        list.add(position, ntripInfo);
        FJXLog.INSTANCE.e("mmkvAddData", "position:" + position);
        FJXLog.INSTANCE.e("mmkvAddData", "ntripInfo:" + ntripInfo);
//        mmkv.remove(CacheKeys.MMKV_KV_NET_RTK);
//        mmkv.encode(CacheKeys.MMKV_KV_NET_RTK, new Gson().toJson(list));
        GnssManager.getInstance().getRTKCommunicationCallBack().addNtripInfo(ntripInfo);

        adapter.refresh(list);
    }

    private void setWindowParams() {
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            int uiOptions = 0x00400000 | 0x00200000 | 0x01000000 | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            window.getDecorView().setSystemUiVisibility(uiOptions);
            WindowManager.LayoutParams params = window.getAttributes();
            if (params != null) {
                params.width = WindowManager.LayoutParams.MATCH_PARENT;
                params.height = WindowManager.LayoutParams.MATCH_PARENT;
                window.setAttributes(params);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        isactive = true;
        setWindowParams();
    }

    private void fullscreen() {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            DisplayUtils.fullScreen(activity);
        }
    }

    @Override
    public void onPause() {
        Log.e(TAG, "onStop:");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.e(TAG, "onStop:");
        isactive = false;
        super.onStop();
        fullscreen();
    }

    /**
     * 链接rtk
     * 里面有两个逻辑，
     * 如果已经连了rtk，那么要先断开再连接，如果没有连rtk，直接连接rtk
     */
    private void connectRtk() {
        //如果是详情进来 那么点击保存
        if (isDetail) {
            NetRtkUtils.getInstance().setNtripInfo(accountData);
        }
        NetRtkUtils.getInstance().connectNtripRtk(binding.etAccountName.getText().toString(), binding.etPassword.getText().toString());
    }

    /**
     * 内部类 监控数据长度
     */
    class AccountEditTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if (TextUtils.isEmpty(s)) {
                mEditTextHaveInputCount++;
                if (mEditTextHaveInputCount == EDITTEXT_AMOUNT) {
                    binding.tvConnect.setEnabled(true);
                }
            }
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (TextUtils.isEmpty(s)) {
                mEditTextHaveInputCount--;
                binding.tvConnect.setEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    @Override
    public void dismiss() {
        super.dismissAllowingStateLoss();
        dialog = null;
        if (subscribe != null) {
            subscribe.dispose();
        }
        FJXLog.INSTANCE.e(TAG, "dismiss");
        FJXLog.INSTANCE.e(TAG, "dismiss" + dialog);
    }
}
