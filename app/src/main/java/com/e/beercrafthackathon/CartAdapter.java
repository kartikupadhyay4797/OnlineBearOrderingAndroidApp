package com.e.beercrafthackathon;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    ArrayList<Item> list;

    public CartAdapter(ArrayList<Item> list) {
        this.list=list;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context=viewGroup.getContext();
        int layoutIdForListItem= R.layout.items_in_cart;
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        boolean attachToParentImm=false;

        View view=layoutInflater.inflate(layoutIdForListItem,viewGroup,attachToParentImm);
        CartAdapter.CartViewHolder itemViewHolder=new CartAdapter.CartViewHolder(view);
        return itemViewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder itemViewHolder, int i) {
        itemViewHolder.bind(list.get(i));
    }
/*@Override
    public void onBindViewHolder(@NonNull ItemViewHolder ItemViewHolder,int i){
        ItemViewHolder.bind(list.get(i));
    }*/

    @Override
    public int getItemCount() {
        return list.size();
    }


    class CartViewHolder extends RecyclerView.ViewHolder{
        TextView itemCartName,itemCartAmount,itemCartQuantity,itemCartNewPrice,plus,minus,deleteItem;
        Context context;
        DatabaseCreation dbc;
        SQLiteDatabase dbw,dbr;
        ContentValues cv;
        Cursor cursor;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            dbc=new DatabaseCreation(itemView.getContext());
            dbw=dbc.getWritableDatabase();
            dbr=dbc.getReadableDatabase();
            cv=new ContentValues();
            itemCartName=(TextView)itemView.findViewById(R.id.cart_item_name);
            itemCartAmount=(TextView)itemView.findViewById(R.id.cart_item_total_amt);
            itemCartNewPrice=(TextView)itemView.findViewById(R.id.cart_item_new_price);
            itemCartQuantity=(TextView)itemView.findViewById(R.id.cart_item_quantity);
            plus=(TextView) itemView.findViewById(R.id.plus_cart);
            context=itemView.getContext();
            minus=(TextView) itemView.findViewById(R.id.minus_cart);
            deleteItem=itemView.findViewById(R.id.delete_item_cart);
            }
        public void bind(final Item item){
            final int quantity=item.getQuantity();
            final double new_price=item.getOunces(),total=quantity*new_price;

            itemCartName.setText(item.getName());

            itemCartQuantity.setText(String.valueOf(item.getQuantity()));
            itemCartNewPrice.setText("Oz"+String.valueOf(item.getOunces()));


            item.setTotal_size(String.valueOf(item.getQuantity()*item.getOunces()));
            itemCartAmount.setText("Oz"+item.getTotal_size());

            plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Integer.parseInt(itemCartQuantity.getText().toString())<9) {
                        int quantity=Integer.parseInt(itemCartQuantity.getText().toString())+1;
                        cv.put(FeedReader.FeedEntry.ITEM_QTY,String.valueOf(quantity));
                        dbw.update(FeedReader.FeedEntry.TABLE_NAME,cv,FeedReader.FeedEntry.ITEM_ID+"="+item.getId(),null);
                        item.setQuantity(quantity);
                        itemCartQuantity.setText(String.valueOf(quantity));
                        itemCartAmount.setText("Oz"+(double)quantity*(Double.parseDouble(itemCartNewPrice.getText().toString().substring(2))));
                        CartActivity.subtotalView.setText("Oz"+(Double.parseDouble(CartActivity.subtotalView.getText().toString().substring(2))+Double.parseDouble(itemCartNewPrice.getText().toString().substring(2))));
                        CartActivity.taxView.setText("Oz"+(Double.parseDouble(CartActivity.subtotalView.getText().toString().substring(2))*CartActivity.tax_percentage/100));
                        CartActivity.total_amt.setText("Oz"+(Double.parseDouble(CartActivity.subtotalView.getText().toString().substring(2))+Double.parseDouble(CartActivity.taxView.getText().toString().substring(2))));
                    }
                }
            });
            deleteItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(context)
                            .setTitle("Ooops!!!")
                            .setMessage("Do you want to delete this item from your cart?")

                            // Specifying a listener allows you to take an action before dismissing the dialog.
                            // The dialog is automatically dismissed when a dialog button is clicked.
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dbw.delete(FeedReader.FeedEntry.TABLE_NAME,FeedReader.FeedEntry.ITEM_ID+"="+item.getId(),null);
                                    CartActivity.list.remove(item);
                                    notifyItemRemoved(getAdapterPosition());
                                    notifyItemRangeChanged(getAdapterPosition(),CartActivity.list.size());
//                                        notify();
                                    CartActivity.recyclerViewItem.setAdapter(CartAdapter.this);
                                    CartActivity.subtotalView.setText("Oz"+(Double.parseDouble(CartActivity.subtotalView.getText().toString().substring(2))-Double.parseDouble(itemCartAmount.getText().toString().substring(2))));
                                    CartActivity.taxView.setText("Oz"+(Double.parseDouble(CartActivity.subtotalView.getText().toString().substring(2))*CartActivity.tax_percentage/100));
                                    CartActivity.total_amt.setText("Oz"+(Double.parseDouble(CartActivity.subtotalView.getText().toString().substring(2))+Double.parseDouble(CartActivity.taxView.getText().toString().substring(2))));
                                    dialog.dismiss();
                                    if(CartActivity.list.size()==0){
                                        CartActivity.unseen();
                                    }
                                    // Continue with delete operation
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setCancelable(false)

                            // A null listener allows the button to dismiss the dialog and take no further action.
                            .setIcon(R.drawable.nav_header_image)
                            .show();

                }
            });
            minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(Integer.parseInt(itemCartQuantity.getText().toString())>1) {
                        int quantity=Integer.parseInt(itemCartQuantity.getText().toString())-1;
                        cv.put(FeedReader.FeedEntry.ITEM_QTY,String.valueOf(quantity));
                        dbw.update(FeedReader.FeedEntry.TABLE_NAME,cv,FeedReader.FeedEntry.ITEM_ID+"="+item.getId(),null);
                        item.setQuantity(quantity);
                        itemCartQuantity.setText(String.valueOf(quantity));
                        itemCartAmount.setText("Oz"+(double)quantity*(Double.parseDouble(itemCartNewPrice.getText().toString().substring(2))));

                        CartActivity.subtotalView.setText("Oz"+(Double.parseDouble(CartActivity.subtotalView.getText().toString().substring(2))-Double.parseDouble(itemCartNewPrice.getText().toString().substring(2))));
                        CartActivity.taxView.setText("Oz"+(Double.parseDouble(CartActivity.subtotalView.getText().toString().substring(2))*CartActivity.tax_percentage/100));
                        CartActivity.total_amt.setText("Oz"+(Double.parseDouble(CartActivity.subtotalView.getText().toString().substring(2))+Double.parseDouble(CartActivity.taxView.getText().toString().substring(2))));
                    }
                }
            });
        }
    }

}
