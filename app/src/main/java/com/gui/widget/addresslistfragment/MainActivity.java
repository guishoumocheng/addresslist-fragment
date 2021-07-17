package com.gui.widget.addresslistfragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.gui.widget.addresslistfragment.fragment.AddressListFragment;
import com.gui.widget.addresslistfragment.fragment.ConferencePrepareFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.vp_fragment_pager)
    ViewPager vpFragmentContainer;

    List<Fragment> fragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        fragments.add(ConferencePrepareFragment.newInstance());
        //初始化通讯录
        fragments.add(AddressListFragment.newInstance().setCallback(new AddressListFragment.Callback() {

        }).setSelectMode(false));
        PagerAdapter pagerAdapter = new ChatPagerAdapter(getSupportFragmentManager());
        vpFragmentContainer.setAdapter(pagerAdapter);
    }

    class ChatPagerAdapter extends FragmentPagerAdapter {

        ChatPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "";
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }
}
