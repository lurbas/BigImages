package com.lucasurbas.bigimages.viewinterface;

import android.graphics.Bitmap;

import com.hannesdorfmann.mosby.mvp.MvpView;

/**
 * Created by l.urbas on 2015-06-13.
 * Interface of the View, list of all methods which are accessible to Presenter
 *
 */
public interface IBigImagesView extends MvpView {

    public static final int LEFT_POSITION = 0;
    public static final int RIGHT_POSITION = 1;

    public static final int ITEMS_COUNT = 2;

    public void showIdleState(int position);

    public void showDelayState(int position);

    public void showLoadingState(int position);

    public void showSuccessState(int position);

    public void showErrorState(int position);

    public void setProgressbarVisibility(int position, boolean visible);

    /**
     * Instead of passing {@link Bitmap} to the View, we are informing View,
     * that it's been stored in {@link com.lucasurbas.bigimages.model.Cache} under passed key
     *
     */
    public void setBitmapKey(int position, String key);

}
