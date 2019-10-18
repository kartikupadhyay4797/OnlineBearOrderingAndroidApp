package com.e.beercrafthackathon;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.GRAY;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static TextView cart_top;
    public static ArrayList<Item> cartList;
    public static RecyclerView recyclerView;
    public static AutoCompleteTextView searchIt;
    public static ArrayList<Item> list;
    public static boolean backFromCart;
    DatabaseCreation dbc;
    LinearLayout inflatorViewGroup;
    SQLiteDatabase dbw,dbr;
    ContentValues cv;
    Cursor cursor;
    LayoutInflater layoutInflater;
    static ItemAdapter mainAdapter;
    static ViewGroup layoutView;
    NavigationView navigationView;
    static FragmentManager fragmentManager;
    public static MainActivity mainActivity;
    static int cartCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_bar);
        mainActivity= MainActivity.this;
        dbc=new DatabaseCreation(getApplicationContext());
        dbw=dbc.getWritableDatabase();
        dbr=dbc.getReadableDatabase();
        cv=new ContentValues();
        cartCount=0;
        try {
            cursor = dbr.rawQuery("select * from " + FeedReader.FeedEntry.TABLE_NAME, null);

            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    cartCount++;
//                    objlist.add(new MyObj(cursor.getString(cursor.getColumnIndex(FeedReader.FeedEntry.STATUS)), cursor.getString(cursor.getColumnIndex(FeedReader.FeedEntry.DETAIL)),
//                            cursor.getString(cursor.getColumnIndex(FeedReader.FeedEntry.DATE_ASSIGNED)), cursor.getString(cursor.getColumnIndex(FeedReader.FeedEntry.ASSIGNED_TO))));
                    cursor.moveToNext();
                }
            }
        }catch (SQLiteException e){}

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();

            navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
            navigationView.setItemHorizontalPadding(32);
            SpannableString spannableString;
            for (int i = 0; i < 7; i++) {
                MenuItem item = navigationView.getMenu().getItem(i);
                spannableString = new SpannableString(item.getTitle());
                if ((i > 1) && (i < 6)) {
                    spannableString.setSpan(new ForegroundColorSpan(GRAY), 0, spannableString.length(), 0);
                } else
                    spannableString.setSpan(new ForegroundColorSpan(BLACK), 0, spannableString.length(), 0);
                item.setTitle(spannableString);
            }
            navigationView.removeHeaderView(navigationView.getHeaderView(0));
            View headerView = navigationView.inflateHeaderView(R.layout.nav_header_navigation_bar);

//            Flo
            cart_top = (TextView) findViewById(R.id.cart_count);
//            if (cart_top.getText().toString().equals(""))
                cart_top.setText(String.valueOf(cartCount));


                list=new ArrayList<>();
        mainAdapter = new ItemAdapter(list);

        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        ProductListFragment fragment=new ProductListFragment();
        fragmentTransaction.replace(R.id.main_fragment,fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        OkHttpClient client=new OkHttpClient();
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url("http://starlord.hackerearth.com/beercraft")
                .get()
                .build();
//                try {
        Log.e("place order req: ",request.toString());
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, final IOException e) {
                Log.e(MainActivity.class.getSimpleName(), e.toString());
              MainActivity.this.runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                      new Toast(MainActivity.this).makeText(MainActivity.this,"failed to fetch data from api!!! due to "+e.getMessage(),Toast.LENGTH_LONG).show();
                  }
              });
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                JsonArray jsonArray=new JsonArray();
                String responseString=response.body().string();
                JsonParser jsonParser=new JsonParser();
                jsonArray= (JsonArray) jsonParser.parse(responseString);
                Item item;
                for (int i=0;i<jsonArray.size();i++){
                    item=new Item();
                    JsonObject jsonObject=jsonArray.get(i).getAsJsonObject();
                    item.setAbv(jsonObject.get("abv").getAsString());
                    item.setIbu(jsonObject.get("ibu").getAsString());
                    item.setId(jsonObject.get("id").getAsInt());
                    item.setName(jsonObject.get("name").getAsString());
                    item.setOunces(jsonObject.get("ounces").getAsFloat());
                    item.setStyle(jsonObject.get("style").getAsString());
                    list.add(item);
                    item=null;
                }

                MainActivity.mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (list!=null)
                            ProductListFragment.adapter.setList(list);
//                new Toast(MainActivity.this).makeText(MainActivity.this,"status code: "+response.body().getStatus()+"",Toast.LENGTH_LONG).show();
//                list = new ArrayList<>(response.body().getResults());
                        if (list==null)
                            new Toast(MainActivity.this).makeText(MainActivity.this,"list is null",Toast.LENGTH_LONG).show();

                    }
                });
            }
        });

//            int layoutInflatorOn = 0;
            layoutInflater = LayoutInflater.from(this);
            inflatorViewGroup = (LinearLayout) findViewById(R.id.search_here);

            cartList = new ArrayList<>();
            final Intent intentCart = new Intent(this, CartActivity.class);
            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(intentCart);
                    finish();
                }
            };
            cart_top.setOnClickListener(onClickListener);
            ImageView cart = (ImageView) findViewById(R.id.cart_top_icon);
            cart.setOnClickListener(onClickListener);

            final RelativeLayout search = (RelativeLayout) findViewById(R.id.search_icon);
            search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (layoutView != null) {
                        if (layoutView.getVisibility() == View.VISIBLE) {
                            searchIt.setText("", true);
                            layoutView.setVisibility(View.GONE);
                            layoutView=null;
                            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                            if(getCurrentFocus()!=null && getCurrentFocus().getWindowToken()!=null)
                            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                            CoordinatorLayout.LayoutParams layoutParams1=new CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            layoutParams1.setMargins(0,0,16*(getApplicationContext().getResources().getDisplayMetrics().densityDpi/ DisplayMetrics.DENSITY_DEFAULT),70*(getApplicationContext().getResources().getDisplayMetrics().densityDpi/ DisplayMetrics.DENSITY_DEFAULT));
                            layoutParams1.gravity= Gravity.BOTTOM|Gravity.END;
                            }
//                        } else if (layoutView.getVisibility() == View.GONE) {
//                            layoutView.setVisibility(View.VISIBLE);
//                            searchIt.setText("", true);
//                        }
                    } else {
                        layoutView = (ViewGroup) layoutInflater.inflate(R.layout.search_view, inflatorViewGroup, true);
                        layoutView.setVisibility(View.VISIBLE);
                        searchIt = (AutoCompleteTextView) findViewById(R.id.search_text);
                        searchIt.setThreshold(1);
                        searchIt.addTextChangedListener(new CustomTextChangedListener(MainActivity.this));
                        searchIt.setTextColor(getResources().getColor(R.color.colorPrimary));
                        searchIt.setText("", true);
                        CoordinatorLayout.LayoutParams layoutParams=new CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        layoutParams.setMargins(0,0,16*(getApplicationContext().getResources().getDisplayMetrics().densityDpi/ DisplayMetrics.DENSITY_DEFAULT),120*(getApplicationContext().getResources().getDisplayMetrics().densityDpi/ DisplayMetrics.DENSITY_DEFAULT));
                        layoutParams.gravity= Gravity.BOTTOM|Gravity.END;
                    }

                }
                    /*
                String[] searchList=new String[list.size()];

                for(int i=0;i<list.size();i++) {
                    searchList[i]=list.get(i).getName();
                }

                ArrayAdapter<String> tempAdapter=new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_spinner_dropdown_item,searchList);
*/
            });
        }

    @Override
    protected void onDestroy() {
        dbc.close();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (layoutView!=null && layoutView.getVisibility()==View.VISIBLE) {
            layoutView.setVisibility(View.GONE);
        }
            else{
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    public static void filterResults(String text){
        if(layoutView!=null && layoutView.getVisibility()==View.VISIBLE)
            layoutView.setVisibility(View.GONE);
        ProductListFragment.filter(text);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.home_activity_start) {
            startActivity(new Intent(this, MainActivity.class));
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.menu_filter) {
            for(int i=2;i<6;i++) {
                    navigationView.getMenu().getItem(i).setVisible(!navigationView.getMenu().getItem(i).isVisible());
            }
        }
        switch (id){
            case R.id.nav_logout:
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                break;
            case R.id.nav_home:
                drawer.closeDrawer(GravityCompat.START);
                Intent intentHome=new Intent(this, MainActivity.class);
                startActivity(intentHome);
                this.finish();
                break;
            case R.id.american_double:
                filterResults("American Double");
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.american_ipa:
                filterResults("American IPA");
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.american_porter:
                filterResults("American Porter");
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.oatmeal_stout:
                filterResults("Oatmeal Stout");
                drawer.closeDrawer(GravityCompat.START);
                break;
        }

        /*DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);*/
        return true;
    }
/**/

}
