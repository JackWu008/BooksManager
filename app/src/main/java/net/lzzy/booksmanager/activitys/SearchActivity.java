package net.lzzy.booksmanager.activitys;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.SearchView;

import net.lzzy.booksmanager.R;
import net.lzzy.booksmanager.models.Book;
import net.lzzy.booksmanager.models.BookFactory;
import net.lzzy.booksmanager.util.GenericAdapter;
import net.lzzy.booksmanager.util.ViewHolder;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;

import java.util.List;
import java.util.UUID;

/**
 * Created by 007 on 2016/6/11.
 * 搜索界面
 */
@EActivity(R.layout.activity_search)
public class SearchActivity extends Activity {
    public static final String BOOK_ID_SEARCH = "bookIdSearch";
    public static final String SEARCH = "search";
    private List<Book> books;
    private GenericAdapter<Book> adapter;
    @ViewById(R.id.activity_search_gv)
    GridView gv_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        books = BookFactory.getInstance(this).getAllBooks();
        adapter = new GenericAdapter<Book>(this, R.layout.search_item, books) {
            @Override
            public void populate(ViewHolder holder, Book book) {
                holder.setImagViewBitmap(R.id.search_item_iv, book.getImgPath())
                        .setTextView(R.id.search_item_tv, "书名:" + book.getName() + "\n"
                                + "作者:" + book.getAuthor() + "\n"
                                + "分类:" + book.getCategory());
            }
        };
    }

    @AfterViews
    void initViews() {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @ItemClick(R.id.activity_search_gv)
    public void onItemClick(int position) {
        UUID uuid = adapter.getItem(position).getUuid();
        BookActivity_.intent(this).extra(BOOK_ID_SEARCH, uuid.toString()).extra(SEARCH, true).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        SearchView search = (SearchView) menu.findItem(R.id.menu_search_search).getActionView();
        search.setIconifiedByDefault(false);
        search.setQueryHint("请输入书名、作者、类别搜索");
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                books.clear();
                books.addAll(BookFactory.getInstance(getApplicationContext()).getBooks(newText));
                adapter.notifyDataSetChanged();
                if (newText.length() > 0)
                    gv_search.setAdapter(adapter);
                else
                    books.clear();
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
