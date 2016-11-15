package net.lzzy.booksmanager.activitys;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.xys.libzxing.zxing.activity.CaptureActivity;

import net.lzzy.booksmanager.R;
import net.lzzy.booksmanager.models.Book;
import net.lzzy.booksmanager.models.ObservationEvent;
import net.lzzy.booksmanager.models.BookFactory;
import net.lzzy.booksmanager.models.Category;
import net.lzzy.booksmanager.models.CategoryFactory;
import net.lzzy.booksmanager.util.GenericAdapter;
import net.lzzy.booksmanager.util.ViewHolder;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;
import java.util.UUID;

/**
 * Created by 007 on 2016/6/7.
 * 书籍列表
 */
@EActivity(R.layout.activity_books)
public class BooksActivity extends Activity {
    public static final String BOOK_ID = "bookId";
    public static final String ADD_ISBN = "addIsbn";
    public static final String EXAMINE = "examine";
    private List<Book> books;
    private boolean allSelected;
    private GenericAdapter<Book> adapter;
    @Extra(CategoryActivity.CATEGORY)
    String category;
    @Extra(CategoryActivity.CATEGORY_ID)
    UUID category_id;
    @ViewById(R.id.activity_books_gv)
    GridView gv_books;
    @ViewById(R.id.activity_books_tv)
    TextView tv_title;
    @ViewById(R.id.activity_books_tv_hint)
    TextView tv_hint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        books = BookFactory.getInstance(this).getBookByCategoryId(category).getBooks();
        adapter = new GenericAdapter<Book>(this, R.layout.book_item, books) {
            @Override
            public void populate(ViewHolder holder, Book book) {
                holder.setTextView(R.id.book_item_tv, book.getName())
                        .setImagViewBitmap(R.id.book_item_iv, book.getImgPath());
            }
        };
    }

    @AfterViews
    void initViews() {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        gv_books.setAdapter(adapter);
        tv_title.setText(category);
        gv_books.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        gv_books.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                mode.setTitle("已选中" + gv_books.getCheckedItemCount() + "项");
                if (gv_books.getCheckedItemCount() == books.size()) {
                    mode.getMenu().getItem(0).setTitle("取消");
                    allSelected = true;
                } else {
                    mode.getMenu().getItem(0).setTitle("全选");
                    allSelected = false;
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.menu_category_or_books, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.category_menu_delete:
                        if (gv_books.getCheckedItemCount() > 0) {
                            new AlertDialog.Builder(BooksActivity.this)
                                    .setTitle("提示")
                                    .setMessage("确定删除？")
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            BookFactory factory = BookFactory.getInstance(BooksActivity.this);
                                            for (int i = adapter.getCount() - 1; i >= 0; i--) {
                                                if (gv_books.isItemChecked(i)) {
                                                    factory.deleteBooK(adapter.getItem(i));
                                                    hint();
                                                }
                                            }
                                            mode.finish();
                                            adapter.notifyDataSetChanged();
                                        }
                                    })
                                    .setNegativeButton("取消", null).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "未选中！", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.category_menu_choose:
                        if (allSelected) {
                            for (int i = adapter.getCount() - 1; i >= 0; i--) {
                                gv_books.setItemChecked(i, false);
                            }
                        } else {
                            for (int i = adapter.getCount() - 1; i >= 0; i--) {
                                gv_books.setItemChecked(i, true);
                            }
                        }
                        break;
                    case R.id.category_menu_invertSelection:
                        for (int i = 0; i < adapter.getCount(); i++)
                            gv_books.setItemChecked(i, !gv_books.isItemChecked(i));
                        break;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });
    }

    @ItemClick(R.id.activity_books_gv)
    public void onItemClick(int position) {
        UUID uuid = adapter.getItem(position).getUuid();
        BookActivity_.intent(this).extra(BOOK_ID, uuid.toString()).start();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_title_books, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_title_books_isbn:
                if (isConn(getApplicationContext())) {
                    View view = View.inflate(this, R.layout.add_isbn, null);
                    final EditText add_isbn = (EditText) view.findViewById(R.id.add_isbn_edt);
                    new AlertDialog.Builder(this)
                            .setView(view)
                            .setTitle("添加书籍")
                            .setNegativeButton("取消", null)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (add_isbn.getText().toString().trim().length() > 0) {
                                      //  BookFactory.getInstance(BooksActivity.this).getBook(category, add_isbn.getText().toString(), BooksActivity.this);
                                        BookActivity_.intent(getApplicationContext()).extra(ADD_ISBN,add_isbn.getText().toString()).extra(EXAMINE,true).start();
                                        hint();
                                    } else
                                        Toast.makeText(getApplicationContext(), "未输入！", Toast.LENGTH_SHORT).show();

                                }
                            }).show();
                } else
                    setNetworkMethod(getApplicationContext());
                break;
            case R.id.menu_title_books_scan:
                if (isConn(this)) {
                    startActivityForResult(new Intent(BooksActivity.this, CaptureActivity.class), 1);
                    hint();
                } else
                    setNetworkMethod(this);
                break;
            case R.id.menu_title_books_search:
                SearchActivity_.intent(this).start();
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String isbn = bundle.getString("result");
            BookFactory.getInstance(this).getBook(category, isbn, this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        hint();
        adapter.notifyDataSetChanged();
    }

    private void hint() {
        if (books.size() == 0) {
            tv_hint.setVisibility(View.VISIBLE);
        } else {
            tv_hint.setVisibility(View.GONE);
        }
    }

    /*
         * 判断网络连接是否已开
         *
         *true 已打开  false 未打开
         * */
    public static boolean isConn(Context context) {
        boolean bisConnFlag = false;
        ConnectivityManager conManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = conManager.getActiveNetworkInfo();
        if (network != null) {
            bisConnFlag = conManager.getActiveNetworkInfo().isAvailable();
        }
        return bisConnFlag;
    }

    /*
       * 打开设置网络界面
       * */
    public void setNetworkMethod(final Context context) {
        //提示对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(BooksActivity.this);
        builder.setTitle("网络设置提示").setMessage("网络连接不可用,是否进行设置?").setPositiveButton("设置", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = null;
                //判断手机系统的版本  即API大于10 就是3.0或以上版本
                if (android.os.Build.VERSION.SDK_INT > 10) {
                    intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                } else {
                    intent = new Intent();
                    ComponentName component = new ComponentName("com.android.settings", "com.android.settings.WirelessSettings");
                    intent.setComponent(component);
                    intent.setAction("android.intent.action.VIEW");
                }
                context.startActivity(intent);
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
    @Subscribe
    public void onMessageEvent(ObservationEvent event) {
        if (category_id.toString().length() > 0) {
            Category category = CategoryFactory.getInstance(this).getCategoryById(category_id);
            int count = BookFactory.getInstance(this).getBookCount(category.getName());
            category.setBookCount(count);
            CategoryFactory.getInstance(this).updateCategory(category);

        }
    }
}
