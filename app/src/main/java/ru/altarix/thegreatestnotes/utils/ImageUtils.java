package ru.altarix.thegreatestnotes.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.LruCache;
import android.widget.ImageView;

import ru.altarix.thegreatestnotes.model.NotesManager;

/**
 * Created by samsmariya on 24.10.15.
 */
public class ImageUtils {

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
                (BitmapFactory.Options) null);
        return bitmap;
    }

    public static void showThumbnailImage(Context context, ImageView imageView, Uri uri) {
        Bitmap bitmap = null;
        if (uri != null) {
            String key = uri.toString();
            LruCache<String, Bitmap> cache = NotesManager.getThumbnailsImageCache();
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
