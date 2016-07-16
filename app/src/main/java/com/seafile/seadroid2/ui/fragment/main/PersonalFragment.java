package com.seafile.seadroid2.ui.fragment.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.seafile.seadroid2.R;
import com.seafile.seadroid2.SeafException;
import com.seafile.seadroid2.data.DataManager;
import com.seafile.seadroid2.data.SeafDirent;
import com.seafile.seadroid2.data.SeafItem;
import com.seafile.seadroid2.data.SeafRepo;
import com.seafile.seadroid2.interf.OnItemClickListener;
import com.seafile.seadroid2.interf.OnItemLongClickListener;
import com.seafile.seadroid2.ui.NavContext;
import com.seafile.seadroid2.ui.activity.MainActivity;
import com.seafile.seadroid2.ui.activity.TransferActivity;
import com.seafile.seadroid2.ui.adapter.SeafItemAdapter;
import com.seafile.seadroid2.ui.base.BaseFragment;
import com.seafile.seadroid2.ui.dialog.FileDirentCreatedDialog;
import com.seafile.seadroid2.ui.dialog.FileOptionDialog;
import com.seafile.seadroid2.util.ConcurrentAsyncTask;
import com.seafile.seadroid2.util.Utils;
import com.seafile.seadroid2.util.log.KLog;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 个人
 * Created by Alfred on 2016/7/11.
 */
public class PersonalFragment extends BaseFragment implements View.OnClickListener, OnItemClickListener, SwipeRefreshLayout.OnRefreshListener, FileOptionDialog.OnItemClickListener, OnItemLongClickListener,FileDirentCreatedDialog.onFileDirentCreatedListener {

    private static final String DEBUG_TAG = "PersonalFragment";

    @Bind(R.id.option_personal_ll)
    LinearLayout optionLinearLayout;
    @Bind(R.id.category_personal_tv)
    TextView categoryTextView;
    @Bind(R.id.sort_personal_tv)
    TextView sortTextView;
    @Bind(R.id.create_personal_tv)
    TextView createTextView;
    @Bind(R.id.transfer_personal_tv)
    TextView transferTextView;
    @Bind(R.id.refresh_layout_personal_srlayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.recycler_view_personal_rl)
    ListView recyclerView;
    @Bind(R.id.empty_rl)
    RelativeLayout emptyRelativeLayout;
    @Bind(R.id.empty_iv)
    ImageView emptyImageView;

    private String[] categoryOptionalList;
    private String[] sortOptionalList;

    private FileOptionDialog categoryDialog;
    private FileOptionDialog sortDialog;
    private FileDirentCreatedDialog fileDirentCreatedDialog;

    private List<SeafDirent> pictureList;
    private List<SeafDirent> videoList;
    private List<SeafDirent> movieList;
    private List<SeafDirent> txtList;
    private List<SeafDirent> appList;

    private List<SeafDirent> fileNameDirentList;
    private List<SeafDirent> dateDirentList;

    private List<SeafDirent> allDirentList;

    private String[] pictureFormat;
    private String[] audioFormat;
    private String[] videoFormat;
    private String[] txtFormat;
    private String[] appFormat;

    private SeafItemAdapter adapter;
    private int lastVisibleItem;
    private LinearLayoutManager linearLayoutManager;

    private DataManager dataManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        categoryOptionalList = getResources().getStringArray(R.array.category_files_options_array);
        sortOptionalList = getResources().getStringArray(R.array.sorts_files_options_array);

        categoryDialog = new FileOptionDialog();
        categoryDialog.setList(categoryOptionalList);
        categoryDialog.setOnItemClickListener(this);
        sortDialog = new FileOptionDialog();
        sortDialog.setList(sortOptionalList);
        sortDialog.setOnItemClickListener(this);
        fileDirentCreatedDialog = new FileDirentCreatedDialog();
        fileDirentCreatedDialog.setOnFileDirentCreatedListener(this);


        Resources resources = getResources();
        pictureFormat = resources.getStringArray(R.array.format_picture);
        audioFormat = resources.getStringArray(R.array.format_audio);
        videoFormat = resources.getStringArray(R.array.format_video);
        appFormat = resources.getStringArray(R.array.format_app);
        txtFormat = resources.getStringArray(R.array.format_document);

        dataManager = ((MainActivity) mActivity).getDataManager();

        allDirentList = new ArrayList<>();
        adapter = new SeafItemAdapter((MainActivity) mActivity);
//		adapter.setOnItemClickListener(this);
//		adapter.setOnItemLongClickListener(this);

        linearLayoutManager = new LinearLayoutManager(mActivity);
//		recyclerView.addItemDecoration(new RecycleViewDivider(mContext, LinearLayoutManager.VERTICAL, 10, ContextCompat.getColor(mContext, R.color.app_main_color)));
//        recyclerView.addOnScrollListener(new PauseOnScrollListener());
//		recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        recyclerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                NavContext navContext = getNavContext();
                SeafItem o = adapter.getItem(i);
                boolean inRepo = navContext.inRepo();
                Log.e(DEBUG_TAG, "inRepo:" + inRepo + " " + navContext.getRepoID() + o.getClass());

                if (inRepo) {
                    if (o instanceof SeafDirent) {
                        SeafDirent seafDirent = (SeafDirent) o;
                        if (seafDirent.isDir()) {
                            String currentPath = navContext.getDirPath();
                            String newPath = currentPath.endsWith("/") ?
                                    currentPath + seafDirent.name : currentPath + "/" + seafDirent.name;
                            navContext.setDir(newPath, seafDirent.id);
                            refreshView(false);
                        } else {
                            // TODO: 16-7-16  file condition
                        }
                    } else
                        return;
                } else {
                    SeafRepo seafRepo = (SeafRepo) o;
                    navContext.setRepoID(seafRepo.id);
                    navContext.setRepoName(seafRepo.getName());
                    navContext.setDir("/", seafRepo.root);
                    refreshView(false);
                }

            }
        });


        swipeRefreshLayout.setOnRefreshListener(this);
//        swipeRefreshLayout.setSize(SwipeRefreshLayout.LARGE);
//        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(mActivity,R.color.app_main_color));
        swipeRefreshLayout.setColorSchemeColors(R.color.swipe_refresh_color_1, R.color.swipe_refresh_color_2, R.color.swipe_refresh_color_3, R.color.swipe_refresh_color_4);
        refreshView(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.category_personal_tv, R.id.sort_personal_tv, R.id.create_personal_tv, R.id.transfer_personal_tv})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.category_personal_tv:
                KLog.i("sortDialog = " + sortDialog);
                if (sortDialog.isVisible()) {
                    sortDialog.dismiss();
                }
                categoryDialog.show(getFragmentManager(), "categoryDialog");
                break;
            case R.id.sort_personal_tv:
                if (categoryDialog.isVisible()) {
                    categoryDialog.dismiss();
                }
                sortDialog.show(getFragmentManager(), "sortDialog");
                break;
            case R.id.create_personal_tv:
                fileDirentCreatedDialog.show(getFragmentManager(),"FileDirentCreatedDialog");
                break;
            case R.id.transfer_personal_tv:
                Intent intent = new Intent(mActivity, TransferActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    public void OnItemClick(DialogInterface dialog, String[] list, int which) {
        switch (which) {
            case 0:
                //照片,按文件排序
                if (list.length > 2) {
                    //照片
                    pictureList = Utils.categoryFile(allDirentList, pictureFormat);
                    setCategoryDataToAdapter(pictureList);
                } else {
                    fileNameDirentList = Utils.sortFileByFileName(allDirentList);
                    setCategoryDataToAdapter(fileNameDirentList);
                }
                break;
            case 1:
                //音乐,按时间倒序排序
                if (list.length > 2) {
                    //音乐
                    videoList = Utils.categoryFile(allDirentList, audioFormat);
                    setCategoryDataToAdapter(videoList);
                }else {
                    dateDirentList = Utils.sortFileByDate(allDirentList);
                    setCategoryDataToAdapter(dateDirentList);
                }
                break;
            case 2:
                //影视
                movieList = Utils.categoryFile(allDirentList, videoFormat);
                setCategoryDataToAdapter(movieList);
                break;
            case 3:
                //文档
                txtList = Utils.categoryFile(allDirentList, txtFormat);
                setCategoryDataToAdapter(txtList);
                break;
            case 4:
                //应用
                appList = Utils.categoryFile(allDirentList, appFormat);
                setCategoryDataToAdapter(appList);
                break;
            case 5:
                //全部
                setCategoryDataToAdapter(allDirentList);
                break;
        }
    }

    private void setCategoryDataToAdapter(List<SeafDirent> list) {
        if (list.size() > 0) {
            swipeRefreshLayout.setVisibility(View.VISIBLE);
            emptyRelativeLayout.setVisibility(View.GONE);

            adapter.clear();
            for (SeafDirent seafDirent : list) {
                adapter.add(seafDirent);
            }
            adapter.notifyDataSetChanged();
        }else{
            swipeRefreshLayout.setVisibility(View.GONE);
            emptyRelativeLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRefresh() {
        allDirentList.clear();
        refreshView(true);
    }

    public DataManager getDataManager() {
        return dataManager;
    }


    @Override
    public boolean onItemLongClick(ViewGroup parent, View view, Object o, int position) {
        return false;
    }

    @Override
    public void onItemClck(ViewGroup parent, View view, Object o, int position) {
        NavContext navContext = getNavContext();
        if (navContext.inRepo()) {
            if (o instanceof SeafDirent) {
                SeafDirent seafDirent = (SeafDirent) o;
                if (seafDirent.isDir()) {
                    String currentPath = navContext.getDirPath();
                    String newPath = currentPath.endsWith("/") ?
                            currentPath + seafDirent.name : currentPath + "/" + seafDirent.name;
                    navContext.setDir(newPath, seafDirent.id);
                    refreshView(false);
                } else {
                }
            } else
                return;
        } else {
            SeafRepo seafRepo = (SeafRepo) o;
            navContext.setRepoID(seafRepo.id);
            navContext.setRepoName(seafRepo.getName());
            navContext.setDir("/", seafRepo.root);
            refreshView(false);
        }

    }

    public void refreshView(boolean forceRefresh) {
        NavContext navContext = getNavContext();
        if (navContext.inRepo()) {
//            if (mActivity.getCurrentPosition() == BrowserActivity.INDEX_LIBRARY_TAB) {
//                mActivity.enableUpButton();
//            }
            navToDirectory(forceRefresh);
            optionLinearLayout.setEnabled(true);
            categoryTextView.setEnabled(true);
            sortTextView.setEnabled(true);
            createTextView.setEnabled(true);
            transferTextView.setEnabled(true);
        } else {
//            mActivity.disableUpButton();
            navToReposView(forceRefresh);
            optionLinearLayout.setEnabled(false);
            categoryTextView.setEnabled(false);
            sortTextView.setEnabled(false);
            createTextView.setEnabled(false);
            transferTextView.setEnabled(false);
        }
    }

    private void navToReposView(boolean forceRefresh) {
        emptyRelativeLayout.setVisibility(View.GONE);
		ConcurrentAsyncTask.execute(new LoadTask(getDataManager()));
    }

    private void navToDirectory(boolean forceRefresh) {
        NavContext navContext = getNavContext();
        if (navContext == null) {
            Log.e(DEBUG_TAG, "navcontext is null");
        } else {
            Log.e(DEBUG_TAG, navContext.getRepoName() + navContext.getDirID() + navContext.getDirPath());
        }
        ConcurrentAsyncTask.execute(new LoadDirTask(getDataManager()),
                navContext.getRepoName(),
                navContext.getRepoID(),
                navContext.getDirPath());
    }

    @Override
    public NavContext getNavContext() {
        return ((MainActivity) mActivity).getNavContext();
    }

    @Override
    public void OnFileDirentCreated(String fileDirentName) {

    }

    private class LoadTask extends AsyncTask<Void, Void, List<SeafRepo>> {
        SeafException err = null;
        DataManager dataManager;

        public LoadTask(DataManager dataManager) {
            this.dataManager = dataManager;
        }

        @Override
        protected void onPreExecute() {
            if (mActivity == null) {
                return;
            }

        }

        @Override
        protected List<SeafRepo> doInBackground(Void... params) {
            try {
                List<SeafRepo> repos = dataManager.getReposFromServer();
                for (SeafRepo repo : repos) {
                    Log.e(DEBUG_TAG, repo.getName() + " " + repo.id);
                }
                return repos;
            } catch (SeafException e) {
                err = e;
                e.printStackTrace();
                Log.e(DEBUG_TAG, e.getMessage());
                return null;
            }
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(List<SeafRepo> rs) {
            if (mActivity == null)
                // this occurs if user navigation to another activity
                return;

            if (getNavContext().inRepo()) {
                // this occurs if user already navigate into a repo
                return;
            }
            adapter.clear();
            if (rs.size() > 0) {
                for (SeafRepo seafRepo : rs) {
                    adapter.add(seafRepo);
                }
            }
            adapter.notifyDataSetChanged();
//            adapter.setFootViewShown(false);
            swipeRefreshLayout.setRefreshing(false);

        }
    }

    private class LoadDirTask extends AsyncTask<String, Void, List<SeafDirent>> {

        SeafException err = null;
        String myRepoName;
        String myRepoID;
        String myPath;

        DataManager dataManager;

        public LoadDirTask(DataManager dataManager) {
            this.dataManager = dataManager;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected List<SeafDirent> doInBackground(String... params) {
            if (params.length != 3) {
                KLog.d("Wrong params to LoadDirTask");
                return null;
            }

            myRepoName = params[0];
            myRepoID = params[1];
            myPath = params[2];
            try {
                return dataManager.getDirentsFromServer(myRepoID, myPath);
            } catch (SeafException e) {
                err = e;
                return null;
            }

        }

        private void resend() {
            if (mActivity == null)
                return;
            NavContext nav = getNavContext();
            if (!myRepoID.equals(nav.getRepoID()) || !myPath.equals(nav.getDirPath())) {
                return;
            }

            ConcurrentAsyncTask.execute(new LoadDirTask(dataManager), myRepoName, myRepoID, myPath);
        }

        private void displaySSLError() {
            if (mActivity == null)
                return;

            NavContext nav = getNavContext();
            if (!myRepoID.equals(nav.getRepoID()) || !myPath.equals(nav.getDirPath())) {
                return;
            }
//                showError(R.string.ssl_error);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(List<SeafDirent> dirents) {
            if (mActivity == null)
                // this occurs if user navigation to another activity
                return;
            allDirentList.clear();
            allDirentList.addAll(dirents);
            adapter.clear();
            if (dirents.size() > 0) {
                for (SeafDirent seafDirent : dirents) {
                    adapter.add(seafDirent);
                }
            }
            adapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);

            NavContext nav = getNavContext();
            if (!myRepoID.equals(nav.getRepoID()) || !myPath.equals(nav.getDirPath())) {
                return;
            }

            getDataManager().setDirsRefreshTimeStamp(myRepoID, myPath);
        }
    }



}
