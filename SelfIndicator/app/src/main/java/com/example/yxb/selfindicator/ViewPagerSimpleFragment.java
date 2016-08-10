package com.example.yxb.selfindicator;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * PACKAGE_NAME:com.example.yxb.selfindicator
 * FUNCTIONAL_DESCRIPTION
 * CREATE_BY:xiaobo
 * CREATE_TIME:2016/7/16
 * MODIFY_BY:
 */
public class ViewPagerSimpleFragment extends Fragment{
    private String mTittle;
    public static final String BUNDLE_TITTLE = "tittle";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null){
            mTittle = bundle.getString(BUNDLE_TITTLE);
        }
        TextView textView = new TextView(getActivity());
        textView.setText(mTittle);
        textView.setGravity(Gravity.CENTER);
        return textView;
    }

    public static ViewPagerSimpleFragment newInstance(String tittle) {

        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_TITTLE, tittle);

        ViewPagerSimpleFragment fragment = new ViewPagerSimpleFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


}
