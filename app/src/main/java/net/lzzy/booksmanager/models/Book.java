package net.lzzy.booksmanager.models;


import android.database.Cursor;

import net.lzzy.booksmanager.dataPersist.DbConstants;
import net.lzzy.booksmanager.dataPersist.SQLiteTable;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by 007 on 2016/6/7.
 * 书的模型
 */
public class Book implements SQLiteTable {
    private UUID uuid;
    private String name;//书名
    private String publisher;//出版(社）商
    private String imgPath;//图片路径
    private String pubdate;//发布(出版)日期
    private String price;//价格
    private String author;//作者
    private String rating;//评分
    private String tags;//标签
    private String isbn;
    private String intro;//简介
    private String translator;//翻译
    private String category;//类型

    public Book() {
        uuid = UUID.randomUUID();
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPubdate() {
        return pubdate;
    }

    public void setPubdate(String pubdate) {
        this.pubdate = pubdate;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getTranslator() {
        return translator;
    }

    public void setTranslator(String translator) {
        this.translator = translator;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String getTableName() {
        return DbConstants.BOOK_TABLE_NAME;
    }

    @Override
    public HashMap<String, Object> getInsertCols() {
        HashMap<String, Object> map = new HashMap<>();
        map.put(DbConstants.BOOK_ID, uuid.toString());
        map.put(DbConstants.BOOK_AUTHOR, author);
        map.put(DbConstants.BOOK_IMG_PATH, imgPath);
        map.put(DbConstants.BOOK_INTRO, intro);
        map.put(DbConstants.BOOK_ISBN, isbn);
        map.put(DbConstants.BOOK_PRICE, price);
        map.put(DbConstants.BOOK_PUBDATE, pubdate);
        map.put(DbConstants.BOOK_PUBLISHER, publisher);
        map.put(DbConstants.BOOK_RATING, rating);
        map.put(DbConstants.BOOK_NAME, name);
        map.put(DbConstants.BOOK_TAGS, tags);
        map.put(DbConstants.BOOK_TRANSLATOR, translator);
        map.put(DbConstants.BOOK_CATEGORY, category);
        return map;
    }

    @Override
    public HashMap<String, Object> getUpdateCols() {
        HashMap<String, Object> map = new HashMap<>();
        map.put(DbConstants.BOOK_AUTHOR, author);
        map.put(DbConstants.BOOK_IMG_PATH, imgPath);
        map.put(DbConstants.BOOK_INTRO, intro);
        map.put(DbConstants.BOOK_ISBN, isbn);
        map.put(DbConstants.BOOK_PRICE, price);
        map.put(DbConstants.BOOK_PUBDATE, pubdate);
        map.put(DbConstants.BOOK_PUBLISHER, publisher);
        map.put(DbConstants.BOOK_RATING, rating);
        map.put(DbConstants.BOOK_NAME, name);
        map.put(DbConstants.BOOK_TAGS, tags);
        map.put(DbConstants.BOOK_TRANSLATOR, translator);
        map.put(DbConstants.BOOK_CATEGORY, category);
        return map;
    }

    @Override
    public String getPKName() {
        return DbConstants.BOOK_ID;
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public void fromCursor(Cursor cursor) {
        uuid = UUID.fromString(cursor.getString(cursor.getColumnIndex(DbConstants.BOOK_ID)));
        name = cursor.getString(cursor.getColumnIndex(DbConstants.BOOK_NAME));
        price = cursor.getString(cursor.getColumnIndex(DbConstants.BOOK_PRICE));
        pubdate = cursor.getString(cursor.getColumnIndex(DbConstants.BOOK_PUBDATE));
        publisher = cursor.getString(cursor.getColumnIndex(DbConstants.BOOK_PUBLISHER));
        imgPath = cursor.getString(cursor.getColumnIndex(DbConstants.BOOK_IMG_PATH));
        intro = cursor.getString(cursor.getColumnIndex(DbConstants.BOOK_INTRO));
        isbn = cursor.getString(cursor.getColumnIndex(DbConstants.BOOK_ISBN));
        author = cursor.getString(cursor.getColumnIndex(DbConstants.BOOK_AUTHOR));
        tags = cursor.getString(cursor.getColumnIndex(DbConstants.BOOK_TAGS));
        translator = cursor.getString(cursor.getColumnIndex(DbConstants.BOOK_TRANSLATOR));
        rating = cursor.getString(cursor.getColumnIndex(DbConstants.BOOK_RATING));
        category = cursor.getString(cursor.getColumnIndex(DbConstants.BOOK_CATEGORY));
    }
}
