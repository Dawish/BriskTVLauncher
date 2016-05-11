package com.danxx.brisktvlauncher.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.danxx.brisktvlauncher.R;
import com.danxx.brisktvlauncher.app.Common;
import com.danxx.brisktvlauncher.base.BaseActivity;
import com.danxx.brisktvlauncher.model.FragmentBean;
import com.danxx.brisktvlauncher.module.FragmentFactory;

import java.util.ArrayList;
import java.util.List;

public class ActivitySetting extends BaseActivity {
    private ListView tabList;
    private List<FragmentBean> fragmentData = new ArrayList<FragmentBean>();
    private MyAdapater mAdapter;
    private int lastPosition = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_setting);

        init();
    }

    private void init(){
        tabList = (ListView) findViewById(R.id.tabList);
        /************************填充假数据，可以根据服务器下发的数据来初始化数据(一定得给出模板类型layout_code)*************************/
        FragmentBean backGround = new FragmentBean(Common.BACKGROUND,0,"背景","http://www.dusa.com/da/index.jsp");
        fragmentData.add(backGround);
        FragmentBean recommend = new FragmentBean(Common.RECOMMEND,1,"推荐","http://www.dusa.com/food/list.jsp");
        fragmentData.add(recommend);
        FragmentBean screensaver = new FragmentBean(Common.SCREENSAVER,2,"屏保" ,"http://www.meitu.com/pic/con.jsp");
        fragmentData.add(screensaver);
        FragmentBean about = new FragmentBean(Common.ABOUT,3,"关于","http://www.alibaba.com/good/index.php");
        fragmentData.add(about);

        mAdapter = new MyAdapater(this , fragmentData);
        tabList.setAdapter(mAdapter);
        tabList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mAdapter.notifyDataSetChanged();
        /**根据用户点击来切换fragment**/
        tabList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("danxx", "position-->" + position);
                if (lastPosition == position) {
                    return;
                }
                if (lastPosition != -1) {
                    Fragment fromFragment = getSupportFragmentManager().findFragmentByTag(String.valueOf(lastPosition));
                    if (fromFragment == null) {
                        fromFragment = buildFragment(lastPosition);
                    }
                    Fragment toFragment = getSupportFragmentManager().findFragmentByTag(String.valueOf(position));
                    if (toFragment == null) {
                        toFragment = buildFragment(position);
                    }
                    switchContent(fromFragment, lastPosition, toFragment, position);

                } else {
                    /*第一次显示fragment*/
                    getSupportFragmentManager().beginTransaction().
                            add(R.id.contentLayout, FragmentFactory.buildFragment(mAdapter.getItemData(position), position), String.valueOf(position)).commit();
                }
                lastPosition = position;
            }
        });
    }

    public Fragment buildFragment(int position){
        /**根据模板类型layout_code的不同来创建对应的fragment**/
        return FragmentFactory.buildFragment(mAdapter.getItemData(position), position);
    }

    /**
     * 切换显示的fragment
     * @param fromFragment
     * @param fromPos
     * @param toFragment
     * @param toPos
     */
    public void switchContent(Fragment fromFragment ,int fromPos, Fragment toFragment ,int toPos) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (!toFragment.isAdded()) {    // 先判断是否被add过
            transaction.hide(fromFragment).add(R.id.contentLayout, toFragment ,String.valueOf(toPos)).commit(); // 隐藏当前的fragment，add下一个到Activity中
        } else {
            transaction.hide(fromFragment).show(toFragment).commit(); // 隐藏当前的fragment，显示下一个
        }
    }

    class MyAdapater extends BaseAdapter {

        private List<FragmentBean> mData;
        private Context mContext;

        public MyAdapater(Context context , List<FragmentBean> data){
            this.mContext = context;
            this.mData = data;
        }

        @Override
        public int getCount() {
            return mData.size();
        }
        @Override
        public Object getItem(int position) {
            return mData.get(position);
        }

        public FragmentBean getItemData(int position){
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        /**
         * 数据不会很多，所以没有使用ViewHolder复用
         * @param position
         * @param convertView
         * @param parent
         * @return
         */
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            Log.d("danxx", "getView_position-->" + position);
            final View view =  LayoutInflater.from(mContext).inflate(R.layout.item_tab_list , null);

            TextView tabName = (TextView) view.findViewById(R.id.tabName);

            tabName.setText(mData.get(position).getName());

            return view;
        }


    }
}
