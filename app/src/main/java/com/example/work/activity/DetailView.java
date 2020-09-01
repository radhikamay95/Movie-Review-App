package com.example.work.activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.work.R;


public class DetailView extends AppCompatActivity {


    private static final String BASE_URL_IMG = "https://image.tmdb.org/t/p/w185";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView title;
        TextView release;
        TextView popularity;
        TextView vote;
        TextView desc;

        setContentView(R.layout.activity_detail_view);
        ImageView poster = findViewById(R.id.movie_poster);
        release = findViewById(R.id.release);
        popularity = findViewById(R.id.popularity);
        vote = findViewById(R.id.vote);
        desc = findViewById(R.id.desc);
        title = findViewById(R.id.title);

        String imagePath = getIntent().getStringExtra("poster image");
        String releaseDate = getIntent().getStringExtra("release date");
        double popularity1 = getIntent().getDoubleExtra("popularity", 0.0);
        int vote1 = getIntent().getIntExtra("vote", 0);
        String posterDesc = getIntent().getStringExtra("poster desc");
        String tit = getIntent().getStringExtra("title");

        release.setText(releaseDate);
        popularity.setText(String.valueOf(popularity1));
        vote.setText(String.valueOf(vote1));
        desc.setText(posterDesc);
        title.setText(tit);

        Glide.with(getApplicationContext())
                .load(BASE_URL_IMG + imagePath)


                .diskCacheStrategy(DiskCacheStrategy.ALL)   // cache both original & resized image
                .centerCrop()
                .into(poster);

    }

    @Override
    public void onBackPressed() {
        ActivityCompat.finishAfterTransition(this);
    }
}
