package com.example.triptrack;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.OverlayItem;

public class CustomOverlayItem extends OverlayItem {
    private String id;

    public CustomOverlayItem(String aTitle, String aDescription, GeoPoint aGeoPoint) {
        super(aTitle, aDescription, aGeoPoint);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

