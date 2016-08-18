package com.jamoogy.popular_movies;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Provides all functionality for the DetailActivity.  Handles the UI for showing the detail info
 * for a movie clicked on in the main movie grid.
 */
public class DetailFragment extends Fragment {

    public DetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        // Get the intent that was passed from the initiating activity
        Intent detailIntent = getActivity().getIntent();
        if (detailIntent != null && detailIntent.hasExtra(getString(R.string.EXTRA_MOVIE))) {
            // Get the clicked Movie object that was added as an extra to the intent
            Movie detailMovie = (Movie)detailIntent.getSerializableExtra(getString(R.string.EXTRA_MOVIE));
            Resources resources = getResources();

            // Set the poster image
            ImageView detailPoster = (ImageView) rootView.findViewById(R.id.detail_poster);
            Picasso.with(getContext()).load(detailMovie.poster_reference)
                    .into(detailPoster);

            // Set the title
            TextView detailTitle = (TextView) rootView.findViewById(R.id.detail_title);
            detailTitle.setText(detailMovie.title);

            // Set the release date
            TextView detailRelease = (TextView) rootView.findViewById(R.id.detail_release);
            String releaseDateDisplay = resources.getString(R.string.release_date_display, detailMovie.releaseDate);
            detailRelease.setText(releaseDateDisplay);

            // Set the user rating
            TextView detailRating = (TextView) rootView.findViewById(R.id.detail_rating);
            String ratingDisplay = resources.getString(R.string.rating_display, Double.toString(detailMovie.rating));
            detailRating.setText(ratingDisplay);

            // Set the overview
            TextView detailOverview = (TextView) rootView.findViewById(R.id.detail_synopsis);
            detailOverview.setText(detailMovie.synopsis);

            // Set the backdrop
            ImageView detailBackdrop = (ImageView) rootView.findViewById(R.id.detail_backdrop);
            Picasso.with(getContext()).load(detailMovie.backdrop)
                    .into(detailBackdrop);
        }
        return rootView;
    }
}
