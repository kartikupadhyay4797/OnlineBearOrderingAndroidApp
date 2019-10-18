package com.e.beercrafthackathon;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class CartActivity extends AppCompatActivity {
    Intent intent1;
    public static RecyclerView recyclerViewItem;
    public static double tax_percentage;
    public static TextView subtotalView,total_amt,taxView;
    static LinearLayout paymentMode;
    static RadioGroup paymentSelection;
    static RadioButton selectedPaymentMethod;
    static RelativeLayout checkout;
    static CartAdapter adapterItem;
    public static ArrayList<Item> list;
    static Activity Cart;
    static Button pressToPlaceOrder;
    static double total;
    static ScrollView scroll;
    static ProgressBar progress;
    static String checksum;
    static View view;
    static String orderId;
    static final int PAYTMTRANSACTIONREQUESTCODE=1;
    static String orderObjString;
    static JsonObject orderObj;
    static DatabaseCreation dbc;
    static SQLiteDatabase dbw,dbr;
    static ContentValues cv;
    static Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_cart);
        progress=findViewById(R.id.progress_in_cart);
        Cart=CartActivity.this;
        paymentMode=findViewById(R.id.payment_method);
        paymentSelection=findViewById(R.id.payment_selection);
        scroll=findViewById(R.id.scroll_in_cart_activity);

        list=new ArrayList<>();
        dbc=new DatabaseCreation(getApplicationContext());
        dbw=dbc.getWritableDatabase();
        dbr=dbc.getReadableDatabase();
        cv=new ContentValues();

        try {
            cursor = dbr.rawQuery("select * from " + FeedReader.FeedEntry.TABLE_NAME, null);

            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    Item item=new Item();
                    item.setName(cursor.getString(cursor.getColumnIndex(FeedReader.FeedEntry.ITEM_NAME)));
                    item.setId((int)Integer.parseInt(cursor.getString(cursor.getColumnIndex(FeedReader.FeedEntry.ITEM_ID))));
                    item.setOunces((float)Float.parseFloat(cursor.getString(cursor.getColumnIndex(FeedReader.FeedEntry.ITEM_PRICE))));
                    item.setQuantity(Integer.parseInt(cursor.getString(cursor.getColumnIndex(FeedReader.FeedEntry.ITEM_QTY))));
                    item.setStyle(cursor.getString(cursor.getColumnIndex(FeedReader.FeedEntry.ITEM_TYPE)));
                    item.setTotal_size(String.valueOf(item.getQuantity()*item.getOunces()));
                    list.add(item);
                    cursor.moveToNext();
                }
            }
        }catch (SQLiteException e){}

        if(list==null)
            list=new ArrayList<Item>();

        orderId=String.valueOf(Math.abs(new Random().nextLong()));
        tax_percentage=0;
        float subtotal=0;
        if(list!=null)
        subtotal=getSubtotal(subtotal,list);
        if(subtotal<0)
            subtotal=0;
        subtotalView=(TextView)findViewById(R.id.subtotal);
        subtotalView.setText("Oz"+(double)subtotal);
        taxView =(TextView)findViewById(R.id.tax_final_amount);
        double tax=subtotal*tax_percentage/100;
        if(tax<=0)
            tax=0.00;
        taxView.setText("Oz"+tax);
        total_amt=(TextView)findViewById(R.id.total_amt);
        total=subtotal+tax;

        total_amt.setText("Oz"+total);

        recyclerViewItem=(RecyclerView)findViewById(R.id.items_in_cart);
        if (list!=null && list.size()!=0)
            findViewById(R.id.item_card_view_in_cart).setVisibility(View.VISIBLE);

        LinearLayoutManager linearLayoutManagerCartItems = new LinearLayoutManager(this);
        recyclerViewItem.setLayoutManager(linearLayoutManagerCartItems);
        recyclerViewItem.setHasFixedSize(true);

        adapterItem = new CartAdapter(list);
        recyclerViewItem.setAdapter(adapterItem);

        Button continueShopping=(Button)findViewById(R.id.continue_shopping);
        continueShopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(CartActivity.this, MainActivity.class);
                intent.putExtra("backFromCart",true);
                startActivity(intent);
                CartActivity.this.finish();
            }
        });
        pressToPlaceOrder=findViewById(R.id.place_order);

        checkout=(RelativeLayout) findViewById(R.id.checkout);
        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(list.size()>0) {

                        paymentMode.setVisibility(View.VISIBLE);
                        checkout.setVisibility(View.GONE);
                        pressToPlaceOrder.setVisibility(View.VISIBLE);
//                    scroll.fullScroll(View.FOCUS_DOWN);
                        scroll.post(new Runnable() {
                            @Override
                            public void run() {
                                scroll.fullScroll(View.FOCUS_DOWN);
                            }
                        });
                    }
                else {
                    new AlertDialog.Builder(CartActivity.this)
                            .setTitle("Oops!!!")
                            .setMessage("Your cart is empty, add at least one item to your cart first.")

                            // Specifying a listener allows you to take an action before dismissing the dialog.
                            // The dialog is automatically dismissed when a dialog button is clicked.
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    // Continue with delete operation
                                }
                            })

                            // A null listener allows the button to dismiss the dialog and take no further action.
                            .setIcon(R.drawable.nav_header_image)
                            .show();
                }
            }
        });

        paymentSelection.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                selectedPaymentMethod=group.findViewById(checkedId);
                if (selectedPaymentMethod.getText().toString().equals("Cash")){
                    pressToPlaceOrder.setText("Place Order");
                }
            }
        });

        pressToPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acknowledge();
            }
        });
    }


    public static void unseen(){
        paymentMode.setVisibility(View.GONE);
        checkout.setVisibility(View.VISIBLE);
        pressToPlaceOrder.setVisibility(View.GONE);
//                    scroll.fullScroll(View.FOCUS_DOWN);
        scroll.post(new Runnable() {
            @Override
            public void run() {
                scroll.fullScroll(View.FOCUS_UP);
            }
        });
    }

    public static float getSubtotal(float subtotal,ArrayList<Item> list){
        for (int i=0;i<list.size();i++){
            subtotal+=(Float.parseFloat(list.get(i).getTotal_size()));
        }
        return subtotal;
    }
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()) {
            case android.R.id.home:
                Intent intent=new Intent(CartActivity.this, MainActivity.class);
                intent.putExtra("backFromCart",true);
                startActivity(intent);
                CartActivity.this.finish();
        }
        return true;
    }

    static void acknowledge(){
        new AlertDialog.Builder(Cart)
                .setTitle("Order Placed!")
                .setMessage("Your order has been placed.")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        // Continue with delete operation
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setIcon(R.drawable.nav_header_image).setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                CartActivity.Cart.startActivity(new Intent(Cart, MainActivity.class));
                CartActivity.Cart.finish();
            }
        }).setCancelable(false).show();
        dbw.execSQL("delete from "+ FeedReader.FeedEntry.TABLE_NAME);
        MainActivity.cartList.clear();
        adapterItem.notifyDataSetChanged();
    }
        @Override
    public void onBackPressed() {
//            MainActivity.backFromCart=true;
            Intent intent=new Intent(CartActivity.this, MainActivity.class);
            intent.putExtra("backFromCart",true);
        startActivity(intent);
            CartActivity.this.finish();
        }

    @Override
    protected void onDestroy() {
        dbc.close();
        super.onDestroy();
    }
}

