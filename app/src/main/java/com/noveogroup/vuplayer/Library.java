package com.noveogroup.vuplayer;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class Library extends Fragment implements View.OnClickListener{
    private Button playVideoBtn;

    public interface OnClickButtonListener {
        void onClickButton();
    }

    @Override
    public void onClick(View v) {
        Log.d("Library", "In onClick");
        if(v.getId() == R.id.play_video) {
            Log.d("Library", "In onClick and condition true");
            OnClickButtonListener listener = (OnClickButtonListener) getActivity();
            listener.onClickButton();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_library, container, false);
        playVideoBtn = (Button) v.findViewById(R.id.play_video);
		playVideoBtn.setOnClickListener(this);

        return v;
    }


}
