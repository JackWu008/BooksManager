package net.lzzy.booksmanager.models;

import android.content.Context;

import net.lzzy.booksmanager.dataPersist.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Created by 007 on 2016/6/7.
 * 类别的数据
 */
public class CategoryFactory {
    private List<Category> categories;
    private static CategoryFactory instance;
    private Repository<Category> repository;

    private CategoryFactory(Context context) {
        repository = new Repository<>(context, Category.class);
        try {
            categories = repository.getByKeyWord(null, new String[]{}, false);
        } catch (Exception e) {
            categories = new ArrayList<>();
        }
    }

    public static CategoryFactory getInstance(Context context) {//锁定多线程
        if (instance == null) {
            synchronized (CategoryFactory.class) {
                if (instance == null)
                    instance = new CategoryFactory(context);
            }
        }
        return instance;
    }

    public Category getCategoryById(UUID uuid) {
        for (Category c : categories) {
            if (c.getUuid().equals(uuid))
                return c;
        }
        return null;
    }


    public void createCategory(Category category) {
        categories.add(category);
        repository.insert(category);
    }

    public Category getCategoryByName(String name) {
        for (Category c : categories) {
            if (c.getName().equals(name))
                return c;
        }
        return null;
    }

    public void sort() {
        Collections.sort(categories, new Comparator<Category>() {
            @Override
            public int compare(Category lhs, Category rhs) {
                int l = lhs.getBookCount();
                int r = rhs.getBookCount();
                if (l > r)
                    return -1;
                if (r > l)
                    return 1;
                return 0;
            }
        });
    }

    public void updateCategory(Category category) {
        repository.update(category);
    }

    public void deleteCategory(Category category) {
        try {
            repository.delete(category.getUuid());
            categories.remove(category);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public List<Category> getCategories() {
        return categories;
    }
}
