package com.example.olapark.nav.logout;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import com.example.olapark.LoginActivity;
import com.example.olapark.MainMenuActivity;
import com.example.olapark.R;
import com.example.olapark.databinding.FragmentLogoutBinding;

public class LogoutFragment extends DialogFragment {

    private FragmentLogoutBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentLogoutBinding.inflate(inflater, container, false);

        View v = binding.getRoot();

        // Handling of logout buttons
        onClickHandler(v);

        return v;
    }

    public void onClickHandler(View v){

        Button yesButton = v.findViewById(R.id.yes);
        Button noButton = v.findViewById(R.id.no);

        // If user clicks on button yes
        yesButton.setOnClickListener(view -> {
            Intent i = new Intent(getContext(), LoginActivity.class);
            startActivity(i);
        });

        // If user clicks on button no
        noButton.setOnClickListener(view -> {
            Intent i = new Intent(getContext(), MainMenuActivity.class);
            startActivity(i);
        });
    }

}