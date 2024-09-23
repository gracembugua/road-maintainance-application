package com.example.myapp;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

class Pothole implements Serializable {
    private String id;
    private String road;
    private String location;
    private String potholeSize;
    private String username;
    private String date;
    private String mediaType;
    private Map<String, Bid> bids;

    public Pothole() {
    }

    public Pothole(String id, String road, String location, String potholeSize, String username, String date, String mediaUrl) {
        this.id = id;
        this.road = road;
        this.location = location;
        this.potholeSize = potholeSize;
        this.username = username;
        this.date = date;
        this.mediaType = mediaUrl;
        this.bids = new HashMap<>(); // Initialize bids map
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public String getRoad() {
        return road;
    }

    public String getLocation() {
        return location;
    }

    public String getPotholeSize() {
        return potholeSize;
    }

    public String getUsername() {
        return username;
    }

    public String getDate() {
        return date;
    }

    public String getMediaUrl() {
        return mediaType;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaType = mediaUrl;
    }

    public Map<String, Bid> getBids() {
        return bids;
    }

    public void setBids(Map<String, Bid> bids) {
        this.bids = bids;
    }

    // Method to add a bid
    public void addBid(Bid bid) {
        bids.put(bid.getId(), bid);
    }
}
