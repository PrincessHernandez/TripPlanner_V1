package travelers.tripplanner.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import travelers.tripplanner.R;
import travelers.tripplanner.addTrip.MainActivity;
import travelers.tripplanner.addTrip.locationsAdapter;

public class Dashboard extends Fragment implements View.OnClickListener{
    private ArrayList<String> name, number;
    private ListView mListView;
    private ListView list;
    protected TripAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        name = new ArrayList<>();
        number = new ArrayList<>();
        name.add("Guru");
        name.add("Rav");
        number.add("2");
        number.add("3");
        FloatingActionButton addTrips = view.findViewById(R.id.fab);
        this.mListView = view.findViewById(R.id.trips_lv_2);
        adapter = new TripAdapter(getActivity(), name, number);
        addTrips.setOnClickListener(this);
        list = view.findViewById(R.id.trips_lv_2);
        list.setAdapter(adapter);

        return view;

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id){
            case R.id.fab:
                Intent i = new Intent(getActivity(), MainActivity.class);
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

    private class TripAdapter extends ArrayAdapter<String> {
        private ArrayList<String> name, number;
        private Activity mActivity;

        TripAdapter(@NonNull Activity activity, ArrayList<String> name, ArrayList<String> type){
            super(activity, R.layout.trip_item, name);
            this.name = name;
            this.number = type;
            this.mActivity = activity;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View mView = convertView;
            if(mView==null){
                LayoutInflater inflater = mActivity.getLayoutInflater();
                mView = inflater.inflate(R.layout.trip_item, null, true);
            }

            TextView trip_name = mView.findViewById(R.id.trip_name);
            TextView trip_num = mView.findViewById(R.id.trip_num);

            trip_name.setText(this.name.get(position));
            trip_num.setText(this.number.get(position));

            return mView;
        }
    }
}
