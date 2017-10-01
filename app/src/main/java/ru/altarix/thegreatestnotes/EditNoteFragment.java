package ru.altarix.thegreatestnotes;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import ru.altarix.thegreatestnotes.model.Note;
import ru.altarix.thegreatestnotes.utils.Constants;

public class EditNoteFragment extends ViewNoteFragment {
    private static final int PICK_IMAGE = 101;

    public static EditNoteFragment newInstance(Note note) {
        EditNoteFragment fragment = new EditNoteFragment();
        fragment.setArguments(bundleFromArguments(note));
        return fragment;
    }

    public EditNoteFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_edit_note, container, false);
        return contentView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE) {
            Uri imageUri = data.getData();
            note.setImageUri(imageUri);
            prepareImageView();
        }
    }

    @Override
    protected boolean onBeforeCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_edit_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_note :
                note.setTitle(titleView.getText().toString());
                note.setText(textView.getText().toString());
                mCallback.noteActionWasSelected(0, note, Constants.Action.SAVE);
                break;
            case R.id.add_picture :
                choosePictureFromGallery();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void choosePictureFromGallery() {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, getString(R.string.choose_picture));
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

        startActivityForResult(chooserIntent, PICK_IMAGE);
    }
}
