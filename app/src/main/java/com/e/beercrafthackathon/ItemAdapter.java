package com.e.beercrafthackathon;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> implements Filterable {
    ArrayList<Item> list,filteredList;
    ArrayFilter mFilter;

    public ItemAdapter(ArrayList<Item> list) {
        this.list=list;
        filteredList=list;
    }


    public void setList(ArrayList<Item> list){
        this.list=list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context=viewGroup.getContext();
        int layoutIdForListItem= R.layout.item_view;
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        boolean attachToParentImm=false;

        View view=layoutInflater.inflate(layoutIdForListItem,viewGroup,attachToParentImm);
        ItemViewHolder itemViewHolder=new ItemViewHolder(view);
        return itemViewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int i) {
        itemViewHolder.bind(list.get(i));
    }
/*@Override
    public void onBindViewHolder(@NonNull ItemViewHolder ItemViewHolder,int i){
        ItemViewHolder.bind(list.get(i));
    }*/

    @Override
    public int getItemCount() {
        if(list!=null)
        return list.size();
        return 0;
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ArrayFilter();
        }
        return mFilter;

    }

    private class ArrayFilter extends Filter {
        private Object lock=new Object();


        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

            if (list == null) {
                synchronized (lock) {
                    list = new ArrayList<>();
                }
            }

            if (prefix == null || prefix.length() == 0) {
                synchronized (lock) {
                    results.values = filteredList;
                    results.count = filteredList.size();
                }
            } else {
                final String prefixString = prefix.toString().toLowerCase();

                ArrayList<Item> values = filteredList;
                int count = filteredList.size();

                ArrayList<Item> newValues = new ArrayList<Item>(count);

                for (int i = 0; i < count; i++) {
                    Item item = values.get(i);
                    if (!newValues.contains(item)) {
                        if (item.getName().toLowerCase().contains(prefixString) || item.getStyle().toLowerCase().contains(prefixString)) {
                            newValues.add(item);
                        }
                    }
                }

                results.values = newValues;
                results.count = newValues.size();
            }

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            if(results.values!=null){
                list = (ArrayList<Item>) results.values;
            }else{
                list = new ArrayList<Item>();

            }
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {

            }
        }
    }

/*
@Override
    public int getCount() {
        return fullList.size();
    }

    @Override
    public String getItem(int position) {
        return fullList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ArrayFilter();
        }
        return mFilter;
    }


    private class ArrayFilter extends Filter {
        private Object lock;

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

            if (list == null) {
                synchronized (lock) {
                    list = new ArrayList<String>(fullList);
                }
            }

            if (prefix == null || prefix.length() == 0) {
                synchronized (lock) {
                    ArrayList<String> list = new ArrayList<String>(list);
                    results.values = list;
                    results.count = list.size();
                }
            } else {
                final String prefixString = prefix.toString().toLowerCase();

                ArrayList<String> values = list;
                int count = values.size();

                ArrayList<String> newValues = new ArrayList<String>(count);

                for (int i = 0; i < count; i++) {
                    String item = values.get(i);
                    if (item.toLowerCase().contains(prefixString)) {
                        newValues.add(item);
                    }

                }

                results.values = newValues;
                results.count = newValues.size();
            }

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

        if(results.values!=null){
        fullList = (ArrayList<String>) results.values;
        }else{
            fullList = new ArrayList<String>();
        }
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
 */

    class ItemViewHolder extends RecyclerView.ViewHolder{
        TextView newprice,oldprice,name,quantity,category;
        Button plus,minus;
        LinearLayout addToCart;
        ImageView itemImage;
        ProgressBar progressBar;
        LinearLayout halfFullLayout;
        CardView cardView;
        String type;
        DatabaseCreation dbc;
        SQLiteDatabase dbw,dbr;
        ContentValues cv;
        Cursor cursor;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            newprice=(TextView)itemView.findViewById(R.id.new_price);
            oldprice=itemView.findViewById(R.id.old_price);
            category=itemView.findViewById(R.id.category_textview);
            type="ANY";
            name=(TextView)itemView.findViewById(R.id.name);
            quantity=(TextView)itemView.findViewById(R.id.quantity_tv);
            plus=(Button)itemView.findViewById(R.id.plus);
            minus=(Button)itemView.findViewById(R.id.minus);
            addToCart=(LinearLayout) itemView.findViewById(R.id.add_to_cart);
//            itemImage=(ImageView)itemView.findViewById(R.id.item_image);
//            progressBar=itemView.findViewById(R.id.progress_bar_in_itemview);
            halfFullLayout=itemView.findViewById(R.id.half_full_layout);
//            cardView=itemView.findViewById(R.id.view2);
            dbc=new DatabaseCreation(itemView.getContext());
            dbw=dbc.getWritableDatabase();
            dbr=dbc.getReadableDatabase();
            cv=new ContentValues();
        }
        public void bind(final Item item){
//            if (item.getFull().isEmpty())
//                item.setFull("0");
//            if (item.getHalf().isEmpty())
//                item.setHalf("0");
//            quantity.setText(item.getQuantity());
            name.setText(item.getName());
            newprice.setText(String.valueOf(item.getOunces()));
            oldprice.setText(item.getAbv());
            category.setText(item.getStyle());
            addToCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean writeToList=true;
                    item.setQuantity(Integer.parseInt(quantity.getText().toString()));
                    item.setTotal_size(String.valueOf(item.getQuantity()*item.getOunces()));
                    try {
                            cursor = dbr.rawQuery("select * from " + FeedReader.FeedEntry.TABLE_NAME, null);

                            if (cursor.moveToFirst()) {
                                while (!cursor.isAfterLast()) {
                                    if (cursor.getString(cursor.getColumnIndex(FeedReader.FeedEntry.ITEM_ID)).equalsIgnoreCase(String.valueOf(item.getId()))) {
                                        dbw.delete(FeedReader.FeedEntry.TABLE_NAME, FeedReader.FeedEntry.ITEM_ID+"="+item.getId(),null);
                                        writeToList = false;
                                    }
                                    cursor.moveToNext();
                                }
                            }
                        }catch (SQLiteException e){}
//                    if (writeToList){
                        cv.put(FeedReader.FeedEntry.ITEM_ID,item.getId());
                        cv.put(FeedReader.FeedEntry.ITEM_NAME,item.getName());
                        cv.put(FeedReader.FeedEntry.ITEM_PRICE,item.getOunces());
                        cv.put(FeedReader.FeedEntry.ITEM_QTY,item.getQuantity());
                        cv.put(FeedReader.FeedEntry.ITEM_TYPE,item.getStyle());

                        long newRowId= dbw.insert(FeedReader.FeedEntry.TABLE_NAME,null,cv);

                        if(newRowId!=-1 && writeToList)
                            MainActivity.cart_top.setText(String.valueOf((int)Integer.parseInt(MainActivity.cart_top.getText().toString())+1));
//                    }
//                    CartActivity.list.add(item);
                }
            });

            plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Integer.parseInt(quantity.getText().toString())<9)
                    quantity.setText(String.valueOf(Integer.parseInt(quantity.getText().toString())+1));
                }
            });
            minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(Integer.parseInt(quantity.getText().toString())>1)
                        quantity.setText(String.valueOf(Integer.parseInt(quantity.getText().toString())-1));
                }
            });
//            itemImage.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    String[] imagess=item.getImagess();
//                    Intent intent=new Intent(itemView.getContext(), ShowImagesActivity.class);
//                    intent.putExtra("imagess",imagess);
//                    intent.putExtra("mainImage",item.getUrl());
//                    MainActivity.mainActivity.startActivity(intent);
//                }
//            });
        }
    }
}
