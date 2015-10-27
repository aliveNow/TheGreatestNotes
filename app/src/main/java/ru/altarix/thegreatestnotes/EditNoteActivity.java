package ru.altarix.thegreatestnotes;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import ru.altarix.thegreatestnotes.model.Note;

public class EditNoteActivity extends AppCompatActivity {

    public static final String EDITABLE_KEY = "editable";

    private Note note;
    boolean editable = false;
    boolean wasShowActivity = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        Intent intent = getIntent();
        note = (Note) intent.getParcelableExtra(Note.class.getCanonicalName());
        editable = intent.getBooleanExtra(EDITABLE_KEY, false);
        wasShowActivity = !editable;
        updateEditable(editable);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        if (!editable) {
            menu.add(0, R.id.edit_note, 0, R.string.edit_note).setIcon(android.R.drawable.ic_menu_edit)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.edit_note :
                updateEditable(true);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (editable && wasShowActivity) {
            updateEditable(false);
            return;
        }
        super.onBackPressed();
    }

    protected void updateEditable(boolean editable) {
        this.editable = editable;

        ViewNoteFragment fragment = editable ?
                EditNoteFragment.newInstance(note, R.layout.fragment_edit_note) :
                ViewNoteFragment.newInstance(note, R.layout.fragment_view_note);
        FragmentTransaction fTrans = getFragmentManager().beginTransaction();
        fTrans.replace(R.id.fragment, fragment);
        fTrans.commit();

        setTitle(editable ? R.string.title_activity_edit_note : R.string.title_activity_show_note);
        invalidateOptionsMenu();
    }
}
