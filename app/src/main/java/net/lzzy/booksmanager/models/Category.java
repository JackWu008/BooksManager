package net.lzzy.booksmanager.models;

import android.database.Cursor;

import net.lzzy.booksmanager.dataPersist.DbConstants;
import net.lzzy.booksmanager.dataPersist.SQLiteTable;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by 007 on 2016/6/6.
 * 类别的模型
 */
public class Category implements SQLiteTable {
    private UUID uuid;
    private String name;
    private int bookCount;


    public Category() {
        uuid = UUID.randomUUID();
    }

    public UUID getUuid() {
        return uuid;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBookCount() {
        return bookCount;
    }

    public void setBookCount(int bookCount) {
        this.bookCount = bookCount;
    }

    @Override
    public String getTableName() {
        return DbConstants.CATEGORY_TABLE_NAME;
    }

    @Override
    public HashMap<String, Object> getInsertCols() {
        HashMap<String, Object> map = new HashMap<>();
        map.put(DbConstants.CATEGORY_ID, uuid.toString());
        map.put(DbConstants.CATEGORY_NAME, name);
        return map;
    }

    @Override
    public HashMap<String, Object> getUpdateCols() {
        HashMap<String, Object> map = new HashMap<>();
        map.put(DbConstants.CATEGORY_NAME, name);
        map.put(DbConstants.CATEGORY_BOOK_COUNT, bookCount);
        return map;
    }

    @Override
    public String getPKName() {
        return DbConstants.CATEGORY_ID;
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }


    @Override
    public void fromCursor(Cursor cursor) {
        uuid = UUID.fromString(cursor.getString(cursor.getColumnIndex(DbConstants.CATEGORY_ID)));
        name = cursor.getString(cursor.getColumnIndex(DbConstants.CATEGORY_NAME));
        bookCount = cursor.getInt(cursor.getColumnIndex(DbConstants.CATEGORY_BOOK_COUNT));
    }
}
