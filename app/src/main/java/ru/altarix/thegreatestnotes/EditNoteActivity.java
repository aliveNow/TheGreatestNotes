package ru.altarix.thegreatestnotes;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ru.altarix.thegreatestnotes.model.Note;
import ru.altarix.thegreatestnotes.model.NotesManager;
import ru.altarix.thegreatestnotes.utils.ImageUtils;

public class EditNoteActivity extends AppCompatActivity {

    public static final String EDITABLE_KEY = "editable";
    private static final int PICK_IMAGE = 101;

    private Note note;
    private TextView titleView;
    private TextView textView;
    boolean editable = false;
    boolean wasShowActivity = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        Intent intent = getIntent();
        note = (Note) intent.getParcelableExtra(Note.class.getCanonicalName());

        titleView = (TextView) findViewById(R.id.editTitle);
        textView = (TextView) findViewById(R.id.editText);
        editable = intent.getBooleanExtra(EDITABLE_KEY, false);
        wasShowActivity = !editable;
        prepareViews();

        ImageView imgFavorite = (ImageView) findViewById(R.id.imageView);
        imgFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = EditNoteActivity.this.note.getImageUri();
                Intent intent = new Intent(EditNoteActivity.this, ShowImageActivity.class);
                intent.putExtra(ShowImageActivity.PICTURE_URI_KEY, uri);
                startActivity(intent);
            }
        });
    }

    protected void prepareViews() {
        titleView.setText(note.getTitle());
        textView.setText(note.getText());
        titleView.setEnabled(editable);
        textView.setEnabled(editable);
        setTitle(editable ? R.string.title_activity_edit_note : R.string.title_activity_show_note);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            note.setImageUri(imageUri);
            Bitmap bitmap = ImageUtils.getThumbnailBitmap(this, imageUri);
            ImageView imageView = (ImageView) findViewById(R.id.imageView);
            imageView.setImageBitmap(bitmap);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        if (editable) {
            getMenuInflater().inflate(R.menu.menu_edit_note, menu);
        }else {
            getMenuInflater().inflate(R.menu.menu_show_note, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_note :
                updateEditable(true);
                break;
            case R.id.save_note :
                note.setTitle(titleView.getText().toString());
                note.setText(textView.getText().toString());
                NotesManager.getNotesManager().saveObject(note);
                break;
            case R.id.delete_note :
                NotesManager.getNotesManager().removeObject(note);
                finish();
                return true;
            case R.id.add_picture :
                choosePictureFromGallery();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (editable && wasShowActivity) {
            updateEditable(false);
            return;
        }
        super.onBackPressed();
    }

    protected void updateEditable(boolean editable) {
        this.editable = editable;
        prepareViews();
        invalidateOptionsMenu();
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
