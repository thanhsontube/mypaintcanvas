package com.example.tapcopaint.utils;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v4.util.LruCache;

public class ImageCacheDisk {
    private DiskLruCache mDiskLruCache;
    private final Object mDiskCacheLock = new Object();
    private boolean mDiskCacheStarting = true;
    private static final int DISK_CACHE_SIZE = 1024 * 1024 * 10; // 10MB
    private static final String DISK_CACHE_SUBDIR = "thumbnails";

    private final LruCache<String, Bitmap> mCache;
    private LruCache<String, Bitmap> mMemoryCache;

    public ImageCacheDisk(Context context) {
        final int maxMem = (int) Runtime.getRuntime().maxMemory() / 1024;
        final int cacheSize = maxMem / 8;

        mCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return (value.getRowBytes() * value.getHeight()) / 1024;
            }
        };
    }

    public void put(String key, Bitmap bitmap) {
        if (get(key) == null) {
            mCache.put(key, bitmap);
        }
    }

    public Bitmap get(String key) {
        return mCache.get(key);
    }

    public void clear() {
        mCache.evictAll();
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    
}
