package com.danxx.brisktvlauncher.model;

/**
 * Created by Dawish on 2016/7/3.
 */
public class VideoBean {
    private String tvName;
    private String tvUrl;
    private Boolean isCanPlay;

    public String getTvName() {
        return tvName;
    }

    public void setTvName(String tvName) {
        this.tvName = tvName;
    }

    public String getTvUrl() {
        return tvUrl;
    }

    public void setTvUrl(String tvUrl) {
        this.tvUrl = tvUrl;
    }

    public Boolean getIsCanPlay() {
        return isCanPlay;
    }

    public void setIsCanPlay(Boolean isCanPlay) {
        this.isCanPlay = isCanPlay;
    }
}
