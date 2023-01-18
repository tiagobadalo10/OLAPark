package com.example.olapark.nav.parks;

public class FilterOptions {

    public float range;
    public Enum<Occupation> occupation;

    public boolean coverage;

    public FilterOptions(float range, Enum<Occupation> occupation, boolean coverage) {
        this.range = range;
        this.occupation = occupation;
        this.coverage = coverage;
    }

}
