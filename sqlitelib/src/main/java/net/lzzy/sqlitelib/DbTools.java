package net.lzzy.sqlitelib;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

/**
 * Created by lzzy_gxy on 2015/9/7.
 * SQLite基本工具类，包括创建表、更新库、CRUD方法等
 */
public class DbTools extends SQLiteOpenHelper {
    private List<String> mCreateTableSqls;//创建表的语句
    private List<String> mUpdateTableSqls;//删除并创建新表的语句

    public void setCreateTableSqls(List<String> mCreateTableSqls) {
        this.mCreateTableSqls = mCreateTableSqls;
    }

    public void setUpdateTableSqls(List<String> mUpdateTableSqls) {
        this.mUpdateTableSqls = mUpdateTableSqls;
    }

    /**
     * 继承的SQLiteOpenHelper对象，用于打开、创建、管理数据库
     *
     * @param context 用于打开或创建数据库的context对象
     * @param name    数据库名称，不包含路径
     * @param factory 用于生产cursor对象，默认null
     * @param version 数据库版本号，根据版本号的变化升级更新数据库
     */
    public DbTools(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    /**
     * 继承的SQLiteOpenHelper对象，用于打开、创建、管理数据库,Cursor默认null
     *
     * @param context 用于打开或创建数据库的context对象
     * @param name    数据库名称，不包含路径
     * @param version 数据库版本号，根据版本号的变化升级更新数据库
     */
    public DbTools(Context context, String name, int version) {
        this(context, name, null, version);
    }

    /**
     * 继承的SQLiteOpenHelper对象，用于打开、创建、管理数据库,Cursor默认null,version=1
     *
     * @param context 用于打开或创建数据库的context对象
     * @param name    数据库名称，不包含路径
     */
    public DbTools(Context context, String name) {
        this(context, name, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO 第一次调用getReadableDatabase()、getWritableDatabase()时执行
        if (mCreateTableSqls.size() > 0) {
            for (String s : mCreateTableSqls) {
                db.execSQL(s);
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO version变化时执行
        if (mUpdateTableSqls.size() > 0) {
            for (String s : mUpdateTableSqls) {
                db.execSQL(s);
            }
        }
    }

    public void insertRow(String table, List<String> cols, List<Object> values) {
        String sql = DataUtils.getInsertString(table, cols, values);
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sql);
    }


    /**
     * 查询指定的表，返回结果集的Cursor对象
     *
     * @param table         表名
     * @param columns       要返回数据的列，null返回全部列
     * @param selection     条件语句，使用？占位符，null返回所有行
     * @param selectionArgs 条件语句参数，与占位符按先后顺序一一对应
     * @param groupBy       分组，null表示不分组
     * @param having        筛选，null表示不筛选，返回所有分组
     * @param orderBy       拍序列，null表示使用默认排序
     * @param limit         等同top
     * @return 结果集的Cursor对象，记住读取完成后关闭cursor
     */
    public Cursor getCursor(String table, String[] columns, String selection,
                            String[] selectionArgs, String groupBy, String having,
                            String orderBy, String limit) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(table, columns, selection, selectionArgs, groupBy,
                having, orderBy, limit);
    }

    /**
     * 更新表数据
     *
     * @param table     表名
     * @param cols      要更新的列
     * @param values    要更新的列的值，与cols必须按顺序一一对应
     * @param whereCols 作为条件的列
     * @param whereVals 条件列参数，与whereCols必须按顺序一一对应
     */
    public void updateRows(String table, List<String> cols, List<Object> values,
                           List<String> whereCols, List<Object> whereVals) {
        String sql = DataUtils.getUpdateString(table, cols, values, whereCols, whereVals);
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sql);
    }

    /**
     * 删除数据行
     *
     * @param table     表名
     * @param whereCols 作为条件的列名
     * @param whereVals 条件列参数，与whereCols必须按顺序一一对应
     */
    public void deleteRows(String table, List<String> whereCols, List<Object> whereVals) {
        String sql = DataUtils.getDeleteString(table, whereCols, whereVals);
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sql);
    }

    /**
     * 对多个数据库操作执行事务处理
     *
     * @param sqls 多个操作的sql语句集合
     */
    public void execTrans(List<String> sqls) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            for (String s : sqls) {
                db.execSQL(s);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void deleteAll(String tableName) {
        getWritableDatabase().delete(tableName, null, null);

    }
}
