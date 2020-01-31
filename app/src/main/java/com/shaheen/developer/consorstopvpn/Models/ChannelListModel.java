package com.shaheen.developer.consorstopvpn.Models;

public class ChannelListModel {


    String id ;
    String name ;
    String channel_url;
    String order ;
    String icon_url ;
    String package_name_android_tv;
    String package_name_android ;
    String package_name_amazon_fs;
    String is_free ;
    String is_new ;


    public ChannelListModel(String id, String name, String channel_url, String order, String icon_url, String package_name_android_tv, String package_name_android, String package_name_amazon_fs, String is_free, String is_new) {
        this.id = id;
        this.name = name;
        this.channel_url = channel_url;
        this.order = order;
        this.icon_url = icon_url;
        this.package_name_android_tv = package_name_android_tv;
        this.package_name_android = package_name_android;
        this.package_name_amazon_fs = package_name_amazon_fs;
        this.is_free = is_free;
        this.is_new = is_new;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getChannel_url() {
        return channel_url;
    }

    public void setChannel_url(String channel_url) {
        this.channel_url = channel_url;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getIcon_url() {
        return icon_url;
    }

    public void setIcon_url(String icon_url) {
        this.icon_url = icon_url;
    }

    public String getPackage_name_android_tv() {
        return package_name_android_tv;
    }

    public void setPackage_name_android_tv(String package_name_android_tv) {
        this.package_name_android_tv = package_name_android_tv;
    }

    public String getPackage_name_android() {
        return package_name_android;
    }

    public void setPackage_name_android(String package_name_android) {
        this.package_name_android = package_name_android;
    }

    public String getPackage_name_amazon_fs() {
        return package_name_amazon_fs;
    }

    public void setPackage_name_amazon_fs(String package_name_amazon_fs) {
        this.package_name_amazon_fs = package_name_amazon_fs;
    }

    public String getIs_free() {
        return is_free;
    }

    public void setIs_free(String is_free) {
        this.is_free = is_free;
    }

    public String getIs_new() {
        return is_new;
    }

    public void setIs_new(String is_new) {
        this.is_new = is_new;
    }
}
