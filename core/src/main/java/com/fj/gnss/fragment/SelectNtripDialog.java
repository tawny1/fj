package com.fj.gnss.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;

import com.fj.construction.tools.utils.DensityUtil;
import com.fj.construction.tools.utils.DisplayUtils;
import com.fj.construction.ui.toast.MyToast;
import com.fj.gnss.GnssManager;
import com.fj.gnss.R;
import com.fj.gnss.databinding.DialogRtkNtripBinding;
import com.fj.gnss.ntrip.NtripManager;
import com.fj.gnss.ntrip.bean.NtripInfo;
import com.fj.gnss.utils.NetRtkUtils;
import com.fj.gnss.utils.SchedulersTransformer;
import com.fj.gnss.view.GridSpacingItemDecoration;
import com.fj.gnss.view.NetNtripAdapter;
import com.fjdynamics.xlog.FJXLog;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import io.reactivex.disposables.Disposable;

public class SelectNtripDialog extends DialogFragment {
    public static final String TAG = "SelectNetAccountDialog";
    private final static Integer LEAVE_NTRIP_ANDJOIN = 0;
    //队列 用来放操作步骤
    private final LinkedBlockingQueue<Integer> levequeue = new LinkedBlockingQueue<>();
    NetNtripAdapter netNtripAdapter;
    private DialogRtkNtripBinding binding;
    private List<NtripInfo> dataList;
    private int position;
    Disposable subscribe;
    /**
     * 连接的监听
     */
    private final NetNtripAdapter.OnItemClickListener itemClickListener = (dataList, position, view) -> {
        this.dataList = dataList;
        this.position = position;
        boolean connect = dataList.get(position).isConnected();
        //如果是连接的
        if (connect) {
            //如果是连接的，那么调用断开连接，等待监听后，改变状态
            NetRtkUtils.getInstance().disconnect();
        } else {
            //本身没有连接
            if (GnssManager.getInstance().getNtripManager().getNtripConnectState() == NtripManager.NtripState.LINK_TO_NODE_SUCCESS) {
                levequeue.offer(LEAVE_NTRIP_ANDJOIN);
            }
            FJXLog.INSTANCE.e(TAG, "itemClickListener" + dataList.get(position));
            NetRtkUtils.getInstance().setNtripInfo(dataList.get(position));

            NetRtkUtils.getInstance().connectNtripRtk(dataList.get(position).getUsername(), dataList.get(position).getPassword());
        }
        //每次点击，都会保存这个数据
        // GnssManager.getInstance().getRTKCommunicationCallBack().saveNtripList(netNtripAdapter.getRealList());
//        if (((TextView) view).getText().toString().equals("连接")) {
//            if (GnssManager.getInstance().getNtripManager().getNtripConnectState() == NtripManager.NtripState.LINK_TO_NODE_SUCCESS) {
//                NetRtkUtils.getInstance().setDialogAction(3);
//            } else {
//                NetRtkUtils.getInstance().setDialogAction(1);
//            }
//        } else {
//            NetRtkUtils.getInstance().setDialogAction(2);
//        }
//        NetRtkUtils.getInstance().connectNtripRtk(dataList.get(position).getUsername(), dataList.get(position).getPassword());
    };

    public static void show(FragmentManager fmg) {
        Log.e(TAG, "show: ");
        SelectNtripDialog dialog = new SelectNtripDialog();
        dialog.show(fmg, TAG);
    }

    @Override
    public void show(@NonNull FragmentManager manager, @Nullable String tag) {
        super.show(manager, tag);
        NetRtkUtils.getInstance().addNtripListener();
    }

    @Override
    public void dismiss() {
        NetRtkUtils.getInstance().removeNtripListener();
        super.dismiss();
        if (subscribe != null) {
            subscribe.dispose();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.ActDialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogRtkNtripBinding.inflate(inflater, container, false);
        initView();
        return binding.getRoot();
    }

    private void initView() {
        setCancelable(false);
        NetRtkUtils.getInstance().initNtripInfo();
        binding.ivClose.setOnClickListener(view -> dismiss());
        binding.rvAccountNet.setLayoutManager(new GridLayoutManager(getContext(), 2));
        binding.rvAccountNet.addItemDecoration(new GridSpacingItemDecoration(2, DensityUtil.dp2Px(getContext(), 15), false));
        netNtripAdapter = new NetNtripAdapter(addData(), getChildFragmentManager(), itemClickListener);
        binding.rvAccountNet.setAdapter(netNtripAdapter);
        rxNtripEnumState();
    }

    /**
     * 观察ntrip的状态 通过rxjava 和队列实现已连接ntrip情况下的切换
     * 主要有 连接，断开两个操作
     * 连接分为已连接下的其他ntrip连接
     */
    private void rxNtripEnumState() {
        subscribe = NetRtkUtils.getInstance().subEnumNtripState()
                .compose(new SchedulersTransformer<NtripManager.NtripState>())
                .subscribe(
                        state -> {
                            FJXLog.INSTANCE.e(TAG, "NtripManager.NtripState:" + state);
                            switch (state) {
                                case DISABLE:
                                    break;
                                case GETTING_SOURCE:
                                    break;
                                case GET_SOURCE_SUCCESS:
                                    //获取源成功
                                    break;
                                case GET_SOURCE_FAILED:
                                    //   MyToast.errorL(R.string.ntrip_get_source_failed);
                                    break;
                                case LINKING_TO_NODE:
                                    //    MyToast.errorL(R.string.ntrip_link_to_node);
                                    break;
                                case LINK_TO_NODE_SUCCESS:
                                    if (dataList != null) {
                                        NtripInfo ntripInfo = GnssManager.getInstance().getNtripManager().getNtripInfo();
                                        FJXLog.INSTANCE.e(TAG, "LINK_TO_NODE_SUCCESS:" + ntripInfo.eqHashCode());
                                        for (NtripInfo data : dataList) {
                                            boolean same = data.eqOtherNtrip(ntripInfo);
                                            if (same) {
                                                FJXLog.INSTANCE.e(TAG, "LINK_TO_NODE_SUCCESS2:" + data.eqHashCode());
                                                data.setConnected(true);
                                                data.setSelected(true);
                                            } else {
                                                data.setConnected(false);
                                                data.setSelected(false);
                                            }

                                        }
//                                        dataList.get(position).setConnect(true);
//                                        dataList.get(position).setSelect(true);
                                        netNtripAdapter.notifyDataSetChanged();
                                    }
                                    MyToast.success(R.string.ntrip_link_to_node_success);
                                    break;
                                case LINK_TO_NODE_FAILED:
                                    if (dataList != null) {
                                        for (NtripInfo data : dataList) {
                                            data.setConnected(false);
                                            data.setSelected(false);
                                        }
                                        netNtripAdapter.notifyDataSetChanged();
                                    }
                                    //  EventBus.getDefault().post(new RtkDialogActionEvent(dialogAction));
                                    MyToast.errorL(R.string.ntrip_link_to_node_disconnected);
                                    //如果是详情
                                    Integer poll = levequeue.poll();
                                    FJXLog.INSTANCE.e("poll", "队列数据是：" + poll);
                                    if (LEAVE_NTRIP_ANDJOIN.equals(poll)) {
                                        // connectRtk();
                                        NtripInfo ntripInfo = dataList.get(position);
                                        FJXLog.INSTANCE.e("poll", "ntripInfo：" + ntripInfo);
                                        if (ntripInfo == null) {
                                            return;
                                        }
                                        NetRtkUtils.getInstance().setNtripInfo(ntripInfo);
                                        NetRtkUtils.getInstance().connectNtripRtk(ntripInfo.getUsername(), ntripInfo.getPassword());
                                    }
                                    break;
                                default:
                                    break;
                            }

                        },
                        throwable -> {
                            FJXLog.INSTANCE.e(TAG, "" + throwable.getMessage());
                            throwable.printStackTrace();
                        }
                );
    }


    private List<NtripInfo> addData() {
        List<NtripInfo> ntripList = GnssManager.getInstance().getRTKCommunicationCallBack().getNtripList();
        return ntripList;
    }

    private void setWindowParams() {
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            int uiOptions = 0x00400000 | 0x00200000 | 0x01000000 | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;

            window.getDecorView().setSystemUiVisibility(uiOptions);
            WindowManager.LayoutParams params = window.getAttributes();
            if (params != null) {
                params.width = DensityUtil.getDisplayWidth(getContext()) - DensityUtil.dp2Px(getContext(), 156);
                params.height = DensityUtil.getDisplayHeight(getContext()) - DensityUtil.dp2Px(getContext(), 134);
                window.setAttributes(params);
            }
            window.getDecorView().setSystemUiVisibility(2);
            window.getDecorView().setOnSystemUiVisibilityChangeListener((visibility) -> {
                window.getDecorView().setSystemUiVisibility(uiOptions);
            });
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
        Log.e(TAG, "onStop: ");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, "onPause: ");
        GnssManager.getInstance().getRTKCommunicationCallBack().saveNtripList(netNtripAdapter.getRealList());
        //在这里，需要做的是进行数据的保存

    }

    @Override
    public void onResume() {
        super.onResume();
        FJXLog.INSTANCE.e(TAG, "onResume");
        netNtripAdapter.refresh(GnssManager.getInstance().getRTKCommunicationCallBack().getNtripList());
    }

}
