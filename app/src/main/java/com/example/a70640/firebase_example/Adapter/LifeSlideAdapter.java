package com.example.a70640.firebase_example.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.a70640.firebase_example.R;

/**
 * Created by min on 2018/3/8.
 */

public class LifeSlideAdapter extends PagerAdapter{
    private Context context;
    private LayoutInflater inflater;

    //list of images
    private int[] lst_image = {
            R.drawable.event1,
            R.drawable.event2,
            R.drawable.event3
    };

    //list of titles
    private String[] lst_title = {
            "AIESEC春季徵才 元智場",
            "AIESEC春季徵才 中原場",
            "期末社大-水漾森林"
    };

    private int[] lst_backgroundcolor = {
            Color.rgb(55,55,55),
            Color.rgb(239,85,85),
            Color.rgb(110,49,89),
    };

    public LifeSlideAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return lst_image.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return (view ==(RelativeLayout)object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.life_slide,container,false);
        RelativeLayout layoutslide = (RelativeLayout) view.findViewById(R.id.linearLayout);
        ImageView imageView= (ImageView) layoutslide.findViewById(R.id.sliding);
        TextView textView = (TextView) layoutslide.findViewById(R.id.title);
        imageView.setImageResource(lst_image[position]);
        textView.setText(lst_title[position]);
//        layoutslide.setBackgroundColor(lst_backgroundcolor[position]);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ((ViewGroup) container).removeView((View) object);
    }
}
