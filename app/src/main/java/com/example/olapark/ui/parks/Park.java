package com.example.olapark.ui.parks;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

public class Park {

    private LatLng location;
    private String name;
    private Occupation occupation;
    private double pricePerHour;

    public Park(LatLng location, String name, Occupation occupation, double pricePerHour) {
        this.location = location;
        this.name = name;
        this.occupation = occupation;
        this.pricePerHour = pricePerHour;
    }

    public String getName() {
        return name;
    }

    public LatLng getLocation() {
        return location;
    }

    public Occupation getOccupation() {
        return occupation;
    }

    public float getMarkerColor() {
        float result;
        if (occupation == Occupation.HIGH) {
            result = BitmapDescriptorFactory.HUE_RED;
        } else if (occupation == Occupation.MEDIUM) {
            result = BitmapDescriptorFactory.HUE_YELLOW;
        } else {
            result = BitmapDescriptorFactory.HUE_GREEN;
        }
        return result;
    }

    public double getPricePerHour() {
        return pricePerHour;
    }

    public double getPrice(int hour) {
        return hour * pricePerHour;
    }
}

enum Occupation{
    LOW, MEDIUM, HIGH;
}
