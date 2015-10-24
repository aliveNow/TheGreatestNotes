package ru.altarix.thegreatestnotes.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by samsmariya on 04.10.15.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String TABLE_NOTES = "notes";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_TEXT = "text";
    public static final String COLUMN_DATETIME = "datetime";
    public static final String COLUMN_IMAGE_URI = "image_uri";

    private static final String DATABASE_NAME = "notes.db";
    private static final int DATABASE_VERSION = 3;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table " + TABLE_NOTES
            + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_TITLE + " text,"
            + COLUMN_TEXT + " text not null,"
            + COLUMN_DATETIME + " int,"
            + COLUMN_IMAGE_URI + " text"
            + ");";

    private static final String DATABASE_ALTER_FROM_2_TO_3 = "alter table " + TABLE_NOTES
            + " add column "
            + COLUMN_IMAGE_URI + " text";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion == 2 && newVersion == 3) {
            db.execSQL(DATABASE_ALTER_FROM_2_TO_3);
        }else {
            Log.w(DBHelper.class.getName(),
                    "Upgrading database from version " + oldVersion + " to "
                            + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
            onCreate(db);
        }
    }
}
