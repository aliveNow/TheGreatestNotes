package ru.altarix.thegreatestnotes.utils;

import ru.altarix.thegreatestnotes.model.Note;

/**
 * Created by samsmariya on 02.10.17.
 */

public interface OnNoteActionSelectedListener {
    void noteActionWasSelected(int position,
                               Note note,
                               Constants.Action action);
}
