package com.e.beercrafthackathon;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseCreation extends SQLiteOpenHelper {
    Context context;
    final static int DB_VERSION=10;
    final static String DB_NAME="mydatabase";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + FeedReader.FeedEntry.TABLE_NAME + " (" +
                    FeedReader.FeedEntry._ID + " INTEGER, " +
                    FeedReader.FeedEntry.ITEM_NAME + " TEXT PRIMARY KEY, " +
                    FeedReader.FeedEntry.ITEM_ID + " TEXT, "+
                    FeedReader.FeedEntry.ITEM_QTY + " TEXT, "+
                    FeedReader.FeedEntry.ITEM_TYPE + " TEXT, "+
                    FeedReader.FeedEntry.ITEM_PRICE + " TEXT ) ";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FeedReader.FeedEntry.TABLE_NAME;

    public DatabaseCreation(Context context) {
        super(context,DB_NAME, null, DB_VERSION);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

}
