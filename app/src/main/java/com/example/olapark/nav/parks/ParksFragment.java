package com.example.olapark.nav.parks;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.olapark.R;
import com.example.olapark.databinding.FragmentParksBinding;

import java.util.ArrayList;

public class ParksFragment extends Fragment implements FilterDialog.MyDialogListener {

    private FragmentParksBinding binding;
    private View root;
    private MapsFragment mapsFragment;
    private ListView listView;
    private SearchView searchView;

    private ArrayList<String> listItems = new ArrayList<String>();
    private ArrayAdapter<String> adapter;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        //getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        binding = FragmentParksBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        FragmentManager fm = getChildFragmentManager();
        mapsFragment = (MapsFragment) fm.findFragmentById(R.id.fragmentContainerView);

        listView = binding.getRoot().findViewById(R.id.list_view);
        searchView = binding.getRoot().findViewById(R.id.searchView);

        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, listItems);
        listView.setAdapter(adapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                listView.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), newText, Toast.LENGTH_SHORT).show();
                addItems(newText);
                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                listView.setVisibility(View.GONE);
                return false;
            }
        });


        configureImageButton();

        return root;
    }

    public void addItems(String text) {
        listItems.clear();
        listItems.add("Clicked");
        //TODO adicionar apenas os parques que contêm no nome text
        adapter.notifyDataSetChanged();
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