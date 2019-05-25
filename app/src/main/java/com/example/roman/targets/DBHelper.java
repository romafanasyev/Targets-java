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
    public static class CardsTable implements BaseColumns {
        public static final String TABLE_NAME = "cards_table";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_TEXT = "text";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_PAGE_ID = "page_id";
        public static final String COLUMN_DIVIDER="divider";
        public static final String COLUMN_TYPE="type";
        public static final String COLUMN_QUESTIONS="questions";
        public static final String COLUMN_LINKS="links";
    }

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
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
                CardsTable.COLUMN_ID + " INTEGER, " +
                DBHelper.CardsTable.COLUMN_TEXT + " TEXT, " +
                DBHelper.CardsTable.COLUMN_TITLE + " TEXT, " +
                DBHelper.CardsTable.COLUMN_PAGE_ID + " TEXT, " +
                CardsTable.COLUMN_DIVIDER + " BOOLEAN, " +
                CardsTable.COLUMN_TYPE + " INTEGER, " +
                CardsTable.COLUMN_QUESTIONS + " STRING, " +
                CardsTable.COLUMN_LINKS + " STRING " +
                ")";
        db.execSQL(SQL_CREATE_CARDS_TABLE);
    }
    public void addCard(Card card) {
        ContentValues cv = new ContentValues();
        cv.put(CardsTable.COLUMN_ID, card.id);
        cv.put(CardsTable.COLUMN_TEXT, card.text);
        cv.put(CardsTable.COLUMN_TITLE, card.title);
        cv.put(CardsTable.COLUMN_PAGE_ID, card.pageid);
        cv.put(CardsTable.COLUMN_DIVIDER, card.hasDivider);
        cv.put(CardsTable.COLUMN_TYPE, card.hasDivider);
        JSONObject json = new JSONObject();
        try {
            json.put("card_questions", new JSONArray(card.questions));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String putQuestions = json.toString();
        cv.put(CardsTable.COLUMN_QUESTIONS, putQuestions);
        json = new JSONObject();
        try {
            json.put("card_links", new JSONArray(card.links));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String putLinks = json.toString();
        cv.put(CardsTable.COLUMN_LINKS, putLinks);
        db.insert(CardsTable.TABLE_NAME, null, cv);
    }
    public int editCard(Card card) {
        ContentValues cv = new ContentValues();
        cv.put(CardsTable.COLUMN_TEXT, card.text);
        cv.put(CardsTable.COLUMN_TITLE, card.title);
        cv.put(CardsTable.COLUMN_PAGE_ID, card.pageid);
        cv.put(CardsTable.COLUMN_DIVIDER, card.hasDivider);
        JSONObject json = new JSONObject();
        try {
            json.put("card_questions", new JSONArray(card.questions));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String putQuestions = json.toString();
        cv.put(CardsTable.COLUMN_QUESTIONS, putQuestions);
        json = new JSONObject();
        try {
            json.put("card_links", new JSONArray(card.questions));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String putLinks = json.toString();
        cv.put(CardsTable.COLUMN_QUESTIONS, putLinks);

        return db.update(CardsTable.TABLE_NAME, cv, CardsTable.COLUMN_ID + " = ?",
                new String[]{String.valueOf(card.id)});
    }
    public void removeCard(Card card) {
        db.delete(CardsTable.TABLE_NAME, CardsTable.COLUMN_ID + " = ?", new String[]{Integer.toString(card.id)});
    }
    public ArrayList<Card> findPageCards(int pageId) {
        ArrayList<Card> list = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT * FROM " + CardsTable.TABLE_NAME + " WHERE " + CardsTable.COLUMN_PAGE_ID + "=?", new String[]{pageId+""});
        if (c.moveToFirst() && cardTableSize() > 0) {
            do {
                Card card = new Card();
                card.id = c.getInt(c.getColumnIndex(CardsTable.COLUMN_ID));
                card.pageid = c.getInt(c.getColumnIndex(CardsTable.COLUMN_PAGE_ID));
                card.title = c.getString(c.getColumnIndex(CardsTable.COLUMN_TITLE));
                card.text = c.getString(c.getColumnIndex(CardsTable.COLUMN_TEXT));
                card.hasDivider = c.getInt(c.getColumnIndex(CardsTable.COLUMN_DIVIDER)) > 0;
                card.type = c.getInt(c.getColumnIndex(CardsTable.COLUMN_TYPE));
                card.questions = new ArrayList<>();
                card.links = new ArrayList<>();
                /*JSONObject json = null;
                try {
                    json = new JSONObject(c.getString(c.getColumnIndex(CardsTable.COLUMN_QUESTIONS)));
                    JSONArray questions = json.optJSONArray("card_questions");
                    for (int i = 0; i < questions.length(); i++) {
                        card.questions.add(questions.optInt(i));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    json = new JSONObject(c.getString(c.getColumnIndex(CardsTable.COLUMN_LINKS)));
                    JSONArray links = json.optJSONArray("card_links");
                    for (int i = 0; i < links.length(); i++) {
                        card.links.add(links.optInt(i));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/
                list.add(card);
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }
    public int cardTableSize()
    {
        Cursor c = db.rawQuery("SELECT * FROM " + CardsTable.TABLE_NAME, null);
        int res = c.getCount();
        c.close();
        return res;
    }
//--------------------------P-A-G-E-S----------------------------------------------------------------------------------------------

    public void addPage(Page page) {
        ContentValues cv = new ContentValues();
        cv.put(PagesTable.COLUMN_ID, page.id);
        cv.put(PagesTable.COLUMN_CATEGORY, page.getCategory());
        cv.put(PagesTable.COLUMN_CATEGORYNAME,(page.getCategoryName()));
        cv.put(PagesTable.COLUMN_TITLE, page.getTitle());
        cv.put(PagesTable.COLUMN_SECTION, page.section);

        JSONObject json = new JSONObject();
        try {
            json.put("page_cards", new JSONArray(page.cards));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String putCards = json.toString();
        cv.put(PagesTable.COLUMN_CARDS, putCards);

        db.insert(PagesTable.TABLE_NAME, null, cv);
    }

    public int editPage(Page page) {
        ContentValues cv = new ContentValues();
        cv.put(PagesTable.COLUMN_ID, page.id);
        cv.put(PagesTable.COLUMN_CATEGORY, page.getCategory());
        cv.put(PagesTable.COLUMN_CATEGORYNAME,(page.getCategoryName()));
        cv.put(PagesTable.COLUMN_TITLE, page.getTitle());
        cv.put(PagesTable.COLUMN_SECTION, page.section);
        JSONObject json = new JSONObject();

        try {
            json.put("page_cards", new JSONArray(page.cards));
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

    public ArrayList<Page> getAllPages() {
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

                JSONObject json = null;
                try {
                    json = new JSONObject(c.getString(c.getColumnIndex(PagesTable.COLUMN_CARDS)));
                    JSONArray cards = json.optJSONArray("page_cards");
                    for (int i = 0; i < cards.length(); i++) {
                        page.cards.add(cards.optInt(i));
                    }
                    pageList.add(page);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } while (c.moveToNext());
        }
        c.close();
        return pageList;
    }
}
