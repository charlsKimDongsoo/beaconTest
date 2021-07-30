package com.example.beacontest;

import java.util.Date;

public class DataBeacon {


     String address;
     String name ;
     long now ;
     int rssi;
     String uuid;


    public DataBeacon(String addr, String name, long now, int rssi, String uuid){
        this.address = addr;
        this.name = name;
        this.now = now;
        this.rssi = rssi;
        this.uuid = uuid;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getNow() {
        return now;
    }

    public void setNow(long now) {
        this.now = now;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
