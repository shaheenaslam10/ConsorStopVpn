package com.shaheen.developer.consorstopvpn.Models;

/**
 * Created by Shani on 8/17/2019.
 */

public class StreamingLocationModel {

    String id, name;

    public StreamingLocationModel() {
    }

    public StreamingLocationModel(String id, String name) {
        this.id = id;
        this.name = name;
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
}
