package ru.altarix.thegreatestnotes;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import ru.altarix.thegreatestnotes.model.Note;
import ru.altarix.thegreatestnotes.model.ObjectManagerFactory;
import ru.altarix.thegreatestnotes.utils.Constants;
import ru.altarix.thegreatestnotes.utils.OnNoteActionSelectedListener;

/**
 * Created by samsmariya on 02.10.17.
 */
public class NoteActivity extends AppCompatActivity
        implements OnNoteActionSelectedListener {

    private Note note;
    Constants.Action action = Constants.Action.NONE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        Intent intent = getIntent();
        note = intent.getParcelableExtra(Constants.Extras.NOTE);
        Constants.Action action = (Constants.Action) intent.getSerializableExtra(Constants.Extras.ACTION);
        changeActionForNote(note, action);
    }

    private void changeActionForNote(Note note, Constants.Action action){
        Fragment fragment = null;
        switch (action) {
            case CREATE:
                note = ObjectManagerFactory.getNotesManager().createObject();
                fragment = EditNoteFragment.newInstance(note);
                setTitle(R.string.title_activity_create_note);
                break;
            case EDIT:
                fragment = EditNoteFragment.newInstance(note);
                setTitle(R.string.title_activity_edit_note);
                break;
            case VIEW:
                fragment = ViewNoteFragment.newInstance(note);
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

    private void applyActionForNote(Note note, Constants.Action action) {
        int messageId = R.string.msg_null;
        switch (action) {
            case EDIT:
            case VIEW:
                changeActionForNote(note, action);
                break;
            case SAVE:
                ObjectManagerFactory.getNotesManager().saveObject(note);
                changeActionForNote(note, Constants.Action.VIEW);
                messageId = R.string.msg_note_saved;
                break;
            case DELETE:
                // FIXME: 02.10.17 добавить диалог
                ObjectManagerFactory.getNotesManager().removeObject(note);
                // FIXME: 02.10.17 вот этого сообщения ни-и-икто не увидит. Надо делать в MainActivity startForResult и прочая
                messageId = R.string.msg_note_deleted;
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
