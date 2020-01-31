package com.shaheen.developer.consorstopvpn.Models;

public class AppData {

    String time, time_elapsed;


    public AppData() {
    }

    public AppData(String time, String time_elapsed) {
        this.time = time;
        this.time_elapsed = time_elapsed;
    }

    public String getTime_elapsed() {
        return time_elapsed;
    }

    public void setTime_elapsed(String time_elapsed) {
        this.time_elapsed = time_elapsed;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
