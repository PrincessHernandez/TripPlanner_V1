package travelers.tripplanner.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import travelers.tripplanner.R;

public class MyLocation extends Fragment {

    public static TextView LatText, LngText, Address;
    double lat,lng;
    public MyLocation() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_location, container, false);

        return view;
    }
}
