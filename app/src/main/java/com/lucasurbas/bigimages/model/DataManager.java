package com.lucasurbas.bigimages.model;

import android.util.Pair;
import android.util.SparseArray;

import com.lucasurbas.bigimages.executor.JobExecutor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executor;

import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Image names of files stored in assets.
 * Created by l.urbas on 2015-06-14.
 */
public class DataManager {
    private final List<String> imageUrlList;

    public DataManager() {
        this.imageUrlList = new ArrayList<>();
        imageUrlList.add("big_image.jpg");
        imageUrlList.add("girl_png.png");
    }

    public Observable<Pair<String, Integer>> getImageUrlList() {
        ArrayList<Pair<String, Integer>> array = new ArrayList<>();
        for (int i = 0; i < imageUrlList.size(); i++) {
            array.add(new Pair<String, Integer>(imageUrlList.get(i), i));
        }
        return Observable.from(array);
    }

}
