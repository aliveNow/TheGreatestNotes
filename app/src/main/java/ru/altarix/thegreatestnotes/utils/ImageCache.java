package ru.altarix.thegreatestnotes.utils;

import android.graphics.Bitmap;
import android.util.LruCache;

/**
 * Created by samsmariya on 24.10.15.
 */
// http://developer.android.com/intl/ru/training/displaying-bitmaps/cache-bitmap.html

// FIXME: 03.10.17 посмотреть что это вообще такое
public class ImageCache extends LruCache<String, Bitmap> {

    public ImageCache(int maxSize) {
        super(maxSize);
    }

    @Override
    protected int sizeOf(String key, Bitmap value) {
        return value.getByteCount() / 1024;
    }

    @Override
    protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
        oldValue.recycle();
    }

}
