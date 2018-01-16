package travelers.tripplanner.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.TextView;
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

import travelers.tripplanner.MainActivity;
import travelers.tripplanner.R;
import travelers.tripplanner.fragments.MyLocation;

public class  GPStracker {
    private Context context;
    private TextView LatText,LngText,Address;
    private RequestQueue requestQueue;
    private double userLat, userLng;
    private ArrayList<Integer> History;

    public GPStracker(Context c){
        context = c;
        requestQueue = Volley.newRequestQueue(c);
    }

    public Location getLocation(){
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            Toast.makeText(context, R.string.permission_not_granted,Toast.LENGTH_SHORT).show();
            return null;
        }
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean isGPSEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(isGPSEnabled){
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,2, new LocationListener(){

                @Override
                public void onLocationChanged(Location location) {
                    userLat = round(location.getLatitude(),5);
                    userLng = round(location.getLongitude(),5);
                    Address = MyLocation.Address;
                    LatText = MyLocation.LatText;
                    LngText = MyLocation.LngText;
                    History = new ArrayList<>();
                    if(!(Address == null||LatText == null||LngText == null)){
                        LatText.setText(String.format("Latitude: %s", userLat));
                        LngText.setText(String.format("Longitude: %s", userLng));
                        startReverseGeocoding();
                    }

                    for(int i = 0; i < MainActivity.place_id.size(); i++){
                        if(calcDist(MainActivity.place_lat.get(i), MainActivity.place_lng.get(i)) < 0.5) {
                            History.add(i);
                        }
                    }

                    FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
                    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
                    DatabaseReference mUserIdRef = mRootRef.child(mFirebaseAuth.getCurrentUser().getUid());
                    DatabaseReference mBucketListRef = mUserIdRef.child(context.getString(R.string.BucketList));
                    DatabaseReference mTripRef, mPlaceRef, visited;
                    for(int i = 0; i < History.size(); i++){
                        mTripRef = mBucketListRef.child(MainActivity.trip_name.get(History.get(i)));
                        mPlaceRef = mTripRef.child(MainActivity.place_id.get(History.get(i)));
                        visited = mPlaceRef.child(context.getString(R.string.visited));
                        visited.setValue(true);
                    }
                }

                private double calcDist(Double landmark_lat, Double landmark_lng) {
                    double R = 6371.000000; // Radius of the earth in km
                    double dLat = deg2rad( userLat - landmark_lat);
                    double dLng = deg2rad( userLng - landmark_lng);

                    double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                            Math.cos(deg2rad(landmark_lat)) * Math.cos(deg2rad(landmark_lng)) * Math.sin(dLng/2) * Math.sin(dLng/2);
                    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
                    return R * c;
                }

                private double deg2rad(double deg) {
                    return deg * (Math.PI/180);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });
            Location l = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            return l;
        }else{
            Toast.makeText(context, R.string.Please_enable_GPS,Toast.LENGTH_LONG).show();
        }
        return null;
    }

    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    private void startReverseGeocoding() {
        String lol = context.getResources().getString(R.string.url_1) +
                context.getString(R.string.url_2);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                JsonObjectRequest request = new JsonObjectRequest(context.getResources().getString(R.string.url_1) +
                        context.getString(R.string.url_2)+userLat+","+userLng,
                        new Response.Listener<JSONObject>(){

                            @Override
                            public void onResponse(JSONObject response) {

                                String location = null;
                                try {
                                    location = response.getJSONArray(context.getResources().getString(R.string.results)).getJSONObject(0).getString(context.getResources().getString(R.string.formatted_address));
                                    Address.setText(location);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }
                        }, new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
                requestQueue.add(request);

            }
        });
    }

}