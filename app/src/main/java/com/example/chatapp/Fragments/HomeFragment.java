package com.example.chatapp.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.chatapp.R;


public class HomeFragment extends Fragment {
    private CardView btn_home,btn_home2,btn_home3;
    //private ActivityMainBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        btn_home = view.findViewById(R.id.trova_cardView);
        btn_home2 = view.findViewById(R.id.cerca_cardView);
        btn_home3 = view.findViewById(R.id.organizza_cardView);

        btn_home.setOnClickListener(view1 -> {
            Intent intent = new Intent(requireActivity(), TrovamicoActivity.class);
            startActivity(intent);
        });

       btn_home2.setOnClickListener(view1 -> {
           Intent intent = new Intent(requireActivity(), Cercaincontro.class);
          startActivity(intent);
       });
        btn_home3.setOnClickListener(view1 -> {
            Intent intent = new Intent(requireActivity(), Organizzaincontro.class);
            startActivity(intent);
        });

        return view.getRootView();
    }
}