package com.example.a70640.firebase_example.Adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 70640 on 2018/1/28.
 */

public class SamplePagerAdapter extends PagerAdapter {

    private String[] titles;
    private List<View> viewList = new ArrayList<View>();

    public SamplePagerAdapter(String[] titles, List<View> viewList){
        this.titles = titles;
        this.viewList = viewList;
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return o == view;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (viewList.get(position).getParent() != null)
            container.removeView(viewList.get(position));
        container.addView(viewList.get(position));
        return viewList.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
