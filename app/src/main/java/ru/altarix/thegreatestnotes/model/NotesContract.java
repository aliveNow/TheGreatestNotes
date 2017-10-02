package ru.altarix.thegreatestnotes.model;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by samsmariya on 02.10.17.
 */

public class NotesContract {
    public static final String AUTHORITY = "ru.altarix.thegreatestnotes.model";
    private static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);

    public static final class Notes implements BaseColumns {

        public static final String TABLE_NAME = "notes";

        public static final Uri CONTENT_URI = AUTHORITY_URI.buildUpon().appendPath(TABLE_NAME).build();

        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_TEXT = "text";
        public static final String COLUMN_DATETIME = "datetime";
        public static final String COLUMN_IMAGE_URI = "image_uri";

        public static Uri buildNoteUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

}
