package com.e.beercrafthackathon;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProductListFragment extends Fragment {
    public static ItemAdapter adapter;
    public static RecyclerView recyclerView;


    public ProductListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View fragmentview= inflater.inflate(R.layout.fragment_product_list, container, false);

        recyclerView = (RecyclerView) fragmentview.findViewById(R.id.reView);
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view= MainActivity.mainActivity.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) MainActivity.mainActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                return false;
            }
        });


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(fragmentview.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        adapter = MainActivity.mainAdapter;
        recyclerView.setAdapter(adapter);

        return fragmentview;
    }
    public static void filter(String text){
        MainActivity.mainAdapter.getFilter().filter(text);
        adapter.notifyDataSetChanged();
    }

}
