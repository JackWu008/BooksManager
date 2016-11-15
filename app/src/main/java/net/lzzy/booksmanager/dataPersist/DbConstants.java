package net.lzzy.booksmanager.dataPersist;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by 007 on 2016/5/4.
 * 静态常量键
 */
public final class DbConstants {
    private DbConstants() {
    }//不让实例化 不给NEW和单例模式不一样

    public static final String DB_NAME = "booksManager.db";
    /**
     * 数据库版本
     */
    public static final int DB_VERSION = 1;
    /**
     * 创建表头语句
     **/
    public static final String CREATE_TABLE_HEAD = "create table ";
    /**
     * 数据库字段类型
     */
    private static final String TYPE_INT = " integer";
    private static final String TYPE_STRING = " text";
    //    private static final String TYPE_DOUBLE = " real";
    private static final String TYPE_REAL = " real";

    public static final String BOOK_VERIFY = "verify";
    private static final String RESTRAIN_AUTOINCREMENT = " primary key autoincrement";

    /**
     * table book
     **/
    public static final String BOOK_TABLE_NAME = "book";
    public static final String BOOK_ID = "id";
    public static final String BOOK_NAME = "name";
    public static final String BOOK_PUBLISHER = "publisher";
    public static final String BOOK_IMG_PATH = "imgPath";
    public static final String BOOK_PUBDATE = "pubdate";
    public static final String BOOK_PRICE = "price";
    public static final String BOOK_AUTHOR = "author";
    public static final String BOOK_RATING = "rating";
    public static final String BOOK_TAGS = "tags";
    public static final String BOOK_ISBN = "isbn";
    public static final String BOOK_INTRO = "intro";
    public static final String BOOK_TRANSLATOR = "translator";
    public static final String BOOK_CATEGORY = "category";
    public static final StringBuilder BOOK_TABLE_SQL = new StringBuilder();

    /**
     * table category
     **/
    public static final String CATEGORY_TABLE_NAME = "category";
    public static final String CATEGORY_ID = "id";
    public static final String CATEGORY_NAME = "name";
    public static final String CATEGORY_BOOK_COUNT = "bookCount";
    public static final StringBuilder CATEGORY_TABLE_SQL = new StringBuilder();

    public static final List<String> CREATE_TABLE_SQLS = new ArrayList<>();
    public static final List<String> UPDATE_TABLE_SQLS = new ArrayList<>();

    static {
        BOOK_TABLE_SQL.append(CREATE_TABLE_HEAD)
                .append(BOOK_TABLE_NAME).append("(")
                .append(BOOK_ID).append(TYPE_STRING).append(",")
                .append(BOOK_NAME).append(TYPE_STRING).append(",")
                .append(BOOK_PUBLISHER).append(TYPE_STRING).append(",")
                .append(BOOK_IMG_PATH).append(TYPE_STRING).append(",")
                .append(BOOK_PUBDATE).append(TYPE_STRING).append(",")
                .append(BOOK_PRICE).append(TYPE_STRING).append(",")
                .append(BOOK_AUTHOR).append(TYPE_STRING).append(",")
                .append(BOOK_RATING).append(TYPE_STRING).append(",")
                .append(BOOK_TAGS).append(TYPE_STRING).append(",")
                .append(BOOK_ISBN).append(TYPE_STRING).append(",")
                .append(BOOK_INTRO).append(TYPE_STRING).append(",")
                .append(BOOK_CATEGORY).append(TYPE_STRING).append(",")
                .append(BOOK_TRANSLATOR).append(TYPE_STRING).append(")");
        CATEGORY_TABLE_SQL.append(CREATE_TABLE_HEAD)
                .append(CATEGORY_TABLE_NAME).append("(")
                .append(CATEGORY_ID).append(TYPE_STRING).append(",")
                .append(CATEGORY_BOOK_COUNT).append(TYPE_INT).append(",")
                .append(CATEGORY_NAME).append(TYPE_STRING).append(")");
        CREATE_TABLE_SQLS.add(CATEGORY_TABLE_SQL.toString());
        CREATE_TABLE_SQLS.add(BOOK_TABLE_SQL.toString());

    }
}
