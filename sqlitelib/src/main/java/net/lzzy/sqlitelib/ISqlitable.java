package net.lzzy.sqlitelib;

import android.database.Cursor;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by lzzy_gxy on 2016/6/3.
 * ISqlitable类约定Java对象存储到sqlite需要实现的方法
 */
public interface ISqlitable {
    //
    String getTableName();

    String getPKName();

    UUID getId();

    HashMap<String, Object> getInsertCols();

    HashMap<String, Object> getUpdateCols();

    void fromCursor(Cursor cursor);
}
