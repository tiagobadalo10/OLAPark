package com.example.olapark.nav.parks;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.olapark.R;
import com.example.olapark.databinding.FragmentParksBinding;

public class ParksFragment extends Fragment implements FilterDialog.MyDialogListener {

    private FragmentParksBinding binding;
    private View root;
    private MapsFragment mapsFragment;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        //getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        binding = FragmentParksBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        FragmentManager fm = getChildFragmentManager();
        mapsFragment = (MapsFragment) fm.findFragmentById(R.id.fragmentContainerView);

        configureImageButton();

        return root;
    }

    private void configureImageButton() {
        ImageButton btn = (ImageButton) root.findViewById(R.id.filter_button);

        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "You Clicked the button!", Toast.LENGTH_LONG).show();
                openDialog();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void openDialog() {
        FilterDialog dialog = FilterDialog.newInstance("Filter");
        dialog.setListener(this);
        dialog.show(getFragmentManager().beginTransaction(), "dialog");
    }

    @Override
    public void setFiter(FilterOptions filterOptions) {
        Toast.makeText(getContext(), "parksFragment", Toast.LENGTH_SHORT).show();
        mapsFragment.setParksMarkersWithFilter(filterOptions);
    }
}