package travelers.tripplanner.addTrip;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import travelers.tripplanner.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private ListView list;
    private ArrayList<String> name, type, address, imageURl, place_id, bucklist_place_id, bucklist_place_name, bucklist_place_address;
    private ArrayList<Double> rating, latitude, longitude, bucketlist_latitude, bucketlist_longitude;
    private EditText mEditText;
    protected locationsAdapter adapter;
    protected AlertDialog.Builder a_builder;
    private RelativeLayout searchRL;
    protected AlertDialog alert;
    private RequestQueue requestQueue;
    private ProgressBar mProgressBar;
    private FloatingActionButton check;
    private DatabaseReference mRootRef;
    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);

        Button btn = findViewById(R.id.searchBtn);
        mEditText = findViewById(R.id.searchEditText);
        mProgressBar = findViewById(R.id.progressBar);
        requestQueue = Volley.newRequestQueue(this);
        check = findViewById(R.id.fabcheck);

        name = new ArrayList<>();
        type = new ArrayList<>();
        address = new ArrayList<>();
        imageURl = new ArrayList<>();
        rating = new ArrayList<>();
        place_id = new ArrayList<>();
        bucklist_place_id = new ArrayList<>();
        latitude = new ArrayList<>();
        longitude = new ArrayList<>();
        bucketlist_latitude = new ArrayList<>();
        bucketlist_longitude = new ArrayList<>();
        bucklist_place_name = new ArrayList<>();
        bucklist_place_address = new ArrayList<>();

        adapter = new locationsAdapter(MainActivity.this, name, type, address, imageURl, rating, place_id, latitude, longitude);
        list =  findViewById(R.id.lv);
        list.setAdapter(adapter);

        btn.setOnClickListener(this);
        check.setOnClickListener(this);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mRootRef = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    protected void onStart() {
        super.onStart();

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(MainActivity.this, name.get(i) + getString(R.string.added_to_bucketlist), Toast.LENGTH_SHORT).show();
                bucklist_place_id.add(place_id.get(i));
                bucketlist_latitude.add(latitude.get(i));
                bucketlist_longitude.add(longitude.get(i));
                bucklist_place_name.add(name.get(i));
                bucklist_place_address.add(address.get(i));
            }
        });


    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.searchBtn){
            new LoadInformation().execute(mEditText.getText().toString());
        } else if(view.getId() == R.id.fabcheck){
            if(bucklist_place_id.size() > 0 ){
                storeInDatabase();
                backToDashboard();
            } else {
                backToDashboard();
            }
        }
    }

    private void storeInDatabase() {
        DatabaseReference mUserIdRef = mRootRef.child(mFirebaseAuth.getCurrentUser().getUid());
        DatabaseReference mBucketListRef = mUserIdRef.child(getString(R.string.bucketlist));
        DatabaseReference mVisitRef = mBucketListRef.child(mEditText.getText().toString());
        DatabaseReference mLatitude, mLongitude, id, name, address, visited;
        for(int i = 0; i < bucklist_place_id.size(); i++){
            DatabaseReference selection = mVisitRef.child(place_id.get(i));

            visited = selection.child(getString(R.string.visited));
            visited.setValue(false);

            mLatitude = selection.child(getString(R.string.Latitude));
            mLatitude.setValue(bucketlist_latitude.get(i));

            mLongitude = selection.child(getString(R.string.Longitude));
            mLongitude.setValue(bucketlist_longitude.get(i));

            id = selection.child(getString(R.string.id));
            id.setValue(place_id.get(i));

            name = selection.child(getString(R.string.Name));
            name.setValue(bucklist_place_name.get(i));

            address = selection.child(getString(R.string.Address));
            address.setValue(bucklist_place_address.get(i));
        }
    }

    private void backToDashboard() {
        Intent i = new Intent(this, travelers.tripplanner.MainActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_righ);
    }

    private class LoadInformation extends AsyncTask<String, Void, String> {

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
                                int NOR = response.getJSONArray(getResources().getString(R.string.results)).length();
                                for(int i = 0; i < NOR; i++){
                                    name.add(response.getJSONArray(getResources().getString(R.string.results)).getJSONObject(i).getString(getString(R.string.name_json)));
                                    type.add(response.getJSONArray(getResources().getString(R.string.results)).getJSONObject(i).getJSONArray(getString(R.string.types_json)).get(0).toString());
                                    address.add(response.getJSONArray(getResources().getString(R.string.results)).getJSONObject(i).getString(getResources().getString(R.string.formatted_address)));
                                    try{
                                        imageURl.add(getString(R.string.map_google_api) +
                                                response.getJSONArray(getResources().getString(R.string.results)).getJSONObject(i).getJSONArray(getString(R.string.photos_json)).getJSONObject(0).getString(getString(R.string.photo_reference_json))
                                                + getString(R.string.API_KEY));
                                    }catch (JSONException e){
                                        imageURl.add(getString(R.string.img_url));
                                        e.printStackTrace();
                                    }

                                    place_id.add(response.getJSONArray(getResources().getString(R.string.results)).getJSONObject(i).getString(getString(R.string.place_id_json)));
                                    latitude.add(Double.parseDouble(response.getJSONArray(getResources().getString(R.string.results)).getJSONObject(i).getJSONObject(getString(R.string.geometry)).getJSONObject(getString(R.string.location_json)).getString(getString(R.string.lat_json))));
                                    longitude.add(Double.parseDouble(response.getJSONArray(getResources().getString(R.string.results)).getJSONObject(i).getJSONObject(getString(R.string.geometry)).getJSONObject(getString(R.string.location_json)).getString(getString(R.string.lng_json))));

                                    try{
                                        String TempRating = response.getJSONArray(getResources().getString(R.string.results)).getJSONObject(i).getString(getString(R.string.rating_json));
                                        rating.add(Double.parseDouble(TempRating));
                                    }catch (JSONException e){
                                        String TempRating = getString(R.string.temp_rating);
                                        rating.add(Double.parseDouble(TempRating));
                                        e.printStackTrace();
                                    }
                                }
                                mProgressBar.setVisibility(View.GONE);
                                if(NOR == 0){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            a_builder = new AlertDialog.Builder(MainActivity.this);
                                            a_builder.setMessage(R.string.no_suggestions_found)
                                                    .setCancelable(false)
                                                    .setPositiveButton("OK!", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {}
                                                    });
                                            alert = a_builder.create();
                                            alert.setTitle(getString(R.string.tryagain));
                                            alert.show();
                                        }
                                    });
                                } else {
                                    searchRL = findViewById(R.id.searchRL);
                                    searchRL.setVisibility(View.GONE);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            list.setAdapter(adapter);
                                        }
                                    });
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();

                            }
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
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Void... values) {}
    }
}