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
        DatabaseReference mBucketListRef = mUserIdRef.child(getString(R.string.BucketList));
        DatabaseReference mPlaceRef = mBucketListRef.child(bundle.getString("name"));
        mPlaceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snap: dataSnapshot.getChildren()) {
                    for (DataSnapshot placesnap: snap.getChildren()) {
                        if(placesnap.getKey().equals(getString(R.string.id))) bucklist_place_id.add(placesnap.getValue(String.class));
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
                    JsonObjectRequest request = new JsonObjectRequest(getString(R.string.url1_textsearch) +
                            getString(R.string.query) + place + getString(R.string.API_KEY),
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        int NOR = response.getJSONArray(getActivity().getResources().getString(R.string.results)).length();
                                        for(int i = 0; i < NOR; i++){

                                            if(!inBucketList(response.getJSONArray(getActivity().getResources().getString(R.string.results)).getJSONObject(i).getString(getString(R.string.place_id)))) continue;

                                            name.add(response.getJSONArray(getActivity().getResources().getString(R.string.results)).getJSONObject(i).getString(getString(R.string.name_json)));
                                            type.add(response.getJSONArray(getActivity().getResources().getString(R.string.results)).getJSONObject(i).getJSONArray(getString(R.string.types_json)).get(0).toString());
                                            address.add(response.getJSONArray(getActivity().getResources().getString(R.string.results)).getJSONObject(i).getString(getActivity().getResources().getString(R.string.formatted_address)));
                                            imageURl.add(getString(R.string.map_google_api) +
                                                    response.getJSONArray(getActivity().getResources().getString(R.string.results)).getJSONObject(i).getJSONArray(getString(R.string.photos_json)).getJSONObject(0).getString(getString(R.string.photo_reference_json))
                                                    + getString(R.string.API_KEY));
                                            String TempRating = response.getJSONArray(getActivity().getResources().getString(R.string.results)).getJSONObject(i).getString(getString(R.string.ratings_json));
                                            rating.add(Double.parseDouble(TempRating));
                                            latitude.add(Double.parseDouble(response.getJSONArray(getActivity().getResources().getString(R.string.results)).getJSONObject(0).getJSONObject(getString(R.string.geometry_json)).getJSONObject(getString(R.string.location_json)).getString(getString(R.string.lat_json))));
                                            longitude.add(Double.parseDouble(response.getJSONArray(getActivity().getResources().getString(R.string.results)).getJSONObject(0).getJSONObject(getString(R.string.geometry_json)).getJSONObject(getString(R.string.location_json)).getString(getString(R.string.lng_json))));
                                            place_id.add(response.getJSONArray(getActivity().getResources().getString(R.string.results)).getJSONObject(i).getString(getString(R.string.place_id_json)));
                                        }
                                        if(NOR == 0){
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    a_builder = new AlertDialog.Builder(getActivity());
                                                    a_builder.setMessage(R.string.no_suggestions_found)
                                                            .setCancelable(false)
                                                            .setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {}
                                                            });
                                                    alert = a_builder.create();
                                                    alert.setTitle(getString(R.string.tryagain));
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
