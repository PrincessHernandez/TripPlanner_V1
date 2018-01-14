package travelers.tripplanner.fragments;

import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import travelers.tripplanner.R;
import travelers.tripplanner.addTrip.locationsAdapter;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;

public class BucketList extends Fragment {
    protected locationsAdapter adapter;
    private ListView list;
    private ArrayList<String> name, type, address, imageURl, place_id, bucklist_place_id;
    private ArrayList<Double> rating, latitude, longitude;
    private RequestQueue requestQueue;
    protected AlertDialog.Builder a_builder;
    protected AlertDialog alert;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mView = inflater.inflate(R.layout.fragment_bucket_list, container, false);

        name = new ArrayList<>();
        type = new ArrayList<>();
        address = new ArrayList<>();
        imageURl = new ArrayList<>();
        rating = new ArrayList<>();
        place_id = new ArrayList<>();
        latitude = new ArrayList<>();
        longitude = new ArrayList<>();
        bucklist_place_id = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(getActivity());
        adapter = new locationsAdapter(getActivity(), name, type, address, imageURl, rating, place_id, latitude, longitude);
        list =  mView.findViewById(R.id.bucketlist_lv);
        list.setAdapter(adapter);

        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
        final Bundle bundle = getArguments();

        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference mUserIdRef = mRootRef.child(mFirebaseAuth.getCurrentUser().getUid());
        DatabaseReference mBucketListRef = mUserIdRef.child("BucketList");
        DatabaseReference mPlaceRef = mBucketListRef.child(bundle.getString("name"));
        mPlaceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snap: dataSnapshot.getChildren()) {
                    for (DataSnapshot placesnap: snap.getChildren()) {
                        if(placesnap.getKey().equals("id")) bucklist_place_id.add(placesnap.getValue(String.class));
                    }
                }
                if(bucklist_place_id.size() > 0) {
                    new LoadInformation().execute(String.valueOf(bundle.getString("name")));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

            class LoadInformation extends AsyncTask<String, Void, String> {

                @Override
                protected String doInBackground(String... location) {

                    startURL(location[0]);

                    return null;
                }

                private void startURL(String place) {
                    JsonObjectRequest request = new JsonObjectRequest("https://maps.googleapis.com/maps/api/place/textsearch/json?" +
                            "query=Tourist%20places%20in%20" + place + "&key=AIzaSyC1Svb1mu2sq-sdXzrRoI-VVsSR4BoWEkA",
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        int NOR = response.getJSONArray("results").length();
                                        for(int i = 0; i < NOR; i++){

                                            if(!inBucketList(response.getJSONArray("results").getJSONObject(i).getString("place_id"))) continue;

                                            name.add(response.getJSONArray("results").getJSONObject(i).getString("name"));
                                            type.add(response.getJSONArray("results").getJSONObject(i).getJSONArray("types").get(0).toString());
                                            address.add(response.getJSONArray("results").getJSONObject(i).getString("formatted_address"));
                                            imageURl.add("https://maps.googleapis.com/maps/api/place/photo?maxheight=400&photoreference=" +
                                                    response.getJSONArray("results").getJSONObject(i).getJSONArray("photos").getJSONObject(0).getString("photo_reference")
                                                    + "&key=AIzaSyC1Svb1mu2sq-sdXzrRoI-VVsSR4BoWEkA");
                                            String TempRating = response.getJSONArray("results").getJSONObject(i).getString("rating");
                                            rating.add(Double.parseDouble(TempRating));
                                            latitude.add(Double.parseDouble(response.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getString("lat")));
                                            longitude.add(Double.parseDouble(response.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getString("lng")));
                                            place_id.add(response.getJSONArray("results").getJSONObject(i).getString("place_id"));
                                        }
                                        if(NOR == 0){
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    a_builder = new AlertDialog.Builder(getActivity());
                                                    a_builder.setMessage("No suggestions found. Try again!")
                                                            .setCancelable(false)
                                                            .setPositiveButton("OK!", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {}
                                                            });
                                                    alert = a_builder.create();
                                                    alert.setTitle("Try Again");
                                                    alert.show();
                                                }
                                            });
                                        } else {
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    ProgressBar progressBar = getView().findViewById(R.id.progressBar_bucketlist);
                                                    progressBar.setVisibility(GONE);
                                                    list.setAdapter(adapter);
                                                }
                                            });
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();

                                    }
                                }

                                private boolean inBucketList(String item_id) {
                                    for(int i = 0; i < bucklist_place_id.size(); i++){
                                        if(item_id.equals(bucklist_place_id.get(i))) return true;
                                    }
                                    return false;
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                        }
                    });
                    requestQueue.add(request);
                }

                @Override
                protected void onPostExecute(String result) {

                }

                @Override
                protected void onPreExecute() {

                }

                @Override
                protected void onProgressUpdate(Void... values) {}
            }

        });

        FloatingActionButton floatingActionButton = getView().findViewById(R.id.fab_bucketlist);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
                transaction.replace(R.id.content_frame, new Dashboard());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }
}
