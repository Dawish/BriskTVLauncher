
package com.danxx.brisktvlauncher.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;

import com.danxx.brisktvlauncher.model.AppBean;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 读取设备上所有的APP
 */
public class ReadAllApp {

    private Context mContext;

    public ReadAllApp(Context context) {
        mContext = context;
    }

    private static final String TAG = "ReadAllApp";

    public ArrayList<AppBean> getLaunchAppList() {
        PackageManager localPackageManager = mContext.getPackageManager();
        Intent localIntent = new Intent("android.intent.action.MAIN");
        localIntent.addCategory("android.intent.category.LAUNCHER");
        List<ResolveInfo> localList = localPackageManager.queryIntentActivities(localIntent, 0);
        ArrayList<AppBean> localArrayList = null;
        Iterator<ResolveInfo> localIterator = null;
        if (localList != null) {
            localArrayList = new ArrayList<AppBean>();
            localIterator = localList.iterator();
        }
        while (true) {
            if (!localIterator.hasNext())
                break;
            ResolveInfo localResolveInfo = (ResolveInfo) localIterator.next();
            AppBean localAppBean = new AppBean();
            localAppBean.setIcon(localResolveInfo.activityInfo.loadIcon(localPackageManager));
            localAppBean.setName(localResolveInfo.activityInfo.loadLabel(localPackageManager).toString());
            localAppBean.setPackageName(localResolveInfo.activityInfo.packageName);
            localAppBean.setDataDir(localResolveInfo.activityInfo.applicationInfo.publicSourceDir);
            localAppBean.setLauncherName(localResolveInfo.activityInfo.name);
            String pkgName = localResolveInfo.activityInfo.packageName;
            PackageInfo mPackageInfo;
            try {
                mPackageInfo = mContext.getPackageManager().getPackageInfo(pkgName, 0);
                if ((mPackageInfo.applicationInfo.flags & mPackageInfo.applicationInfo.FLAG_SYSTEM) > 0) {// 系统预装
                    localAppBean.setSysApp(true);
                }
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }

            String noSeeApk = localAppBean.getPackageName();

            /**
             * 屏蔽自己
             */
            if (!noSeeApk.equals("com.danxx.brisktvlauncher")) {
                localArrayList.add(localAppBean);
            }
        }
        return localArrayList;
    }

    public  List getAllInstallApp() {
        List<PackageInfo> packageInfoList = mContext.getPackageManager().getInstalledPackages(0);
        List<AppBean> result = new ArrayList<AppBean>();
        for (PackageInfo packageInfo : packageInfoList) {
            /*
             * 判断是否为非系统应用
             * */
            AppBean app = new AppBean();
            app.setIcon(packageInfo.applicationInfo.loadIcon(mContext.getPackageManager()));
            app.setName(packageInfo.applicationInfo.loadLabel(mContext.getPackageManager()).toString());
            app.setPackageName(packageInfo.packageName);
            app.setVersionName(packageInfo.versionName);
            if ((packageInfo.applicationInfo.flags & packageInfo.applicationInfo.FLAG_SYSTEM) > 0) {// 系统预装
                app.setSysApp(true);
            }else{
                /**如果为系统app就不加入到集合中**/
                /**
                 * 屏蔽自己
                 */
                String noSeeApk = app.getPackageName();
                if (!noSeeApk.equals("com.danxx.brisktvlauncher")) {
                    result.add(app);
                }
            }
        }
        return result;
    }

    public ArrayList<AppBean> getUninstallAppList() {
        PackageManager localPackageManager = mContext.getPackageManager();
        Intent localIntent = new Intent("android.intent.action.MAIN");
        localIntent.addCategory("android.intent.category.LAUNCHER");
        List<ResolveInfo> localList = localPackageManager.queryIntentActivities(localIntent, 0);
        ArrayList<AppBean> localArrayList = null;
        Iterator<ResolveInfo> localIterator = null;
        if (localList != null) {
            localArrayList = new ArrayList<AppBean>();
            localIterator = localList.iterator();
        }
        while (true) {
            if (!localIterator.hasNext())
                break;
            ResolveInfo localResolveInfo = (ResolveInfo) localIterator.next();
            AppBean localAppBean = new AppBean();
            localAppBean.setIcon(localResolveInfo.activityInfo.loadIcon(localPackageManager));
            localAppBean.setName(localResolveInfo.activityInfo.loadLabel(localPackageManager).toString());
            localAppBean.setPackageName(localResolveInfo.activityInfo.packageName);
            localAppBean.setDataDir(localResolveInfo.activityInfo.applicationInfo.publicSourceDir);
            String pkgName = localResolveInfo.activityInfo.packageName;
            PackageInfo mPackageInfo;
            try {
                mPackageInfo = mContext.getPackageManager().getPackageInfo(pkgName, 0);
                if ((mPackageInfo.applicationInfo.flags & mPackageInfo.applicationInfo.FLAG_SYSTEM) > 0) {// 系统预装
                    localAppBean.setSysApp(true);
                } else {
                    localArrayList.add(localAppBean);
                }
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return localArrayList;
    }

    public ArrayList<AppBean> getAutoRunAppList() {
        PackageManager localPackageManager = mContext.getPackageManager();
        Intent localIntent = new Intent("android.intent.action.MAIN");
        localIntent.addCategory("android.intent.category.LAUNCHER");
        List<ResolveInfo> localList = localPackageManager.queryIntentActivities(localIntent, 0);
        ArrayList<AppBean> localArrayList = null;
        Iterator<ResolveInfo> localIterator = null;
        if (localList != null) {
            localArrayList = new ArrayList<AppBean>();
            localIterator = localList.iterator();
        }

        while (true) {
            if (!localIterator.hasNext())
                break;
            ResolveInfo localResolveInfo = localIterator.next();
            AppBean localAppBean = new AppBean();
            localAppBean.setIcon(localResolveInfo.activityInfo.loadIcon(localPackageManager));
            localAppBean.setName(localResolveInfo.activityInfo.loadLabel(localPackageManager).toString());
            localAppBean.setPackageName(localResolveInfo.activityInfo.packageName);
            localAppBean.setDataDir(localResolveInfo.activityInfo.applicationInfo.publicSourceDir);
            String pkgName = localResolveInfo.activityInfo.packageName;
            String permission = "android.permission.RECEIVE_BOOT_COMPLETED";
            try {
                PackageInfo mPackageInfo = mContext.getPackageManager().getPackageInfo(pkgName, 0);
                if ((PackageManager.PERMISSION_GRANTED == localPackageManager.checkPermission(permission, pkgName))
                        && !((mPackageInfo.applicationInfo.flags & mPackageInfo.applicationInfo.FLAG_SYSTEM) > 0)) {
                    localArrayList.add(localAppBean);
                }
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return localArrayList;
    }
}