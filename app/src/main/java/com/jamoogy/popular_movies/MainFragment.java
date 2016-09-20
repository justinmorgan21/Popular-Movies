package com.jamoogy.popular_movies;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;

import java.util.ArrayList;

/**
 * Provides all functionality for the MainActivity.  Handles UI for sort options and grid view
 * of movies.
 */
public class MainFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    // Custom Adapter object that holds definitions for how to place Movie objects in the grid.
    private MovieAdapter mMovieGridAdapter;
    private int mPosition = GridView.INVALID_POSITION;
    private static final String SELECTED_KEY = "grid_position";
    private GridView mGridView;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        if (mPosition != GridView.INVALID_POSITION) {
            mGridView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment and set the root to the inflated view
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Create an instance of the custom MovieAdapter with the mainActivity context and empty list
        mMovieGridAdapter = new MovieAdapter(getActivity(), new ArrayList<Movie>());

        mGridView = (GridView) rootView.findViewById(R.id.gridview_movies);

        // Set the adapter for the main movie grid
        mGridView.setAdapter(mMovieGridAdapter);

        // Set up the spinner for selecting sort option
        initializeSpinner(rootView);

        // Define the intent to start the DetailActivity when a movie poster is clicked, passing
        // the Movie object represented by the poster as an extra.
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                Movie movieAtPos = mMovieGridAdapter.getItem(pos);
//                FetchTrailersTask trailersTask = new FetchTrailersTask(getContext());
//                trailersTask.execute(movieAtPos);
                Intent detailIntent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra(getString(R.string.EXTRA_MOVIE), (Parcelable)movieAtPos);
                startActivity(detailIntent);
                mPosition = pos;
            }
        });

//        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
//            mPosition = savedInstanceState.getInt(SELECTED_KEY);
//        }
//
//        if (mPosition != GridView.INVALID_POSITION) {
//            mGridView.smoothScrollToPosition(mPosition);
//        }

        return rootView;
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        FetchMoviesTask moviesTask = new FetchMoviesTask(getActivity(), mMovieGridAdapter);
        if (pos == 0) {
            moviesTask.execute(getString(R.string.sort_popular));
        } else if (pos == 1) {
            moviesTask.execute(getString(R.string.sort_rating));
        }
    }

    // Required for OnItemSelectedListener interface
    public void onNothingSelected(AdapterView<?> parent) {}

    @Override
    public void onSaveInstanceState(Bundle out) {
        if (mPosition != GridView.INVALID_POSITION) {
            out.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(out);
    }

    /**
     * Set up the spinner with two options for movie sorting, popular and top_rated
     * @param rootView
     */
    private void initializeSpinner(View rootView) {
        Spinner sortSpinner = (Spinner) rootView.findViewById(R.id.sort_by_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getActivity(), R.array.sort_by_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(adapter);
        sortSpinner.setOnItemSelectedListener(this);
    }
}