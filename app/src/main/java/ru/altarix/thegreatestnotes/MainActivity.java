package ru.altarix.thegreatestnotes;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import ru.altarix.thegreatestnotes.model.Note;
import ru.altarix.thegreatestnotes.model.ObjectManager;
import ru.altarix.thegreatestnotes.model.ObjectManagerFactory;
import ru.altarix.thegreatestnotes.utils.Constants;
import ru.altarix.thegreatestnotes.utils.Constants.Extras;
import ru.altarix.thegreatestnotes.utils.OnNoteActionSelectedListener;

/**
 * Created by samsmariya on 01.10.17.
 */
public class MainActivity extends AppCompatActivity
        implements OnNoteActionSelectedListener {

    private ObjectManager<Note> notesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        notesManager = ObjectManagerFactory.getNotesManager(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startNoteActivity(null, Constants.Action.CREATE);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        notesManager.addObserver(notesObserver);
    }

    @Override
    protected void onStop() {
        super.onStop();
        notesManager.deleteObserver(notesObserver);
    }

    @Override
    public void onNoteActionSelected(Note note, Constants.Action action) {
        switch (action) {
            case VIEW:
            case EDIT:
                startNoteActivity(note, action);
                break;
            case DELETE:
                notesManager.removeObject(note);
                break;
            default:
                // FIXME: 02.10.17 добавить вывод "что-то пошло не так" в лог
                break;
        }
    }

    private void startNoteActivity(Note note, Constants.Action action){
        Intent intent = new Intent(this, NoteActivity.class);
        intent.putExtra(Extras.NOTE, note);
        intent.putExtra(Extras.ACTION, action);
        startActivity(intent);
    }

    // FIXME: 03.10.17 временное решение
    private Observer notesObserver = new Observer() {
        @Override
        public void update(Observable o, Object arg) {
            Map map = (Map)arg;
            Note note = (Note) map.get(ObjectManager.OBJECT_KEY);
            ObjectManager.Action action = (ObjectManager.Action) map.get(ObjectManager.ACTION_KEY);
            if (action == ObjectManager.Action.DELETE) {
                Snackbar.make(findViewById(R.id.coordinator_layout),
                        getString(R.string.msg_note_deleted, note.getTitle()),
                        Snackbar.LENGTH_LONG).show();
            }
        }
    };
}
