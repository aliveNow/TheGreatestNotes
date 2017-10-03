package ru.altarix.thegreatestnotes.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;

import ru.altarix.thegreatestnotes.model.NotesContract.Notes;

/**
 * Created by samsmariya on 04.10.15.
 */
public class NotesManager extends Observable implements ObjectManager<Note> {

    protected Context mContext;
    private String[] allColumns = {
            Notes._ID,
            Notes.COLUMN_TITLE,
            Notes.COLUMN_TEXT,
            Notes.COLUMN_IMAGE_URI
    };

    public NotesManager(Context context) {
        mContext = context;
        // TODO: 02.10.17 тут можно вставить проверку на тип сборки для интереса
        //createStubNotes();
    }

    public void insertNote(Note note) {
        ContentValues values = contentValuesFromObject(note);
        // TODO: 03.10.17 сделать в AsyncQueryHandler?
        Uri newNoteUri = mContext.getContentResolver().insert(Notes.CONTENT_URI, values);
        if (newNoteUri != null) {
            long insertId = Long.valueOf(newNoteUri.getLastPathSegment());
            note.setId(insertId);
            notifyObservers(Action.INSERT, note);
        }
    }

    public void updateNote(Note note) {
        ContentValues values = contentValuesFromObject(note);
        Uri noteUri = Notes.buildNoteUri(note.getId());
        int updatedRows = mContext.getContentResolver().update(noteUri, values, null, null);
        if (updatedRows == 1) {
            notifyObservers(Action.UPDATE, note);
        }
    }

    public void deleteNote(Note note) {
        Uri noteUri = Notes.buildNoteUri(note.getId());
        int deletedRows = mContext.getContentResolver().delete(noteUri, null, null);
        if (deletedRows == 1) {
            // FIXME: 02.10.17 еще логи надо поправить
            Log.w(NotesManager.class.getName(), "Note deleted: " + noteUri);
            notifyObservers(Action.DELETE, note);
        }
    }

    public List<Note> getAllNotes() {
        List<Note> notes = new ArrayList<>();

        Cursor cursor = getAllObjectsCursor();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Note note = cursorToObject(cursor);
            notes.add(note);
            cursor.moveToNext();
        }
        cursor.close();
        return notes;
    }

    // ObjectManager

    @Override
    public List<Note> getAllObjects()
    {
        return getAllNotes();
    }

    @Override
    public Cursor getAllObjectsCursor() {
        return mContext.getContentResolver().query(Notes.CONTENT_URI,
                allColumns,
                null, // selection
                null, // selectionArgs
                Notes.COLUMN_DATETIME + " DESC");
    }

    @Override
    public Note createObject() {
        return new Note();
    }

    @Override
    public void saveObject(Note note) {
        if (note.getId() == Note.EMPTY_ID) {
            insertNote(note);
        }else {
            updateNote(note);
        }
    }

    @Override
    public void removeObject(Note note) {
        deleteNote(note);
    }

    @Override
    public Note cursorToObject(Cursor cursor) {
        Note note = new Note();
        note.setId(cursor.getLong(cursor.getColumnIndex(Notes._ID)));
        note.setTitle(cursor.getString(cursor.getColumnIndex(Notes.COLUMN_TITLE)));
        note.setText(cursor.getString(cursor.getColumnIndex(Notes.COLUMN_TEXT)));
        String imageStr = cursor.getString(cursor.getColumnIndex(Notes.COLUMN_IMAGE_URI));
        if (!TextUtils.isEmpty(imageStr)) {
            note.setImageUri(Uri.parse(imageStr));
        }
        return note;
    }

    @Override
    public ContentValues contentValuesFromObject(Note note) {
        ContentValues values = new ContentValues();
        values.put(Notes.COLUMN_TITLE, note.getTitle());
        values.put(Notes.COLUMN_TEXT, note.getText());
        values.put(Notes.COLUMN_DATETIME, System.currentTimeMillis());
        if (note.getImageUri() != null) {
            values.put(Notes.COLUMN_IMAGE_URI, note.getImageUri().toString());
        }else {
            values.putNull(Notes.COLUMN_IMAGE_URI);
        }
        return values;
    }

    // Observable

    private void notifyObservers(final ObjectManager.Action action, final Note note) {
        HashMap<String, Object> data = new HashMap<>();
        data.put(ObjectManager.OBJECT_KEY, note);
        data.put(ObjectManager.ACTION_KEY, action);
        setChanged();
        notifyObservers(data);
    }

    // вспомогательный метод для тестирования
    void createStubNotes() {
        List<Note> notes = getAllNotes();
        if (notes.size() > 0) {
            return;
        }
        for (String title : new String[] {
                "просто",
                "еще",
                "одна",
                "заметка"
        }) {
            Note note = createObject();
            note.setTitle(title);
            note.setText(title);
            saveObject(note);
        }
    }
}
