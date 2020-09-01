package com.example.work.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.work.R;
import com.example.work.activity.DetailView;
import com.example.work.model.Result;

import java.util.ArrayList;
import java.util.List;


public class PaginationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private static final String BASE_URL_IMG = "https://image.tmdb.org/t/p/w185";

    private final List<Result> movieResult;
    private final Context context;

    private boolean isLoadingAdded = false;

    public PaginationAdapter(Context context) {
        this.context = context;
        movieResult = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;

     LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                viewHolder = getViewHolder(parent, layoutInflater);
                break;
            case LOADING:
                View v2 = layoutInflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new LoadingVH(v2);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + viewType);
        }
        return viewHolder;
    }

    @NonNull
    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        RecyclerView.ViewHolder viewHolder;
        View v1 = inflater.inflate(R.layout.item_list, parent, false);
        viewHolder = new MovieViewHolder(v1);
        return viewHolder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final Result result = movieResult.get(position); // Movie

        switch (getItemViewType(position)) {
            case ITEM:
                final MovieViewHolder movieVH = (MovieViewHolder) holder;
                //animation
                movieVH.frameLayout.setAnimation(AnimationUtils.loadAnimation(context,R.anim.recyler_animation));
                movieVH.mMovieTitle.setText(result.getTitle());


                movieVH.mYear.setText(
                        result.getReleaseDate().substring(0, 4)  //  want the year only
                                + " | "
                                + result.getOriginalLanguage().toUpperCase()
                );
                movieVH.mMovieDesc.setText(result.getOverview());

                /*
                  Using Glide to handle image loading.
                  Learn more about Glide here:
                  <a href="http://blog.grafixartist.com/image-gallery-app-android-studio-1-4-glide/" />
                 */
                Glide.with(context)
                        .load(BASE_URL_IMG + result.getPosterPath())


                        .diskCacheStrategy(DiskCacheStrategy.ALL)   // cache both original & resized image
                        .centerCrop()
                        .into(movieVH.mPosterImg);
                movieVH.mPosterImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(context, DetailView.class);
                        intent.putExtra("poster image", result.getPosterPath());
                        intent.putExtra("poster desc",result.getOverview());
                        intent.putExtra("release date", result.getReleaseDate());
                        intent.putExtra("popularity", result.getPopularity());
                        intent.putExtra("vote", result.getVoteCount());
                        intent.putExtra("title", result.getTitle());


                        // Get the transition name from the string
                        String transitionName =context.getString(R.string.transition);
                        ActivityOptionsCompat activityOptionsCompat=ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context,
                                v, //starting view --v
                                transitionName // the string from res
                        );
                        ActivityCompat.startActivity(context,intent,activityOptionsCompat.toBundle());

                    }
                });
                break;

            case LOADING:

                break;
            default:
        }
    }


    @Override
    public int getItemCount() {
        return movieResult.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == movieResult.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }
 /*
   Helpers
   _________________________________________________________________________________________________
    */

    private void add(Result r) {
        movieResult.add(r);
        notifyItemInserted(movieResult.size() - 1);
    }

    public void addAll(List<Result> moveResults) {
        for (Result result : moveResults) {
            add(result);
        }
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new Result());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = movieResult.size() - 1;
        Result result = getItem(position);

        if (result != null) {
            movieResult.remove(position);
            notifyItemRemoved(position);
        }
    }

    private Result getItem(int position) {
        return movieResult.get(position);
    }

    //view Holders
    private static class MovieViewHolder extends RecyclerView.ViewHolder {
        private final TextView mMovieTitle;
        private final TextView mMovieDesc;
        private final TextView mYear; // displays "year | language"
        private final ImageView mPosterImg;
         final  FrameLayout frameLayout;

        MovieViewHolder(@NonNull View itemView) {

            super(itemView);
            mMovieTitle =  itemView.findViewById(R.id.movie_title);
            mMovieDesc =  itemView.findViewById(R.id.movie_desc);
            mYear = itemView.findViewById(R.id.movie_year);
            mPosterImg =  itemView.findViewById(R.id.movie_poster);
            frameLayout=itemView.findViewById(R.id.frame_recycler);
        }
    }

    private static class LoadingVH extends RecyclerView.ViewHolder {

        private LoadingVH(View itemView) {
            super(itemView);
        }
    }

}



