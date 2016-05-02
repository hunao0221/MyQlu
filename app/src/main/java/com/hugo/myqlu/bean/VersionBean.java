package com.hugo.myqlu.bean;

/**
 * @auther Hugo
 * Created on 2016/4/17 21:58.
 */
public class VersionBean {

    /**
     * versionName : 2.0
     * versionCode : 2
     * description : 新增超强牛逼功能快来体验吧
     * downloadUrl : http://10.0.2.2:8080/mobilesafe.apk
     */

    private String versionName;
    private int versionCode;
    private String description;
    private String downloadUrl;

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
}
