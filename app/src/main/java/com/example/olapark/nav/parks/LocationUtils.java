package com.example.olapark.nav.parks;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class LocationUtils {

    public static double distance(LatLng location1, LatLng location2) {
        double earthRadius = 6371*1000; //meters
        double lat1 = location1.latitude;
        double lon1 = location1.longitude;
        double lat2 = location2.latitude;
        double lon2 = location2.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.pow(Math.sin(dLat / 2), 2) +
                Math.pow(Math.sin(dLon / 2), 2) *
                        Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return earthRadius * c;
    }

    public LatLng getClosestLocation(List<LatLng> locations, LatLng currentLocation) {
        LatLng closestLocation = null;
        double closestDistance = Double.MAX_VALUE;
        for (LatLng location : locations) {
            double distance = distance(location, currentLocation);
            if (distance < closestDistance) {
                closestDistance = distance;
                closestLocation = location;
            }
        }
        return closestLocation;
    }

    public static double getClosestDistance(List<LatLng> locations, LatLng currentLocation) {
        double closestDistance = Double.MAX_VALUE;
        for (LatLng location : locations) {
            double distance = distance(location, currentLocation);
            if (distance < closestDistance) {
                closestDistance = distance;
            }
        }
        return closestDistance;
    }


    public static boolean isCloseToTheNearestPark(LatLng currLocation, double minDistanceInMeters){
        ParkCatalog parks = new ParkCatalog();
        parks.setParksCatalog();
        double closestDistance= getClosestDistance(parks.getAllLocations(), currLocation);
        Log.d("closestDistance", String.valueOf(closestDistance));
        return closestDistance <= minDistanceInMeters;
    }



}
