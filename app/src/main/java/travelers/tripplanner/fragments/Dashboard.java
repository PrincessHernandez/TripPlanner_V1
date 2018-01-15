package travelers.tripplanner.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import travelers.tripplanner.R;
import travelers.tripplanner.addTrip.MainActivity;
import travelers.tripplanner.addTrip.locationsAdapter;

public class Dashboard extends Fragment implements View.OnClickListener{
    private ArrayList<String> name, number;
    private ListView list;
    protected TripAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        name = new ArrayList<>();
        number = new ArrayList<>();
        FloatingActionButton addTrips = view.findViewById(R.id.fab);
        adapter = new TripAdapter(getActivity(), name, number);
        addTrips.setOnClickListener(this);
        list = view.findViewById(R.id.trips_lv_2);
        list.setAdapter(adapter);

        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference mUserIdRef = mRootRef.child(mFirebaseAuth.getCurrentUser().getUid());
        DatabaseReference mBucketListRef = mUserIdRef.child(getString(R.string.BucketList));
        //You can use the single or the value.. depending if you want to keep track
        mBucketListRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snap: dataSnapshot.getChildren()) {
                    name.add(snap.getKey());
                    number.add(String.valueOf(snap.getChildrenCount()));
                }
                ProgressBar progressBar = getView().findViewById(R.id.progressBar_dashboard);
                progressBar.setVisibility(View.GONE);
                if(name.size() > 0){
                    list.setAdapter(adapter);
                }else {
                    TextView tv = getView().findViewById(R.id.dashboard_empty_tv);
                    tv.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Bundle bundle=new Bundle();
                bundle.putString(getString(R.string.Name),name.get(i));
                bundle.putString(getString(R.string.Number),number.get(i));
                BucketList mBucketList = new BucketList();
                mBucketList.setArguments(bundle);

                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
                transaction.replace(R.id.content_frame, mBucketList);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
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

            trip_name.setText(String.format(getString(R.string.Place), this.name.get(position)));
            trip_num.setText(String.format(getString(R.string.Number_of_trips), this.number.get(position)));

            return mView;
        }
    }
}
