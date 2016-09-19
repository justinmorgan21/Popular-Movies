package com.jamoogy.popular_movies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.detail_fragment_container, new DetailFragment())
                    .commit();
        }
    }

//    public void playTrailer(View view) {
//        Button trailer = (Button) findViewById(R.id.trailer_button);
//        trailer.setText("Do it!");
//    }
}
