package com.example.yxb.selfindicator;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ViewPager mViewPager;
    private ViewPagerIndicator mIndicator;
    private List<String> titles = Arrays.asList("短信1","收藏2","推荐3","短信4","收藏5","推荐6","短信7","收藏8","推荐9");
    private List<ViewPagerSimpleFragment> mContents = new ArrayList<>();
    private FragmentPagerAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main2);

        initView();
        initDatas();

        mIndicator.setVisableTabCount(4);
        mIndicator.setTabTittles(titles);

        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mContents.get(position);
            }

            @Override
            public int getCount() {
                return mContents.size();
            }
        });

       mIndicator.setViewPager(mViewPager,0);
    }



    private void initDatas() {
        for (String title : titles){
            ViewPagerSimpleFragment fragment = ViewPagerSimpleFragment.newInstance(title);
            mContents.add(fragment);
        }

    }

    private void initView() {
        mViewPager = (ViewPager) findViewById(R.id.mViewPager);
        mIndicator = (ViewPagerIndicator) findViewById(R.id.mIndicator);
    }


}
