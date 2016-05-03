package com.danxx.brisk.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.danxx.brisk.R;
import com.danxx.brisk.adapter.BaseRecyclerViewAdapter;
import com.danxx.brisk.adapter.BaseRecyclerViewHolder;
import com.danxx.brisk.adapter.FocusGridLayoutManager;
import com.danxx.brisk.base.BaseActivity;
import com.danxx.brisk.model.AppBean;
import com.danxx.brisk.utils.FocusAnimUtils;
import com.danxx.brisk.utils.ReadAllApp;
import com.danxx.brisk.widget.FocusListenerLinearLayout;
import com.danxx.brisk.widget.FocusRecyclerView;
import com.danxx.brisk.widget.SpaceItemDecoration;
import com.victor.loading.rotate.RotateLoading;

import java.util.ArrayList;
import java.util.List;

/**
 * app管理
 * created by danxingxi on 2016/4/19
 */
public class ActivityManagerApp extends BaseActivity {
    private RotateLoading rotateLoading;
    private FocusRecyclerView gRecyclerView;
    private FocusGridLayoutManager fgLayoutManager;
    private SpaceItemDecoration itemDecoration;
    private ReadAllApp readAllApp;
    private MyAdapter mAdapter;
    private List<AppBean> mData = new ArrayList<AppBean>();
    private AppCompatSeekBar seekBar;
    private LinearLayout deleteBtn;
    private TextView tvConut;
    private ImageView ivUninstall;
    private FocusListenerLinearLayout focusListenerLinearLayout;
    private AppReceiver appReceiver;
    private TextView tvTips;
    private View mOldView;
    private static boolean BATCH_DELETE = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_manager_app);
        rotateLoading = (RotateLoading) findViewById(R.id.rotateloading);
        rotateLoading.start();
        initView();
    }

    private void initView(){

        focusListenerLinearLayout = (FocusListenerLinearLayout) findViewById(R.id.layoutContent);
        focusListenerLinearLayout.setOnFocusSearchListener(focusSearchListener);
        focusListenerLinearLayout.setOnChildFocusChangeListener(focusChangeListener);

        seekBar = (AppCompatSeekBar) findViewById(R.id.seekBar);
        seekBar.setMax(100);
        seekBar.setProgress(45);
        tvTips = (TextView) findViewById(R.id.tvTips);
        tvConut = (TextView) findViewById(R.id.tvCount);
        ivUninstall = (ImageView) findViewById(R.id.imgUninstall);
        deleteBtn = (LinearLayout) findViewById(R.id.deleteBtn);
        deleteBtn.requestFocus();
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(BATCH_DELETE){
                    if(mAdapter.getSelectedItems().size()>0){
                        //批量卸载代码
                        batchUninstallApp(mAdapter.getSelectedItems());
                    }else {
                        BATCH_DELETE = false;
                        ivUninstall.setImageDrawable(getResources().getDrawable(R.drawable.delete_icon));
                        tvTips.setText("Tips:点击'垃圾桶'进入批量卸载模式");
                        mAdapter.notifyDataSetChanged();
                    }
                }else{
                    BATCH_DELETE = true;
                    ivUninstall.setImageDrawable(getResources().getDrawable(R.drawable.delete_icon_selected));
                    tvTips.setText("Tips:点击'返回'键清除选中的应用");
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
        gRecyclerView = (FocusRecyclerView) findViewById(R.id.gRecyclerView);
        fgLayoutManager = new FocusGridLayoutManager(this ,4);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.d_8dp);
        itemDecoration = new SpaceItemDecoration(spacingInPixels);

        gRecyclerView.setLayoutManager(fgLayoutManager);
        gRecyclerView.addItemDecoration(itemDecoration);
        gRecyclerView.setScrollDy(220);
        gRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new MyAdapter(this);
        readAllApp = new ReadAllApp(this);
        gRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, Object data) {
                toggleSelection(position);
            }

            @Override
            public void onItemLongClick(int position, Object data) {
                toggleSelection(position);
            }
        });
        upDateApp();
    }

    /**
     *
     */
    private void upDateApp(){
        mAdapter.clearSelection();
        mData = readAllApp.getAllInstallApp();
        mAdapter.setData(mData);
        Log.d("danxx", "size-->" + mData.size());
        mAdapter.notifyDataSetChanged();
        rotateLoading.stop();
    }

    private final FocusListenerLinearLayout.OnChildFocusChangeListener focusChangeListener = new FocusListenerLinearLayout.OnChildFocusChangeListener() {
        @Override
        public void focusChange(View oldView, View newView) {

            if(mOldView != null){
                FocusAnimUtils.unFocusAnim(mOldView);
            }
            if(newView != null){
                FocusAnimUtils.focusAnim(newView);
            }
            mOldView = newView;  //4.3以下版本需要自己保存上一次的焦点.
        }
    };

    private final FocusListenerLinearLayout.OnFocusSearchListener focusSearchListener = new FocusListenerLinearLayout.OnFocusSearchListener() {
        @Override
        public View onFocusSearch(View focused, int direction) {
            View res = null;
            switch (direction){
                case View.FOCUS_DOWN:
                    break;
                case  View.FOCUS_UP:
                    if(gRecyclerView.hasFocus()){
                        res = focused;
                    }
                    break;
                case View.FOCUS_LEFT:
                    break;
                case  View.FOCUS_RIGHT:
                    break;
            }
            return res;
        }
    };

    /**
     * 切换item的选中状态，并刷新UI
     * @param position
     */
    private void toggleSelection(int position) {
        View viewItem = fgLayoutManager.findViewByPosition(position);
        ImageView deletePic = null;
        if(BATCH_DELETE){  //批量卸载状态
            mAdapter.toggleSelection(position);
            tvConut.setText(mAdapter.getSelectedItems().size()+"");
            //点击显示选中状态
            if(mAdapter.isSelected(position)){
                if(viewItem != null){
                    deletePic = (ImageView) viewItem.findViewById(R.id.deletePic);
                    deletePic.setImageDrawable(this.getResources().getDrawable(R.drawable.delete_icon_selected));
                }
            }else {
                if(viewItem != null){
                    deletePic = (ImageView) viewItem.findViewById(R.id.deletePic);
                    deletePic.setImageDrawable(this.getResources().getDrawable(R.drawable.delete_icon));
                }
            }
        }else{ //单个卸载状态，点击直接卸载
//            deletePic.setVisibility(View.GONE);
            uninstallApp(position);
        }
    }

    /**
     * 批量卸载
     * @param appList
     */
    private void batchUninstallApp(List<Integer> appList){
        for(int position : appList){
            uninstallApp(position);
        }
    }

    /**
     * 单个卸载
     * @param position
     */
    private void uninstallApp(int position){
        String packageName = mAdapter.getItemData(position).getPackageName();
        Uri packageURI = Uri.parse("package:" + packageName);
        Intent uninstallIntent = new Intent(Intent.ACTION_DELETE,packageURI);
        startActivity(uninstallIntent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        appReceiver = new AppReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.PACKAGE_ADDED");
        intentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
        intentFilter.addDataScheme("package");
        registerReceiver(appReceiver, intentFilter);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode == event.KEYCODE_BACK){
            if(mAdapter.getSelectedItems().size()>0){
                mAdapter.clearSelection();
                tvConut.setText("");
                deleteBtn.requestFocus();
                return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    static class MyAdapter extends BaseRecyclerViewAdapter<AppBean>{
        private Context mContext;

        public MyAdapter(Context mContext) {
            this.mContext = mContext;
        }

        /**
         * 创建item view
         *
         * @param parent
         * @param viewType
         * @return
         */
        @Override
        protected BaseRecyclerViewHolder createItem(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_app_manager ,null);
            AppViewHolder viewHolder = new AppViewHolder(view);
            viewHolder.appIcon = (ImageView) view.findViewById(R.id.appIcon);
            viewHolder.appName = (TextView) view.findViewById(R.id.appName);
            viewHolder.appVersion = (TextView) view.findViewById(R.id.appVersion);
            viewHolder.deletePic = (ImageView) view.findViewById(R.id.deletePic);
            return viewHolder;
        }

        /**
         * 绑定数据
         *
         * @param holder
         * @param position
         */
        @Override
        protected void bindData(BaseRecyclerViewHolder holder, int position) {
            ((AppViewHolder)holder).appIcon.setImageDrawable(getItemData(position).getIcon());
            ((AppViewHolder)holder).appName.setText(getItemData(position).getName());
            ((AppViewHolder)holder).appVersion.setText("版本:" + getItemData(position).getVersionName());
            ((AppViewHolder)holder).deletePic.setImageDrawable(isSelected(position) ? getDrawableById(R.drawable.delete_icon_selected) : getDrawableById(R.drawable.delete_icon));
            ((AppViewHolder)holder).deletePic.setVisibility(BATCH_DELETE ? View.VISIBLE : View.GONE);
        }

        static class AppViewHolder extends BaseRecyclerViewHolder{
            View mView;
            ImageView appIcon ,deletePic;
            TextView appName ,appVersion;
            public AppViewHolder(View itemView) {
                super(itemView);
                mView = itemView;
            }

            @Override
            protected View getView() {
                return mView;
            }
        }
        private Drawable getDrawableById (int id){
            return mContext.getResources().getDrawable(id);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BATCH_DELETE = false;
    }


    private class AppReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //安装广播
            if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {
                upDateApp();
            }
            //卸载广播
            if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {
                tvConut.setText("0");
                upDateApp();
            }
        }
    }
}
