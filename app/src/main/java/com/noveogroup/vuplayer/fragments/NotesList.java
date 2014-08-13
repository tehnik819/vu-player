package com.noveogroup.vuplayer.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.noveogroup.vuplayer.R;
import com.noveogroup.vuplayer.provider.ContentDescriptor;

public class NotesList extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private ListView mListNotes;
    private SimpleCursorAdapter adapter;

    private static final String TAG = "NotesList";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        String[] from = new String[]{ContentDescriptor.Notes.Cols.WORD_COMBINATION
                , ContentDescriptor.Notes.Cols.SOURCE, ContentDescriptor.Notes.Cols.COMMENT};
        int[] to = new int[]{R.id.item_word_combination, R.id.item_source, R.id.item_comment};
        adapter = new SimpleCursorAdapter(getActivity(), R.layout.list_item, null,from, to, 0);
        getLoaderManager().initLoader(0, null, this);
        Log.d(TAG, "onCreateView()");
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        mListNotes = (ListView) view.findViewById(R.id.notes_view_notes);
        mListNotes.setAdapter(adapter);

        mListNotes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String wordCombination = ((TextView) view.findViewById(R.id.item_word_combination)).getText().toString();
                String source = ((TextView) view.findViewById(R.id.item_source)).getText().toString();
                String comment = ((TextView) view.findViewById(R.id.item_comment)).getText().toString();
                NotesFragment fragment = NotesFragment.newInstance(wordCombination, source, comment, NotesFragment.MODE_VIEW, id);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG, "onDestroyView()");
        super.onDestroyView();
        mListNotes = null;
        adapter = null;
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.notes_list_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.add_item:
                NotesFragment fragment = NotesFragment.newInstance(null, null, null);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, fragment)
                        .addToBackStack(null)
                        .commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
