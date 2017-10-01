package ru.altarix.thegreatestnotes;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import ru.altarix.thegreatestnotes.model.Note;
import ru.altarix.thegreatestnotes.model.NotesManager;
import ru.altarix.thegreatestnotes.model.ObjectManager;
import ru.altarix.thegreatestnotes.utils.Constants;

/**
 * Created by samsmariya on 01.10.17.
 */
public class MainActivity extends AppCompatActivity
        implements NotesListFragment.OnNoteSelectedListener {


    private ObjectManager<Note> notesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // FIXME: 01.10.17 что я думала, когда это писала? =) воспользоваться LoaderManager?
        notesManager = NotesManager.createNotesManager(getApplicationContext());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startNoteActivity(notesManager.createObject(), Constants.Action.EDIT);
            }
        });
    }


    @Override
    public void noteWasSelectedWithAction(int position, Note note, Constants.Action action) {
        switch (action) {
            case VIEW:
            case EDIT:
                startNoteActivity(note, action);
                break;
            case DELETE:
                notesManager.removeObject(note);
                break;
            default:
                break;
        }
    }

    // Open Note Activity
    private void startNoteActivity(Note note, Constants.Action action){
        // FIXME: 01.10.17 нужно разделить на две разные Activity - View и Edit. Или одну с двумя фрагментами?
        Intent intent = new Intent(this, EditNoteActivity.class);
        intent.putExtra(Constants.Extras.NOTE, note);
        intent.putExtra(Constants.Extras.ACTION, action);
        startActivity(intent);
    }
}
