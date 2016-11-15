package net.lzzy.booksmanager.models;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import net.lzzy.bookfinder.ApiConstants;
import net.lzzy.bookfinder.ApiService;
import net.lzzy.booksmanager.activitys.BookActivity_;
import net.lzzy.booksmanager.dataPersist.DbConstants;
import net.lzzy.booksmanager.dataPersist.Repository;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by 007 on 2016/6/7.
 * 书的数据
 */
public class BookFactory {
    private List<Book> books;
    private static BookFactory instance;
    private Repository<Book> repository;

    private BookFactory(Context context) {
        repository = new Repository<>(context, Book.class);
    }

    public static BookFactory getInstance(Context context) {//锁定多线程
        if (instance == null) {
            synchronized (CategoryFactory.class) {
                if (instance == null)
                    instance = new BookFactory(context);
            }
        }
        return instance;
    }

    public void createBook(Book book) {
        books.add(0, book);
        repository.insert(book);
    }

    public void updateBook(Book book) {
        repository.update(book);
    }

    public void deleteBooK(Book book) {
        File file = new File(book.getImgPath());
        try {
            repository.delete(book.getUuid());
            books.remove(book);
        } catch (Exception e) {
            e.printStackTrace();
        }
        file.delete();
        EventBus.getDefault().post(new ObservationEvent());
    }

    public List<Book> getBooks() {
        return books;
    }

    public Book getBookById(UUID uuid) {
        for (Book b : books) {
            if (b.getUuid().equals(uuid))
                return b;
        }
        return null;
    }

    private boolean findBook(String isbn) {
        List<Book> books = getAllBooks();
        for (Book b : books) {
            if (b.getIsbn().equals(isbn))
                return false;
        }
        return true;
    }

    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        try {
            books = repository.getByKeyWord(null, new String[]{}, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return books;
    }

    public Book getBookByIdByAll(UUID bookId) {
        List<Book> books = new ArrayList<>();
        try {
            books = repository.getByKeyWord(null, new String[]{}, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (Book b : books) {
            if (b.getUuid().equals(bookId))
                return b;
        }
        return null;
    }

    public List<Book> getBooks(String kw) {
        try {
            return repository.getByKeyWord(kw, new String[]{DbConstants.BOOK_NAME, DbConstants.BOOK_AUTHOR, DbConstants.BOOK_CATEGORY}, true);
        } catch (Exception e) {
            return new ArrayList<>();
        }

    }

    public BookFactory getBookByCategoryId(String category) {
        try {
            books = repository.getByKeyWord(category, new String[]{"category"}, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    public int getBookCount(String category) {
        return repository.getColCount(DbConstants.BOOK_TABLE_NAME, category);
    }



    private static String saveImageFromUri(String url) {
        String local = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                .getPath().concat("/pics/");
        File dri = new File(local);
        boolean success = dri.mkdirs();
        local += UUID.randomUUID().toString() + url.substring(url.lastIndexOf("."));
        HttpURLConnection conn = null;
        try {
            url = URLEncoder.encode(url, "utf-8").replaceAll("\\+", "%20");
            url = url.replaceAll("%3A", ":").replaceAll("%2F", "/");
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.connect();
            InputStream stream = conn.getInputStream();
            FileOutputStream out = new FileOutputStream(local);
            byte buf[] = new byte[512];
            int numRead;
            while ((numRead = stream.read(buf)) != -1) {
                out.write(buf, 0, numRead);
            }
            out.flush();
            stream.close();
            out.close();
        } catch (Exception e) {
            return "";
        } finally {
            if (conn != null)
                conn.disconnect();
        }
        return local;
    }

    public void getBook(final String category, String isbn, final Context context) {

        new AsyncTask<String, Integer, Boolean>() {
            boolean exist = false;
            ProgressDialog dialog;
            Book book = new Book();

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if (aBoolean) {
                    EventBus.getDefault().post(new ObservationEvent());
                    dialog.dismiss();
                    BookActivity_.intent(context).extra(DbConstants.BOOK_ID, book.getUuid().toString()).extra(DbConstants.BOOK_VERIFY, true).start();
                } else if (exist) {
                    exist = false;
                    dialog.dismiss();
                    Toast.makeText(context, "该书籍已存在！类别请勿重复添加!", Toast.LENGTH_SHORT).show();
                } else {
                    dialog.dismiss();
                    Toast.makeText(context, "没有找到该书籍！", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            protected Boolean doInBackground(String... params) {
                HashMap<String, String> map;
                if (params.length > 0) {
                    try {
                        map = (HashMap<String, String>) ApiService.getBookFromApi(params[0]);
                    } catch (Exception e) {
                        return false;
                    }
                    if (findBook(map.get(ApiConstants.JSON_ISBN_13))) {
                        book.setName(map.get(ApiConstants.JSON_TITLE));
                        book.setAuthor(map.get(ApiConstants.JSON_AUTHOR));
                        book.setImgPath(saveImageFromUri(map.get(ApiConstants.JSON_IMAGE_LARGE)));
                        book.setPubdate(map.get(ApiConstants.JSON_PUBDATE));
                        book.setPublisher(map.get(ApiConstants.JSON_PUBLISHER));
                        book.setIsbn(map.get(ApiConstants.JSON_ISBN_13));
                        book.setCategory(category);
                        book.setIntro(map.get(ApiConstants.JSON_SUMMARY));
                        book.setRating("共有" + map.get(ApiConstants.JSON_RATING_NUMRATERS) + "人评价  平均得分为 " + map.get(ApiConstants.JSON_RATING_AVERAGE));
                        book.setPrice(map.get(ApiConstants.JSON_PRICE));
                        book.setTags(map.get(ApiConstants.JSON_TAGS));
                        book.setTranslator(map.get(ApiConstants.JSON_TRANSLATOR));
                        createBook(book);
                        return true;
                    } else {
                        exist = true;
                        return false;
                    }
                } else {
                    return false;
                }
            }

            @Override
            protected void onPreExecute() {
                dialog = new ProgressDialog(context);
                dialog.setMessage("数据加载中...");
                dialog.setTitle("数据加载");
                dialog.show();
                super.onPreExecute();
            }
        }.execute(isbn);

    }
}
