package net.lzzy.booksmanager.activitys;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import net.lzzy.booksmanager.R;
import net.lzzy.booksmanager.models.ObservationEvent;
import net.lzzy.booksmanager.models.BookFactory;
import net.lzzy.booksmanager.models.Category;
import net.lzzy.booksmanager.models.CategoryFactory;
import net.lzzy.booksmanager.util.GenericAdapter;
import net.lzzy.booksmanager.util.ViewHolder;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

/**
 * Created by 007 on 2016/6/7.
 * 类别列表
 */
@EActivity(R.layout.activity_category)
public class CategoryActivity extends Activity {
    public static final String CATEGORY = "category";
    public static final String CATEGORY_ID = "category_id";
    private List<Category> categories;
    private GenericAdapter<Category> adapter;
    private long exitTime = 0;
    private boolean allSelected;
    private int count;
    @ViewById(R.id.activity_category_lv)
    ListView lv_categories;
    @ViewById(R.id.activity_category_tv_hint)
    TextView tv_hint;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        categories = CategoryFactory.getInstance(this).getCategories();
        CategoryFactory.getInstance(this).sort();
        adapter = new GenericAdapter<Category>(this, R.layout.category_item, categories) {
            @Override
            public void populate(ViewHolder holder, Category category) {
                holder.setTextView(R.id.category_item_name, category.getName());
                count = BookFactory.getInstance(CategoryActivity.this).getBookCount(category.getName());
                category.setBookCount(count);
                CategoryFactory.getInstance(CategoryActivity.this).updateCategory(category);
                TextView tv_bookCount = (TextView) holder.getView(R.id.category_item_count);
                tv_bookCount.setText(String.valueOf(count));
            }
        };

    }

    @AfterViews
    void initViews() {
        lv_categories.setAdapter(adapter);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            registerForContextMenu(lv_categories);
        } else {
            lv_categories.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
            lv_categories.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

                @Override
                public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                    mode.setTitle("已选中" + lv_categories.getCheckedItemCount() + "项");
                    if (lv_categories.getCheckedItemCount() == categories.size()) {
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
                            if (lv_categories.getCheckedItemCount() > 0) {
                                new AlertDialog.Builder(CategoryActivity.this)
                                        .setTitle("提示")
                                        .setMessage("确定删除？")
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                CategoryFactory factory = CategoryFactory.getInstance(CategoryActivity.this);
                                                for (int i = adapter.getCount() - 1; i >= 0; i--) {
                                                    if (lv_categories.isItemChecked(i)) {
                                                        if (adapter.getItem(i).getBookCount() == 0) {
                                                            factory.deleteCategory(adapter.getItem(i));
                                                            hint();
                                                        } else
                                                            Toast.makeText(getApplicationContext(), "请先删除书籍，再删除类别！", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                                mode.finish();
                                                adapter.notifyDataSetChanged();
                                            }
                                        })
                                        .setNegativeButton("取消", null).show();
                            } else {
                                Toast.makeText(CategoryActivity.this, "未选中！", Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case R.id.category_menu_choose:
                            if (allSelected) {
                                for (int i = adapter.getCount() - 1; i >= 0; i--) {
                                    lv_categories.setItemChecked(i, false);
                                }
                            } else {
                                for (int i = adapter.getCount() - 1; i >= 0; i--) {
                                    lv_categories.setItemChecked(i, true);
                                }
                            }
                            break;
                        case R.id.category_menu_invertSelection:
                            for (int i = 0; i < adapter.getCount(); i++)
                                lv_categories.setItemChecked(i, !lv_categories.isItemChecked(i));
                            break;
                    }
                    return false;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {

                }
            });
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.menu_category_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        View view = View.inflate(this, R.layout.add_category, null);
        final EditText add_category = (EditText) view.findViewById(R.id.add_category_edt);
        final int pos = ((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position;
        String category_content = adapter.getItem(pos).getName();
        switch (item.getItemId()) {
            case R.id.category_context_menu_delete:
                new AlertDialog.Builder(CategoryActivity.this)
                        .setTitle("提示")
                        .setMessage("确定删除？")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (count == 0) {
                                    CategoryFactory.getInstance(CategoryActivity.this).deleteCategory(adapter.getItem(pos));
                                    hint();
                                } else
                                    Toast.makeText(getApplicationContext(), "请先删除书籍，再删除类别！", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("取消", null).show();
                break;
            case R.id.category_context_menu_add:
                new AlertDialog.Builder(this)
                        .setView(view)
                        .setTitle("添加类别")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (add_category.getText().toString().length() > 0) {
                                    Category category = new Category();
                                    category.setName(add_category.getText().toString());
                                    category.setBookCount(count);
                                    CategoryFactory.getInstance(getApplicationContext()).createCategory(category);
                                    hint();
                                } else
                                    Toast.makeText(getApplicationContext(), "未输入！", Toast.LENGTH_SHORT).show();
                                adapter.notifyDataSetChanged();
                            }
                        }).show();
                break;
            case R.id.category_context_menu_editor:
//                add_category.setFocusableInTouchMode(true);
//                add_category.requestFocus();
//                InputMethodManager methodManager = (InputMethodManager) add_category.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//                methodManager.showSoftInput(add_category, 0);
                add_category.setText(category_content);
                add_category.requestFocus();
                add_category.setSelection(add_category.getText().length());//让光标后置
                new AlertDialog.Builder(this)
                        .setView(view)
                        .setTitle("编辑类别")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (add_category.getText().toString().length() > 0) {
                                    adapter.getItem(pos).setName(add_category.getText().toString());
                                    CategoryFactory.getInstance(CategoryActivity.this).updateCategory(adapter.getItem(pos));
                                } else
                                    Toast.makeText(getApplicationContext(), "未输入！", Toast.LENGTH_SHORT).show();
                                adapter.notifyDataSetChanged();
                            }
                        }).show();
                break;
        }
        return super.onContextItemSelected(item);
    }

    @ItemClick(R.id.activity_category_lv)
    public void onItemClick(int position) {
        BooksActivity_.intent(this).extra(CATEGORY, adapter.getItem(position).getName()).extra(CATEGORY_ID, adapter.getItem(position).getUuid()).start();
        EventBus.getDefault().post(new ObservationEvent(position));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_title_category, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_title_category_add:
                View view = View.inflate(this, R.layout.add_category, null);
                final EditText add_category = (EditText) view.findViewById(R.id.add_category_edt);
//                add_category.setFocusable(true);
//                add_category.setFocusableInTouchMode(true);
//                add_category.requestFocus();
//                InputMethodManager methodManager = (InputMethodManager) add_category.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//                methodManager.showSoftInput(add_category, 0);
                new AlertDialog.Builder(this)
                        .setView(view)
                        .setTitle("添加类别")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (add_category.getText().toString().trim().length() > 0) {
                                    if (findCategory(add_category.getText().toString())) {
                                        Category category = new Category();
                                        category.setName(add_category.getText().toString());
                                        category.setBookCount(count);
                                        CategoryFactory.getInstance(CategoryActivity.this).createCategory(category);
                                        hint();
                                    } else
                                        Toast.makeText(getApplicationContext(), "该类别已存在，请勿重复添加", Toast.LENGTH_SHORT).show();
                                } else
                                    Toast.makeText(getApplicationContext(), "未输入！", Toast.LENGTH_SHORT).show();
                            }
                        }).show();
                break;
            case R.id.menu_title_category_search:
                SearchActivity_.intent(this).start();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        hint();
        CategoryFactory.getInstance(this).sort();
        adapter.notifyDataSetChanged();
    }

    private void hint() {
        if (categories.size() == 0) {
            tv_hint.setVisibility(View.VISIBLE);
        } else {
            tv_hint.setVisibility(View.GONE);
        }
    }

    private boolean findCategory(String categoryName) {
        for (Category c : categories) {
            if (c.getName().equals(categoryName))
                return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - exitTime > 2000) {
            Toast.makeText(getApplicationContext(), "再按一次退出应用", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
            super.onBackPressed();
            System.exit(0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onMessageEvent(ObservationEvent event) {
        adapter.notifyDataSetChanged();
    }


}
