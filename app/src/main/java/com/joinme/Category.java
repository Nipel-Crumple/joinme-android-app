package com.joinme;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by Johnny D on 11.04.2015.
 */
public class Category extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Typeface font = Typeface.createFromAsset( getAssets(), "fontawesome-webfont.ttf" );
                TextView food = (TextView)findViewById(R.id.food_header);
                TextView sport = (TextView)findViewById(R.id.sport_header);
                TextView teaching = (TextView)findViewById(R.id.teaching_header);
                TextView movie = (TextView)findViewById(R.id.movie_header);
                TextView theater = (TextView)findViewById(R.id.theater_header);
                food.setTypeface(font);
                sport.setTypeface(font);
                teaching.setTypeface(font);
                movie.setTypeface(font);
                theater.setTypeface(font);
            }
        }).start();
    }
}
