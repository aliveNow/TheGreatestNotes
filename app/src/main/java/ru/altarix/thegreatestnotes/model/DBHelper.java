package ru.altarix.thegreatestnotes.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ru.altarix.thegreatestnotes.model.NotesContract.Notes;

/**
 * Created by samsmariya on 04.10.15.
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "notes.db";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE = "create table " + Notes.TABLE_NAME
            + "("
            + Notes._ID + " integer primary key autoincrement, "
            + Notes.COLUMN_TITLE + " text,"
            + Notes.COLUMN_TEXT + " text not null,"
            + Notes.COLUMN_DATETIME + " int,"
            + Notes.COLUMN_IMAGE_URI + " text"
            + ");";



    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

       /* private static final String DATABASE_ALTER_FROM_2_TO_3 = "alter table " + NoteColumns.TABLE_NAME
            + " add column "
            + NoteColumns.COLUMN_IMAGE_URI + " text"; */

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

     /*   if (oldVersion == 2 && newVersion == 3) {
            db.execSQL(DATABASE_ALTER_FROM_2_TO_3);
        }else {
            Log.w(DBHelper.class.getName(),
                    "Upgrading database from version " + oldVersion + " to "
                            + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
            onCreate(db);
        } */
    }
}
