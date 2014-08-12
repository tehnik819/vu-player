package com.noveogroup.vuplayer.fragments;

import android.content.AsyncQueryHandler;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.TextUtils;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.noveogroup.vuplayer.R;
import com.noveogroup.vuplayer.provider.ContentDescriptor;

public class NotesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String EXTRA_SELECT = "com.noveogroup.vuplayer.fragments.selected";
    public static final String EXTRA_VIDEO_NAME = "com.noveogroup.vuplayer.fragments.video.name";
    public static final String EXTRA_COMMENT = "com.noveogroup.vuplayer.fragments.comment";
    public static final String EXTRA_MODE = "com.noveogroup.vuplayer.fragments";
    public static final String EXTRA_ID = "com.noveogroup.vuplayer.fragments";

    private static final String KEY_CURRENT_ID = "com.noveogroup.vuplayer.fragments.current.id";
    private static final String KEY_CURRENT_POSITION = "com.noveogroup.vuplayer.fragments.current.position";
    private static final String KEY_CURRENT_MODE = "com.noveogroup.vuplayer.fragments.current.mode";

    public static final int MODE_ADD = 0;
    public static final int MODE_EDIT = 1;
    public static final int MODE_VIEW = 2;

    private long currentID = 0;
    private int currentPosition = 0;
    private int currentMode = MODE_ADD;
    private EditText mEditSelect;
    private EditText mEditSource;
    private EditText mEditComment;
    private ToggleButton mToggleButton;
    private Button mButtonDelete;

    private ListView mListNotes;
    private SimpleCursorAdapter adapter;

    private static final String TAG = "NotesFragment";

    public static NotesFragment newInstance(String selected,String videoName, String comment) {
        NotesFragment notesFragment = new NotesFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_SELECT, selected);
        bundle.putString(EXTRA_VIDEO_NAME, videoName);
        bundle.putString(EXTRA_COMMENT, comment);
        bundle.putInt(EXTRA_MODE, MODE_ADD);
        bundle.putLong(EXTRA_ID, 0);
        notesFragment.setArguments(bundle);
        return notesFragment;
    }

    public static NotesFragment newInstance(String selected,String videoName, String comment, int mode, long idInTable) {
        NotesFragment notesFragment = new NotesFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_SELECT, selected);
        bundle.putString(EXTRA_VIDEO_NAME, videoName);
        bundle.putString(EXTRA_COMMENT, comment);
        bundle.putInt(EXTRA_MODE, mode);
        if(mode == MODE_ADD) {
            bundle.putLong(EXTRA_ID, 0);
        }
        else {
            bundle.putLong(EXTRA_ID, idInTable);
        }
        notesFragment.setArguments(bundle);
        return notesFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String[] from = new String[]{ContentDescriptor.Notes.Cols.WORD_COMBINATION
                , ContentDescriptor.Notes.Cols.SOURCE, ContentDescriptor.Notes.Cols.COMMENT};
        int[] to = new int[]{R.id.item_word_combination, R.id.item_source, R.id.item_comment};
        adapter = new SimpleCursorAdapter(getActivity(), R.layout.list_item, null,from, to, 0);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notes, container, false);

        mEditSelect = (EditText) view.findViewById(R.id.notes_edit_word_combination);
        mEditSource = (EditText) view.findViewById(R.id.notes_edit_source);
        mEditComment = (EditText) view.findViewById(R.id.notes_edit_comment);
        mEditSelect.setTag(mEditSelect.getKeyListener());
        mEditSource.setTag(mEditSource.getKeyListener());
        mEditComment.setTag(mEditComment.getKeyListener());

        mToggleButton = (ToggleButton) view.findViewById(R.id.notes_toggle_button);
        mButtonDelete = (Button) view.findViewById(R.id.notes_button_delete);
        mListNotes = (ListView) view.findViewById(R.id.notes_view_notes);
        mListNotes.setAdapter(adapter);
        if(savedInstanceState == null) {
            Bundle bundle = getArguments();
            mEditSelect.setText(bundle.getString(EXTRA_SELECT));
            mEditSource.setText(bundle.getString(EXTRA_VIDEO_NAME));
            mEditComment.setText(bundle.getString(EXTRA_COMMENT));
            changeMode(bundle.getInt(EXTRA_MODE));
            currentID = bundle.getLong(EXTRA_ID);
        }


        if(currentMode == MODE_ADD) {
            mToggleButton.setChecked(true);
            mToggleButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_save, 0);
        }

        mToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    changeMode(MODE_EDIT);
                    mToggleButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_save, 0);
                }
                else {
                    if(currentMode != MODE_VIEW) {
                        addNote();
                    }
                    changeMode(MODE_VIEW);
                    mToggleButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_edit, 0);
                }
            }
        });

        mButtonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selection = ContentDescriptor.Notes.Cols.ID + " = " + currentID;
                new AsyncQueryHandler(getActivity().getContentResolver()){}.startDelete(1, null, ContentDescriptor.Notes.TABLE_URI, selection, null);
                int pos = currentPosition - 1;
                if(pos >= 0) {
                    mListNotes.performItemClick(mListNotes.getChildAt(pos), pos, mListNotes.getItemIdAtPosition(pos));
                }
                else if((pos += 2) < mListNotes.getChildCount()) {
                    mListNotes.performItemClick(mListNotes.getChildAt(pos), pos, mListNotes.getItemIdAtPosition(pos));
                }
                else {
                    currentID = 0;
                    currentPosition = 0;
                    mEditSelect.setText("");
                    mEditSource.setText("");
                    mEditComment.setText("");
                }
            }
        });

        mListNotes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                changeMode(MODE_VIEW);
                mToggleButton.setChecked(false);
                mToggleButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_edit, 0);
                String wordCombination = ((TextView)view.findViewById(R.id.item_word_combination)).getText().toString();
                String source = ((TextView)view.findViewById(R.id.item_source)).getText().toString();
                String comment = ((TextView)view.findViewById(R.id.item_comment)).getText().toString();

                mEditSelect.setText(wordCombination);
                mEditSource.setText(source);
                mEditComment.setText(comment);
                currentID = id;
                currentPosition = position;
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState != null) {
            currentID = savedInstanceState.getLong(KEY_CURRENT_ID);
            currentPosition = savedInstanceState.getInt(KEY_CURRENT_POSITION);
            changeMode(savedInstanceState.getInt(KEY_CURRENT_MODE));
        }
    }

    private void changeMode(int mode) {
        switch (mode) {
            case MODE_ADD:
                currentMode = MODE_ADD;
                mEditSelect.setKeyListener((KeyListener)mEditSelect.getTag());
                mEditSource.setKeyListener((KeyListener)mEditSource.getTag());
                mEditComment.setKeyListener((KeyListener)mEditComment.getTag());
                break;
            case MODE_EDIT:
                currentMode = MODE_EDIT;
                mEditSelect.setKeyListener((KeyListener) mEditSelect.getTag());
                mEditSource.setKeyListener((KeyListener) mEditSource.getTag());
                mEditComment.setKeyListener((KeyListener) mEditComment.getTag());
                break;
            case MODE_VIEW:
                currentMode = MODE_VIEW;
                mEditSelect.setKeyListener(null);
                mEditSource.setKeyListener(null);
                mEditComment.setKeyListener(null);
                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(KEY_CURRENT_ID, currentID);
        outState.putInt(KEY_CURRENT_POSITION, currentPosition);
        outState.putInt(KEY_CURRENT_MODE, currentMode);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mEditSelect = null;
        mEditSource = null;
        mEditComment = null;
        mToggleButton = null;
        mButtonDelete = null;
        mListNotes = null;
        adapter = null;
    }

    private void addNote() {
        if(!TextUtils.isEmpty(mEditSelect.getText())) {
            ContentValues values = new ContentValues();
            switch (currentMode) {
                case MODE_ADD :
                    values.put(ContentDescriptor.Notes.Cols.WORD_COMBINATION, mEditSelect.getText().toString());
                    values.put(ContentDescriptor.Notes.Cols.SOURCE, mEditSource.getText().toString());
                    values.put(ContentDescriptor.Notes.Cols.COMMENT, mEditComment.getText().toString());
                    new AsyncQueryHandler(getActivity().getContentResolver()){}.startInsert(1, null, ContentDescriptor.Notes.TABLE_URI, values);
                    break;
                case MODE_EDIT :
                    values.put(ContentDescriptor.Notes.Cols.WORD_COMBINATION, mEditSelect.getText().toString());
                    values.put(ContentDescriptor.Notes.Cols.SOURCE, mEditSource.getText().toString());
                    values.put(ContentDescriptor.Notes.Cols.COMMENT, mEditComment.getText().toString());
                    String selection = ContentDescriptor.Notes.Cols.ID + " = " + currentID;
                    new AsyncQueryHandler(getActivity().getContentResolver()){}.startUpdate(1, null, ContentDescriptor.Notes.TABLE_URI, values, selection, null);
                    break;
            }
        }
        else {
            Toast.makeText(getActivity(), getString(R.string.empty_field_message), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), ContentDescriptor.Notes.TABLE_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(adapter != null && data != null) {
            adapter.changeCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (adapter != null) {
            adapter.changeCursor(null);
        }
    }
}
