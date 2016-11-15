package net.lzzy.booksmanager.activitys;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import net.lzzy.booksmanager.R;
import net.lzzy.booksmanager.models.Book;
import net.lzzy.booksmanager.models.BookFactory;
import net.lzzy.booksmanager.models.Category;
import net.lzzy.booksmanager.models.CategoryFactory;
import net.lzzy.booksmanager.util.GenericAdapter;
import net.lzzy.booksmanager.util.ViewHolder;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.List;
import java.util.UUID;

/**
 * Created by 007 on 2016/6/11.
 * 编辑界面
 */
@EActivity(R.layout.activity_editor)
public class EditorActivity extends Activity {
    private Book book;
    private GenericAdapter<Category> adapter;
    @ViewById(R.id.activity_editor_edt_name)
    EditText edt_name;
    @ViewById(R.id.activity_editor_edt_author)
    EditText edt_author;
    @ViewById(R.id.activity_editor_spi_category)
    Spinner spi_category;
    @ViewById(R.id.activity_editor_edt_intro)
    EditText edt_intro;
    @ViewById(R.id.activity_editor_edt_publisher)
    EditText edt_publisher;
    @Extra(BookActivity.BOOK_ID)
    String bookId;
    private String category;
    int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID uuid = UUID.fromString(bookId);
        book = BookFactory.getInstance(this).getBookByIdByAll(uuid);
        List<Category> categories = CategoryFactory.getInstance(this).getCategories();
        adapter = new GenericAdapter<Category>(this, R.layout.editor_select_item, categories) {
            @Override
            public void populate(ViewHolder holder, Category category) {
                holder.setTextView(R.id.editor_select_item_tv, category.getName());
            }
        };
        Category category = CategoryFactory.getInstance(this).getCategoryByName(book.getCategory());
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).equals(category)) {
                pos = i;
            }
        }
    }

    @AfterViews
    void initViews() {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        spi_category.setAdapter(adapter);
        spi_category.setSelection(pos);
        edt_name.setText(book.getName());
        edt_author.setText(book.getAuthor());
        edt_intro.setText(book.getIntro());
        edt_publisher.setText(book.getPublisher());
        spi_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = adapter.getItem(position).getName();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_editor_save:
                book.setName(edt_name.getText().toString());
                book.setAuthor(edt_author.getText().toString());
                book.setCategory(category);
                book.setPublisher(edt_publisher.getText().toString());
                BookFactory.getInstance(this).updateBook(book);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
