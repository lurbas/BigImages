package com.lucasurbas.bigimages.viewstate;

import android.os.Bundle;
import android.util.Log;

import com.hannesdorfmann.mosby.mvp.viewstate.RestoreableViewState;
import com.lucasurbas.bigimages.viewinterface.IBigImagesView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by l.urbas on 2015-06-13.
 * The ViewState holds actual state of the View. Based on this information View will
 * recreate itself after screen rotation, or recreation after memory shortage.
 *
 */
public class BigImagesViewState implements RestoreableViewState<IBigImagesView> {

    private static final String TAG = BigImagesViewState.class.getSimpleName();

    private final String KEY_STATE = "key_base_state";
    private final String KEY_IS_PROGRESSBAR = "key_base_is_progressbar";
    private final String KEY_BITMAP_KEY = "key_bitmap_key";

    public static final int STATE_IDLE = 0;
    public static final int STATE_DELAY = 1;
    public static final int STATE_LOADING = 3;
    public static final int STATE_SUCCESS = 4;
    public static final int STATE_ERROR = 5;

    private int[] stateArray;
    private boolean[] isProgressBarVisibleArray;
    private String[] bitmapKeyArray;

    public BigImagesViewState() {
        stateArray = new int[IBigImagesView.ITEMS_COUNT];
        for (int i = 0; i < stateArray.length; i++) {
            stateArray[i] = STATE_IDLE;
        }
        isProgressBarVisibleArray = new boolean[IBigImagesView.ITEMS_COUNT];
        for (int i = 0; i < isProgressBarVisibleArray.length; i++) {
            isProgressBarVisibleArray[i] = false;
        }
        bitmapKeyArray = new String[IBigImagesView.ITEMS_COUNT];
        for (int i = 0; i < bitmapKeyArray.length; i++) {
            bitmapKeyArray[i] = null;
        }
    }

    @Override
    public void saveInstanceState(Bundle out) {
        Log.v(TAG, "saveInstanceState");
        out.putIntArray(KEY_STATE, stateArray);
        out.putBooleanArray(KEY_IS_PROGRESSBAR, isProgressBarVisibleArray);
        out.putStringArray(KEY_BITMAP_KEY, bitmapKeyArray);
    }

    @Override
    public RestoreableViewState<IBigImagesView> restoreInstanceState(Bundle in) {
        Log.v(TAG, "restoreInstanceState");
        if (in == null) {
            return null;
        }
        stateArray = in.getIntArray(KEY_STATE);
        isProgressBarVisibleArray = in.getBooleanArray(KEY_IS_PROGRESSBAR);
        bitmapKeyArray = in.getStringArray(KEY_BITMAP_KEY);
        return this;
    }

    @Override
    public void apply(IBigImagesView view, boolean b) {
        for (int i = 0; i < stateArray.length; i++) {
            switch (stateArray[i]) {
                case STATE_IDLE:
                    view.showIdleState(i);
                    break;

                case STATE_DELAY:
                    view.showDelayState(i);
                    break;

                case STATE_LOADING:
                    view.showLoadingState(i);
                    break;

                case STATE_SUCCESS:
                    view.showSuccessState(i);
                    break;

                case STATE_ERROR:
                    view.showErrorState(i);
                    break;
            }
            view.setProgressbarVisibility(i, isProgressBarVisibleArray[i]);
            view.setBitmapKey(i, bitmapKeyArray[i]);
        }
    }

    private void checkPositionRange(int position) {
        if (position >= IBigImagesView.ITEMS_COUNT || position < 0) {
            throw new IllegalArgumentException("position not in range");
        }
    }

    public void setState(int position, int state) {
        checkPositionRange(position);
        stateArray[position] = state;
    }

    public void setProgressbarVisible(int position, boolean visible) {
        checkPositionRange(position);
        isProgressBarVisibleArray[position] = visible;
    }

    public void setBitmapKey(int position, String key) {
        checkPositionRange(position);
        bitmapKeyArray[position] = key;
    }
}
