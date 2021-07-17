package com.gui.widget.addresslistfragment.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.gui.widget.addresslistfragment.R;
import com.gui.widget.addresslistfragment.model.ChatAddressUserModel;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ConferencePrepareFragment extends Fragment {
    private static final String TAG = "ConferencePrepareFragment";

    public static ConferencePrepareFragment newInstance() {
        return new ConferencePrepareFragment();
    }

    public ConferencePrepareFragment() {
    }

    @BindView(R.id.tv_invited)
    TextView tvInvited;

    AddressListFragment addressSelectFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.conference_prepare_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    //todo fragment复用问题
    Map<String, ChatAddressUserModel> selectedUsers = new HashMap<>();

    AddressListFragment.Callback selectCallback = new AddressListFragment.Callback() {
        @Override
        public void selectUserMap(Map<String, ChatAddressUserModel> selectedUserMap) {
            selectedUsers = selectedUserMap;
        }

        @Override
        public void selectDone() {
            showSelectedUsers();
        }
    };

    Runnable showSelectedUsersRunnable = new Runnable() {
        @Override
        public void run() {
            String user = "";
            for (Map.Entry<String, ChatAddressUserModel> entry : selectedUsers.entrySet()) {
                ChatAddressUserModel userModel = entry.getValue();
                user += userModel.getUserName() + ",";
            }
            tvInvited.setText(user.isEmpty() ? "" : user.substring(0, user.length() - 1));
        }
    };

    private void showSelectedUsers() {
        getActivity().runOnUiThread(showSelectedUsersRunnable);
    }

    @OnClick({R.id.btn_begin, R.id.btn_invite})
    public void onClick(View view) {
        long currentTimeMillis = System.currentTimeMillis();
        Object timeStampObj = view.getTag(R.id.btn_click_timestamp);
        if (timeStampObj != null) {
            long btnTimeStamp = (long) timeStampObj;
            if (currentTimeMillis - btnTimeStamp < 600) {
                return;
            }
        }
        view.setTag(R.id.btn_click_timestamp, currentTimeMillis);
        switch (view.getId()) {
            case R.id.btn_begin:
                break;
            case R.id.btn_invite:
                if (addressSelectFragment == null) {
                    addressSelectFragment = AddressListFragment.newInstance()
                            .setSelectMode(true)
                            .setCallback(selectCallback);
                    addressSelectFragment.setCancelable(false);
                }
                addressSelectFragment.show(getFragmentManager(), "");
                break;
            default:
        }
    }

}
