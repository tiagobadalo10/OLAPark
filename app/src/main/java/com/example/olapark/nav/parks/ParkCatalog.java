package com.example.olapark.nav.parks;

import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ParkCatalog implements Iterable<Park>{

    private List<Park> parks;
    private int listSize;

    public ParkCatalog() {
        parks = new ArrayList<>();
        listSize = 0;
    }

    public void addPark(Park park) {
        parks.add(park);
        listSize++;
    }

    public Park findParkByName(String name) {
        for (Park park : parks) {
            if (park.getName().equals(name)) {
                return park;
            }
        }
        return null;
    }

    public Park findParkByLocation(LatLng location) {
        for (Park park : parks) {
            if (park.getLocation().equals(location)) {
                return park;
            }
        }
        return null;
    }

    @Override

    public Iterator<Park> iterator() {
        Iterator<Park> it = new Iterator<Park>() {

            private int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentIndex < listSize && parks.get(currentIndex) != null;
            }

            @Override
            public Park next() {
                return parks.get(currentIndex++);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
        return it;
    }

    public ArrayList<Park> filterParks(FilterOptions filterOptions, LatLng currLocation) {
        ArrayList<Park> res = new ArrayList<>();

        Log.d("distances", filterOptions.toString());

        for (Park park : parks) {
            if (park.getOccupation() == filterOptions.occupation || filterOptions.occupation == null) {
                if ((distance(park.getLocation(), currLocation) / 1000) <= filterOptions.range || filterOptions.range == 0) {
                    res.add(park);
                }
            }
        }

        return res;
    }

    /**
     *
     * @param p1
     * @param p2
     * @return distance between p1 and p2 in meters
     */
    private float distance(LatLng p1, LatLng p2) {
        float res[] = new float[1];
        Location.distanceBetween(p1.latitude, p1.longitude, p2.latitude, p2.longitude, res);
        return res[0];
    }
}
