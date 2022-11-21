package com.example.olapark.nav.parks;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.olapark.R;
import com.example.olapark.databinding.FragmentParksBinding;

public class ParksFragment extends Fragment {

    private FragmentParksBinding binding;
    private View root;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        //getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        binding = FragmentParksBinding.inflate(inflater, container, false);
        root = binding.getRoot();

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
        dialog.show(getFragmentManager().beginTransaction(), "dialog");
    }

}