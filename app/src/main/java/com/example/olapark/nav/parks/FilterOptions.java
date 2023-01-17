package com.example.olapark.nav.parks;

public class FilterOptions {

    public float range;
    public Enum<Occupation> occupation;

    public Enum<Coverage> coverage;

    public FilterOptions(float range, Enum<Occupation> occupation, Enum<Coverage> coverage) {
        this.range = range;
        this.occupation = occupation;
        this.coverage = coverage;
    }

}
