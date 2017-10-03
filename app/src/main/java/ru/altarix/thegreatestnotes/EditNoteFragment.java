package ru.altarix.thegreatestnotes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import ru.altarix.thegreatestnotes.model.Note;
import ru.altarix.thegreatestnotes.utils.Constants;
import ru.altarix.thegreatestnotes.utils.Constants.Action;

public class EditNoteFragment extends ViewNoteFragment {
    private static final int PICK_IMAGE = 101;

    public static EditNoteFragment newInstance(Note note, Constants.Action action) {
        EditNoteFragment fragment = new EditNoteFragment();
        fragment.setArguments(bundleFromArguments(note, action));
        return fragment;
    }

    public EditNoteFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_edit_note, container, false);
        textTitle = (TextView) contentView.findViewById(R.id.edit_title);
        textNote = (TextView) contentView.findViewById(R.id.edit_note);
        return contentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        /*
        Получилось как в сказке про дудочку и кувшинчик. Есть кувшинчик - нет дудочки и наоборот.
        Один способ показа клавиатуры работает только при смене фрагментов,
        другой - только при добавлении фрагмента сразу при запуске Активности.
        Так что - пока так.
         */
        // на эмуляторе без этого не показывалась клавиатура при смене фрагментов
        InputMethodManager imgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imgr.showSoftInput(textTitle, InputMethodManager.SHOW_IMPLICIT);
        // на эмуляторе без этого не показывалась клавиатура для только что открытой Активности
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @Override
    protected void prepareViews() {
        super.prepareViews();
        View contentView = getView();
        TextInputLayout layoutEditTitle = (TextInputLayout) contentView.findViewById(R.id.layout_edit_title);
        TextInputLayout layoutEditNote = (TextInputLayout) contentView.findViewById(R.id.layout_edit_note);
        layoutEditTitle.setHint(getString(action == Action.CREATE
                ? R.string.hint_enter_title
                : R.string.hint_edit_title));
        layoutEditNote.setHint(getString(action == Action.CREATE
                ? R.string.hint_enter_note
                : R.string.hint_edit_note));

        textNote.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    saveNote();
                    return true;
                }
                return false;
            }
        });
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
            case R.id.menu_save_note:
                saveNote();
                break;
            case R.id.menu_add_picture:
                choosePictureFromGallery();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void saveNote() {
        note.setTitle(textTitle.getText().toString());
        note.setText(textNote.getText().toString());
        mCallback.onNoteActionSelected(note, Constants.Action.SAVE);
    }

    protected void choosePictureFromGallery() {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, getString(R.string.title_dialog_choose_picture));
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

        startActivityForResult(chooserIntent, PICK_IMAGE);
    }
}
