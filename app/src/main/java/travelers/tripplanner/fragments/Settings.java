package travelers.tripplanner.fragments;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import travelers.tripplanner.R;

public class Settings extends Fragment {

    public Settings() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        Button settings = (Button) getView().findViewById(R.id.settingButton);
        settings.setText(R.string.change_language);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setComponent( new ComponentName("com.android.settings","com.android.settings.Settings$InputMethodAndLanguageSettingsActivity" ));
                startActivity(intent);

            }
        });


        return inflater.inflate(R.layout.fragment_settings, container, false);

    }
}
