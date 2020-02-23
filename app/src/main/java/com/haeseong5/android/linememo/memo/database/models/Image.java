package com.haeseong5.android.linememo.memo.database.models;

public class Image {
    // Table Names
    public static final String TABLE_NAME = "table_image";
    // column names
    public static final String COLUMN_IMAGE_KEY = "image_key";
    public static final String COLUMN_IMAGE_DATA = "image_data";

    // Table create statement
    public static final String CREATE_TABLE_IMAGE = "CREATE TABLE " + TABLE_NAME + "("+
            COLUMN_IMAGE_KEY + " TEXT," +
            COLUMN_IMAGE_DATA + " BLOB);";

    private byte[] bytes;
    private int id;

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
