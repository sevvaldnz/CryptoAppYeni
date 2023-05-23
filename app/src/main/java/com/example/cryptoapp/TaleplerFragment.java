package com.example.cryptoapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class TaleplerFragment extends Fragment {

    private View TaleplerFragmentView;

    private RecyclerView taleplerlistem;



    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    public TaleplerFragment() {

    }


    public static TaleplerFragment newInstance(String param1, String param2) {
        TaleplerFragment fragment = new TaleplerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        TaleplerFragmentView = inflater.inflate(R.layout.fragment_talepler, container, false);

        taleplerlistem= TaleplerFragmentView.findViewById(R.id.chat_talepleri_listesi);
        taleplerlistem.setLayoutManager(new LinearLayoutManager(getContext()));



        return TaleplerFragmentView;
    }
}