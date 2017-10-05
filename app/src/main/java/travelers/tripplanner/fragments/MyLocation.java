package travelers.tripplanner.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.math.BigDecimal;
import java.math.RoundingMode;

import travelers.tripplanner.MainActivity;
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

        LatText = (TextView) view.findViewById(R.id.Lat);
        LngText = (TextView) view.findViewById(R.id.Lng);
        Address = (TextView) view.findViewById(R.id.Address);

        lat = MainActivity.latitude;
        lng = MainActivity.longitude;

        LatText.setText("Latitude: "+round(lat,5));
        LngText.setText("Longitude: "+round(lng,5));

        return view;
    }

    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
