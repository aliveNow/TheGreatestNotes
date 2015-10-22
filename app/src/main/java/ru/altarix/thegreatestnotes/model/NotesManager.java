package ru.altarix.thegreatestnotes.model;

import android.content.Context;

/**
 * Created by samsmariya on 14.10.15.
 */
public class NotesManager {

    private static ObjectManager<Note> notesManager;

    public static ObjectManager<Note> createNotesManager(Context context)
    {
        if (notesManager == null) {
            notesManager = new NotesDataSource(context);
        }
        return notesManager;
    }

    public static ObjectManager<Note> getNotesManager()
    {
        return notesManager;
    }

}
