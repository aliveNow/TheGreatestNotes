package ru.altarix.thegreatestnotes;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import ru.altarix.thegreatestnotes.model.ListViewNotesAdapter;
import ru.altarix.thegreatestnotes.model.Note;
import ru.altarix.thegreatestnotes.model.NotesManager;
import ru.altarix.thegreatestnotes.utils.Constants;

public class NotesListFragment extends Fragment implements AdapterView.OnItemClickListener {

    public interface OnNoteSelectedListener {
        public void noteWasSelectedWithAction(int position,
                                              Note note,
                                              Constants.Action action);
    }

    private ListView mListView;
    private ListViewNotesAdapter mAdapter;
    private OnNoteSelectedListener mCallback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnNoteSelectedListener) context;
        }catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement NotesListListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setHasOptionsMenu(true);
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
        mAdapter = new ListViewNotesAdapter(getActivity(), R.layout.listview_item, R.id.text_data, NotesManager.getNotesManager());
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        registerForContextMenu(mListView);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Note note = (Note) parent.getItemAtPosition(position);
        mCallback.noteWasSelectedWithAction(position, note, Constants.Action.VIEW);
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
        int position = info.position;
        Note note = mAdapter.getItem(position);
        switch (item.getItemId()) {
            case R.id.edit_note :
                mCallback.noteWasSelectedWithAction(position, note, Constants.Action.EDIT);
                break;
            case R.id.remove_note :
                mCallback.noteWasSelectedWithAction(position, note, Constants.Action.DELETE);
                break;
        }
        return true;
    }

    @Override
    public void onDetach() {
        mCallback = null;
        super.onDetach();
    }
}
