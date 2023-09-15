package com.example.triptrack;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Plans {
    private String tripId;
    private long date;
    private String time;
    private String location;
    private int priority;
    private String description;

    public Plans(String tripId, long date, String time, String location, int priority, String description) {
        this.tripId = tripId;
        this.date = date;
        this.time = time;
        this.location = location;
        this.priority = priority;
        this.description = description;
    }

    public String getTripId() {
        return tripId;
    }

    public long getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getLocation() {
        return location;
    }

    public int getPriority() {
        return priority;
    }

    public String getDescription() {
        return description;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @NotNull
    @Override
    public String toString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String dateString = dateFormat.format(new Date(date));

        return dateString + " - " + time + " - " + location + " - " + description;
    }
}
