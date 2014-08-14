package com.noveogroup.vuplayer.fragments;

import android.content.AsyncQueryHandler;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.text.method.KeyListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.noveogroup.vuplayer.LoginActivity;
import com.noveogroup.vuplayer.R;
import com.noveogroup.vuplayer.provider.ContentDescriptor;

public class NotesFragment extends Fragment {
    public static final String EXTRA_SELECT = "com.noveogroup.vuplayer.fragments.selected";
    public static final String EXTRA_VIDEO_NAME = "com.noveogroup.vuplayer.fragments.video.name";
    public static final String EXTRA_COMMENT = "com.noveogroup.vuplayer.fragments.comment";
    public static final String EXTRA_MODE = "com.noveogroup.vuplayer.fragments.mode";
    public static final String EXTRA_ID = "com.noveogroup.vuplayer.fragments.id";

    private static final String KEY_CURRENT_ID = "com.noveogroup.vuplayer.fragments.current.id";
    private static final String KEY_CURRENT_MODE = "com.noveogroup.vuplayer.fragments.current.mode";

    public static final String EXTRA_POST_STRINGS = "com.noveogroup.vuplayer.fragments.post.strings";

    public static final int MODE_ADD = 0;
    public static final int MODE_EDIT = 1;
    public static final int MODE_VIEW = 2;

    private long currentID = 0;
    private int currentMode;
    private EditText mEditSelect;
    private EditText mEditSource;
    private EditText mEditComment;
    private TextView mTextShare;
    private Button shareButton;

    private String[] postStrings;
    private boolean isToggle = false;
    private Menu mOptionsMenu;

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
        bundle.putLong(EXTRA_ID, idInTable);
        notesFragment.setArguments(bundle);
        return notesFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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

        mTextShare = (TextView) view.findViewById(R.id.notes_share_text);
        shareButton = (Button) view.findViewById(R.id.share_vk);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postStrings = new String[3];
                postStrings[0] = mEditSelect.getText().toString();
                postStrings[1] = mEditSource.getText().toString();
                postStrings[2] = mEditComment.getText().toString();
                Intent i = new Intent(getActivity(), LoginActivity.class);
                i.putExtra(EXTRA_POST_STRINGS, postStrings);
                startActivity(i);
            }
        });

        if(savedInstanceState == null) {
            Bundle bundle = getArguments();
            mEditSelect.setText(bundle.getString(EXTRA_SELECT));
            mEditSource.setText(bundle.getString(EXTRA_VIDEO_NAME));
            mEditComment.setText(bundle.getString(EXTRA_COMMENT));
            currentID = bundle.getLong(EXTRA_ID);
            changeMode(bundle.getInt(EXTRA_MODE));
        }

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState != null) {
            currentID = savedInstanceState.getLong(KEY_CURRENT_ID);
            changeMode(savedInstanceState.getInt(KEY_CURRENT_MODE));
            changeMenuItems();
        }
    }

    private void changeMode(int mode) {
        switch (mode) {
            case MODE_ADD:
                currentMode = MODE_ADD;
                mTextShare.setVisibility(View.GONE);
                shareButton.setVisibility(View.GONE);
                mEditSelect.setKeyListener((KeyListener)mEditSelect.getTag());
                mEditSource.setKeyListener((KeyListener)mEditSource.getTag());
                mEditComment.setKeyListener((KeyListener)mEditComment.getTag());
                changeMenuItems();
                break;
            case MODE_EDIT:
                currentMode = MODE_EDIT;
                mTextShare.setVisibility(View.GONE);
                shareButton.setVisibility(View.GONE);
                mEditSelect.setKeyListener((KeyListener) mEditSelect.getTag());
                mEditSource.setKeyListener((KeyListener) mEditSource.getTag());
                mEditComment.setKeyListener((KeyListener) mEditComment.getTag());
                changeMenuItems();
                break;
            case MODE_VIEW:
                currentMode = MODE_VIEW;
                mTextShare.setVisibility(View.VISIBLE);
                shareButton.setVisibility(View.VISIBLE);
                mEditSelect.setKeyListener(null);
                mEditSource.setKeyListener(null);
                mEditComment.setKeyListener(null);
                changeMenuItems();
                break;
        }
        changeMenuItems();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(KEY_CURRENT_ID, currentID);
        outState.putInt(KEY_CURRENT_MODE, currentMode);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mEditSelect = null;
        mEditSource = null;
        mEditComment = null;
        mTextShare = null;
        shareButton = null;
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.notes_menu, menu);
        mOptionsMenu = menu;
        changeMenuItems();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        mOptionsMenu = menu;
        changeMenuItems();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.toggle_item:
                if(isToggle) {
                    if(currentMode != MODE_VIEW) {
                        addNote();
                    }
                    if(!TextUtils.isEmpty(mEditSelect.getText())) {
                        item.setTitle(R.string.notes_edit);
                        item.setIcon(R.drawable.ic_edit);
                        changeMode(MODE_VIEW);
                        isToggle = false;
                    }
                }
                else {
                    item.setTitle(R.string.notes_save);
                    item.setIcon(R.drawable.ic_save);
                    changeMode(MODE_EDIT);
                    isToggle = true;
                }
                return true;
            case R.id.delete_item:
                String selection = ContentDescriptor.Notes.Cols.ID + " = " + currentID;
                new AsyncQueryHandler(getActivity().getContentResolver()){}.startDelete(1, null, ContentDescriptor.Notes.TABLE_URI, selection, null);
                getActivity().getSupportFragmentManager().popBackStack();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void changeMenuItems() {
        switch (currentMode) {
            case MODE_ADD:
                isToggle = true;
                break;
            case MODE_EDIT:
                isToggle = true;
                break;
            case MODE_VIEW:
                isToggle = false;
                break;
        }
        if(isToggle && mOptionsMenu != null) {
            mOptionsMenu.getItem(0).setTitle(R.string.notes_save);
            mOptionsMenu.getItem(0).setIcon(R.drawable.ic_save);
        }
        else if(mOptionsMenu != null) {
            mOptionsMenu.getItem(0).setTitle(R.string.notes_edit);
            mOptionsMenu.getItem(0).setIcon(R.drawable.ic_edit);
        }
    }
}
