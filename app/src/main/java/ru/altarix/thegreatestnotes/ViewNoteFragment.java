package ru.altarix.thegreatestnotes;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import ru.altarix.thegreatestnotes.model.Note;
import ru.altarix.thegreatestnotes.utils.Constants;
import ru.altarix.thegreatestnotes.utils.ImageUtils;
import ru.altarix.thegreatestnotes.utils.OnNoteActionSelectedListener;
import ru.altarix.thegreatestnotes.utils.Constants.Extras;
import ru.altarix.thegreatestnotes.utils.Utils;

public class ViewNoteFragment extends Fragment
        implements View.OnClickListener {

    private static final String TAG = ViewNoteFragment.class.getSimpleName();

    protected Note mNote;
    protected Constants.Action mAction = Constants.Action.NONE;
    protected OnNoteActionSelectedListener mCallback;

    protected TextView textTitle;
    protected TextView textNote;
    protected ImageView imageView;

    public static ViewNoteFragment newInstance(@NonNull Note note, @NonNull Constants.Action action) {
        ViewNoteFragment fragment = new ViewNoteFragment();
        fragment.setArguments(bundleFromArguments(note, action));
        return fragment;
    }

    protected static Bundle bundleFromArguments(Note note, Constants.Action action) {
        Bundle args = new Bundle();
        args.putParcelable(Extras.NOTE, note);
        args.putSerializable(Extras.ACTION, action);
        return args;
    }

    public ViewNoteFragment() {}

    public Constants.Action getCurrentAction() {
        return mAction;
    }

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            if (getArguments() != null) {
                mNote = getArguments().getParcelable(Extras.NOTE);
                mAction = (Constants.Action) getArguments().getSerializable(Extras.ACTION);
            }
            Utils.requireNonNull(mNote, TAG + ": NOTE extra can't be null");
        }else {
            mNote = savedInstanceState.getParcelable(Extras.NOTE);
            mAction = (Constants.Action) savedInstanceState.getSerializable(Extras.ACTION);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_view_note, container, false);
        textTitle = (TextView) contentView.findViewById(R.id.text_title);
        textNote = (TextView) contentView.findViewById(R.id.text_note);
        return contentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View contentView = getView();
        // FIXME: 02.10.17 интересно, где-то было написано про механизм binding, он во всех версиях будет работать?
        imageView = (ImageView) contentView.findViewById(R.id.image_thumbnail);
        prepareViews();
        imageView.setOnClickListener(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Extras.NOTE, mNote);
        outState.putSerializable(Extras.ACTION, mAction);
    }

    public void refreshState(@NonNull Note note, @NonNull Constants.Action action) {
        mNote = note;
        mAction = action;
        prepareViews();
    }

    protected void prepareViews() {
        textTitle.setText(mNote.getTitle());
        textNote.setText(mNote.getText());
        prepareImageView();
    }

    protected void prepareImageView() {
        showImage(mNote.getImageUri());
        imageView.setVisibility(mNote.getImageUri() == null ? View.GONE : View.VISIBLE);
    }

    protected void showImage(Uri uri) {
        ImageUtils.showThumbnailImage(getActivity(), imageView, mNote.getImageUri());
    }

    @Override
    public void onClick(View v) {
        Uri uri = mNote.getImageUri();
        Intent intent = new Intent(getActivity(), ShowImageActivity.class);
        intent.putExtra(ShowImageActivity.PICTURE_URI_KEY, uri);
        startActivity(intent);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (!onBeforeCreateOptionsMenu(menu, inflater)) {
            inflater.inflate(R.menu.menu_view_note, menu);
        }
    }

    protected boolean onBeforeCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_edit_note :
                mCallback.onNoteActionSelected(mNote, Constants.Action.EDIT);
                return true;
            case R.id.menu_delete_note:
                mCallback.onNoteActionSelected(mNote, Constants.Action.DELETE);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDetach() {
        mCallback = null;
        super.onDetach();
    }

}
