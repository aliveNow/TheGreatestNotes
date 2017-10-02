package ru.altarix.thegreatestnotes.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
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
    
    // Database fields
    private SQLiteDatabase database;
    private SQLiteOpenHelper dbHelper;
    private String[] allColumns = {
            Notes._ID,
            Notes.COLUMN_TITLE,
            Notes.COLUMN_TEXT,
            Notes.COLUMN_IMAGE_URI
    };

    public NotesManager(SQLiteOpenHelper dbHelper) {
        this.dbHelper = dbHelper;
        open();
        createStubNotes();
    }

    public void open() throws SQLException {
        // теперь это класс-заглушка, так что пока что и так сойдёт
        database = dbHelper.getWritableDatabase();
    }

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

    public void saveNote(Note note) {
        ContentValues values = contentValuesFromObject(note);
        long insertId = database.insert(Notes.TABLE_NAME, null, values);
        note.setId(insertId);
        notifyObservers(Action.INSERT, note);
    }

    public void updateNote(Note note) {
        ContentValues values = contentValuesFromObject(note);
        database.update(Notes.TABLE_NAME, values, Notes._ID + " = " + note.getId(), null);
        notifyObservers(Action.UPDATE, note);
    }

    public void deleteNote(Note note) {
        long id = note.getId();
        Log.w(NotesManager.class.getName(), "Note deleted with id: " + id);
        database.delete(Notes.TABLE_NAME, Notes._ID
                + " = " + id, null);
        notifyObservers(Action.DELETE, note);
    }

    public List<Note> getAllNotes() {
        List<Note> notes = new ArrayList<Note>();

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
        return database.query(Notes.TABLE_NAME,
                allColumns, null, null, null, null, Notes.COLUMN_DATETIME + " DESC");
    }

    @Override
    public Note createObject() {
        Note note = new Note(0, null, null);
        return note;
    }

    @Override
    public void saveObject(Note note) {
        if (note.getId() > 0) {
            updateNote(note);
        }else {
            saveNote(note);
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
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put(ObjectManager.OBJECT_KEY, note);
        data.put(ObjectManager.ACTION_KEY, action);
        setChanged();
        notifyObservers(data);
    }
}
