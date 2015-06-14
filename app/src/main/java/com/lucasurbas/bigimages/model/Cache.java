package com.lucasurbas.bigimages.model;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

/**
 * Created by l.urbas on 2015-06-14.
 * Memory cache. Loaded bitmaps are stored here. Because it's a singleton injected by dagger,
 * we can be sure that cache operates on fixed size of memory.
 *
 */
public class Cache {

    private LruCache<String, Bitmap> memoryCache;

    /**
     *
     * @param  availablePart  a part of all application's memory available by cache
     */
    public Cache(float availablePart){

        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = (int) (maxMemory * availablePart);

        memoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemoryCache(key) == null) {
            memoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemoryCache(String key) {
        return key == null ? null : memoryCache.get(key);
    }

    public void clear(){
        memoryCache.evictAll();
    }
}
