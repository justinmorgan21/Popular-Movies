package com.jamoogy.popular_movies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    String sortMethod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sortMethod = "popularity";
    }

    private void updateMovieList() {
        Picasso.with(this).load("http://image.tmdb.org/t/p/w185/9O7gLzmreU0nGkIB6K3BsJbzvNv.jpg")
                .into((ImageView) findViewById(R.id.poster_0));
        Picasso.with(this).load("http://image.tmdb.org/t/p/w185/gj282Pniaa78ZJfbaixyLXnXEDI.jpg")
                .into((ImageView) findViewById(R.id.poster_1));
        Picasso.with(this).load("http://image.tmdb.org/t/p/w185/5N20rQURev5CNDcMjHVUZhpoCNC.jpg")
                .into((ImageView) findViewById(R.id.poster_2));
        Picasso.with(this).load("http://image.tmdb.org/t/p/w185/lFSSLTlFozwpaGlO31OoUeirBgQ.jpg")
                .into((ImageView) findViewById(R.id.poster_3));
        Picasso.with(this).load("http://image.tmdb.org/t/p/w185/gj282Pniaa78ZJfbaixyLXnXEDI.jpg")
                .into((ImageView) findViewById(R.id.poster_4));
        Picasso.with(this).load("http://image.tmdb.org/t/p/w185/5N20rQURev5CNDcMjHVUZhpoCNC.jpg")
                .into((ImageView) findViewById(R.id.poster_5));
        Picasso.with(this).load("http://image.tmdb.org/t/p/w185/lFSSLTlFozwpaGlO31OoUeirBgQ.jpg")
                .into((ImageView) findViewById(R.id.poster_6));
        Picasso.with(this).load("http://image.tmdb.org/t/p/w185/gj282Pniaa78ZJfbaixyLXnXEDI.jpg")
                .into((ImageView) findViewById(R.id.poster_7));
        Picasso.with(this).load("http://image.tmdb.org/t/p/w185/5N20rQURev5CNDcMjHVUZhpoCNC.jpg")
                .into((ImageView) findViewById(R.id.poster_8));
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovieList();
    }
}
