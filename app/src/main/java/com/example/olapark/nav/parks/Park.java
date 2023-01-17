package com.example.olapark.nav.parks;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

public class Park {

    private final LatLng location;
    private final String name;
    private final Occupation occupation;
    private final double pricePerHour;

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
        if (occupation == Occupation.UNKNOWN) {
            result = BitmapDescriptorFactory.HUE_BLUE;
        } else if (occupation == Occupation.HIGH) {
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
    UNKNOWN, LOW, MEDIUM, HIGH
}
