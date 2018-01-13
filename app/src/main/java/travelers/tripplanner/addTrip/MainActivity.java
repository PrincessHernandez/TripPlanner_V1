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
    private ArrayList<String> name, type, address, imageURl, place_id, bucklist_place_id;
    private ArrayList<Double> rating;
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
    private Integer ListLength = 0;

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

        adapter = new locationsAdapter(MainActivity.this, name, type, address, imageURl, rating, place_id);
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
//                DatabaseReference mUserIdRef = mRootRef.child(mFirebaseAuth.getCurrentUser().getUid());
//                DatabaseReference mBucketListRef = mUserIdRef.child("BucketList");
//                DatabaseReference mVisitRef = mBucketListRef.child(mEditText.getText().toString());
//                DatabaseReference selection = mVisitRef.child("place" + ListLength);
//                ListLength++;
//                selection.setValue(place_id.get(i));
                Toast.makeText(MainActivity.this, name.get(i) + " added to bucketlist!", Toast.LENGTH_SHORT).show();
                bucklist_place_id.add(place_id.get(i));
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
        DatabaseReference mBucketListRef = mUserIdRef.child("BucketList");
        DatabaseReference mVisitRef = mBucketListRef.child(mEditText.getText().toString());

        for(int i = 0; i < bucklist_place_id.size(); i++){
            DatabaseReference selection = mVisitRef.child("place_" + i);
            selection.setValue(place_id.get(i));
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
            JsonObjectRequest request = new JsonObjectRequest("https://maps.googleapis.com/maps/api/place/textsearch/json?" +
                    "query=Tourist%20places%20in%20" + place + "&key=AIzaSyC1Svb1mu2sq-sdXzrRoI-VVsSR4BoWEkA",
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                int NOR = response.getJSONArray("results").length();
                                for(int i = 0; i < NOR; i++){
                                    name.add(response.getJSONArray("results").getJSONObject(i).getString("name"));
                                    type.add(response.getJSONArray("results").getJSONObject(i).getJSONArray("types").get(0).toString());
                                    address.add(response.getJSONArray("results").getJSONObject(i).getString("formatted_address"));
                                    imageURl.add("https://maps.googleapis.com/maps/api/place/photo?maxheight=400&photoreference=" +
                                            response.getJSONArray("results").getJSONObject(i).getJSONArray("photos").getJSONObject(0).getString("photo_reference")
                                            + "&key=AIzaSyC1Svb1mu2sq-sdXzrRoI-VVsSR4BoWEkA");
                                    place_id.add(response.getJSONArray("results").getJSONObject(i).getString("place_id"));
                                    String TempRating = response.getJSONArray("results").getJSONObject(i).getString("rating");
                                    rating.add(Double.parseDouble(TempRating));
                                }
                                mProgressBar.setVisibility(View.INVISIBLE);
                                if(NOR == 0){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            a_builder = new AlertDialog.Builder(MainActivity.this);
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