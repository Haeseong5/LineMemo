package com.haeseong5.android.linememo.memo.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.haeseong5.android.linememo.memo.database.models.Image;
import com.haeseong5.android.linememo.memo.database.models.Memo;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "memos_db";
    private static final String TAG = DatabaseHelper.class.getName();

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        // create notes table
        db.execSQL(Memo.CREATE_TABLE);
        db.execSQL(Image.CREATE_TABLE_IMAGE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + Memo.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Image.TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    public long insertNote(String title, String content, ArrayList<byte[]> images) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(Memo.COLUMN_TITLE, title);
        values.put(Memo.COLUMN_CONTENT, content);
//        values.put(Memo.COLUMN_IMAGE, image);

        // insert row
        long id = db.insert(Memo.TABLE_NAME, null, values);

        // insert image_table
        for (byte[] b : images){
            addImage(id, b);
        }

        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }

    public Memo getNote(long id) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor memoCursor = db.query(Memo.TABLE_NAME,
                new String[]{Memo.COLUMN_ID, Memo.COLUMN_TITLE, Memo.COLUMN_CONTENT},
                Memo.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (memoCursor != null)
            memoCursor.moveToFirst();

        // prepare note object
        Memo memo = new Memo(
                memoCursor.getInt(memoCursor.getColumnIndex(Memo.COLUMN_ID)),
                memoCursor.getString(memoCursor.getColumnIndex(Memo.COLUMN_TITLE)),
                memoCursor.getString(memoCursor.getColumnIndex(Memo.COLUMN_CONTENT)));
        ArrayList<byte[]> images = getImages(memo.getId());
        if(images != null && images.size()>0){
            memo.setThumbNail(images.get(0));
        }

        memoCursor.close();

        return memo;
    }
    public ArrayList<byte[]> getImages(int id){
        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<byte[]> images = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + Image.TABLE_NAME + " WHERE "
                + Image.COLUMN_IMAGE_KEY + " = " + id;
        Cursor cursor = db.rawQuery(selectQuery, null);


        // looping through all rows and adding to list

        if (cursor.moveToFirst()) {
            do {
                Image image = new Image();
                image.setId(cursor.getInt(cursor.getColumnIndex(Image.COLUMN_IMAGE_KEY)));
                image.setBytes(cursor.getBlob(cursor.getColumnIndex(Image.COLUMN_IMAGE_DATA)));
                images.add(image.getBytes());
            } while (cursor.moveToNext());
        }



        // close db connection
        db.close();

        // return notes list
        return images;
    }
    public List<Memo> getAllNotes() {
        List<Memo> memos = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT * FROM " + Memo.TABLE_NAME + " ORDER BY " +
                Memo.COLUMN_ID + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Memo memo = new Memo();
                memo.setId(cursor.getInt(cursor.getColumnIndex(Memo.COLUMN_ID)));
                memo.setTitle(cursor.getString(cursor.getColumnIndex(Memo.COLUMN_TITLE)));
                memo.setContent(cursor.getString(cursor.getColumnIndex(Memo.COLUMN_CONTENT)));
                ArrayList<byte[]> images = getImages(memo.getId());
                if(images != null && images.size()>0){
                    memo.setThumbNail(images.get(0));
                }
                Log.d(TAG, memo.toString());
                memos.add(memo);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return memos;
    }

    public int getNotesCount() {
        String countQuery = "SELECT  * FROM " + Memo.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }

    public int updateNote(Memo memo) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Memo.COLUMN_TITLE, memo.getTitle());
        values.put(Memo.COLUMN_CONTENT, memo.getContent());

        // updating row
        return db.update(Memo.TABLE_NAME, values, Memo.COLUMN_ID + " = ?",
                new String[]{String.valueOf(memo.getId())});
    }

    public void deleteNote(Memo memo) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Memo.TABLE_NAME, Memo.COLUMN_ID + " = ?",
                new String[]{String.valueOf(memo.getId())});
        db.close();
    }
    public void deleteImage(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Image.TABLE_NAME, Image.COLUMN_IMAGE_KEY+ " = ?",
                new String[]{String.valueOf(id)});
        db.close();
    }
    public void addImage(long id, byte[] image) throws SQLiteException {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues cv = new  ContentValues();
        cv.put(Image.COLUMN_IMAGE_KEY, id);
        cv.put(Image.COLUMN_IMAGE_DATA, image);
        database.insert( Image.TABLE_NAME, null, cv );
    }

}