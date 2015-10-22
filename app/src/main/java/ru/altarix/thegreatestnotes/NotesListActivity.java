package ru.altarix.thegreatestnotes;

        import android.content.Context;
        import android.content.Intent;
        import android.os.Bundle;
        import android.view.ContextMenu;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.AdapterView;
        import android.widget.ListView;

        import ru.altarix.thegreatestnotes.model.ListViewNotesAdapter;
        import ru.altarix.thegreatestnotes.model.Note;
        import ru.altarix.thegreatestnotes.model.NotesManager;
        import ru.altarix.thegreatestnotes.model.ObjectManager;

        import android.support.v7.app.AppCompatActivity;

public class NotesListActivity extends AppCompatActivity {

    private ListView mListView;
    private ListViewNotesAdapter mAdapter;
    private Context mContext = this;
    private ObjectManager<Note> notesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_list);
        mListView = (ListView) findViewById(R.id.listview);

        notesManager = NotesManager.createNotesManager(this.getApplicationContext());

        mAdapter = new ListViewNotesAdapter(this, R.layout.listview_item, R.id.text_data, notesManager);
        mListView.setAdapter(mAdapter);
        registerForContextMenu(mListView);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Note note = (Note) parent.getItemAtPosition(position);
                startNoteActivity(note, false);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_notes_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_note:
                startNoteActivity(notesManager.createObject(), true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Menu for note items

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.listview) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            Note note = mAdapter.getItem(info.position);
            menu.setHeaderTitle(note.getTitle());
            getMenuInflater().inflate(R.menu.menu_edit_note_in_list, menu);
        }
    }

    @Override
   public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        Note note = mAdapter.getItem(info.position);
        switch (item.getItemId()) {
            case R.id.edit_note :
                startNoteActivity(note, true);
                break;
            case R.id.remove_note :
                notesManager.removeObject(note);
                break;
        }
        return true;
    }

    // Open Note Activity

    private void startNoteActivity(Note note, boolean editable){
        Intent intent = new Intent(this, EditNoteActivity.class);
        intent.putExtra(Note.class.getCanonicalName(), note);
        intent.putExtra(EditNoteActivity.EDITABLE_KEY, editable);
        startActivity(intent);
    }
}