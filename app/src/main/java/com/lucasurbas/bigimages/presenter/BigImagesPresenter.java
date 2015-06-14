package com.lucasurbas.bigimages.presenter;

import android.graphics.Bitmap;
import android.util.Log;
import android.util.Pair;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.lucasurbas.bigimages.App;
import com.lucasurbas.bigimages.executor.JobExecutor;
import com.lucasurbas.bigimages.model.Cache;
import com.lucasurbas.bigimages.model.DataManager;
import com.lucasurbas.bigimages.model.ImageLoader;
import com.lucasurbas.bigimages.viewinterface.IBigImagesView;


import java.util.concurrent.Executor;

import javax.inject.Inject;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by l.urbas on 2015-06-13.
 * The Presenter, link between View and Model. View should be as dumb as possible,
 * every change in the View should be requested by Presenter
 *
 */
public class BigImagesPresenter extends MvpBasePresenter<IBigImagesView> {

    private static final String TAG = BigImagesPresenter.class.getSimpleName();
    private static final int DELAY_TIME = 3000;

    private final Executor jobExecutor;

    private DataManager dataManager;

    @Inject
    Cache cache;

    private long start;
    private int width;
    private int height;

    public BigImagesPresenter() {
        App.getObjectGraph().inject(this);

        this.jobExecutor = JobExecutor.getInstance();
        this.dataManager = new DataManager();
    }

    /**
     * Reset Presenter to initial state
     */
    public void reset() {
        this.cache.clear();
        for (int i = 0; i < IBigImagesView.ITEMS_COUNT; i++) {
            if (isViewAttached()) {
                getView().showIdleState(i);
                getView().setProgressbarVisibility(i, false);
                getView().setBitmapKey(i, null);
            }
        }
    }

    private Observable<Pair<String, Integer>> getImageUrlListObservable() {
        return dataManager.getImageUrlList();
    }

    /**
     * For logging time
     */
    private long getTime() {
        return System.currentTimeMillis() - start;
    }

    /**
     * Main method in Presenter. Logic and architecture of delaying and loading images.
     *
     * @param  width  width of a View. Used to decode in sample size when image is too big.
     * @param  height height of a View. Used to decode in sample size when image is too big.
     */
    public void loadImages(int width, int height) {

        this.width = width;
        this.height = height;

        start = System.currentTimeMillis();

        final Observer<Pair<String, Integer>> observer = new Observer<Pair<String, Integer>>() {

            @Override
            public void onNext(Pair<String, Integer> pair) {
                Log.v(TAG, "observer onNext: " + pair.second + " time: " + getTime());
                if (pair.first != null) {
                    if (isViewAttached()) {
                        getView().showSuccessState(pair.second);
                        getView().setProgressbarVisibility(pair.second, false);
                        getView().setBitmapKey(pair.second, pair.first);
                    }
                } else {
                    if (isViewAttached()) {
                        getView().showErrorState(pair.second);
                        getView().setProgressbarVisibility(pair.second, false);
                    }
                }
            }

            @Override
            public void onCompleted() {
                Log.v(TAG, "observer onCompleted" + " time: " + getTime());
            }

            @Override
            public void onError(Throwable e) {
                Log.v(TAG, "observer onError: " + e.getMessage() + " time: " + getTime());
            }
        };

        for (int i = 0; i < IBigImagesView.ITEMS_COUNT; i++) {
            if (isViewAttached()) {
                getView().showIdleState(i);
                getView().setProgressbarVisibility(i, true);
            }
        }

        this.getImageUrlListObservable().concatMap(STARTED).concatMap(DELAYED).concatMap(LOADED)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.from(this.jobExecutor))
                .subscribe(observer);

    }

    /**
     * Func1 to change ViewState from Idle -> Delay. Delay is about to start.
     */
    private final Func1<Pair<String, Integer>, Observable<Pair<String, Integer>>> STARTED =
            new Func1<Pair<String, Integer>, Observable<Pair<String, Integer>>>() {
                @Override
                public Observable<Pair<String, Integer>> call(Pair<String, Integer> imageUrl) {
                    return start(imageUrl);
                }
            };

    /**
     * Observable to change ViewState from Idle -> Delay. Delay is about to start.
     */
    private Observable<Pair<String, Integer>> start(final Pair<String, Integer> string) {

        final Action1<Pair<String, Integer>> startLog = new Action1<Pair<String, Integer>>() {
            @Override
            public void call(Pair<String, Integer> imageUrl) {
                Log.v(TAG, "startLog: " + imageUrl.first + " time: " + getTime());
                if (isViewAttached()) {
                    getView().showDelayState(imageUrl.second);
                }
            }
        };

        return Observable.just(string).doOnNext(startLog);
    }

    /**
     * Func1 to change ViewState from Delay -> Loading. Loading is about to start.
     */
    private final Func1<Pair<String, Integer>, Observable<Pair<String, Integer>>> DELAYED =
            new Func1<Pair<String, Integer>, Observable<Pair<String, Integer>>>() {
                @Override
                public Observable<Pair<String, Integer>> call(Pair<String, Integer> imageUrl) {
                    return delay(imageUrl);
                }
            };

    /**
     * Observable to change ViewState from Delay -> Loading. Loading is about to start.
     */
    private Observable<Pair<String, Integer>> delay(final Pair<String, Integer> imageUrl) {

        final Action1<Pair<String, Integer>> delayLog = new Action1<Pair<String, Integer>>() {
            @Override
            public void call(Pair<String, Integer> imageUrl) {
                Log.v(TAG, "delayLog: " + imageUrl.first + " time: " + getTime());
                if (isViewAttached()) {
                    getView().showLoadingState(imageUrl.second);
                }
            }
        };

        return Observable.just(delaySync(imageUrl)).doOnNext(delayLog);
    }

    /**
     * Simple thread sleeping
     */
    private Pair<String, Integer> delaySync(Pair<String, Integer> imageUrl) {
        try {
            Thread.sleep(DELAY_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return imageUrl;
    }

    private final Func1<Pair<String, Integer>, Observable<Pair<String, Integer>>> LOADED =
            new Func1<Pair<String, Integer>, Observable<Pair<String, Integer>>>() {
                @Override
                public Observable<Pair<String, Integer>> call(Pair<String, Integer> imageUrl) {
                    return load(imageUrl);
                }
            };

    private Observable<Pair<String, Integer>> load(Pair<String, Integer> imageUrl) {

        final Action1<Pair<String, Integer>> loadLog = new Action1<Pair<String, Integer>>() {
            @Override
            public void call(Pair<String, Integer> pair) {
                Log.v(TAG, "loadLog: " + pair.first + " time: " + getTime());
            }
        };

        return Observable.just(loadImageSync(imageUrl)).doOnNext(loadLog);
    }

    /**
     * Loading image from assets with {@link ImageLoader}. Next {@link Bitmap} is stored in
     * {@link Cache}
     */
    private Pair<String, Integer> loadImageSync(Pair<String, Integer> imageUrl) {
        Log.v(TAG, "loadImageSync: width: " + width + " height: " + height);
        Bitmap b = new ImageLoader().getBitmapFromAsset(imageUrl.first, width, height);
        String key = String.valueOf(imageUrl.first.hashCode());
        cache.addBitmapToMemoryCache(key, b);
        return new Pair<String, Integer>(key, imageUrl.second);
    }
}
