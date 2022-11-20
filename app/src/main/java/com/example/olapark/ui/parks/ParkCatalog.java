package com.example.olapark.ui.parks;

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
}
