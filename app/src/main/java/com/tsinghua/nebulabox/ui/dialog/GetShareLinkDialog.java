package com.tsinghua.nebulabox.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.tsinghua.nebulabox.R;
import com.tsinghua.nebulabox.SeafConnection;
import com.tsinghua.nebulabox.SeafException;
import com.tsinghua.nebulabox.account.Account;

class GetShareLinkTask extends TaskDialog.Task {
    String repoID;
    String path;
    boolean isdir;
    SeafConnection conn;
    String link;

    public GetShareLinkTask(String repoID, String path, boolean isdir, SeafConnection conn) {
        this.repoID = repoID;
        this.path = path;
        this.isdir = isdir;
        this.conn = conn;
    }

    @Override
    protected void runTask() {
        try {
            link = conn.getShareLink(repoID, path, isdir);
        } catch (SeafException e) {
            setTaskException(e);
        }
    }

    public String getResult() {
        return link;
    }
}

public class GetShareLinkDialog extends TaskDialog {
    private String repoID;
    private String path;
    private boolean isdir;
    private SeafConnection conn;

    public void init(String repoID, String path, boolean isdir, Account account) {
        this.repoID = repoID;
        this.path = path;
        this.isdir = isdir;
        this.conn = new SeafConnection(account);
    }

    @Override
    protected View createDialogContentView(LayoutInflater inflater, Bundle savedInstanceState) {
        return null;
    }

    @Override
    protected boolean executeTaskImmediately() {
        return true;
    }

    @Override
    protected void onDialogCreated(Dialog dialog) {
        dialog.setTitle(getActivity().getString(R.string.generating_link));
        // dialog.setTitle(getActivity().getString(R.string.generating_link));
    }

    @Override
    protected GetShareLinkTask prepareTask() {
        GetShareLinkTask task = new GetShareLinkTask(repoID, path, isdir, conn);
        return task;
    }

    public String getLink() {
        if (getTask() != null) {
            GetShareLinkTask task = (GetShareLinkTask)getTask();
            return task.getResult();
        }

        return null;
    }
}