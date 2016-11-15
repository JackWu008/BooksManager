package net.lzzy.sqlitelib;

import android.support.annotation.NonNull;
import android.util.SparseArray;

import java.util.List;

/**
 * Created by lzzy_gxy on 2015/9/15.
 * 数据访问的相关辅助方法
 */
public class DataUtils {
    private DataUtils() {
    }

    public static final int KEY_SELECTION = 0;
    public static final int KEY_ARGUMENTS = 1;

    /**
     * 生成插入语句，提取该方法为外面执行存储过程做准备
     *
     * @param table  表名
     * @param cols   列名
     * @param values 列参数，必须与cols列按顺序一一对应
     * @return insert语句
     */
    @NonNull
    public static String getInsertString(String table, List<String> cols, List<Object> values) {
        String sql = "insert into " + table + "(";
        for (String col : cols) {
            sql += col + ",";
        }
        sql = sql.substring(0, sql.length() - 1);
        sql += ") values (";
        for (Object val : values) {
            if (val instanceof Integer)
                sql += val + ",";
            else
                sql += "'" + val + "',";
        }
        sql = sql.substring(0, sql.length() - 1) + ")";
        return sql;
    }

    /**
     * 生成更新语句，提取该方法为外面执行存储过程做准备
     *
     * @param table     表名
     * @param cols      要更新的列
     * @param values    要更新的列参数，与cols必须按顺序一一对应
     * @param whereCols 条件列
     * @param whereVals 条件列参数，与whereCols必须按顺序一一对应
     * @return update语句
     */
    @NonNull
    public static String getUpdateString(String table, List<String> cols, List<Object> values
            , List<String> whereCols, List<Object> whereVals) {
        String sql = "update " + table + " set ";
        int i = 0;
        for (String col : cols) {
            Object val = values.get(i++);
            if (val instanceof Integer)
                sql += col + "=" + val + ",";
            else
                sql += col + "='" + val + "',";
        }
        sql = sql.substring(0, sql.length() - 1);
        i = 0;
        if (whereCols != null && whereCols.size() > 0) {
            sql += " where ";
            for (String col : whereCols) {
                Object val = whereVals.get(i++);
                if (val instanceof Integer)
                    sql += col + "=" + val + " and ";
                else
                    sql += col + "='" + val + "' and ";
            }
            sql = sql.substring(0, sql.length() - 4);
        }
        return sql;
    }

    /**
     * 生成删除语句，提取该方法为外面执行存储过程做准备
     *
     * @param table     表名
     * @param whereCols 条件列
     * @param whereVals 条件列参数，与whereCols必须按顺序一一对应
     * @return delete语句
     */
    @NonNull
    public static String getDeleteString(String table, List<String> whereCols, List<Object> whereVals) {
        String sql = "delete from " + table;
        if (whereCols != null && whereCols.size() > 0) {
            sql += " where ";
            int i = 0;
            for (String col : whereCols) {
                Object val = whereVals.get(i++);
                if (val instanceof Integer)
                    sql += col + "=" + val + " and ";
                else
                    sql += col + "='" + val + "' and ";
            }
            sql = sql.substring(0, sql.length() - 4);
        }
        return sql;
    }

    /**
     * 调用者传入查询关键词及要查询的列，结果生成selection语句以及匹配参数占位符的数组(模糊查询)
     *
     * @param keyword 查询关键词
     * @param cols    查询关键词
     * @return Map对象，包括条件语句及匹配占位符？的值
     */
    public static SparseArray<String> getStringArgs(String keyword, String[] cols) {
        SparseArray<String> array = new SparseArray<>();
        String selection;
        if (keyword==null||keyword.isEmpty()) {
            array.put(KEY_SELECTION, null);
            array.put(KEY_ARGUMENTS, null);
            return array;
        }
        selection = "";
        String keywordArgs = "";
        for (String col : cols) {
            selection += col + " like ? or ";
            keywordArgs += "%" + keyword + "%,";
        }
        selection = selection.substring(0, selection.length() - 4);
        keywordArgs = keywordArgs.substring(0, keywordArgs.length() - 1);
        array.put(KEY_SELECTION, selection);
        array.put(KEY_ARGUMENTS, keywordArgs);
        return array;
    }

    /**
     * 调用者传入查询关键词及要查询的列，结果生成selection语句以及匹配参数占位符的数组(精确查询)
     *
     * @param key  查询关键词
     * @param cols 查询关键词
     * @return Map对象，包括条件语句及匹配占位符？的值
     */
    public static SparseArray<String> getStringArgsExplicit(String key, String[] cols) {
        SparseArray<String> array = new SparseArray<>();
        String selection;
        if (key==null||key.isEmpty()) {
            array.put(KEY_SELECTION, null);
            array.put(KEY_ARGUMENTS, null);
            return array;
        }
        selection = "";
        String keywordArgs = "";
        for (String col : cols) {
            selection += col + " = ? or ";
            keywordArgs += key + ",";
        }
        selection = selection.substring(0, selection.length() - 4);
        keywordArgs = keywordArgs.substring(0, keywordArgs.length() - 1);
        array.put(KEY_SELECTION, selection);
        array.put(KEY_ARGUMENTS, keywordArgs);
        return array;
    }


    public static String filterQuery(String[] cols, String[] vals, boolean isAccurate) {
        if (cols == null || vals == null || cols.length != vals.length)
            return null;
        String selection = "(";
        if (isAccurate) {
            for (int i = 0; i < cols.length; i++) {
                selection = selection + cols[i] + " like '" + vals[i] + "' and ";
            }
        } else for (int i = 0; i < cols.length; i++) {
            selection = selection + cols[i] + " like '%" + vals[i] + "%' and ";
        }
        selection = selection.substring(0, selection.length() - 5);
        selection += ")";
        return selection;

    }
}
