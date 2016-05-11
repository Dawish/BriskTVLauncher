package com.danxx.brisktvlauncher.module;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.danxx.brisktvlauncher.app.Common;
import com.danxx.brisktvlauncher.model.FragmentBean;
import com.danxx.brisktvlauncher.ui.FragmentSetAbout;
import com.danxx.brisktvlauncher.ui.FragmentSetBackground;
import com.danxx.brisktvlauncher.ui.FragmentSetRecommend;
import com.danxx.brisktvlauncher.ui.FragmentSetScreensaver;

import java.util.HashMap;

/**
 * fragment build factory
 * Created by Danxx on 2016/4/28.
 */
public class FragmentFactory {
    /**fragment缓存**/
    private static HashMap<String,Fragment> fragments = new HashMap<String, Fragment>();

    public static Fragment buildFragment(FragmentBean data ,int pos){
        Fragment fragment = null;
        String id = data.getID();
        String name = data.getName();
        int position = data.getPosition();
        String url = data.getUrl();

        fragment = fragments.get(String.valueOf(pos));
        if(fragment != null){
            return fragment;
        }

        if(id.equalsIgnoreCase(Common.BACKGROUND)){
            fragment = new FragmentSetBackground();
        }else if(id.equalsIgnoreCase(Common.RECOMMEND)){
            fragment = new FragmentSetRecommend();
        }else if(id.equalsIgnoreCase(Common.SCREENSAVER)){
            fragment = new FragmentSetScreensaver();
        }else if(id.equalsIgnoreCase(Common.ABOUT)){
            fragment = new FragmentSetAbout();
        }

        Bundle bundle = new Bundle();
        bundle.putString("id" ,id);
        bundle.putString("name" ,name);
        bundle.putInt("position", position);
        bundle.putString("url" , url);

        if(fragment != null){
            fragments.put(String.valueOf(pos), fragment);
            fragment.setArguments(bundle);
        }
        return fragment;

    }

}
