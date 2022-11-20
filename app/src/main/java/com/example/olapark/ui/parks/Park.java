package com.example.olapark.ui.parks;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

public class Park {
    private LatLng location;
    private String name;
    private Occupation occupation;

    public Park(LatLng location, String name, Occupation occupation) {
        this.location = location;
        this.name = name;
        this.occupation = occupation;
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
}

enum Occupation{
    LOW, MEDIUM, HIGH;
}
