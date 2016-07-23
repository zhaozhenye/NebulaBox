package com.tsinghua.nebulabox.ui.adapter;

import android.widget.ImageView;

import com.tsinghua.nebulabox.R;
import com.tsinghua.nebulabox.data.SeafRepo;

/**
 * Repos adapter
 */
public class SeafReposAdapter extends ReposAdapter {

    public SeafReposAdapter(boolean onlyShowWritableRepos, String encryptedRepoId) {
        super(onlyShowWritableRepos, encryptedRepoId);
    }

    @Override
    public int getCount() {
        return repos.size();
    }

    @Override
    public boolean isEmpty() {
        return repos.isEmpty();
    }

    public void add(SeafRepo repo) {
        repos.add(repo);
    }

    @Override
    public SeafRepo getItem(int position) {
        return repos.get(position);
    }

    @Override
    protected int getChildLayout() {
        return R.layout.repo_list_item;
    }

    @Override
    protected int getChildTitleId() {
        return R.id.repo_list_item_title;
    }

    @Override
    protected int getChildSubTitleId() {
        return R.id.repo_list_item_subtitle;
    }

    @Override
    protected int getChildIconId() {
        return R.id.repo_list_item_icon;
    }

    @Override
    protected int getChildActionId() {
        return R.id.repo_list_item_action;
    }

    @Override
    protected SeafRepo getChildSeafRepo(int position) {
        return repos.get(position);
    }

    @Override
    protected void showRepoSelectedIcon(int position, ImageView imageView) {
        return;
    }
}