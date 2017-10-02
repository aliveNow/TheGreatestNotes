package ru.altarix.thegreatestnotes.model;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ru.altarix.thegreatestnotes.R;
import ru.altarix.thegreatestnotes.model.NotesContract.Notes;

/**
 * Created by samsmariya on 02.10.17.
 */

public class GreatestNotesContentProvider extends ContentProvider {

    private DBHelper dbHelper;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int ONE_NOTE = 1;
    private static final int NOTES = 2;

    static {
        uriMatcher.addURI(NotesContract.AUTHORITY, Notes.TABLE_NAME + "/#", ONE_NOTE);
        uriMatcher.addURI(NotesContract.AUTHORITY, Notes.TABLE_NAME, NOTES);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(Notes.TABLE_NAME);
        switch (uriMatcher.match(uri)) {
            case ONE_NOTE:
                queryBuilder.appendWhere(Notes._ID + "=" + uri.getLastPathSegment());
                break;
            case NOTES:
                break;
            default: {
                throw new UnsupportedOperationException(
                        getContext().getString(R.string.error_invalid_uri, uri));
            }
        }

        Cursor cursor = queryBuilder.query(dbHelper.getReadableDatabase(),
                projection, selection, selectionArgs, null, null, sortOrder);


        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Uri newUri = null;
        switch (uriMatcher.match(uri)) {
            case NOTES:
                // При успехе возвращается идентификатор новой записи
                long rowId = dbHelper.getWritableDatabase().insert(Notes.TABLE_NAME, null, values);
                if (rowId > 0) { // SQLite row IDs start at 1
                    newUri = Notes.buildNoteUri(rowId);
                    // Оповестить наблюдателей об изменениях в базе данных
                    getContext().getContentResolver().notifyChange(uri, null);
                } else {
                    throw new SQLException(
                            getContext().getString(R.string.error_db_operation_failed, uri));
                }
                break;
            default: {
                throw new UnsupportedOperationException(
                        getContext().getString(R.string.error_invalid_uri, uri));
            }
        }
        return newUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int numberOfRowsDeleted;

        switch (uriMatcher.match(uri)) {
            case ONE_NOTE:
                // Получение из URI идентификатора контакта
                String id = uri.getLastPathSegment();

                // Удаление контакта
                numberOfRowsDeleted = dbHelper.getWritableDatabase().delete(
                        Notes.TABLE_NAME, Notes._ID + "=" + id, selectionArgs);
                break;
            default:
            {
                throw new UnsupportedOperationException(
                        getContext().getString(R.string.error_invalid_uri, uri));
            }
        }

        // Оповестить наблюдателей об изменениях в базе данных
        if (numberOfRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numberOfRowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int numberOfRowsUpdated;
        switch (uriMatcher.match(uri)) {
            case ONE_NOTE:
                String id = uri.getLastPathSegment();
                numberOfRowsUpdated = dbHelper.getWritableDatabase().update(
                        Notes.TABLE_NAME, values, Notes._ID + "=" + id,
                        selectionArgs);
                break;
            default: {
                throw new UnsupportedOperationException(
                        getContext().getString(R.string.error_invalid_uri, uri));
            }
        }
        // Если были внесены изменения, оповестить наблюдателей
        if (numberOfRowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numberOfRowsUpdated;
    }
}
