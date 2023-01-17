package com.example.olapark.nav.parks;

import android.location.Location;
import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ParkCatalog implements Iterable<Park>{

    private final List<Park> parks;
    private int listSize;

    public ParkCatalog() {
        parks = new ArrayList<>();
        listSize = 0;
    }

    public void setParksCatalog(){

        this.addPark(new Park(new LatLng(38.69770840269444, -9.2930477191839),
                "Parque de estacionamento da quinta das amendoeiras", Occupation.LOW,
                2.0));
        this.addPark(new Park(new LatLng(38.75073524758464, -9.154801959985548),
                "Estacionamento Cidade Universitária - EMEL", Occupation.HIGH,
                3.0));
        this.addPark(new Park(new LatLng(38.75762912547855, -9.155196744003156),
                "Estacionamento Campo Grande - EMEL", Occupation.HIGH,
                0.40));
        this.addPark(new Park(new LatLng(38.76234930369637, -9.161149889720422),
                "Estacionamento Alvalade XXI Entrada Norte", Occupation.MEDIUM,
                0.89));
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
        return new Iterator<Park>() {

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
    }

    public ArrayList<Park> filterParks(FilterOptions filterOptions, LatLng currLocation) {
        ArrayList<Park> res = new ArrayList<>();

        for (Park park : parks) {
            if (park.getOccupation() == filterOptions.occupation || filterOptions.occupation == null) {
                if ((distance(park.getLocation(), currLocation) / 1000) <= filterOptions.range || filterOptions.range == 0) {
                    res.add(park);
                }
            }
        }

        return res;
    }

    public List<LatLng> getAllLocations() {
        List<LatLng> locations = new ArrayList<>();
        for (Park park : parks) {
            locations.add(park.getLocation());
        }
        return locations;
    }


    private float distance(LatLng p1, LatLng p2) {
        float[] res = new float[1];
        Location.distanceBetween(p1.latitude, p1.longitude, p2.latitude, p2.longitude, res);
        return res[0];
    }
}
