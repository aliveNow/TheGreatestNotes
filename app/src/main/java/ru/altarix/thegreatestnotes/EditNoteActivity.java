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
    private boolean editable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        Intent intent = getIntent();
        note = (Note) intent.getParcelableExtra(Note.class.getCanonicalName());

        titleView = (TextView) findViewById(R.id.editTitle);
        textView = (TextView) findViewById(R.id.editText);
        titleView.setText(note.getTitle());
        textView.setText(note.getText());
        setEditable(intent.getBooleanExtra(EDITABLE_KEY, false));
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.edit_note :
                setEditable(true);
                invalidateOptionsMenu();
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

    public void setEditable(boolean editable){
        this.editable = editable;
        titleView.setEnabled(editable);
        textView.setEnabled(editable);
    }
}
