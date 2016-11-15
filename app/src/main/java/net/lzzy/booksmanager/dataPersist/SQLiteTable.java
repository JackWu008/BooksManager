package net.lzzy.booksmanager.dataPersist;

import android.database.Cursor;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by 007 on 2016/5/4.
 * sql接口
 */
public interface SQLiteTable {
    String getTableName();//表名

    HashMap<String, Object> getInsertCols();//列集合

    HashMap<String, Object> getUpdateCols();//更新列集合

    String getPKName();//主键

    UUID getUUID();//uuid

    void fromCursor(Cursor cursor);
}
