package ru.altarix.thegreatestnotes.model;

import android.content.Context;

/**
 * Created by samsmariya on 14.10.15.
 */
public class ObjectManagerFactory {

    private static ObjectManager<Note> notesManager;

    public static ObjectManager<Note> getNotesManager(Context context)
    {
        if (notesManager == null) {
            notesManager = new NotesManager(context.getApplicationContext());
        }
        return notesManager;
    }

}
