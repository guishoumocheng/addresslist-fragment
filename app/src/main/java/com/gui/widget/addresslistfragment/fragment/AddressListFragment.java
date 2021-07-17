package com.gui.widget.addresslistfragment.fragment;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gui.widget.addresslistfragment.R;
import com.gui.widget.addresslistfragment.model.ChatAddressGroupModel;
import com.gui.widget.addresslistfragment.model.ChatAddressUserModel;
import com.gui.widget.addresslistfragment.model.TreeNode;
import com.gui.widget.addresslistfragment.util.Constants;
import com.gui.widget.addresslistfragment.util.ScreenUtils;
import com.gui.widget.addresslistfragment.view.LastItemDecoration;
import com.gui.widget.addresslistfragment.view.SmoothCheckBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * isSelectMode 是否为选中模式，为true显示checkbox
 * callback，操作事件回调，点击、选中
 * todo 搜索框、选中人员显示
 */
public class AddressListFragment extends DialogFragment {
    private static final String TAG = "AddressListFragment";

    boolean isSelectMode = false;
    Callback callback;

    public static AddressListFragment newInstance() {
        return new AddressListFragment();
    }

    public AddressListFragment() {
    }

    public AddressListFragment setCallback(Callback callback) {
        this.callback = callback;
        return this;
    }

    public AddressListFragment setSelectMode(boolean isSelectMode) {
        this.isSelectMode = isSelectMode;
        return this;
    }

    public static abstract class Callback {

        public void userClick(ChatAddressUserModel userModel) {
        }

        public void groupClick(ChatAddressGroupModel userModel) {
        }

        public void selectedChanged(ChatAddressUserModel userModel, boolean selected) {
        }

        public void selectDone() {
        }

        public void selectUserMap(Map<String, ChatAddressUserModel> selectedUserMap) {

        }
    }

    @BindView(R.id.rv_address_list)
    RecyclerView rvAddressList;

    @BindView(R.id.rv_group_path)
    RecyclerView rvGroupPath;

    @BindView(R.id.ll_select_bar)
    LinearLayout llSelectBar;


    List<TreeNode> addressList = new ArrayList<>();
    //树根节点
    ChatAddressGroupModel topGroup;
    AddressListAdapter addressListAdapter;

    GroupPathAdapter groupPathAdapter;
    Dialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.d(TAG, "onCreateDialog");
        if (dialog == null) {
            dialog = new Dialog(getActivity(), getTheme());
        }
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.chat_address_fragment, container, false);
        ButterKnife.bind(this, view);
        initGroupPath();
        initAddressList();
        if (topGroup == null) {
            topGroup = new ChatAddressGroupModel();
            topGroup.setGroupName("总部");
            List<ChatAddressGroupModel> childGroup = new ArrayList<>();
            ChatAddressGroupModel groupModel = new ChatAddressGroupModel();
            groupModel.setGroupName("分部");
            childGroup.add(groupModel);

            List<ChatAddressUserModel> childUser = new ArrayList<>();
            ChatAddressUserModel userModel = new ChatAddressUserModel();
            userModel.setUserName("小张");
            childUser.add(userModel);
            topGroup.setChildGroup(childGroup);
            topGroup.setChildUser(childUser);
            groupPathAdapter.pushPath(topGroup);
            showSelectedGroupChildList();
        }
        if (isSelectMode) {
            llSelectBar.setVisibility(View.VISIBLE);
        }
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");
        if (dialog != null) {
            Window window = dialog.getWindow();
            window.setGravity(Gravity.BOTTOM);
            window.getDecorView().setPadding(0, 0, 0, 0);
            window.setWindowAnimations(R.style.mypopwindow_anim_style);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            int screenHeight = ScreenUtils.getScreenHeight(getContext());
            lp.height = screenHeight * 2 / 3;
            window.setAttributes(lp);

            Context context = dialog.getContext();
            int divierId = context.getResources().getIdentifier("android:id/titleDivider", null, null);
            View divider = dialog.findViewById(divierId);
            if (divider != null) {
                divider.setBackgroundColor(Color.TRANSPARENT);
            }
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (callback != null) {
            callback.selectDone();
        }
        Log.d(TAG, "onDismiss");
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        Log.d(TAG, "onCancel");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach");
    }

    @OnClick({R.id.tv_select_done})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_select_done:
                dismiss();
                break;
            default:
        }
    }

    private void initAddressList() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvAddressList.setLayoutManager(linearLayoutManager);
        if (addressListAdapter == null) {
            addressListAdapter = new AddressListAdapter();
            if (callback != null) {
                callback.selectUserMap(addressListAdapter.selectedUserMap);
            }
        }
        rvAddressList.setAdapter(addressListAdapter);
    }

    private void initGroupPath() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvGroupPath.setLayoutManager(linearLayoutManager);
        rvGroupPath.addItemDecoration(new LastItemDecoration(getContext(), DividerItemDecoration.HORIZONTAL));
        if (groupPathAdapter == null) {
            groupPathAdapter = new GroupPathAdapter();
        }
        rvGroupPath.setAdapter(groupPathAdapter);
    }

    /**
     * 显示当前选中的组成员
     */
    private void showSelectedGroupChildList() {
        //选中栈顶组，显示子组、成员
        ChatAddressGroupModel groupModel = groupPathAdapter.getSelectdModel();
        addressList.clear();
        if (groupModel.getChildGroup() != null) {
            addressList.addAll(groupModel.getChildGroup());
        }
        List<ChatAddressUserModel> users = groupModel.getChildUser();
        if (users == null) {
            getChatUserList(groupModel);
        } else {
            addressList.addAll(users);
        }
        addressListAdapter.notifyDataSetChanged();
    }

    /**
     * 获取子成员
     *
     * @param groupModel
     */
    private void getChatUserList(final ChatAddressGroupModel groupModel) {

    }

    class AddressListAdapter extends RecyclerView.Adapter {

        View.OnClickListener groupClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChatAddressGroupModel groupModel = (ChatAddressGroupModel) view.getTag();
                groupPathAdapter.pushPath(groupModel);
                showSelectedGroupChildList();
                if (callback != null) {
                    callback.groupClick(groupModel);
                }
            }
        };

        View.OnClickListener userClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChatAddressUserModel userModel = (ChatAddressUserModel) view.getTag();
                if (callback != null) {
                    callback.userClick(userModel);
                }
                if (isSelectMode) {
                    SmoothCheckBox checkBox = (SmoothCheckBox) view.getTag(R.id.chat_address_user_checkbox_tag);
                    checkBox.toggle();
                }
            }
        };

        SmoothCheckBox.OnCheckedChangeListener checkedChangeListener = new SmoothCheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SmoothCheckBox checkBox, boolean isChecked) {
                ChatAddressUserModel userModel = (ChatAddressUserModel) checkBox.getTag();
                if (isChecked) {
                    selectedUserMap.put(userModel.getUserId(), userModel);
                } else {
                    selectedUserMap.remove(userModel.getUserId());
                }
                if (callback != null) {
                    callback.selectedChanged(userModel, isChecked);
                }
            }
        };

        Map<String, ChatAddressUserModel> selectedUserMap;

        AddressListAdapter() {
            if (isSelectMode) {
                selectedUserMap = new HashMap<>();
            }
        }

        @Override
        public int getItemViewType(int position) {
            TreeNode treeNode = addressList.get(position);
            if (treeNode.getClass().equals(ChatAddressGroupModel.class)) {
                return Constants.VIEW_TYPE_GROUP;
            } else if (treeNode.getClass().equals(ChatAddressUserModel.class)) {
                return Constants.VIEW_TYPE_USER;
            }
            return -1;
        }

        class GroupViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.tv_name)
            TextView tvName;

            GroupViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }

        class UserViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.tv_name)
            TextView tvName;

            @BindView(R.id.tv_status)
            TextView tvStatus;

            @BindView(R.id.sc_check)
            SmoothCheckBox checkBox;

            UserViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case Constants.VIEW_TYPE_GROUP: {
                    GroupViewHolder holder = new GroupViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_address_group_item, parent, false));
                    holder.itemView.setOnClickListener(groupClickListener);
                    return holder;
                }
                case Constants.VIEW_TYPE_USER: {
                    UserViewHolder holder = new UserViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_address_user_item, parent, false));
                    if (isSelectMode) {
                        holder.checkBox.setVisibility(View.VISIBLE);
                        holder.checkBox.setOnCheckedChangeListener(checkedChangeListener);
                        holder.itemView.setTag(R.id.chat_address_user_checkbox_tag, holder.checkBox);
                    } else {
                        holder.checkBox.setVisibility(View.GONE);
                    }
                    holder.itemView.setOnClickListener(userClickListener);
                    return holder;
                }
                default:
            }
            return null;
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
            TreeNode treeNode = addressList.get(position);
            holder.itemView.setTag(treeNode);
            if (holder instanceof GroupViewHolder) {
                ChatAddressGroupModel groupModel = (ChatAddressGroupModel) treeNode;
                ((GroupViewHolder) holder).tvName.setText(groupModel.getGroupName());
            } else if (holder instanceof UserViewHolder) {
                ChatAddressUserModel userModel = (ChatAddressUserModel) treeNode;
                ((UserViewHolder) holder).tvName.setText(userModel.getUserName());
                if (userModel.isLogin()) {
                    ((UserViewHolder) holder).tvStatus.setText("[在线]");
                    ((UserViewHolder) holder).tvStatus.setTextColor(Color.rgb(18, 150, 219));
                } else {
                    ((UserViewHolder) holder).tvStatus.setText("[离线]");
                    ((UserViewHolder) holder).tvStatus.setTextColor(Color.rgb(155, 155, 155));
                }
                if (isSelectMode) {
                    ((UserViewHolder) holder).checkBox.setTag(userModel);
                    //一个人只能选中一次
                    if (selectedUserMap.containsKey(userModel.getUserId())) {
                        ((UserViewHolder) holder).checkBox.setChecked(true);
                    } else {
                        ((UserViewHolder) holder).checkBox.setChecked(false);
                    }
                }
            }
        }

        @Override
        public int getItemCount() {
            return addressList.size();
        }
    }

    class GroupPathAdapter extends RecyclerView.Adapter<GroupPathAdapter.ViewHolder> implements View.OnClickListener {

        List<ChatAddressGroupModel> groupPathList = new ArrayList<>();

        void pushPath(ChatAddressGroupModel model) {
            groupPathList.add(model);
            notifyDataSetChanged();
        }

        void popPath() {
            if (groupPathList.size() > 2) {
                groupPathList.remove(groupPathList.size() - 1);
                notifyDataSetChanged();
            }
        }

        ChatAddressGroupModel getSelectdModel() {
            return groupPathList.get(groupPathList.size() - 1);
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.tv_name)
            TextView name;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }

        @Override
        public void onClick(View view) {
            int position = (int) view.getTag();
            if (position + 1 == groupPathList.size()) {
                return;
            }
            for (int i = groupPathList.size(); i > position + 1; i--) {
                groupPathList.remove(groupPathList.size() - 1);
            }
            notifyDataSetChanged();
            showSelectedGroupChildList();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ViewHolder holder = new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.group_path_item, parent, false));
            holder.itemView.setOnClickListener(this);
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.name.setText(groupPathList.get(position).getGroupName());
            if (position + 1 < groupPathList.size()) {
                holder.name.setTextColor(getResources().getColor(R.color.group_path));
            } else {
                holder.name.setTextColor(getResources().getColor(R.color.group_path_selected));
            }
            holder.itemView.setTag(position);
        }

        @Override
        public int getItemCount() {
            return groupPathList.size();
        }
    }
}
