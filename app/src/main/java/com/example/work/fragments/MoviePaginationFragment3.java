package com.example.work.fragments;


import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.work.apiclientservices.MovieApiClient;
import com.example.work.apiclientservices.MovieServiceInterface;
import com.example.work.R;
import com.example.work.adapter.PaginationAdapter;
import com.example.work.model.Result;
import com.example.work.model.TopRatedMovies;
import com.example.work.utils.PaginationScrollListener;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class MoviePaginationFragment3 extends Fragment {
    private PaginationAdapter adapter;
    private ProgressBar progressBar;


    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;

    private static final int TOTAL_PAGE = 10;
    private int currentPage = PAGE_START;
    private MovieServiceInterface movieServiceInterface;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        GridLayoutManager gridLayoutManager = null;

        RecyclerView recyclerView;
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pagination5, container, false);
        recyclerView = view.findViewById(R.id.recycle_pagination);
        progressBar = view.findViewById(R.id.main_progress);


        adapter = new PaginationAdapter(getActivity());

        //grid layout view-- mobile
        if (!isTablet(Objects.requireNonNull(getActivity()))) {

            if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                gridLayoutManager = new GridLayoutManager(getActivity(), 1);
                recyclerView.setLayoutManager(gridLayoutManager);
            } else {
                gridLayoutManager = new GridLayoutManager(getActivity(), 2);
                recyclerView.setLayoutManager(gridLayoutManager);
            }
        }

        //grid layout view--tablet
        if (isTablet(getActivity())) {
            if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                gridLayoutManager = new GridLayoutManager(getActivity(), 2);
                recyclerView.setLayoutManager(gridLayoutManager);
            } else {
                gridLayoutManager = new GridLayoutManager(getActivity(), 3);
                recyclerView.setLayoutManager(gridLayoutManager);
            }
        }


        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new PaginationScrollListener(gridLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;

                // mocking network delay for API call
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadNextPage();
                    }
                }, 1000);
            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGE;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });

        //init service and load data
        movieServiceInterface = MovieApiClient.getClient().create(MovieServiceInterface.class);


        loadFirstPage();

        // animation into detail view

        return view;
    }

    private void loadFirstPage() {
        Log.d(String.valueOf(getActivity()), "loadFirstPage: ");

        call().enqueue(new Callback<TopRatedMovies>() {
            @Override
            public void onResponse(Call<TopRatedMovies> call, Response<TopRatedMovies> response) {
                // Got data. Send it to adapter

                List<Result> results = fetchResults(response);
                progressBar.setVisibility(View.GONE);
                adapter.addAll(results);

                if (currentPage <= TOTAL_PAGE) adapter.addLoadingFooter();
                else isLastPage = true;
            }

            @Override
            public void onFailure(Call<TopRatedMovies> call, Throwable t) {
                t.printStackTrace();

            }
        });

    }

    private List<Result> fetchResults(Response<TopRatedMovies> response) {
        TopRatedMovies topRatedMovies = response.body();
        return topRatedMovies.getResults();
    }

    private void loadNextPage() {
        Log.d(String.valueOf(getActivity()), "loadNextPage: " + currentPage);

        call().enqueue(new Callback<TopRatedMovies>() {
            @Override
            public void onResponse(Call<TopRatedMovies> call, Response<TopRatedMovies> response) {
                adapter.removeLoadingFooter();
                isLoading = false;

                List<Result> results = fetchResults(response);
                adapter.addAll(results);

                if (currentPage != TOTAL_PAGE) adapter.addLoadingFooter();
                else isLastPage = true;
            }

            @Override
            public void onFailure(Call<TopRatedMovies> call, Throwable t) {
                t.printStackTrace();

            }
        });
    }

    private Call<TopRatedMovies> call() {
        return movieServiceInterface.getTopRatedMovies(
                getString(R.string.pageApiKey),
                String.valueOf(R.string.page_language),
                currentPage
        );
    }

    private static boolean isTablet(Context context) {

        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

}