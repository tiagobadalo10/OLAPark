package com.example.olapark.nav.parks;

public class FilterOptions {

    public float range;
    public Enum<Occupation> occupation;

    public FilterOptions(float range, Enum<Occupation> occupation) {
        this.range = range;
        this.occupation = occupation;
    }

    public String toString() {
        return "FilterOptions: range" + range + " occupation" + occupation;
    }
}
