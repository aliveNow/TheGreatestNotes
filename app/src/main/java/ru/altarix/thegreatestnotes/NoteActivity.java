package ru.altarix.thegreatestnotes;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import ru.altarix.thegreatestnotes.model.Note;
import ru.altarix.thegreatestnotes.model.ObjectManager;
import ru.altarix.thegreatestnotes.model.ObjectManagerFactory;
import ru.altarix.thegreatestnotes.utils.Constants;
import ru.altarix.thegreatestnotes.utils.Constants.Extras;
import ru.altarix.thegreatestnotes.utils.OnNoteActionSelectedListener;

/**
 * Created by samsmariya on 02.10.17.
 */
public class NoteActivity extends AppCompatActivity
        implements OnNoteActionSelectedListener {

    private ObjectManager<Note> notesManager;
    private Note note;
    Constants.Action action = Constants.Action.NONE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        notesManager = ObjectManagerFactory.getNotesManager(this);

        Bundle bundle = savedInstanceState != null ? savedInstanceState : getIntent().getExtras();
        note = bundle.getParcelable(Extras.NOTE);
        Constants.Action newAction = (Constants.Action) bundle.getSerializable(Extras.ACTION);
        if (savedInstanceState == null) {
            changeActionForNote(note, newAction);
        }else {
            action = newAction;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(Extras.ACTION, action);
        outState.putParcelable(Extras.NOTE, note);
    }

    protected void changeActionForNote(Note note, Constants.Action action){
        Fragment fragment = null;
        switch (action) {
            case CREATE:
                note = notesManager.createObject();
                fragment = EditNoteFragment.newInstance(note, action);
                setTitle(R.string.title_activity_create_note);
                break;
            case EDIT:
                fragment = EditNoteFragment.newInstance(note, action);
                setTitle(R.string.title_activity_edit_note);
                break;
            case VIEW:
                fragment = ViewNoteFragment.newInstance(note, action);
                setTitle(R.string.title_activity_view_note);
                break;
            default:
                break;
        }
        if (fragment != null) {
            FragmentTransaction fTrans = getSupportFragmentManager().beginTransaction();
            fTrans.replace(R.id.fragment_container, fragment);
            // FIXME: 02.10.17 если переходим на редактирование с просмотра стоит ли добавить fTrans.addToBackStack(null); ?
            fTrans.commit();
            this.action = action;
        }
    }

    protected void applyActionForNote(Note note, Constants.Action action) {
        int messageId = R.string.msg_null;
        switch (action) {
            case EDIT:
            case VIEW:
                changeActionForNote(note, action);
                break;
            case SAVE:
                notesManager.saveObject(note);
                changeActionForNote(note, Constants.Action.VIEW);
                messageId = R.string.msg_note_saved;
                break;
            case DELETE:
                // FIXME: 02.10.17 добавить диалог
                notesManager.removeObject(note);
                finish();
                break;
            default:
                messageId = R.string.msg_such_action_not_exist;
                break;
        }
        if (messageId != R.string.msg_null) {
            Snackbar.make(findViewById(R.id.coordinator_layout), messageId, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onNoteActionSelected(Note note, Constants.Action action) {
        applyActionForNote(note, action);
    }
}
