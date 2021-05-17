package com.example.snapapp;

public class ImageUploadInfo {

    public String name;
    public String url;

    public ImageUploadInfo() {
    }

    public ImageUploadInfo(String name, String url) {
        this.name = name;
        this.url = url;
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
