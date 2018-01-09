package travelers.tripplanner.addTrip;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
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
import travelers.tripplanner.sqliteUtils.DBhelper;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private ListView list;
    private ArrayList<String> name, type, address, imageURl, place_id;
    private ArrayList<Integer> save2BucketList;
    private ArrayList<Double> rating;
    private EditText mEditText;
    protected locationsAdapter adapter;
    protected AlertDialog.Builder a_builder;
    protected AlertDialog alert;
    private RequestQueue requestQueue;
    private ProgressBar mProgressBar;
    private int selectedRow;
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
        save2BucketList = new ArrayList<>();

        adapter = new locationsAdapter(MainActivity.this, name, type, address, imageURl, rating, place_id);
        list =  findViewById(R.id.lv);
        list.setAdapter(adapter);

        btn.setOnClickListener(this);
        check.setOnClickListener(this);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mRootRef = FirebaseDatabase.getInstance().getReference();
    }
    //
    //
    //
    @Override
    protected void onStart() {
        super.onStart();

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                MainActivity.this.selectedRow = i;
                DatabaseReference mUserIdRef = mRootRef.child(mFirebaseAuth.getCurrentUser().getUid());
                DatabaseReference mBucketListRef = mUserIdRef.child("BucketList");
                DatabaseReference mVisitRef = mBucketListRef.child(mEditText.getText().toString());
                DatabaseReference selection = mVisitRef.child("place" + ListLength);
                ListLength++;
                selection.setValue(place_id.get(i));
                Toast.makeText(MainActivity.this, name.get(i) + " added to bucketlist!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.searchBtn){
            new LoadInformation().execute(mEditText.getText().toString());
        } else if(view.getId() == R.id.fabcheck){
            if(name.size() == 0){
                Intent i = new Intent(this, travelers.tripplanner.MainActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_righ);
            } else {
                int Trip_id = saveTrip(mEditText.getText().toString(), save2BucketList.size());
                int visitID = -1;
                for(int i = 0; i < save2BucketList.size(); i++){
                    visitID = saveVisit(name.get(save2BucketList.get(i)), type.get(save2BucketList.get(i)), address.get(save2BucketList.get(i)), imageURl.get(save2BucketList.get(i)), rating.get(save2BucketList.get(i)), Trip_id);
                }
                Toast.makeText(this, Trip_id + " " + visitID, Toast.LENGTH_SHORT).show();
            }
            Intent i = new Intent(this, travelers.tripplanner.MainActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_righ);
        }
    }

    private int saveVisit(String name, String type, String address, String imageURl, Double rating, int trip_id) {
        SQLiteOpenHelper dbHelper = new DBhelper(this);
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        ContentValues values = new ContentValues();

        values.put("VisitName", name);
        values.put("Type", type);
        values.put("Address", address);
        values.put("photoURL", imageURl);
        values.put("Rating", rating);
        values.put("TripId", trip_id);
        long row = database.insert("Visit",null,values);

        database.close();

        return (int)row;
    }

    private int saveTrip(String s, int size) {

        SQLiteOpenHelper dbHelper = new DBhelper(this);
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        ContentValues values = new ContentValues();

        values.put("TripName", s);
        values.put("visitNum", size);
        long row = database.insert("Trip",null,values);

        database.close();

        if(row < 0) return -1;
        else return (int) row;
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