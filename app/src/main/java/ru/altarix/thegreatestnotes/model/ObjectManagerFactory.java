package ru.altarix.thegreatestnotes.model;

import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.util.LruCache;

import ru.altarix.thegreatestnotes.utils.ImageCache;

/**
 * Created by samsmariya on 14.10.15.
 */
public class ObjectManagerFactory {

    private static ObjectManager<Note> notesManager;
    private static LruCache<String, Bitmap> thumbnailsImageCache;

    public static ObjectManager<Note> createNotesManager(SQLiteOpenHelper dbHelper)
    {
        if (notesManager == null) {
            notesManager = new NotesManager(dbHelper);
        }
        return notesManager;
    }

    public static ObjectManager<Note> getNotesManager()
    {
        return notesManager;
    }

    public static LruCache<String, Bitmap> getThumbnailsImageCache() {
        if (thumbnailsImageCache == null) {
            Runtime rt = Runtime.getRuntime();
            long maxMemory = rt.maxMemory() / 1024;
            int cacheSize = (int) (maxMemory / 8);
            thumbnailsImageCache = new ImageCache(cacheSize);
        }
        return thumbnailsImageCache;
    }

}
