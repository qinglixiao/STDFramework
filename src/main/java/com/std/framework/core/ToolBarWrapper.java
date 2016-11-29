package com.std.framework.core;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPresenter;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.std.framework.R;

/**
 * Created by gfy on 2016/4/25.
 */
public class ToolBarWrapper {
    private Toolbar toolbar;
    private TextView mTitle;

    public Toolbar getToolbar() {
        return toolbar;
    }

    public ToolBarWrapper(Toolbar toolbar) {
        this.toolbar = toolbar;
        mTitle = (TextView) toolbar.findViewById(R.id.tv_title);
    }

    public void setTitle(@StringRes int resId) {
        mTitle.setText(resId);
    }

    public void setTitle(CharSequence title) {
        mTitle.setText(title);
    }

    public void addMenu(String name, @DrawableRes int iconRes, Toolbar.OnMenuItemClickListener callback) {
        toolbar.getMenu().add(name).setIcon(iconRes).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        if (callback != null) {
            toolbar.setOnMenuItemClickListener(callback);
        }
    }

    public void clearMenu(){
        toolbar.getMenu().clear();
    }

    public void removeMenu(int menuId){
        toolbar.getMenu().removeItem(menuId);
    }

}
