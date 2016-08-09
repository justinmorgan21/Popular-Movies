package com.jamoogy.popular_movies;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by jmorgan on 8/7/2016.
 * Custom adapter used to define how Movie objects will be displayed in a ListView
 */
public class MovieAdapter extends ArrayAdapter<Movie> {
    public MovieAdapter(Activity context, List<Movie> movies) {
        super(context, 0, movies);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Movie movie = getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.grid_item_movie_poster,
                    parent,
                    false);
        }

        ImageView poster = (ImageView) convertView.findViewById(R.id.grid_item_movie_poster_imageview);
        Picasso.with(getContext()).load(movie.poster_reference)
                .into(poster);

        TextView name = (TextView) convertView.findViewById(R.id.grid_item_movie_name_textview);
        name.setText(movie.title);

        return convertView;
    }
}
