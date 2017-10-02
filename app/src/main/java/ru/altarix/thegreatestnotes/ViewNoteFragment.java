package ru.altarix.thegreatestnotes;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

public class ViewNoteFragment extends Fragment
        implements View.OnClickListener {

    protected Note note;
    protected OnNoteActionSelectedListener mCallback;
    protected TextView titleView;
    protected TextView textView;
    protected ImageView imageView;

    public static ViewNoteFragment newInstance(Note note) {
        ViewNoteFragment fragment = new ViewNoteFragment();
        fragment.setArguments(bundleFromArguments(note));
        return fragment;
    }

    protected static Bundle bundleFromArguments(Note note) {
        Bundle args = new Bundle();
        args.putParcelable(Constants.Extras.NOTE, note);
        return args;
    }

    public ViewNoteFragment() {}

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
        if (getArguments() != null) {
            note = getArguments().getParcelable(Constants.Extras.NOTE);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_view_note, container, false);
        return contentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View contentView = getView();
        // FIXME: 02.10.17 интересно, где-то было написано про механизм binding, он во всех версиях будет работать?
        titleView = (TextView) contentView.findViewById(R.id.editTitle);
        textView = (TextView) contentView.findViewById(R.id.editText);
        imageView = (ImageView) contentView.findViewById(R.id.imageView);
        prepareViews();
        imageView.setOnClickListener(this);
    }

    protected void prepareViews() {
        titleView.setText(note.getTitle());
        textView.setText(note.getText());
        prepareImageView();
    }

    protected void prepareImageView() {
        showImage(note.getImageUri());
        imageView.setVisibility(note.getImageUri() == null ? View.GONE : View.VISIBLE);
    }

    protected void showImage(Uri uri) {
        ImageUtils.showThumbnailImage(getActivity(), imageView, note.getImageUri());
    }

    @Override
    public void onClick(View v) {
        Uri uri = note.getImageUri();
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
            case R.id.edit_note :
                mCallback.onNoteActionSelected(note, Constants.Action.EDIT);
                return true;
            case R.id.delete_note :
                mCallback.onNoteActionSelected(note, Constants.Action.DELETE);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
