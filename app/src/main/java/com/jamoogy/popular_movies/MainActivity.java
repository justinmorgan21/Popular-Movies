package com.jamoogy.popular_movies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Adds MainFragment for all main functionality.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, new MainFragment())
                    .commit();
        }
    }
}
