package com.example.roman.targets;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.Inet4Address;
import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Database.db";
    private static final int DATABASE_VERSION = 1;

    private SQLiteDatabase db;

    public static class PagesTable implements BaseColumns
    {
        public static final String COLUMN_ID="id";
        public static final String TABLE_NAME = "pages_table";
        public static final String COLUMN_CATEGORY="category";
        public static final String COLUMN_CATEGORYNAME="category_name";
        public static final String COLUMN_TITLE="title";
        public static final String COLUMN_CARDS="cards";
        public static final String COLUMN_SECTION="section";
    }
    public static class CardsTable implements BaseColumns
    {
        public static final String TABLE_NAME = "cards_table";
        public static final String COLUMN_ID="id";
        public static final String COLUMN_TEXT="text";
        public static final String COLUMN_TITLE="title";
    }

    public  DBHelper(Context context)
    {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        this.db = db;

        final String SQL_CREATE_PAGES_TABLE = "CREATE TABLE " +
                DBHelper.PagesTable.TABLE_NAME + " ( " +
                DBHelper.PagesTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DBHelper.PagesTable.COLUMN_CATEGORY + " BOOLEAN, " +
                DBHelper.PagesTable.COLUMN_CATEGORYNAME + " TEXT, " +
                DBHelper.PagesTable.COLUMN_TITLE + " TEXT, " +
                PagesTable.COLUMN_ID + " INTEGER, " +
                PagesTable.COLUMN_CARDS + " TEXT, " +
                PagesTable.COLUMN_SECTION + " BOOLEAN " +
                ")";
        db.execSQL(SQL_CREATE_PAGES_TABLE);

        final String SQL_CREATE_CARDS_TABLE = "CREATE TABLE " +
                DBHelper.CardsTable.TABLE_NAME + " ( " +
                DBHelper.CardsTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DBHelper.CardsTable.COLUMN_TEXT + " TEXT, " +
                DBHelper.CardsTable.COLUMN_TITLE + " TEXT " +
                ")";
        db.execSQL(SQL_CREATE_CARDS_TABLE);
    }

    public void addPage(Page page) throws JSONException {
        ContentValues cv = new ContentValues();
        cv.put(PagesTable.COLUMN_ID, page.id);
        cv.put(PagesTable.COLUMN_CATEGORY, page.getCategory());
        cv.put(PagesTable.COLUMN_CATEGORYNAME,(page.getCategoryName()));
        cv.put(PagesTable.COLUMN_TITLE, page.getTitle());
        cv.put(PagesTable.COLUMN_SECTION, page.section);

        JSONObject json = new JSONObject();
        json.put("page_cards", new JSONArray(page.cards));
        String putCards = json.toString();
        cv.put(PagesTable.COLUMN_CARDS, putCards);

        db.insert(PagesTable.TABLE_NAME, null, cv);
    }

    public Card getCard(int id)
    {
        Cursor c = db.rawQuery("SELECT * FROM " + CardsTable.TABLE_NAME, null);
        Card card = new Card();
        c.moveToPosition(id);

        card.id = c.getInt(c.getColumnIndex(CardsTable._ID));
        card.text = c.getString(c.getColumnIndex(CardsTable.COLUMN_TEXT));
        card.title = c.getString(c.getColumnIndex(CardsTable.COLUMN_TITLE));
        c.close();
        return card;
    }

    public void addCard(Card card) {
        ContentValues cv = new ContentValues();
        cv.put(CardsTable.COLUMN_TEXT, card.text);
        cv.put(CardsTable.COLUMN_TITLE, card.title);
        db.insert(CardsTable.TABLE_NAME, null, cv);
    }

    public int editCard(Card card) {
        ContentValues cv = new ContentValues();
        cv.put(CardsTable.COLUMN_TEXT, card.text);
        cv.put(CardsTable.COLUMN_TITLE, card.title);

        return db.update(CardsTable.TABLE_NAME, cv, CardsTable.COLUMN_ID + " = ?",
                new String[]{String.valueOf(card.id)});
    }

    public int editPage(Page page) throws JSONException {
        ContentValues cv = new ContentValues();
        cv.put(PagesTable.COLUMN_CATEGORY, page.getCategory());
        cv.put(PagesTable.COLUMN_CATEGORYNAME,(page.getCategoryName()));
        cv.put(PagesTable.COLUMN_TITLE, page.getTitle());
        cv.put(PagesTable.COLUMN_SECTION, page.section);
        JSONObject json = new JSONObject();

        json.put("page_cards", new JSONArray(page.cards));
        String putCards = json.toString();
        cv.put(PagesTable.COLUMN_CARDS, putCards);
        return db.update(PagesTable.TABLE_NAME, cv, PagesTable.COLUMN_ID + " = ?",
                new String[]{String.valueOf(page.id)});
    }

        public void removePage(Page page){
            db.delete(PagesTable.TABLE_NAME,PagesTable.COLUMN_CATEGORY+"=? and "+PagesTable.COLUMN_CATEGORYNAME+"=? and "+PagesTable.COLUMN_TITLE+"=?",new String[]{page.getCategory(),page.category_name,page.getTitle()});
        }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + PagesTable.TABLE_NAME);
        onCreate(db);
        db.execSQL("DROP TABLE IF EXISTS " + CardsTable.TABLE_NAME);
        onCreate(db);
    }

    public ArrayList<Page> getAllPages() throws JSONException {
        ArrayList<Page> pageList = new ArrayList<>();
        db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + PagesTable.TABLE_NAME, null);

        if (c.moveToFirst()) {
            do {
                Page page = new Page();
                page.setCategory(c.getString(c.getColumnIndex(PagesTable.COLUMN_CATEGORY)));
                page.setCategoryName(c.getString(c.getColumnIndex(PagesTable.COLUMN_CATEGORYNAME)));
                page.setTitle(c.getString(c.getColumnIndex(PagesTable.COLUMN_TITLE)));
                page.id = c.getInt(c.getColumnIndex(PagesTable.COLUMN_ID));
                page.section = c.getInt(c.getColumnIndex(PagesTable.COLUMN_SECTION)) > 0;

                JSONObject json = new JSONObject(c.getString(c.getColumnIndex(PagesTable.COLUMN_CARDS)));
                JSONArray cards = json.optJSONArray("page_cards");
                for (int i = 0; i < cards.length(); i++) {
                    page.cards.add(cards.optInt(i));
                }
                pageList.add(page);
            } while (c.moveToNext());
        }
        c.close();
        return pageList;
    }

    public int cardTableSize()
    {
        Cursor c = db.rawQuery("SELECT * FROM " + CardsTable.TABLE_NAME, null);
        int res = c.getCount();
        c.close();
        return res;
    }
}