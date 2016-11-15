package net.lzzy.booksmanager.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import net.lzzy.bookfinder.ApiConstants;
import net.lzzy.bookfinder.ApiService;
import net.lzzy.booksmanager.activitys.BookActivity_;
import net.lzzy.booksmanager.dataPersist.DbConstants;
import net.lzzy.booksmanager.models.Book;
import net.lzzy.booksmanager.models.BookFactory;
import net.lzzy.booksmanager.models.ObservationEvent;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by Administrator on 2016/6/15.
 */
public class BookAsyncTask extends AsyncTask<String, Void, Book> {
    boolean exist = false;
    ProgressDialog dialog;
    Book book = new Book();
    private Context context;
    HashMap<String, String> map;


    @Override
    protected void onPostExecute(Book book) {
        super.onPostExecute(book);
    }

    @Override
    protected Book doInBackground(String... params) {
        return null;
    }

    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(context);
        dialog.setMessage("数据加载中...");
        dialog.setTitle("数据加载");
        dialog.show();
        super.onPreExecute();
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

    private boolean findBook(String isbn) {
        List<Book> books = BookFactory.getInstance(context).getAllBooks();
        for (Book b : books) {
            if (b.getIsbn().equals(isbn))
                return false;
        }
        return true;
    }
}
