package com.joinme;

import android.app.Activity;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Johnny D on 11.04.2015.
 */
public class Category extends Activity{

    private String[] mCategoryTitles;
    private int[] mCategoryBackgrounds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category);

        mCategoryTitles = getResources().getStringArray(R.array.category_array);
        mCategoryBackgrounds = getResources().getIntArray(R.array.category_color);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.categories);
        LayoutInflater inflater = LayoutInflater.from(this);
        Typeface font = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");
        for (int i = 0; i < mCategoryTitles.length; i++) {
            CardView view = (CardView) inflater.inflate(R.layout.category_list_item,
                    linearLayout, false);
            TextView text = (TextView) view.findViewById(R.id.category_title);
            text.setText(mCategoryTitles[i]);
            text.setTypeface(font);
            view.setCardBackgroundColor(mCategoryBackgrounds[i]);
            linearLayout.addView(view);
        }
    }
}
