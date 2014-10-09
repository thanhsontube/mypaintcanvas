package com.example.tapcopaint.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.Log;

public class ImageCache {

    private final LruCache<String, Bitmap> mCache;

    public ImageCache(Context context) {
        final int maxMem = (int) Runtime.getRuntime().maxMemory() / 1024;
        final int cacheSize = maxMem/4 ;

        mCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return (value.getRowBytes() * value.getHeight()) / 1024;
            }
        };
    }

    public void put(String key, Bitmap bitmap) {
        if (get(key) == null) {
            Log.v("cache", ">>>PUT:" + key);
            mCache.put(key, bitmap);
        }
    }

    public Bitmap get(String key) {
        return mCache.get(key);
    }

    public void clear() {
        mCache.evictAll();
    }
   
}
