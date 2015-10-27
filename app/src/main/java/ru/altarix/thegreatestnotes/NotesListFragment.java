package ru.altarix.thegreatestnotes;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import ru.altarix.thegreatestnotes.model.ListViewNotesAdapter;
import ru.altarix.thegreatestnotes.model.Note;
import ru.altarix.thegreatestnotes.model.ObjectManager;

public class NotesListFragment extends Fragment implements AdapterView.OnItemClickListener {

    private ListView mListView;
    private ListViewNotesAdapter mAdapter;
    private ObjectManager<Note> notesManager;

    public static NotesListFragment newInstance(ObjectManager<Note> notesManager) {
        NotesListFragment fragment = new NotesListFragment();
        fragment.notesManager = notesManager;
        return fragment;
    }

    public NotesListFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_notes_list, container, false);
        mListView = (ListView) contentView.findViewById(R.id.listview);
        return contentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAdapter = new ListViewNotesAdapter(getActivity(), R.layout.listview_item, R.id.text_data, notesManager);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        registerForContextMenu(mListView);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Note note = (Note) parent.getItemAtPosition(position);
        startNoteActivity(note, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_notes_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
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
            getActivity().getMenuInflater().inflate(R.menu.menu_edit_note_in_list, menu);
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
        Intent intent = new Intent(getActivity(), EditNoteActivity.class);
        intent.putExtra(Note.class.getCanonicalName(), note);
        intent.putExtra(EditNoteActivity.EDITABLE_KEY, editable);
        startActivity(intent);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
