package ru.altarix.thegreatestnotes;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import ru.altarix.thegreatestnotes.model.Note;
import ru.altarix.thegreatestnotes.model.ObjectManager;
import ru.altarix.thegreatestnotes.model.ObjectManagerFactory;
import ru.altarix.thegreatestnotes.utils.Constants;
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
        intent.putExtra(Constants.Extras.NOTE, note);
        intent.putExtra(Constants.Extras.ACTION, action);
        startActivity(intent);
    }
}
