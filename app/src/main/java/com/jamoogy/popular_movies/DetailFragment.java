package com.jamoogy.popular_movies;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jamoogy.popular_movies.data.MovieContract;
import com.jamoogy.popular_movies.data.MovieDbHelper;
import com.squareup.picasso.Picasso;

/**
 * Provides all functionality for the DetailActivity.  Handles the UI for showing the detail info
 * for a movie clicked on in the main movie grid.
 */
public class DetailFragment extends Fragment {

    private Movie mDetailMovie;
    private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();
    private Uri mMovieByIdUri;

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
        //if (detailIntent != null && detailIntent.hasExtra(getString(R.string.EXTRA_MOVIE))) {
        String extraTag = getString(R.string.EXTRA_MOVIE);
        if (detailIntent.hasExtra(extraTag)) {
            // Get the clicked Movie object that was added as an extra to the intent
            mDetailMovie = (Movie)detailIntent.getParcelableExtra(getString(R.string.EXTRA_MOVIE));
            Resources resources = getResources();
            mMovieByIdUri = MovieContract.FavoriteEntry.buildFavoriteWithMovieId(mDetailMovie.tmdbId);

            // Set the poster image
            ImageView detailPoster = (ImageView) rootView.findViewById(R.id.detail_poster);
            Picasso.with(getContext()).load(mDetailMovie.poster_url)
                    .into(detailPoster);

            // Set the title
            TextView detailTitle = (TextView) rootView.findViewById(R.id.detail_title);
            detailTitle.setText(mDetailMovie.title);

            // Set the release date
            TextView detailRelease = (TextView) rootView.findViewById(R.id.detail_release);
            String releaseDateDisplay = resources.getString(R.string.release_date_display, mDetailMovie.releaseDate);
            detailRelease.setText(releaseDateDisplay);

            // Set the user rating
            TextView detailRating = (TextView) rootView.findViewById(R.id.detail_rating);
            String ratingDisplay = resources.getString(R.string.rating_display, Double.toString(mDetailMovie.rating));
            detailRating.setText(ratingDisplay);

            // Set the overview
            TextView detailOverview = (TextView) rootView.findViewById(R.id.detail_synopsis);
            detailOverview.setText(mDetailMovie.synopsis);

            // Set the backdrop
//            ImageView detailBackdrop = (ImageView) rootView.findViewById(R.id.detail_backdrop);
//            Picasso.with(getContext()).load(mDetailMovie.backdrop_url)
//                    .into(detailBackdrop);

            Button detailTrailerButton1 = (Button) rootView.findViewById(R.id.detail_trailer_button_1);
            detailTrailerButton1.setOnClickListener(trailerBtnListener);
            Button detailTrailerButton2 = (Button) rootView.findViewById(R.id.detail_trailer_button_2);
            detailTrailerButton2.setOnClickListener(trailerBtnListener);

            String[] reviewsArray = convertStringToArray(mDetailMovie.reviews);
            if (reviewsArray.length > 0) {
                TextView reviewHeaderTextView = (TextView) rootView.findViewById(R.id.detail_review_header);
                reviewHeaderTextView.setText("Reviews");

                String review1 = reviewsArray[0];
                int contentStartIndex = review1.indexOf("content:");
                String review1Author = review1.substring(7, contentStartIndex);
                String review1Content = review1.substring(contentStartIndex + 8);

                TextView review1AuthorTextView = (TextView) rootView.findViewById(R.id.detail_review_1_author);
                review1AuthorTextView.setText(review1Author);
                TextView review1ContentTextView = (TextView) rootView.findViewById(R.id.detail_review_1_content);
                review1ContentTextView.setText(review1Content);

                if (reviewsArray.length > 1) {
                    String review2 = reviewsArray[1];
                    contentStartIndex = review2.indexOf("content:");
                    String review2Author = review2.substring(7, contentStartIndex);
                    String review2Content = review2.substring(contentStartIndex + 8);

                    TextView review2AuthorTextView = (TextView) rootView.findViewById(R.id.detail_review_2_author);
                    review2AuthorTextView.setText(review2Author);
                    TextView review2ContentTextView = (TextView) rootView.findViewById(R.id.detail_review_2_content);
                    review2ContentTextView.setText(review2Content);
                }
            }

            CheckBox favoriteButton = (CheckBox) rootView.findViewById(R.id.detail_favorite_button);
            // query database to look for mDetailMovie by id
            // if found, set favorite button checked to true,
            // else false
            if (movieFound()) {
                favoriteButton.setChecked(true);
            } else {
                favoriteButton.setChecked(false);
            }
            favoriteButton.setOnClickListener(favBtnListener);

        }
        return rootView;
    }

    private View.OnClickListener favBtnListener = new View.OnClickListener() {
        public void onClick(View button) {
            CheckBox favButton = (CheckBox) button;
            if (favButton.isChecked()) {
                favButton.setChecked(true);
                insertMovieToDatabase();
                Log.d(LOG_TAG, "check");
            } else {
                favButton.setChecked(false);
                deleteMovieFromDatabase();
                Log.d(LOG_TAG, "uncheck");
            }
            SQLiteDatabase db = new MovieDbHelper(getContext()).getReadableDatabase();
            long numRows = DatabaseUtils.queryNumEntries(db, MovieContract.FavoriteEntry.TABLE_NAME);
            Log.d(LOG_TAG, "Number of database rows: " + numRows);
        }
    };

    // query database to look for mDetailMovie
    private boolean movieFound() {
        Cursor cursor = getActivity().getContentResolver().query(
                mMovieByIdUri,
                new String[] {MovieContract.FavoriteEntry._ID},
                null,
                null,
                null);
        return cursor.moveToFirst();
    }

    private void insertMovieToDatabase() {
        ContentValues values = new ContentValues();
        values.put(MovieContract.FavoriteEntry.COLUMN_TITLE, mDetailMovie.title);
        values.put(MovieContract.FavoriteEntry.COLUMN_POSTER_URL, mDetailMovie.poster_url);
        values.put(MovieContract.FavoriteEntry.COLUMN_SYNOPSIS, mDetailMovie.synopsis);
        values.put(MovieContract.FavoriteEntry.COLUMN_RATING, mDetailMovie.rating);
        values.put(MovieContract.FavoriteEntry.COLUMN_RELEASE_DATE, mDetailMovie.releaseDate);
        values.put(MovieContract.FavoriteEntry.COLUMN_BACKDROP_URL, mDetailMovie.backdrop_url);
        values.put(MovieContract.FavoriteEntry.COLUMN_TMDB_MOVIE_ID, mDetailMovie.tmdbId);
        values.put(MovieContract.FavoriteEntry.COLUMN_TRAILER_URLS, mDetailMovie.trailerUrls);
        values.put(MovieContract.FavoriteEntry.COLUMN_REVIEWS, mDetailMovie.reviews);

        Uri uri = getActivity().getContentResolver().insert(MovieContract.FavoriteEntry.CONTENT_URI, values);
        Log.d(LOG_TAG, "Uri inserted: " + uri.toString());
    }

    private void deleteMovieFromDatabase() {
        int numDeleted = getActivity().getContentResolver().delete(
                MovieContract.FavoriteEntry.CONTENT_URI,
                MovieContract.FavoriteEntry.COLUMN_TMDB_MOVIE_ID + " = " + mDetailMovie.tmdbId,
                null);
        Log.d(LOG_TAG, "rows deleted: " + numDeleted);
    }

    private View.OnClickListener trailerBtnListener = new View.OnClickListener() {
        public void onClick(View button) {
            String url = null;
            String trailersArray[] = convertStringToArray(mDetailMovie.trailerUrls);
            switch(button.getId()) {
                case(R.id.detail_trailer_button_1):
                    url = trailersArray[0];
                    break;
                case(R.id.detail_trailer_button_2):
                    if (trailersArray.length > 1) {
                        url = trailersArray[1];
                    } else {
                        Toast.makeText(
                                getContext(),
                                "No Trailer",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                    break;
            }
            if (url != null) {
                Intent trailerIntent = new Intent(Intent.ACTION_VIEW);
                trailerIntent.setData(Uri.parse(url));
                startActivity(trailerIntent);
            }
        }
    };

    private String[] convertStringToArray(String str) {
        if (str.length() == 0) { return new String[0]; }
        String[] result = str.split(Utility.strSeparator);
        return result;
    }
}
