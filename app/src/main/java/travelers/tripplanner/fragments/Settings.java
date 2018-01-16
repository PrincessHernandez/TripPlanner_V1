package travelers.tripplanner.fragments;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;


import travelers.tripplanner.R;
import travelers.tripplanner.register.signIn;
import travelers.tripplanner.register.signUp;

public class Settings extends Fragment {

    public Settings() {
        // Required empty public constructor
    }
/*
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getChildFragmentManager().beginTransaction()
                .replace(R.id.settings_container, new SettingsFragment())
                .commit();
    }

*/
    @Override
    public View onCreateView(LayoutInflater inflater,@Nullable ViewGroup container,@Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_settings, null, false);
        super.onCreate(savedInstanceState);
        getChildFragmentManager().beginTransaction()
                .replace(R.id.settings_container, new SettingsFragment())
                .commit();

        return view;
    }

    public static class SettingsFragment extends PreferenceFragment {
    @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_fragment_pref);

            Preference langPref = findPreference("lang_key");
            langPref.setTitle(R.string.lang_settings);
            langPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent i = new Intent(android.provider.Settings.ACTION_LOCALE_SETTINGS);
                    startActivity(i);
                    return false;
                }
            });

            Preference locPref = findPreference("location_key");
            locPref.setTitle(R.string.location_settings);
            locPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
               @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent i = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(i);
                    return false;
                }
            });

            Preference signinPref = findPreference("signin_key");
            signinPref.setTitle(R.string.sign_out);
            signinPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                   // FirebaseAuth.getInstance().signOut();
                   // Intent i = new Intent(Settings.this, signIn.class);
                   // startActivity(i);
                    return false;
                }
            });

        }


    }
}

