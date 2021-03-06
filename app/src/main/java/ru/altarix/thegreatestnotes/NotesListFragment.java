package ru.altarix.thegreatestnotes;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.altarix.thegreatestnotes.adapters.RecyclerViewNotesAdapter;
import ru.altarix.thegreatestnotes.model.NotesContract.Notes;
import ru.altarix.thegreatestnotes.utils.Constants;
import ru.altarix.thegreatestnotes.utils.OnNoteActionSelectedListener;

public class NotesListFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private RecyclerView mRecyclerView;
    private RecyclerViewNotesAdapter mAdapter;
    private OnNoteActionSelectedListener mCallback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnNoteActionSelectedListener) context;
        }catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement " + OnNoteActionSelectedListener.class);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_notes_list, container, false);
        mRecyclerView = (RecyclerView) contentView.findViewById(R.id.recycler_view);
        return contentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new RecyclerViewNotesAdapter(getActivity(), mCallback);
        mRecyclerView.setAdapter(mAdapter);
        getLoaderManager().initLoader(Constants.Loaders.NOTES, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == Constants.Loaders.NOTES) {
                return new CursorLoader(getActivity(),
                        Notes.CONTENT_URI,
                        null, // все столбцы
                        null, // все записи
                        null, // без аргументов
                        Notes.COLUMN_DATETIME + " DESC"); // сортировка
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onDetach() {
        mCallback = null;
        super.onDetach();
    }
}
