package com.example.olapark.nav.parks;

public class FragmentHelper {

    private static FragmentHelper instance;
    private MapsFragment fragment;

    private FragmentHelper(){}

    public static FragmentHelper getInstance(){
        if(instance == null){
            instance = new FragmentHelper();
        }
        return instance;
    }

    public void setFragment(MapsFragment fragment){
        this.fragment = fragment;
    }

    public MapsFragment getFragment(){
        return fragment;
    }
}
