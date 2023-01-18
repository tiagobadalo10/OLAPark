package com.example.olapark.nav.parks;

import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ParkCatalog implements Iterable<Park>{

    private static ParkCatalog instance;
    private List<Park> parks;
    private int listSize;
    private FirebaseFirestore db;
    private MapsFragment mapsFragment;

    public static ParkCatalog getInstance(MapsFragment mapsFragment) {
        if (instance == null) {
            instance = new ParkCatalog(mapsFragment);
        }
        return instance;
    }

    public ParkCatalog(MapsFragment mapsFragment) {
        this.mapsFragment = mapsFragment;
        parks = new ArrayList<>();
        listSize = 0;
        db = FirebaseFirestore.getInstance();

        db.collection("parks")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("error", "listen:error", e);
                            return;
                        }

                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                case REMOVED:
                                case MODIFIED:
                                    setParksCatalog();
                                    break;
                            }
                        }
                    }
                });
    }

    public synchronized void removePark(String name) {
        for (Park park : parks) {
            if (park.getName().equals(name)){
                parks.remove(park);
            }
        }
        listSize--;
    }

    public synchronized void modifyPark(String name, Map<String, Object> map) {
        removePark(name);
        addPark(name, map);
    }

    public synchronized void addPark(String name, Map<String, Object> map) {
        LatLng location = new LatLng((double) map.get("lat"), (double) map.get("lng"));
        Occupation occupation = Occupation.valueOf((String) map.get("occupation"));
        double pricePerHour = (double) map.get("pricePerHour");
        boolean coverage = (boolean) map.get("coverage");
        long places = (long) map.get("places");
        Park park = new Park(name,
                location,
                occupation,
                pricePerHour,
                coverage,
                (int) places);
        parks.add(park);
        listSize++;
    }

    public void setParksCatalog() {

        parks.clear();

        try {
            new AsyncTask<Void, Void, QuerySnapshot>() {
                @Override
                protected QuerySnapshot doInBackground(Void... voids) {
                    // Obter a referência da coleção
                    CollectionReference collectionRef = db.collection("parks");
                    // Executar a consulta síncrona
                    QuerySnapshot querySnapshot = null;
                    try {
                        querySnapshot = Tasks.await(collectionRef.get());
                    } catch (ExecutionException e) {
                        throw new RuntimeException(e);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return querySnapshot;
                }

                @Override
                protected void onPostExecute(QuerySnapshot querySnapshot) {
                    super.onPostExecute(querySnapshot);
                    // Iterar sobre os documentos retornados
                    for (QueryDocumentSnapshot document : querySnapshot) {

                        Map<String, Object> map = document.getData();

                        LatLng location = new LatLng((double) map.get("lat"), (double) map.get("lng"));
                        Map occupationMap = (Map) map.get("occupation");
                        Occupation occupation = calculateOccupation(occupationMap);
                        double pricePerHour = (double) map.get("pricePerHour");
                        boolean coverage = (boolean) map.get("coverage");
                        long places = (long) map.get("places");
                        Park park = new Park(document.getId(),
                                location,
                                occupation,
                                pricePerHour,
                                coverage,
                                (int) places);
                        parks.add(park);
                        listSize++;
                        Log.d("oi", park.getName());
                    }
                    Log.d("oi", "acabou");
                    Log.d("oi", parks.toString());
                    printParks();
                    mapsFragment.setParkMarkers();
                }
            }.execute().get();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private Occupation calculateOccupation(Map occupationMap) {

        long res = 0;
        int counter = 0;
        Iterator<Map.Entry<String, Long>> itr = occupationMap.entrySet().iterator();

        while(itr.hasNext()) {
            Map.Entry<String, Long> entry = itr.next();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
                LocalDateTime dateTime = LocalDateTime.parse(entry.getKey(), formatter);
                if (DateTimeUtils.isWithinLast30Minutes(dateTime)) {
                    counter++;
                    res += (long) entry.getValue();
                }
            }
        }

        if (res == 0) {
            return Occupation.UNKNOWN;
        }

        Log.d("calculate", String.valueOf(res));
        Log.d("calculate", String.valueOf(counter));

        double avg = (double) res/counter;

        Log.d("calculate", String.valueOf(avg));

        int y = (int) Math.round(avg);

        Log.d("calculate", "y == " + String.valueOf(y));

        if (y == 1) {
            return Occupation.LOW;
        }
        if (y == 2) {
            return Occupation.MEDIUM;
        }
        if (y == 3) {
            return Occupation.HIGH;
        }
        return Occupation.UNKNOWN;
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

    // Filter parks and return the list of parks
    public ArrayList<Park> filterParks(FilterOptions filterOptions, LatLng currLocation) {
        ArrayList<Park> res = new ArrayList<>();

        for (Park park : parks) {
            if (park.getOccupation() == filterOptions.occupation || filterOptions.occupation == null) {
                if ((distance(park.getLocation(), currLocation) / 1000) <= filterOptions.range || filterOptions.range == 0) {
                    if(filterOptions.coverage) {
                        if(park.getCoverage() == filterOptions.coverage) {
                            res.add(park);
                        }
                    }
                    else {
                        res.add(park);
                    }
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

    @Override
    public String toString() {
        return "ParkCatalog{" +
                "parks=" + parks +
                '}';
    }

    public List<Park> getParks() {
        return this.parks;
    }

    private float distance(LatLng p1, LatLng p2) {
        float[] res = new float[1];
        Location.distanceBetween(p1.latitude, p1.longitude, p2.latitude, p2.longitude, res);
        return res[0];
    }

    public void printParks() {
        for (Park park : this.getParks()) {
            Log.d("oit", park.getName());
        }
    }

}
