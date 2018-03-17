package com.example.a70640.firebase_example;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.a70640.firebase_example.Adapter.LifeSlideAdapter;

/**
 * Created by min on 2018/3/9.
 */

public class LifeActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();
    private ViewPager slideViewPager;
    private LifeSlideAdapter slideAdapter;
    private LinearLayout mDotsLayout;

    private TextView[] mDots;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_life);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setDisplayShowTitleEnabled(false);
//        }

        slideViewPager = (ViewPager) findViewById(R.id.viewpaper);
        mDotsLayout = (LinearLayout) findViewById(R.id.dotslayout);
        slideAdapter = new LifeSlideAdapter(this);
        slideViewPager.setAdapter(slideAdapter);

        addDotsIndicator(0);

        slideViewPager.addOnPageChangeListener(viewListener);
    }

    private void addDotsIndicator(int position){
        mDots = new TextView[3];
        mDotsLayout.removeAllViews();

        for (int i =0;i<mDots.length;i++){
            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226;"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(getResources().getColor(R.color.bg_circle_default));

            mDotsLayout.addView(mDots[i]);
        }

        if(mDots.length > 0){
            mDots[position].setTextColor(getResources().getColor(R.color.colorWhite));
        }
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDotsIndicator(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
