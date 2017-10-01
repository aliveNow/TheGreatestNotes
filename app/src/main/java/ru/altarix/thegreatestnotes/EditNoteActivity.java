package ru.altarix.thegreatestnotes;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import ru.altarix.thegreatestnotes.model.Note;
import ru.altarix.thegreatestnotes.utils.Constants;

public class EditNoteActivity extends AppCompatActivity {

    private Note note;
    boolean editable = false;
    Constants.Action action = Constants.Action.VIEW;
    boolean wasShowActivity = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        Intent intent = getIntent();
        note = intent.getParcelableExtra(Constants.Extras.NOTE);
        action = (Constants.Action) intent.getSerializableExtra(Constants.Extras.ACTION);
        editable = (action == Constants.Action.EDIT);
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
