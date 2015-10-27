package ru.altarix.thegreatestnotes;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import java.io.IOException;

public class ShowImageActivity extends Activity implements View.OnClickListener {

    public static final String PICTURE_URI_KEY = "picture_uri";

    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);
        setTitle("");

        Intent intent = getIntent();
        Uri uri = intent.getParcelableExtra(PICTURE_URI_KEY);
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            imageView.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageView.setOnClickListener(this);

        hideSystemBars();
    }

    protected void hideSystemBars() {
        getWindow().
                getDecorView().
                setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_show_image, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        if (bitmap != null) {
            bitmap.recycle();
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        hideSystemBars();
    }
}
