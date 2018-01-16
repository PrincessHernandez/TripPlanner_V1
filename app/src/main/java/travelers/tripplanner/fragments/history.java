package travelers.tripplanner.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import travelers.tripplanner.R;

public class history extends Fragment {

    private ArrayList<String> place_name, place_address;
    private ListView list;
    protected HistoryAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_history, container, false);

        place_name = new ArrayList<>();
        place_address = new ArrayList<>();
        //list.setAdapter(adapter);

        DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference mUserIdRef = mRootRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        DatabaseReference mBucketList = mUserIdRef.child(getString(R.string.BucketList));
        mBucketList.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                place_name = new ArrayList<>();
                place_address = new ArrayList<>();
                for (DataSnapshot tripSnap: dataSnapshot.getChildren()) {
                    for(DataSnapshot placeSnap: tripSnap.getChildren()){
                        String tempName = new String();
                        String tempAddress = new String();
                        for(DataSnapshot placeAttributeSnap: placeSnap.getChildren()){
                            if(placeAttributeSnap.getKey().equals(getString(R.string.visited))) {
                                if (placeAttributeSnap.getValue(Boolean.class)) {
                                    place_name.add(tempName);
                                    place_address.add(tempAddress);
                                }
                            }
                            if(placeAttributeSnap.getKey().equals(getString(R.string.Name))) {
                                tempName = placeAttributeSnap.getValue(String.class);
                            }
                            if(placeAttributeSnap.getKey().equals(getString(R.string.Address))) {
                                tempAddress = placeAttributeSnap.getValue(String.class);
                            }
                        }
                    }
                }
                adapter = new HistoryAdapter(getActivity(), place_name, place_address);
                list = view.findViewById(R.id.history_lv);
                list.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }

    private class HistoryAdapter extends ArrayAdapter<String> {
        private ArrayList<String> name, address;
        private Activity mActivity;

        public HistoryAdapter(@NonNull Activity activity, ArrayList<String> name, ArrayList<String> address) {
            super(activity, R.layout.history_item, name);
            this.name = name;
            this.address = address;
            this.mActivity = activity;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View mView = convertView;
            if(mView==null){
                LayoutInflater inflater = mActivity.getLayoutInflater();
                mView = inflater.inflate(R.layout.history_item, null, true);
            }

            TextView place_name = mView.findViewById(R.id.place_name_checked_tv);
            TextView place_address = mView.findViewById(R.id.place_address_checked_tv);

            place_name.setText(String.format(getString(R.string.Place), this.name.get(position)));
            place_address.setText(String.format(getString(R.string.address_s), this.address.get(position)));

            return mView;
        }
    }
}
