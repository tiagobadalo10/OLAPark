package com.example.olapark.nav.parks;

public class FragmentHelper {

    private static FragmentHelper instance;
    private MapsFragment fragment;


    public interface OnFragmentReadyListener {
        void onFragmentReady();
    }

    public void setOnFragmentReadyListener(OnFragmentReadyListener listener) {
        this.listener = listener;
    }

    private OnFragmentReadyListener listener;

    private FragmentHelper(){}

    public static FragmentHelper getInstance(){
        if(instance == null){
            instance = new FragmentHelper();
        }
        return instance;
    }

    public void setFragment(MapsFragment fragment){
        this.fragment = fragment;
        if (listener != null && fragment != null) {
            listener.onFragmentReady();
        }
    }

    public MapsFragment getFragment(){
        return fragment;
    }
}
