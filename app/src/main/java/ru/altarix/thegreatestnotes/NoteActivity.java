package ru.altarix.thegreatestnotes;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import java.io.Serializable;
import java.util.Stack;

import ru.altarix.thegreatestnotes.model.Note;
import ru.altarix.thegreatestnotes.model.ObjectManager;
import ru.altarix.thegreatestnotes.model.ObjectManagerFactory;
import ru.altarix.thegreatestnotes.utils.Constants;
import ru.altarix.thegreatestnotes.utils.Constants.Action;
import ru.altarix.thegreatestnotes.utils.Constants.Extras;
import ru.altarix.thegreatestnotes.utils.OnNoteActionSelectedListener;
import ru.altarix.thegreatestnotes.utils.Utils;

/**
 * Created by samsmariya on 02.10.17.
 *
 * Отвечает за Создание/Просмотр/Редактирование заметки.
 * Использует фрагменты: {@link ViewNoteFragment} и {@link EditNoteFragment}
 *
 */
public class NoteActivity extends AppCompatActivity
        implements OnNoteActionSelectedListener {

    private static final String TAG = NoteActivity.class.getSimpleName();
    private static final String STATE_STACK = "STATE_STACK";

    private ObjectManager<Note> mNotesManager;
    private Note mNote;

    /**
     * Здесь используется паттерн Состояние для переключения между просмотром/редактированием.
     * Для поддержки актуального значения mActivityState нужно использовать для добавления/удаления
     * фрагментов только функции {@link #replaceFragment(Fragment, ActivityState)},
     * {@link #popFragment()}, {@link #popFragmentForState(ActivityState)}
     */
    ActivityState mActivityState; // текущее состояние
    StateStack mStateStack = new StateStack(); // история состояний
    ActivityState mInitialState = new InitialState();
    ActivityState mViewState = new ViewState();
    ActivityState mCreateState = new CreateState();
    ActivityState mEditState = new EditState();

    {
        // mInitialState всегда остаётся первым в стеке
        mStateStack.push(mInitialState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        mNotesManager = ObjectManagerFactory.getNotesManager(this);

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            mNote = intent.getParcelableExtra(Extras.NOTE);
            Utils.requireNonNull(mNote, TAG + ": NOTE extra can't be null");
            Constants.Action newAction = (Constants.Action) intent.getSerializableExtra(Extras.ACTION);
            if (newAction == null) {
                newAction = Action.VIEW;
            }
            mActivityState.applyAction(newAction);
        }else {
            mNote = savedInstanceState.getParcelable(Extras.NOTE);
            StateStack savedStack = (StateStack) savedInstanceState.getSerializable(STATE_STACK);
            if (savedStack != null) {
                mStateStack = savedStack;
                mActivityState = savedStack.peek();
            }
            /* FIXME: 09.10.17 пока на код никак не влияет, но это разрушает классику паттерна
             Состояние - восстановленные объекты НЕ равны ссылкам на переменные состояния Activity.
             Если начать сравнивать mActivityState == mEditState - не пройдёт.
            */
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Extras.NOTE, mNote);
        outState.putSerializable(STATE_STACK, mStateStack);
    }

    protected void replaceFragment(Fragment nextFragment, ActivityState nextState) {
        if (nextFragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            String fragmentTag = nextState.getFragmentTag();
            FragmentTransaction fTrans = fragmentManager.beginTransaction();
            fTrans.replace(R.id.fragment_container, nextFragment, fragmentTag);
            // первый добавленный фрагмент не должен удаляться по кнопке Назад,
            // а mInitialState - всегда на дне стека, поэтому размер mStateStack > 1
            boolean addToBackStack = mStateStack.size() > 1;
            if (addToBackStack) {
                fTrans.addToBackStack(fragmentTag);
            }
            fTrans.commit();
            mStateStack.push(nextState);
        }
    }

    /**
     * Удаляет верний фрагмент из стека.
     * @return Возвращает true если фрагмент был удалён, иначе - false.
     */
    protected boolean popFragment() {
        boolean result = false;
        FragmentManager fragmentManager = getSupportFragmentManager();
        int backStackSize = fragmentManager.getBackStackEntryCount();
        if (backStackSize > 0) {
            if (!popFragmentForState(mStateStack.peek())) {
                fragmentManager.popBackStack();
            }
            result = true;
        }
        return result;
    }

    /**
     * Удаляет фрагмент из стека, только если фрагмент соответствует состоянию аргумента state.
     * @return Возвращает true если фрагмент был удалён, иначе - false.
     */
    protected boolean popFragmentForState(ActivityState state) {
        boolean result = false;
        if (state != null) {
            String fragmentTag = state.getFragmentTag();
            if (fragmentTag != null) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                int backStackSize = fragmentManager.getBackStackEntryCount();
                if (backStackSize > 0) {
                    String entryTag = fragmentManager.getBackStackEntryAt(backStackSize - 1)
                            .getName();
                    if (fragmentTag.equals(entryTag)) {
                        fragmentManager.popBackStack();
                        mStateStack.pop();
                        result = true;
                    }
                }else if (fragmentManager.findFragmentByTag(fragmentTag) != null) {
                    mStateStack.pop();
                }
            }
        }
        return result;
    }

    protected void refreshFragmentState(Fragment fragment, Note note, Constants.Action action) {
        if (fragment == null) {
            return;
        }
        ViewNoteFragment viewNoteFragment = (ViewNoteFragment) fragment;
        viewNoteFragment.refreshState(note, action);
    }

    @Override
    public void onNoteActionSelected(Note note, Constants.Action action) {
        mNote = note;
        mActivityState.applyAction(action);
    }

    @Override
    public void onBackPressed() {
        if (mActivityState.onBackPressed()) {
            return;
        }
        forceOnBackPressed();
    }

    public void forceOnBackPressed() {
        if (!popFragment()) {
            finish();
        }
    }

    /**
     * Контейнер для состояний Активности. Поддерживает актуальное значение {@link #mActivityState}
     */
    protected class StateStack extends Stack<ActivityState> {

        @Override
        public ActivityState push(ActivityState item) {
            mActivityState = item;
            return super.push(item);
        }

        @Override
        public synchronized ActivityState pop() {
            ActivityState result = null;
            if (size() > 1) {
                result = super.pop();
                ActivityState nextState = peek();
                nextState.refreshState();
                mActivityState = nextState;
            }
            return result;
        }
    }

    /**
     * Базовый класс состояния Активности.
     * Каждому состоянию может соответствовать один фрагмент, которому обязательно должен быть
     * назначен Tag {@link #getFragmentTag()}
     * @param <T> - тип фрагмента, соответствующего состоянию.
     */
    protected abstract class ActivityState<T extends Fragment> implements Serializable {
        boolean mWasBackButtonPressed;

        public abstract @NonNull Constants.Action getAssignedAction();
        public T newInstanceOfFragment() { return null; }
        public Class<T> getFragmentClass() { return null; }
        public abstract int getTitleId();

        public T getFragment() {
            String fragmentTag = getFragmentTag();
            return (fragmentTag == null)
                    ? null
                    : (T) getSupportFragmentManager().findFragmentByTag(fragmentTag);
        }

        public String getFragmentTag() {
            if (getFragmentClass() == null) {
                return null;
            }
            return getFragmentClass().getSimpleName() + "&" + getAssignedAction().name();
        }

        public void applyAction(@NonNull Constants.Action action) {
            Note note = mNote;
            switch (action) {
                case NONE:
                    Utils.illegalArgument("Action can't be NONE there");
                    break;
                case CREATE: create(note); break;
                case VIEW: view(note); break;
                case EDIT: edit(note); break;
                case SAVE: save(note); break;
                case DELETE: delete(note); break;
            }
        }

        void create(Note note) { throw new UnsupportedOperationException(); }
        void view(Note note) { throw new UnsupportedOperationException(); }
        void edit(Note note) { throw new UnsupportedOperationException(); }
        void save(Note note) { throw new UnsupportedOperationException(); }

        void delete(Note note) {
            // FIXME: 08.10.17 добавить диалог
            mNotesManager.removeObject(note);
            finish();
        }

        // FIXME: 09.10.17 rename!
        public boolean onBackPressed() {
            return false;
        }

        void changeState(ActivityState nextState, boolean popCurrentState) {
            if (mWasBackButtonPressed) {
                mWasBackButtonPressed = false;
                NoteActivity.this.onBackPressed();
            }else if (popCurrentState) {
                boolean addNextStateFragment = (nextState.getFragment() == null);
                popFragmentForState(this);
                if (addNextStateFragment) {
                    showFragmentForState(nextState);
                }
            }else {
                showFragmentForState(nextState);
            }
        }

        void changeState(ActivityState nextState) {
            changeState(nextState, false);
        }

        void showFragmentForState(ActivityState state) {
            Fragment fragment = state.newInstanceOfFragment();
            replaceFragment(fragment, state);
            setTitle(state.getTitleId());
        }

        public void refreshState() {
            refreshFragmentState(getFragment(), mNote, getAssignedAction());
            setTitle(getTitleId());
        }
    }

    protected class InitialState extends ActivityState {

        @NonNull
        @Override
        public Constants.Action getAssignedAction() { return Action.NONE; }

        @Override
        public int getTitleId() { return R.string.title_fragment_view_note; }

        @Override
        void create(Note note) {
            changeState(mCreateState);
        }

        @Override
        void view(Note note) {
            changeState(mViewState);
        }

        @Override
        void edit(Note note) {
            changeState(mEditState);
        }
    }

    protected class EditState extends ActivityState<EditNoteFragment> {

        @NonNull
        @Override
        public Action getAssignedAction() { return Action.EDIT; }

        @Override
        public EditNoteFragment newInstanceOfFragment() {
            return EditNoteFragment.newInstance(mNote, getAssignedAction());
        }

        @Override
        public Class<EditNoteFragment> getFragmentClass() {
            return EditNoteFragment.class;
        }

        @Override
        public int getTitleId() { return R.string.title_fragment_edit_note; }

        @Override
        void save(Note note) {
            mNotesManager.saveObject(note);
            Snackbar.make(findViewById(R.id.coordinator_layout),
                    R.string.msg_note_saved,
                    Snackbar.LENGTH_LONG).show();
            changeState(mViewState, true);
        }

        @Override
        public boolean onBackPressed() {
            final EditNoteFragment fragment = getFragment();
            if (fragment != null && fragment.isSavingRequired()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(NoteActivity.this);
                builder.setMessage(getString(R.string.msg_is_saving_required));
                builder.setPositiveButton(R.string.button_save,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mWasBackButtonPressed = true;
                                fragment.saveNote();
                            }
                        });
                builder.setNegativeButton(R.string.button_cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                NoteActivity.this.forceOnBackPressed();
                            }
                        });
                builder.create().show();
                return true;
            }
            return super.onBackPressed();
        }
    }

    protected class CreateState extends EditState {

        @NonNull
        @Override
        public Action getAssignedAction() { return Action.CREATE; }

        @Override
        public int getTitleId() { return R.string.title_fragment_create_note; }
    }

    protected class ViewState extends ActivityState<ViewNoteFragment> {

        @NonNull
        @Override
        public Action getAssignedAction() { return Action.VIEW; }

        @Override
        public int getTitleId() { return R.string.title_fragment_view_note; }

        @Override
        public Class<ViewNoteFragment> getFragmentClass() {
            return ViewNoteFragment.class;
        }

        @Override
        public ViewNoteFragment newInstanceOfFragment() {
            return ViewNoteFragment.newInstance(mNote, getAssignedAction());
        }

        @Override
        void edit(Note note) {
            changeState(mEditState);
        }
    }

}
