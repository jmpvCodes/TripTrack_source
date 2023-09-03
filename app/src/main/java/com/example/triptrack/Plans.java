package com.example.triptrack;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Plans {
    private final String tripId;
    private final long date;
    private final String time;
    private final String location;
    private final int priority;
    private final String description;

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

    @NotNull
    @Override
    public String toString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String dateString = dateFormat.format(new Date(date));

        return dateString + " - " + time + " - " + location + " - " + description;
    }
}
