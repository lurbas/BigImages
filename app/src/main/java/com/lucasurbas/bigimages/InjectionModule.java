package com.lucasurbas.bigimages;

import android.content.Context;

import com.lucasurbas.bigimages.fragment.BigImagesFragment;
import com.lucasurbas.bigimages.model.Cache;
import com.lucasurbas.bigimages.model.ImageLoader;
import com.lucasurbas.bigimages.presenter.BigImagesPresenter;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by l.urbas on 2015-05-31.
 */
@Module(
        library = true,
        injects = {
                ImageLoader.class, BigImagesPresenter.class, BigImagesFragment.class,
        })

public class InjectionModule {

    private Context applicationContext;

    public InjectionModule(Context context) {
        this.applicationContext = context;
    }

    @Provides
    @Singleton
    public Context provideContext() {
        return applicationContext;
    }

    @Provides
    @Singleton
    public Cache provideCache() {
        final float AVAILABLE_PART = 0.2f;
        return new Cache(AVAILABLE_PART);
    }
}

