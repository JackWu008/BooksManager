package net.lzzy.booksmanager.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.net.URI;


/**
 * Created by 007 on 2016/4/26.
 * 优化适配器
 */
public class ViewHolder {
    private SparseArray<View> views;
    private Context context;
    private View convertView;

    public View getConvertView() {
        return convertView;
    }

    private ViewHolder(Context context, ViewGroup parent, int layout) {
        this.context = context;
        views = new SparseArray<>();
        convertView = LayoutInflater.from(context).inflate(layout, parent);
        convertView.setTag(this);
    }

    public static ViewHolder getInstance(Context context, ViewGroup parent, int layout, View convertView) {
        if (convertView == null)
            return new ViewHolder(context, parent, layout);
        else
            return (ViewHolder) convertView.getTag();
    }

    public View getView(int resId) {
        View view = views.get(resId);
        if (view == null) {
            view = convertView.findViewById(resId);
            views.put(resId, view);
        }
        return view;
    }

    public ViewHolder setTextView(int tvId, String text) {
        TextView tv = (TextView) getView(tvId);
        tv.setText(text);
        return this;
    }

    public ViewHolder setCheckBox(int cbId, boolean checked) {
        CheckBox cb = (CheckBox) getView(cbId);
        cb.setChecked(checked);
        return this;
    }

    public ViewHolder setCheckBoxListener(int cbId, View.OnClickListener listener) {
        CheckBox cb = (CheckBox) getView(cbId);
        cb.setOnClickListener(listener);
        return this;
    }

    public ViewHolder setImagView(int imgId, Drawable drawable) {
        ImageView img = (ImageView) getView(imgId);
        img.setImageDrawable(drawable);
        return this;
    }

    public ViewHolder setImagViewBitmap(int imgId, String path) {
        ImageView img = (ImageView) getView(imgId);
        img.setImageBitmap(BitmapFactory.decodeFile(path));
        return this;
    }

    public ViewHolder setImagViewURI(int imgId, String path) {
        ImageView img = (ImageView) getView(imgId);
        img.setImageURI(Uri.parse(path));
        return this;
    }

    public ViewHolder setScreenWidth(int lLayoutId) {
        LinearLayout.LayoutParams params;
        int screenWidth;
        Display defaultDisplay = ((Activity) context).getWindowManager()
                .getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        defaultDisplay.getMetrics(metrics);
        screenWidth = metrics.widthPixels;
        params = new LinearLayout.LayoutParams(screenWidth,
                LinearLayout.LayoutParams.MATCH_PARENT);
        LinearLayout layout = (LinearLayout) getView(lLayoutId);
        layout.setLayoutParams(params);
        return this;
    }
}
