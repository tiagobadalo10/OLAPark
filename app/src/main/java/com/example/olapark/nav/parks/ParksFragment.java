package com.example.olapark.nav.parks;

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
    private final ArrayList<String> listItems = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    private ParkCatalog parks;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        //getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        binding = FragmentParksBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        FragmentManager fm = getChildFragmentManager();
        mapsFragment = (MapsFragment) fm.findFragmentById(R.id.fragmentContainerView);

        parks = ParkCatalog.getInstance(mapsFragment);

        listView = binding.getRoot().findViewById(R.id.list_view);
        SearchView searchView = binding.getRoot().findViewById(R.id.searchView);

        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, listItems);
        listView.setAdapter(adapter);

        searchView.setOnSearchClickListener(v -> parks = mapsFragment.getParkCatalog());

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                listView.setVisibility(View.VISIBLE);
                addItems(newText);
                return false;
            }
        });

        searchView.setOnCloseListener(() -> {
            listView.setVisibility(View.GONE);
            return false;
        });


        configureImageButton();

        return root;
    }

    public void addItems(String text) {
        listItems.clear();

        for (Park park : parks) {
            String name = park.getName();
            if(name.toLowerCase().contains(text.toLowerCase())) {
                listItems.add(name);
            }
        }

        adapter.notifyDataSetChanged();
    }

    private void configureImageButton() {
        ImageButton btn = root.findViewById(R.id.filter_button);

        btn.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "You Clicked the button!", Toast.LENGTH_LONG).show();
            openDialog();
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
    public void setFilter(FilterOptions filterOptions) {
        Toast.makeText(getContext(), "parksFragment", Toast.LENGTH_SHORT).show();
        mapsFragment.setParksMarkersWithFilter(filterOptions);
    }
}