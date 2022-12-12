package com.example.chatapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.chatapp.R;


public class ForumFragment extends Fragment {
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle
                                     savedInstanceState) {
        View  view = inflater.inflate(R.layout.fragment_forum, container, false);

        return view.getRootView();
    }
}