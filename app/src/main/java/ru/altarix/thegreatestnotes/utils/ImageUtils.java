package ru.altarix.thegreatestnotes.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.LruCache;
import android.widget.ImageView;

/**
 * Created by samsmariya on 24.10.15.
 */
// FIXME: 03.10.17 посмотреть что это вообще такое
public class ImageUtils {

    private static LruCache<String, Bitmap> thumbnailsImageCache;

    public static LruCache<String, Bitmap> getThumbnailsImageCache() {
        if (thumbnailsImageCache == null) {
            Runtime rt = Runtime.getRuntime();
            long maxMemory = rt.maxMemory() / 1024;
            int cacheSize = (int) (maxMemory / 8);
            thumbnailsImageCache = new ImageCache(cacheSize);
        }
        return thumbnailsImageCache;
    }

    public static Uri getThumbnailPath(Context context, Uri uri) {
        String result = null;
        long imageId = Long.parseLong(uri.getLastPathSegment().toString());
        Cursor cursor = MediaStore.Images.Thumbnails.queryMiniThumbnail(
                context.getContentResolver(),
                imageId,
                MediaStore.Images.Thumbnails.MINI_KIND,
                null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            result = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails.DATA));
            cursor.close();
        }
        return result != null ? Uri.parse(result) : null;
    }

    public static Bitmap getThumbnailBitmap(Context context, Uri uri) {
        long imageId = Long.parseLong(uri.getLastPathSegment().toString());
        Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(
                context.getContentResolver(),
                imageId,
                MediaStore.Images.Thumbnails.MICRO_KIND,
                null);
        return bitmap;
    }

    public static void showThumbnailImage(Context context, ImageView imageView, Uri uri) {
        Bitmap bitmap = null;
        if (uri != null) {
            String key = uri.toString();
            LruCache<String, Bitmap> cache = getThumbnailsImageCache();
            bitmap = cache.get(key);
            if (bitmap == null) {
                bitmap = ImageUtils.getThumbnailBitmap(context, uri);
                if (bitmap != null) {
                    cache.put(key, bitmap);
                }
            }
        }
        imageView.setImageBitmap(bitmap);
    }
}
