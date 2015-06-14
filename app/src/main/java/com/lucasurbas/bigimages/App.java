package com.lucasurbas.bigimages;

import android.app.Application;

import dagger.ObjectGraph;

/**
 * Created by l.urbas on 2015-05-31.
 */
public class App extends Application {

    private static ObjectGraph objectGraph;

    @Override public void onCreate() {
        super.onCreate();
        objectGraph = ObjectGraph.create(new InjectionModule(this));
    }

    public static ObjectGraph getObjectGraph() {
        return objectGraph;
    }
}
