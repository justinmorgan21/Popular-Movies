package com.jamoogy.popular_movies;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

/**
 * Adds MainFragment for all main functionality.
 */
public class MainActivity extends AppCompatActivity {

    Fragment mContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            mContent = new MainFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, mContent)
                    .commit();
        }
        if (savedInstanceState != null) {
            mContent = getSupportFragmentManager().getFragment(savedInstanceState, "mContent");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        getSupportFragmentManager().putFragment(outState, "mContent", mContent);
        super.onSaveInstanceState(outState);
    }
}
