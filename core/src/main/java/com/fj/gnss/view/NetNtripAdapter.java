package com.fj.gnss.view;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fj.gnss.R;
import com.fj.gnss.fragment.RtkNtripDetailDialog;
import com.fj.gnss.ntrip.bean.NtripInfo;
import com.fj.gnss.utils.ViewClickUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * ntrip适配器
 */
public class NetNtripAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_GROUP = 0;
    public static final int TYPE_ITEM = 1;
    private final List<NtripInfo> showList;
    private final FragmentManager fragmentManager;
    private final OnItemClickListener clickListener;

    /**
     * 实例化
     * @param showList ntrip列表
     * @param fragmentManager fragmentmanager
     * @param clickListener 点击监听
     */
    public NetNtripAdapter(List<NtripInfo> showList, FragmentManager fragmentManager, OnItemClickListener clickListener) {
        this.showList = showList;
        this.fragmentManager = fragmentManager;
        this.clickListener = clickListener;
    }

    /**
     * 将保存的数据反序列化，并绘制
     * 第一位是填加按钮 对象
     * @param datas ntrip列表
     */
    public void refresh(List<NtripInfo> datas) {
        showList.clear();
        Log.e("TAG", "refresh2: " + datas.size());
        showList.add(0, new NtripInfo());
        showList.addAll(datas);
        notifyDataSetChanged();
    }

    /**
     * 获取实际的列表
     * @return
     */
    public List<NtripInfo> getRealList() {
        List<NtripInfo> realList = new ArrayList<>();
        for (int i = 0; i < showList.size(); i++) {
            if (i != 0) {
                realList.add(showList.get(i));
            }
        }
        return realList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        RecyclerView.ViewHolder holder;
        if (viewType == TYPE_GROUP) {
            View view = inflater.inflate(R.layout.layout_item_ntrip_add, parent, false);
            holder = new NetAddViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.layout_item_ntrip_list, parent, false);
            holder = new NetDataItemViewHolder(view);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof NetAddViewHolder) {
            NetAddViewHolder dvh = (NetAddViewHolder) holder;
            dvh.tvAddAccount.setOnClickListener(view -> {
                RtkNtripDetailDialog.show(fragmentManager, false, false, new NtripInfo(), this);
            });
        } else if (holder instanceof NetDataItemViewHolder) {
            NtripInfo accountData = showList.get(position);
            NetDataItemViewHolder dih = (NetDataItemViewHolder) holder;
            dih.bind(position, accountData);
            dih.layoutAccount.setOnClickListener(view -> {
                if (ViewClickUtil.isFastClick()){
                    return;
                }
                if (accountData.isSelected()) {
                    RtkNtripDetailDialog.show(fragmentManager, true, accountData.isConnected(), accountData, this);
                }
                for (NtripInfo data : showList) {
                    data.setSelected(false);
                }
                accountData.setSelected(true);
                notifyDataSetChanged();
            });
            dih.tvConnect.setOnClickListener(view -> {
                if (!ViewClickUtil.isFastClick()){
                    clickListener.onClick(showList, position, view);
                }

            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_GROUP;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        if (showList == null || showList.size() == 0) {
            return 1;
        }
        return showList.size();
    }

    public interface OnItemClickListener {
        void onClick(List<NtripInfo> data, int position, View view);
    }

    public static class NetAddViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvAddAccount;

        public NetAddViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnTouchListener(null);
            itemView.setClickable(false);
            tvAddAccount = itemView.findViewById(R.id.tv_add_account);
        }
    }

    public static class NetDataItemViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvName;
        private final TextView tvTitle;
        private final TextView tvConnect;
        private final ImageView ivSelect;
        private final RelativeLayout layoutAccount;

        public NetDataItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvConnect = itemView.findViewById(R.id.tv_connect);
            ivSelect = itemView.findViewById(R.id.iv_select);
            layoutAccount = itemView.findViewById(R.id.layout_account);
        }

        public void bind(int position, NtripInfo accountData) {
            tvName.setText(accountData.getUsername());
            tvTitle.setText(accountData.getCompanyName());
            boolean isSelect = accountData.isSelected();
            boolean isConnect = accountData.isConnected();
            if (isSelect) {
                ivSelect.setVisibility(View.VISIBLE);
            } else {
                ivSelect.setVisibility(View.GONE);
            }
            if (isConnect) {
                tvConnect.setText(R.string.ntrip_disconnect);
            } else {
                tvConnect.setText(R.string.ntrip_connect);
            }
            layoutAccount.setSelected(accountData.isSelected());

        }
    }
}
