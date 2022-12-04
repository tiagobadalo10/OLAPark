package com.example.olapark.nav.logout;

import android.content.Intent;
import android.content.SharedPreferences;
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

    private SharedPreferences sp;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        FragmentLogoutBinding binding = FragmentLogoutBinding.inflate(inflater, container, false);
        try{
            sp = getActivity().getSharedPreferences("auto-login", getContext().MODE_PRIVATE);
        }
        catch(NullPointerException e){

        }


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

            cleanSP();

            Intent i = new Intent(getContext(), LoginActivity.class);
            startActivity(i);
        });

        // If user clicks on button no
        noButton.setOnClickListener(view -> {
            Intent i = new Intent(getContext(), MainMenuActivity.class);
            startActivity(i);
        });
    }

    public void cleanSP(){
        SharedPreferences.Editor editor = sp.edit();
        editor.remove("username");
        editor.apply();
    }
}
