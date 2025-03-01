/*
 * Copyright (C) 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settingslib.collapsingtoolbar;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.android.settingslib.R;
import com.android.settingslib.utils.BuildCompatUtils;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

/**
 * A base Activity that has a collapsing toolbar layout is used for the activities intending to
 * enable the collapsing toolbar function.
 */
public class CollapsingToolbarBaseActivity extends FragmentActivity {

    private class DelegateCallback implements CollapsingToolbarDelegate.HostCallback {
        @Nullable
        @Override
        public ActionBar setActionBar(Toolbar toolbar) {
            CollapsingToolbarBaseActivity.super.setActionBar(toolbar);
            return CollapsingToolbarBaseActivity.super.getActionBar();
        }

        @Override
        public void setOuterTitle(CharSequence title) {
            CollapsingToolbarBaseActivity.super.setTitle(title);
        }
    }

    private CollapsingToolbarDelegate mToolbardelegate;

    private int mCustomizeLayoutResId = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mCustomizeLayoutResId > 0 && !BuildCompatUtils.isAtLeastS()) {
            super.setContentView(mCustomizeLayoutResId);
            return;
        }

        View view = getToolbarDelegate().onCreateView(getLayoutInflater(), null);
        super.setContentView(view);
    }

    @Override
    public void setContentView(int layoutResID) {
        final ViewGroup parent = (mToolbardelegate == null) ? findViewById(R.id.content_frame)
                : mToolbardelegate.getContentFrameLayout();
        if (parent != null) {
            parent.removeAllViews();
        }
        LayoutInflater.from(this).inflate(layoutResID, parent);
    }

    @Override
    public void setContentView(View view) {
        final ViewGroup parent = (mToolbardelegate == null) ? findViewById(R.id.content_frame)
                : mToolbardelegate.getContentFrameLayout();
        if (parent != null) {
            parent.addView(view);
        }
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        final ViewGroup parent = (mToolbardelegate == null) ? findViewById(R.id.content_frame)
                : mToolbardelegate.getContentFrameLayout();
        if (parent != null) {
            parent.addView(view, params);
        }
    }

    /**
     * This method allows an activity to replace the default layout with a customize layout. Notice
     * that it will no longer apply the features being provided by this class when this method
     * gets called.
     */
    protected void setCustomizeContentView(int layoutResId) {
        mCustomizeLayoutResId = layoutResId;
    }

    @Override
    public void setTitle(CharSequence title) {
        getToolbarDelegate().setTitle(title);
    }

    @Override
    public void setTitle(int titleId) {
        setTitle(getText(titleId));
    }

    @Override
    public boolean onNavigateUp() {
        if (!super.onNavigateUp()) {
            finishAfterTransition();
        }
        return true;
    }

    /**
     * Returns an instance of collapsing toolbar.
     */
    @Nullable
    public CollapsingToolbarLayout getCollapsingToolbarLayout() {
        return getToolbarDelegate().getCollapsingToolbarLayout();
    }

    /**
     * Return an instance of app bar.
     */
    @Nullable
    public AppBarLayout getAppBarLayout() {
        return getToolbarDelegate().getAppBarLayout();
    }

    private CollapsingToolbarDelegate getToolbarDelegate() {
        if (mToolbardelegate == null) {
            mToolbardelegate = new CollapsingToolbarDelegate(new DelegateCallback());
        }
        return mToolbardelegate;
    }
}
