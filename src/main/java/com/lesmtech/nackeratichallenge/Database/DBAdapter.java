package com.lesmtech.nackeratichallenge.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBAdapter {

    public static final String DATABASE_NAME = "myDB";
    public static final String DATABASE_TABLE = "collections";
    public static final int DATABASE_VERSION = 1;
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String IMAGE = "image";
    public static final String SUMMARY = "summary";
    public static final String PRICE = "price";
    public static final String CONTENTTYPE = "contentType";
    public static final String RIGHTS = "rights";
    public static final String TITLE = "title";
    public static final String ARTIST = "artist";
    public static final String CATEGORY = "category";
    public static final String RELEASEDATE = "releaseDate";
    static final String DATABASE_CREATE = "CREATE TABLE collections (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT,image TEXT,summary TEXT,price TEXT,contentType TEXT, rights TEXT,title TEXT,artist TEXT, category TEXT,releaseDate TEXT);";

    DatabaseHelper DBHelper;
    final Context context;
    SQLiteDatabase db;

    public DBAdapter(Context ctx) {

        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    public static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // TODO Auto-generated method stub
            try {
                db.execSQL(DATABASE_CREATE);
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS contacts");
            onCreate(db);
        }

    }

    // open the database
    public DBAdapter open() {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        DBHelper.close();
    }

    public Cursor getAllCollection() {
        // query can order data set
        return db.query(DATABASE_TABLE, null, null, null, null, null,
                null);
    }

    public Cursor getFavourite(long rowId) {
        return db.query(DATABASE_TABLE, null, ID + "=" + rowId, null,
                null, null, null);
    }

    public boolean deleteCollection(String id) {
        return db.delete(DATABASE_TABLE,
                ID + "='" + id + "'", null) > 0;
    }

    public boolean insertCollection(String id, String name, String image, String summary, String price, String contenttype, String rights, String title, String artist, String category, String releaseDate) {
        ContentValues insertvalue = new ContentValues();
        insertvalue.put(ID, id);
        insertvalue.put(NAME, name);
        insertvalue.put(IMAGE, image);
        insertvalue.put(SUMMARY, summary);
        insertvalue.put(PRICE, price);
        insertvalue.put(CONTENTTYPE, contenttype);
        insertvalue.put(RIGHTS, rights);
        insertvalue.put(TITLE, title);
        insertvalue.put(ARTIST, artist);
        insertvalue.put(CATEGORY, category);
        insertvalue.put(RELEASEDATE, releaseDate);
        return db.insert(DATABASE_TABLE, null, insertvalue) > 0;
    }

    // judge whether database has same entry
    public Cursor queryCollection(String id) {
        // query whether has same row data in the table
        // MutiParameter "owner=? and price=?", new String[]{ owner, price }
        return db.query(DATABASE_TABLE, null, ID + "=?",
                new String[]{id}, null, null, null, null);

    }
}