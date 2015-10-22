package ru.altarix.thegreatestnotes.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by samsmariya on 04.10.15.
 */
public class NotesDataSource extends Observable implements ObjectManager<Note> {
    
    // Database fields
    private SQLiteDatabase database;
    private DBHelper dbHelper;
    private String[] allColumns = { DBHelper.COLUMN_ID,
            DBHelper.COLUMN_TITLE, DBHelper.COLUMN_TEXT };

    public NotesDataSource(Context context) {
        dbHelper = new DBHelper(context);
        open();
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Note createNote(String title, String text) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_TITLE, title);
        values.put(DBHelper.COLUMN_TEXT, text);
        long insertId = database.insert(DBHelper.TABLE_NOTES, null,
                values);
        Cursor cursor = database.query(DBHelper.TABLE_NOTES,
                allColumns, DBHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Note newNote = cursorToNote(cursor);
        cursor.close();
        notifyObservers(Action.INSERT, newNote);
        return newNote;
    }

    public void saveNote(Note note) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_TITLE, note.getTitle());
        values.put(DBHelper.COLUMN_TEXT, note.getText());
        long insertId = database.insert(DBHelper.TABLE_NOTES, null,
                values);
        note.setId(insertId);
        notifyObservers(Action.INSERT, note);
    }

    public void updateNote(Note note) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_TITLE, note.getTitle());
        values.put(DBHelper.COLUMN_TEXT, note.getText());
        database.update(DBHelper.TABLE_NOTES, values, DBHelper.COLUMN_ID + " = " + note.getId(), null);
        notifyObservers(Action.UPDATE, note);
    }

    public void deleteNote(Note note) {
        long id = note.getId();
        Log.w(NotesDataSource.class.getName(), "Note deleted with id: " + id);
        database.delete(DBHelper.TABLE_NOTES, DBHelper.COLUMN_ID
                + " = " + id, null);
        notifyObservers(Action.DELETE, note);
    }

    public List<Note> getAllNotes() {
        List<Note> notes = new ArrayList<Note>();

        Cursor cursor = database.query(DBHelper.TABLE_NOTES,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Note note = cursorToNote(cursor);
            notes.add(note);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return notes;
    }

    private Note cursorToNote(Cursor cursor) {
        Note note = new Note();
        note.setId(cursor.getLong(0));
        note.setTitle(cursor.getString(1));
        note.setText(cursor.getString(2));
        return note;
    }

    // ObjectManager

    public List<Note> getAllObjects()
    {
        return getAllNotes();
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

    // Observable

    private void notifyObservers(final ObjectManager.Action action, final Note note) {
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put(ObjectManager.OBJECT_KEY, note);
        data.put(ObjectManager.ACTION_KEY, action);
        setChanged();
        notifyObservers(data);
    }
}