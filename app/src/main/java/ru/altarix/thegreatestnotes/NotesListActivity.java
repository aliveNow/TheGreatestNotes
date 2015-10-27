package ru.altarix.thegreatestnotes;

import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import ru.altarix.thegreatestnotes.model.NotesManager;

public class NotesListActivity extends AppCompatActivity {

    private Context mContext = this;
    private NotesListFragment listFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_list);
        listFragment = NotesListFragment.newInstance(NotesManager.createNotesManager(this.getApplicationContext()));
        FragmentTransaction fTrans = getFragmentManager().beginTransaction();
        fTrans.replace(R.id.fragment, listFragment);
        fTrans.commit();
    }

}