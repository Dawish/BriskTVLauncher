package com.danxx.brisktvlauncher.ui;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.danxx.brisktvlauncher.R;
import com.danxx.brisktvlauncher.adapter.BaseRecyclerViewAdapter;
import com.danxx.brisktvlauncher.adapter.BaseRecyclerViewHolder;
import com.danxx.brisktvlauncher.model.VideoBean;
import com.danxx.brisktvlauncher.module.RecentMediaStorage;
import com.danxx.brisktvlauncher.module.Settings;
import com.danxx.brisktvlauncher.widget.media.CustomMediaController;
import com.danxx.brisktvlauncher.widget.media.IjkVideoView;
import com.danxx.brisktvlauncher.widget.media.MeasureHelper;
import com.open.androidtvwidget.bridge.RecyclerViewBridge;
import com.open.androidtvwidget.recycle.LinearLayoutManagerTV;
import com.open.androidtvwidget.recycle.OnChildSelectedListener;
import com.open.androidtvwidget.recycle.RecyclerViewTV;
import com.open.androidtvwidget.view.MainUpView;

import java.util.ArrayList;
import java.util.List;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.misc.ITrackInfo;

/**
 * 直播播放器
 */
public class LiveVideoActivity extends AppCompatActivity implements TracksFragment.ITrackHolder ,View.OnFocusChangeListener {
    private static final String TAG = "LiveVideoActivity";

    private String mVideoPath;
    private Uri mVideoUri;

    //    private AndroidMediaController mMediaController;
    private CustomMediaController customMediaController;
    private IjkVideoView mVideoView;
    private RecyclerViewTV videoList;
//    private TextView mToastTextView;
//    private TableLayout mHudView;
//    private DrawerLayout mDrawerLayout;
    private ViewGroup mRightDrawer;

    private View oldView;
    MainUpView mainUpView1;
    RecyclerViewBridge mRecyclerViewBridge;

    private Settings mSettings;
    private boolean mBackPressed;
    private List<VideoBean> datas = new ArrayList<>();;
    private String []names = new String[]{
            "香港电影","综艺频道","高清音乐","动作电影","电影","周星驰","成龙","喜剧","儿歌","LIVE生活"
    };

    private String []urls = new String[]{
            "http://live.gslb.letv.com/gslb?stream_id=lb_hkmovie_1300&tag=live&ext=m3u8&sign=live_tv&platid=10&splatid=1009&format=letv&expect=1",
            "http://live.gslb.letv.com/gslb?stream_id=lb_ent_1300&tag=live&ext=m3u8&sign=live_tv&platid=10&splatid=1009&format=letv&expect=1",
            "http://live.gslb.letv.com/gslb?stream_id=lb_music_1300&tag=live&ext=m3u8&sign=live_tv&platid=10&splatid=1009&format=letv&expect=1",
            "http://live.gslb.letv.com/gslb?tag=live&stream_id=lb_dzdy_720p&tag=live&ext=m3u8&sign=live_tv&platid=10&splatid=1009&format=C1S&expect=1",
            "http://live.gslb.letv.com/gslb?tag=live&stream_id=lb_movie_720p&tag=live&ext=m3u8&sign=live_tv&platid=10&splatid=1009&format=C1S&expect=1",
            "http://live.gslb.letv.com/gslb?tag=live&stream_id=lb_zxc_720p&tag=live&ext=m3u8&sign=live_tv&platid=10&splatid=1009&format=C1S&expect=1",
            "http://live.gslb.letv.com/gslb?tag=live&stream_id=lb_cl_720p&tag=live&ext=m3u8&sign=live_tv&platid=10&splatid=1009&format=C1S&expect=1",
            "http://live.gslb.letv.com/gslb?tag=live&stream_id=lb_comedy_720p&tag=live&ext=m3u8&sign=live_tv&platid=10&splatid=1009&format=C1S&expect=1",
            "http://live.gslb.letv.com/gslb?tag=live&stream_id=lb_erge_720p&tag=live&ext=m3u8&sign=live_tv&platid=10&splatid=1009&format=C1S&expect=1",
            "http://live.gslb.letv.com/gslb?tag=live&stream_id=lb_livemusic_720p&tag=live&ext=m3u8&sign=live_tv&platid=10&splatid=1009&format=C1S&expect=1"
    };
    public static Intent newIntent(Context context, String videoPath, String videoTitle) {
        Intent intent = new Intent(context, LiveVideoActivity.class);
        intent.putExtra("videoPath", videoPath);
        intent.putExtra("videoTitle", videoTitle);
        return intent;
    }

    public static void intentTo(Context context, String videoPath, String videoTitle) {
        context.startActivity(newIntent(context, videoPath, videoTitle));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_video);
        initTiemData();
        initVideoList();
        mSettings = new Settings(this);

        // handle arguments
        mVideoPath = getIntent().getStringExtra("videoPath");
        mVideoPath = urls[0];
        Intent intent = getIntent();
        String intentAction = intent.getAction();
        if (!TextUtils.isEmpty(intentAction)) {
            if (intentAction.equals(Intent.ACTION_VIEW)) {
                mVideoPath = intent.getDataString();
            } else if (intentAction.equals(Intent.ACTION_SEND)) {
                mVideoUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    String scheme = mVideoUri.getScheme();
                    if (TextUtils.isEmpty(scheme)) {
                        Log.e(TAG, "Null unknown ccheme\n");
                        finish();
                        return;
                    }
                    if (scheme.equals(ContentResolver.SCHEME_ANDROID_RESOURCE)) {
                        mVideoPath = mVideoUri.getPath();
                    } else if (scheme.equals(ContentResolver.SCHEME_CONTENT)) {
                        Log.e(TAG, "Can not resolve content below Android-ICS\n");
                        finish();
                        return;
                    } else {
                        Log.e(TAG, "Unknown scheme " + scheme + "\n");
                        finish();
                        return;
                    }
                }
            }
        }

        if (!TextUtils.isEmpty(mVideoPath)) {
            new RecentMediaStorage(this).saveUrlAsync(mVideoPath);
        }

        customMediaController = new CustomMediaController(this, false);
        customMediaController.setVisibility(View.GONE);
//        customMediaController.setSupportActionBar(actionBar);
//        actionBar.setDisplayHomeAsUpEnabled(true);

//        mToastTextView = (TextView) findViewById(R.id.toast_text_view);
//        mHudView = (TableLayout) findViewById(R.id.hud_view);
//        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
//        mRightDrawer = (ViewGroup) findViewById(R.id.right_drawer);
//        mDrawerLayout.setScrimColor(Color.TRANSPARENT);

        // init player
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");

        mVideoView = (IjkVideoView) findViewById(R.id.video_view);
        mVideoView.setMediaController(customMediaController);
//        mVideoView.setHudView(mHudView);
        // prefer mVideoPath
        if (mVideoPath != null)
            mVideoView.setVideoPath(mVideoPath);
        else if (mVideoUri != null)
            mVideoView.setVideoURI(mVideoUri);
        else {
            Log.e(TAG, "Null Data Source\n");
            finish();
            return;
        }
        mVideoView.start();
    }
    public void initTiemData()
    {
        for(int i = 0; i < 10; i++)
        {
            VideoBean videoBean = new VideoBean();
            videoBean.setTvName(names[i]);
            videoBean.setTvUrl(urls[i]);
            datas.add(videoBean);
        }
//        ivLoading.setVisibility(View.GONE);
//        adapter.notifyDataSetChanged();
    }

    private void initVideoList(){
        videoList = (RecyclerViewTV) findViewById(R.id.videoList);

        mainUpView1 = (MainUpView) findViewById(R.id.mainUpView);
        mainUpView1.setEffectBridge(new RecyclerViewBridge());
        mRecyclerViewBridge = (RecyclerViewBridge) mainUpView1.getEffectBridge();
        mRecyclerViewBridge.setUpRectResource(R.drawable.item_rectangle);
        mRecyclerViewBridge.setTranDurAnimTime(200);
        mRecyclerViewBridge.setShadowResource(R.drawable.item_shadow);

        LinearLayoutManagerTV linearLayoutManager = new LinearLayoutManagerTV(LiveVideoActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        videoList.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setOnChildSelectedListener(new OnChildSelectedListener() {
            @Override
            public void onChildSelected(RecyclerView parent, View focusview, int position, int dy) {
                focusview.bringToFront();
                if (oldView == null) {
                    Log.d("danxx", "oldView == null");
                }
                mRecyclerViewBridge.setFocusView(focusview, oldView, 1.1f);
                oldView = focusview;
            }
        });
//        findViewById(R.id.videoContent).setOnFocusChangeListener(this);

        MyAdapter myAdapter = new MyAdapter();
        myAdapter.setData(datas);
        videoList.setAdapter(myAdapter);
        videoList.setFocusable(false);
        myAdapter.notifyDataSetChanged();
        myAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, Object data) {
                if(videoList.getVisibility() == View.VISIBLE) {
                    videoList.setVisibility(View.INVISIBLE);
                    /**隐藏焦点**/
                    mRecyclerViewBridge.setVisibleWidget(true);
                }
            }

            @Override
            public void onItemLongClick(int position, Object data) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        mBackPressed = true;
        finish();
        super.onBackPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(KeyEvent.KEYCODE_DPAD_CENTER == keyCode || KeyEvent.KEYCODE_ENTER == keyCode){
            if(videoList.getVisibility() != View.VISIBLE){
                videoList.setVisibility(View.VISIBLE);
                mRecyclerViewBridge.setVisibleWidget(false);
                videoList.requestFocus();
            }
        }else if(KeyEvent.KEYCODE_BACK == keyCode){
            if(videoList.getVisibility() == View.VISIBLE){
                videoList.setVisibility(View.INVISIBLE);
                mRecyclerViewBridge.setVisibleWidget(true);
                return true;
            }
        }else if(KeyEvent.KEYCODE_MENU == keyCode){
            if(videoList.getVisibility() != View.VISIBLE){
                videoList.setVisibility(View.VISIBLE);
                videoList.requestFocus();
                mRecyclerViewBridge.setVisibleWidget(false);
            }
        }
        return super.onKeyDown(keyCode, event);
    }



    @Override
    protected void onStop() {
        super.onStop();

        if (mBackPressed || !mVideoView.isBackgroundPlayEnabled()) {
            mVideoView.stopPlayback();
            mVideoView.release(true);
            mVideoView.stopBackgroundPlay();
        } else {
            mVideoView.enterBackground();
        }
        IjkMediaPlayer.native_profileEnd();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_player, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_toggle_ratio) {
            int aspectRatio = mVideoView.toggleAspectRatio();
            String aspectRatioText = MeasureHelper.getAspectRatioText(this, aspectRatio);
//            mToastTextView.setText(aspectRatioText);
//            customMediaController.showOnce(mToastTextView);
            return true;
        }
        /**隐藏重播选项**/
//        else if (id == R.id.action_toggle_player) {
//            int player = mVideoView.togglePlayer();
//            String playerText = IjkVideoView.getPlayerText(this, player);
//            mToastTextView.setText(playerText);
//            customMediaController.showOnce(mToastTextView);
//            return true;
//        }
        /**隐藏render选项**/
//        else if (id == R.id.action_toggle_render) {
//            int render = mVideoView.toggleRender();
//            String renderText = IjkVideoView.getRenderText(this, render);
//            mToastTextView.setText(renderText);
//            customMediaController.showOnce(mToastTextView);
//            return true;
//        }
        else if (id == R.id.action_show_info) {
            mVideoView.showMediaInfo();
        }
        /**隐藏tracks选项**/
//        else if (id == R.id.action_show_tracks) {
//            if (mDrawerLayout.isDrawerOpen(mRightDrawer)) {
//                Fragment f = getSupportFragmentManager().findFragmentById(R.id.right_drawer);
//                if (f != null) {
//                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//                    transaction.remove(f);
//                    transaction.commit();
//                }
//                mDrawerLayout.closeDrawer(mRightDrawer);
//            } else {
//                Fragment f = TracksFragment.newInstance();
//                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//                transaction.replace(R.id.right_drawer, f);
//                transaction.commit();
//                mDrawerLayout.openDrawer(mRightDrawer);
//            }
//        }
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public ITrackInfo[] getTrackInfo() {
        if (mVideoView == null)
            return null;

        return mVideoView.getTrackInfo();
    }

    @Override
    public void selectTrack(int stream) {
        mVideoView.selectTrack(stream);
    }

    @Override
    public void deselectTrack(int stream) {
        mVideoView.deselectTrack(stream);
    }

    @Override
    public int getSelectedTrack(int trackType) {
        if (mVideoView == null)
            return -1;

        return mVideoView.getSelectedTrack(trackType);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.activity_up_in, R.anim.activity_up_out);
    }

    /**
     * Called when the focus state of a view has changed.
     *
     * @param v        The view whose state has changed.
     * @param hasFocus The new focus state of v.
     */
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        mRecyclerViewBridge.setFocusView(v, oldView, 1.0f);
        oldView = v;
    }

    class MyAdapter extends BaseRecyclerViewAdapter<VideoBean> {

        @Override
        protected BaseRecyclerViewHolder createItem(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(LiveVideoActivity.this).inflate(R.layout.item_live,null);
            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }

        @Override
        protected void bindData(BaseRecyclerViewHolder holder, int position) {
            ((MyViewHolder)holder).name.setText(getItemData(position).getTvName());
        }
        class MyViewHolder extends BaseRecyclerViewHolder{
            TextView name;
            public MyViewHolder(View itemView) {
                super(itemView);
                name = (TextView) itemView.findViewById(R.id.name);
            }

            @Override
            protected View getView() {
                return null;
            }
        }
    }
}
