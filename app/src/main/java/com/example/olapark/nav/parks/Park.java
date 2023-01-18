package com.example.olapark.nav.parks;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

public class Park {

    private final String name;
    private final LatLng location;
    private final Occupation occupation;
    private final double pricePerHour;
    private final boolean coverage;

    private final int places;

    public Park(String name, LatLng location, Occupation occupation, double pricePerHour,
                boolean coverage, int places) {
        this.location = location;
        this.name = name;
        this.occupation = occupation;
        this.pricePerHour = pricePerHour;
        this.coverage = coverage;
        this.places = places;
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
    public double getTotalPrice(int hour) {
        return hour * pricePerHour;
    }

    public boolean getCoverage(){ return coverage;  }

    public int getNumberPlaces(){ return places; }
}
