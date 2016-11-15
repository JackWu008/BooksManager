package net.lzzy.sqlitelib;

import android.content.Context;
import android.database.Cursor;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by lzzy_gxy on 2016/4/24.
 * 参考库模式引入，初始化数据库并封装CRUD，隔离数据访问代码
 *
 * @param <T> Repository类进行持久化操作的业务模型类
 */
public class SqlRepository<T extends ISqlitable> {
    private DbTools dbTools;
    private Class<T> classType;

    public SqlRepository(Context context, Class<T> classType, DbPackage dbPackage) {
        dbTools = new DbTools(context, dbPackage.getDatabaseName(), dbPackage.getVersion());
        dbTools.setCreateTableSqls(dbPackage.getCreateTableSqls());
        dbTools.setUpdateTableSqls(dbPackage.getUpdateTableSqls());
        this.classType = classType;
    }

    public void delete(UUID id) {
        List<String> whereCols = new ArrayList<>();
        List<Object> valCols = new ArrayList<>();
        try {
            T t = classType.newInstance();
            whereCols.add(t.getPKName());
            valCols.add(id);
            dbTools.deleteRows(t.getTableName(), whereCols, valCols);
        } catch (Exception ignored) {
        }
    }

    public void delete(T t) {
        List<String> whereCols = new ArrayList<>();
        List<Object> valCols = new ArrayList<>();
        whereCols.add(t.getPKName());
        valCols.add(t.getId());
        dbTools.deleteRows(t.getTableName(), whereCols, valCols);
    }

    public void insert(T t) {
        List<String> cols = new ArrayList<>();
        List<Object> vals = new ArrayList<>();
        for (Map.Entry<String, Object> entry : t.getInsertCols().entrySet()) {
            cols.add(entry.getKey());
            vals.add(entry.getValue());
        }
        dbTools.insertRow(t.getTableName(), cols, vals);
    }

    public void update(T t) {
        List<String> cols = new ArrayList<>();
        List<Object> vals = new ArrayList<>();
        List<String> whereCols = new ArrayList<>();
        List<Object> whereVals = new ArrayList<>();
        whereCols.add(t.getPKName());
        whereVals.add(t.getId());
        for (Map.Entry<String, Object> entry : t.getUpdateCols().entrySet()) {
            cols.add(entry.getKey());
            vals.add(entry.getValue());
        }
        dbTools.updateRows(t.getTableName(), cols, vals, whereCols, whereVals);
    }

    public List<T> get() {
        try {
            return getByKeyword(null, new String[]{}, false);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public List<T> getByKeyword(String keyword, String[] cols, boolean explicit)
            throws IllegalAccessException, InstantiationException {
        List<T> list = new ArrayList<>();
        String[] selectionArgs;
        String selection;
        SparseArray<String> array;
        if (explicit)
            array = DataUtils.getStringArgsExplicit(keyword, cols);
        else
            array = DataUtils.getStringArgs(keyword, cols);
        String allArgs = array.get(DataUtils.KEY_ARGUMENTS);
        selectionArgs = allArgs == null ? null : allArgs.split(",");
        selection = array.get(DataUtils.KEY_SELECTION);
        Cursor cursor = dbTools.getCursor(classType.newInstance().getTableName(), null, selection
                , selectionArgs, null, null, null, null);
        while (cursor.moveToNext()) {
            T t = classType.newInstance();
            t.fromCursor(cursor);
            list.add(t);
        }
        cursor.close();
        return list;
    }

    public int getCount(String[] cols, String[] vals, boolean isAccurate, T t) {
        String selection = DataUtils.filterQuery(cols, vals, isAccurate);
        Cursor cursor = dbTools.getReadableDatabase().query(t.getTableName(), null, selection, null, null, null, null);
        return cursor.getCount();

    }

    public void deleteAll(String tableName) {
        dbTools.deleteAll(tableName);
    }

    public void exeSqls(List<String> sqls) {
        dbTools.execTrans(sqls);
    }

    public void close() {
        dbTools.close();
    }
}
