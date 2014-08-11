/*******************************************************************************
 * Copyright (c) 2014 Sergey Bragin and Alexandr Valov
 ******************************************************************************/

package com.noveogroup.vuplayer.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.noveogroup.vuplayer.R;

public class Library extends Fragment {
    private Button playVideoBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("Library", "onCreateView()");
        View v = inflater.inflate(R.layout.fragment_library, container, false);
        playVideoBtn = (Button) v.findViewById(R.id.play_video);
		playVideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Library", "Button start clicked");

                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, new VideoFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });
        return v;
    }

    @Override
    public void onDestroyView() {
        Log.d("Library", "onDestroyView()");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.d("Library", "onDestroy()");
        super.onDestroy();
    }
}
