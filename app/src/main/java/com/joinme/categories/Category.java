package com.joinme.categories;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.joinme.R;
import com.joinme.events.Events;

/**
 * Created by Johnny D on 11.04.2015.
 */
public class Category extends Activity {

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
            text.setClickable(true);
            text.setText(mCategoryTitles[i]);
            text.setId(i);
            text.setTypeface(font);
            text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i;
                    switch(v.getId()) {
                        case 0:
                            i = new Intent(getApplicationContext(), Events.class);
                            i.putExtra("category", "food");
                            startActivity(i);
                            break;
                        case 1:
                            i = new Intent(getApplicationContext(), Events.class);
                            i.putExtra("category", "sport");
                            startActivity(i);
                            break;
                        case 2:
                            i = new Intent(getApplicationContext(), Events.class);
                            i.putExtra("category", "teaching");
                            startActivity(i);
                            break;
                        case 3:
                            i = new Intent(getApplicationContext(), Events.class);
                            i.putExtra("category", "movie");
                            startActivity(i);
                            break;

                        case 4:
                            i = new Intent(getApplicationContext(), Events.class);
                            i.putExtra("category", "theater");
                            startActivity(i);
                            break;
                    }
                }
            });
            view.setCardBackgroundColor(mCategoryBackgrounds[i]);
            linearLayout.addView(view);
        }
    }
}
