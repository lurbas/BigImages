package com.lucasurbas.bigimages.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.hannesdorfmann.mosby.mvp.viewstate.MvpViewStateFragment;
import com.hannesdorfmann.mosby.mvp.viewstate.ViewState;
import com.lucasurbas.bigimages.App;
import com.lucasurbas.bigimages.R;
import com.lucasurbas.bigimages.model.Cache;
import com.lucasurbas.bigimages.presenter.BigImagesPresenter;
import com.lucasurbas.bigimages.viewinterface.IBigImagesView;
import com.lucasurbas.bigimages.viewstate.BigImagesViewState;

import javax.inject.Inject;

import butterknife.InjectView;

/**
 * Main Fragment. In this pattern Fragment is View in MVP.
 * Created by l.urbas on 2015-06-13.
 */
public class BigImagesFragment extends MvpViewStateFragment<IBigImagesView, BigImagesPresenter> implements IBigImagesView {

    private static final String TAG = BigImagesFragment.class.getSimpleName();

    @InjectView(R.id.image_left)
    ImageView imageLeft;

    @InjectView(R.id.image_right)
    ImageView imageRight;

    @InjectView(R.id.progressbar_left)
    View progressbarLeft;

    @InjectView(R.id.progressbar_right)
    View progressbarRight;

    @InjectView(R.id.tv_state_left)
    TextView tvStateLeft;

    @InjectView(R.id.tv_state_right)
    TextView tvStateRight;

    @Inject
    Cache cache;

    private OnImageBoundsAvailable boundsListener;

    private interface OnImageBoundsAvailable {
        public void boundsAvailable(int width, int height);
    }

    public static Fragment newInstance() {
        Fragment fragment = new BigImagesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getObjectGraph().inject(this);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_reload:
                presenter.reset();
                presenter.loadImages(imageLeft.getWidth(), imageLeft.getHeight());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_big_images;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imageLeft.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (boundsListener != null) {
                    boundsListener.boundsAvailable(imageLeft.getWidth(), imageLeft.getHeight());
                    boundsListener = null;
                }
            }
        });
    }

    @Override
    public void showIdleState(final int position) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (position) {
                    case LEFT_POSITION:
                        tvStateLeft.setText(R.string.state_idle);
                        break;

                    case RIGHT_POSITION:
                        tvStateRight.setText(R.string.state_idle);
                        break;
                }
            }
        });
        ((BigImagesViewState) getViewState()).setState(position, BigImagesViewState.STATE_IDLE);
    }

    @Override
    public void showDelayState(final int position) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (position) {
                    case LEFT_POSITION:
                        tvStateLeft.setText(R.string.state_delay);
                        break;

                    case RIGHT_POSITION:
                        tvStateRight.setText(R.string.state_delay);
                        break;
                }
            }
        });
        ((BigImagesViewState) getViewState()).setState(position, BigImagesViewState.STATE_DELAY);
    }

    @Override
    public void showLoadingState(final int position) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (position) {
                    case LEFT_POSITION:
                        tvStateLeft.setText(R.string.state_loading);
                        break;

                    case RIGHT_POSITION:
                        tvStateRight.setText(R.string.state_loading);
                        break;
                }
            }
        });
        ((BigImagesViewState) getViewState()).setState(position, BigImagesViewState.STATE_LOADING);
    }

    @Override
    public void showSuccessState(final int position) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (position) {
                    case LEFT_POSITION:
                        tvStateLeft.setText(R.string.state_success);
                        break;

                    case RIGHT_POSITION:
                        tvStateRight.setText(R.string.state_success);
                        break;
                }
            }
        });
        ((BigImagesViewState) getViewState()).setState(position, BigImagesViewState.STATE_SUCCESS);
    }

    @Override
    public void showErrorState(final int position) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (position) {
                    case LEFT_POSITION:
                        tvStateLeft.setText(R.string.state_error);
                        break;

                    case RIGHT_POSITION:
                        tvStateRight.setText(R.string.state_error);
                        break;
                }
            }
        });
        ((BigImagesViewState) getViewState()).setState(position, BigImagesViewState.STATE_ERROR);
    }

    @Override
    public void setProgressbarVisibility(final int position, final boolean visible) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (position) {
                    case LEFT_POSITION:
                        progressbarLeft.setVisibility(visible ? View.VISIBLE : View.GONE);
                        break;

                    case RIGHT_POSITION:
                        progressbarRight.setVisibility(visible ? View.VISIBLE : View.GONE);
                        break;
                }
            }
        });
        ((BigImagesViewState) getViewState()).setProgressbarVisible(position, visible);
    }

    @Override
    public void setBitmapKey(final int position, final String key) {
        final Bitmap bitmap = cache.getBitmapFromMemoryCache(key);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (position) {
                    case LEFT_POSITION:
                        imageLeft.setImageBitmap(bitmap);
                        break;

                    case RIGHT_POSITION:
                        imageRight.setImageBitmap(bitmap);
                        break;
                }
            }
        });
        ((BigImagesViewState) getViewState()).setBitmapKey(position, key);
    }

    @Override
    public ViewState createViewState() {
        return new BigImagesViewState();
    }

    @Override
    public void onNewViewStateInstance() {

        // Delay loading till ImageView will be measured and layouted
        setOnImageBoundsAvailable(new OnImageBoundsAvailable() {
            @Override
            public void boundsAvailable(int width, int height) {
                presenter.loadImages(width, height);
            }
        });
    }

    @Override
    public BigImagesPresenter createPresenter() {
        return new BigImagesPresenter();
    }

    private void setOnImageBoundsAvailable(OnImageBoundsAvailable boundsListener) {
        this.boundsListener = boundsListener;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ((BigImagesViewState) getViewState()).saveInstanceState(outState);
        Log.v(TAG, "onSaveInstanceState");
    }
}
