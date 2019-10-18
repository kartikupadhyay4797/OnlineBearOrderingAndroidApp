package com.e.beercrafthackathon;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import java.util.ArrayList;

public class CustomTextChangedListener implements TextWatcher {

    public static final String TAG = "CustomTextChanged";
    Context context;
    public static ArrayList<Item> fullList;

    public CustomTextChangedListener(Context context){
        this.context = context;
        }

    @Override
    public void afterTextChanged(Editable s) {
        // TODO Auto-generated method stub

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTextChanged(CharSequence userInput, int start, int before, int count) {

        try{

            // if you want to see in the logcat what the user types
            Log.e(TAG, "User input: " + userInput);
            ProductListFragment.adapter.getFilter().filter(userInput);

            // update the adapater
           /* MainActivity.list.clear();
            for (int i=0;i<fullList.size();i++){
                MainActivity.list.add(fullList.get(i));
            }

            // get suggestions from the database
            for (int i=0;i<MainActivity.list.size();i++){
                if (!MainActivity.list.get(i).getName().contains(userInput.toString())){
                        MainActivity.list.remove(i);
                }
            }


            // update the adapter

            MainActivity.adapter.notifyDataSetChanged();

          //  MainActivity.searchIt.setAdapter(MainActivity.adapter);
          */

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
