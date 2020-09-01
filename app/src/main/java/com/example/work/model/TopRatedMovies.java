package com.example.work.model;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TopRatedMovies {


    @SerializedName("results")
    @Expose
    private final List<Result> results = null;

    public List<Result> getResults() {
        return results;
    }



}