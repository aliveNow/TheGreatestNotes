package ru.altarix.thegreatestnotes;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.Menu;
import android.view.MenuItem;

import ru.altarix.thegreatestnotes.model.Note;
import ru.altarix.thegreatestnotes.model.NotesManager;

public class EditNoteFragment extends ViewNoteFragment {
    private static final int PICK_IMAGE = 101;

    public static EditNoteFragment newInstance(Note note, int layoutResource) {
        EditNoteFragment fragment = new EditNoteFragment();
        fragment.setArguments(bundleFromArguments(note, layoutResource));
        return fragment;
    }

    public EditNoteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            Uri imageUri = data.getData();
            note.setImageUri(imageUri);
            prepareImageView();
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        getActivity().getMenuInflater().inflate(R.menu.menu_edit_note, menu);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.save_note :
                note.setTitle(titleView.getText().toString());
                note.setText(textView.getText().toString());
                NotesManager.getNotesManager().saveObject(note);
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
