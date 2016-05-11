package com.danxx.brisktvlauncher.model;

/**
 * Created by Danxx on 2016/4/27.
 */
public class FragmentBean {

    private String ID;
    private int position;
    private String name;
    private String url;

    public FragmentBean(String ID, int position, String name, String mUrl) {
        this.ID = ID;
        this.position = position;
        this.name = name;
        this.url = mUrl;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
