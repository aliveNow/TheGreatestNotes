package ru.altarix.thegreatestnotes;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import ru.altarix.thegreatestnotes.model.Note;
import ru.altarix.thegreatestnotes.model.NotesManager;
import ru.altarix.thegreatestnotes.utils.ImageUtils;

public class ViewNoteFragment extends Fragment implements View.OnClickListener {
    private static final String NOTE_PARAM = "note_param";
    private static final String LAYOUT_RESOURCE_PARAM = "layout_resource_param";

    protected Note note;
    protected int layoutResource;
    protected TextView titleView;
    protected TextView textView;
    protected ImageView imageView;

    public static ViewNoteFragment newInstance(Note note, int layoutResource) {
        ViewNoteFragment fragment = new ViewNoteFragment();
        fragment.setArguments(bundleFromArguments(note, layoutResource));
        return fragment;
    }

    protected static Bundle bundleFromArguments(Note note, int layoutResource) {
        Bundle args = new Bundle();
        args.putParcelable(NOTE_PARAM, note);
        args.putInt(LAYOUT_RESOURCE_PARAM, layoutResource);
        return args;
    }

    public ViewNoteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            note = getArguments().getParcelable(NOTE_PARAM);
            layoutResource = getArguments().getInt(LAYOUT_RESOURCE_PARAM);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View contentView = inflater.inflate(layoutResource, container, false);
        titleView = (TextView) contentView.findViewById(R.id.editTitle);
        textView = (TextView) contentView.findViewById(R.id.editText);
        imageView = (ImageView) contentView.findViewById(R.id.imageView);
        return contentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        prepareViews();
        imageView.setOnClickListener(this);
    }

    protected void prepareViews() {
        titleView.setText(note.getTitle());
        textView.setText(note.getText());
        getActivity().setTitle(R.string.title_activity_show_note); //fixme перенести в активити?
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
    public void onPrepareOptionsMenu(Menu menu) {
        getActivity().getMenuInflater().inflate(R.menu.menu_show_note, menu);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_show_note, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.delete_note :
                NotesManager.getNotesManager().removeObject(note);
                getActivity().finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
