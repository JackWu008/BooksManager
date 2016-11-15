package net.lzzy.booksmanager.activitys;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import net.lzzy.booksmanager.R;
import net.lzzy.booksmanager.dataPersist.DbConstants;
import net.lzzy.booksmanager.models.Book;
import net.lzzy.booksmanager.models.BookFactory;
import net.lzzy.booksmanager.util.BookAsyncTask;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.UUID;

/**
 * Created by 007 on 2016/6/9.
 * 一本书的详细信息的界面
 */
@EActivity(R.layout.activity_book)
public class BookActivity extends Activity {
    public static final String BOOK_ID = "bookId";
    private Book book;
    @ViewById(R.id.activity_book_iv_img)
    ImageView iv_bookImg;
    @ViewById(R.id.activity_book_tv_bookName)
    TextView tv_bookName;
    @ViewById(R.id.activity_book_info)
    TextView tv_bookInfo;
    @ViewById(R.id.activity_book_tags)
    TextView tv_book_tags;
    @ViewById(R.id.activity_book_rating)
    TextView tv_bookRating;
    @ViewById(R.id.activity_book_intro)
    TextView tv_bookIntro;
    @Extra(BooksActivity.BOOK_ID)
    String bookId;
    @Extra(DbConstants.BOOK_VERIFY)
    boolean verify;
    @Extra(DbConstants.BOOK_ID)
    String newBookId;
    @Extra(SearchActivity.BOOK_ID_SEARCH)
    String searchBookId;
    @Extra(SearchActivity.SEARCH)
    boolean search;
    @Extra(BooksActivity.EXAMINE)
    boolean examine;
    @Extra(BooksActivity.ADD_ISBN)
    String isbn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent() != null) {
            if (verify) {
                UUID uuid = UUID.fromString(newBookId);
                book = BookFactory.getInstance(this).getBookByIdByAll(uuid);
            } else if (search) {
                UUID uuid = UUID.fromString(searchBookId);
                book = BookFactory.getInstance(this).getBookByIdByAll(uuid);
            } else if (examine) {



            } else {
                UUID uuid = UUID.fromString(bookId);
                book = BookFactory.getInstance(this).getBookByIdByAll(uuid);
            }
        }
    }

    @AfterViews
    void initViews() {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        iv_bookImg.setImageBitmap(BitmapFactory.decodeFile(book.getImgPath()));
        tv_bookName.setText(book.getName());
        tv_bookInfo.setText("出版社:" + book.getPublisher() + "\n" + "分类:" + book.getCategory() + "\n" + "ISBN:" + book.getIsbn()
                + "\n" + "作者:" + book.getAuthor() + "\n" + "翻译:" + book.getTranslator() + "\n" + "出版时间:" + book.getPubdate() + "\n" + "价格:" + book.getPrice());
        tv_book_tags.setText(book.getTags());
        tv_bookRating.setText(book.getRating());
        tv_bookIntro.setText(book.getIntro());
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item_editor = menu.findItem(R.id.menu_book_editor);
        MenuItem item_verify = menu.findItem(R.id.menu_book_verify);
        if (verify) {
            item_editor.setVisible(false);
            item_verify.setVisible(true);
        } else {
            item_editor.setVisible(true);
            item_verify.setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_book, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_book_editor:
                if (search)
                    EditorActivity_.intent(this).extra(BOOK_ID, searchBookId).start();
                else
                    EditorActivity_.intent(this).extra(BOOK_ID, bookId).start();
                break;
            case android.R.id.home:
                if (verify)
                    Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
                finish();
                break;
            case R.id.menu_book_verify:
                Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
