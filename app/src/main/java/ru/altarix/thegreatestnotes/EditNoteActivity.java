package ru.altarix.thegreatestnotes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import ru.altarix.thegreatestnotes.model.Note;
import ru.altarix.thegreatestnotes.model.NotesManager;

public class EditNoteActivity extends AppCompatActivity {

    public static final String EDITABLE_KEY = "editable";

    private Note note;
    private TextView titleView;
    private TextView textView;
    boolean editable = false;
    boolean wasShowActivity = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        Intent intent = getIntent();
        note = (Note) intent.getParcelableExtra(Note.class.getCanonicalName());

        titleView = (TextView) findViewById(R.id.editTitle);
        textView = (TextView) findViewById(R.id.editText);
        editable = intent.getBooleanExtra(EDITABLE_KEY, false);
        wasShowActivity = !editable;
        prepareViews();
    }

    protected void prepareViews() {
        titleView.setText(note.getTitle());
        textView.setText(note.getText());
        titleView.setEnabled(editable);
        textView.setEnabled(editable);
        setTitle(editable ? R.string.title_activity_edit_note : R.string.title_activity_show_note);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        if (editable) {
            getMenuInflater().inflate(R.menu.menu_edit_note, menu);
        }else {
            getMenuInflater().inflate(R.menu.menu_show_note, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_note :
                updateEditable(true);
                break;
            case R.id.save_note :
                note.setTitle(titleView.getText().toString());
                note.setText(textView.getText().toString());
                NotesManager.getNotesManager().saveObject(note);
                break;
            case R.id.delete_note :
                NotesManager.getNotesManager().removeObject(note);
                finish();
                return true;
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
        prepareViews();
        invalidateOptionsMenu();
    }
}
