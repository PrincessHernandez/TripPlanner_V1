package travelers.tripplanner;

import android.Manifest;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import travelers.tripplanner.fragments.Dashboard;
import travelers.tripplanner.fragments.GPStracker;
import travelers.tripplanner.fragments.Maps;
import travelers.tripplanner.fragments.Settings;
import travelers.tripplanner.fragments.history;
import travelers.tripplanner.fragments.MyLocation;
import travelers.tripplanner.register.signUp;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static double latitude, longitude;
    private FirebaseAuth mFirebaseAuth;
    public static ArrayList<String> trip_name, place_id;
    public static ArrayList<Double> place_lat, place_lng;
    View Header;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NavigationView navigationView = findViewById(R.id.nav_view);
        Header = navigationView.getHeaderView(0);
        trip_name = new ArrayList<>();
        place_id = new ArrayList<>();
        place_lat =  new ArrayList<>();
        place_lng = new ArrayList<>();
        //getting current user
        mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        if(user == null){
            finish();
            startActivity(new Intent(this, signUp.class));
        }else{
            TextView useremail = Header.findViewById(R.id.useremail);
            useremail.setText(user.getEmail());
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //GPS functionality
        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},123);

        navigationView.setNavigationItemSelectedListener(this);

        //check the dashboard
        onNavigationItemSelected(navigationView.getMenu().getItem(0));
        navigationView.setCheckedItem(R.id.nav_dashboard);
    }

    @Override
    protected void onStart() {
        super.onStart();
        DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference mUserIdRef = mRootRef.child(mFirebaseAuth.getCurrentUser().getUid());
        DatabaseReference mNameRef = mUserIdRef.child("Name");

        mNameRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TextView username = Header.findViewById(R.id.username);
                username.setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference mBucketList = mUserIdRef.child("BucketList");
        mBucketList.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                trip_name = new ArrayList<>();
                place_id = new ArrayList<>();
                place_lat =  new ArrayList<>();
                place_lng = new ArrayList<>();
                for (DataSnapshot tripSnap: dataSnapshot.getChildren()) {
                    for(DataSnapshot placeSnap: tripSnap.getChildren()){
                        for(DataSnapshot placeAttributeSnap: placeSnap.getChildren()){
                            if(placeAttributeSnap.getKey().equals("id")) {
                                trip_name.add(tripSnap.getKey());
                                place_id.add(placeAttributeSnap.getValue(String.class));
                            }
                            else if(placeAttributeSnap.getKey().equals("latitude")) place_lat.add(placeAttributeSnap.getValue(Double.class));
                            else if(placeAttributeSnap.getKey().equals("longitude")) place_lng.add(placeAttributeSnap.getValue(Double.class));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        FragmentManager fm = getFragmentManager();

        if (id == R.id.nav_dashboard) {
            fm.beginTransaction().replace(R.id.content_frame, new Dashboard()).commit();
        } else if (id == R.id.nav_maps) {
            fm.beginTransaction().replace(R.id.content_frame, new Maps()).commit();
        } else if (id == R.id.nav_history) {
            fm.beginTransaction().replace(R.id.content_frame, new history()).commit();
        } else if (id == R.id.nav_my_location) {
            fm.beginTransaction().replace(R.id.content_frame, new MyLocation()).commit();
        } else if (id == R.id.nav_settings) {
            fm.beginTransaction().replace(R.id.content_frame, new Settings()).commit();
        } else if (id == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent i = new Intent(this, signUp.class);
            startActivity(i);
            finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case 123:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    final GPStracker g = new GPStracker(getApplicationContext());
                    Location l = g.getLocation();

                    if(l!=null){
                        latitude = l.getLatitude();
                        longitude = l.getLongitude();
                    }

                } else {
                    Toast.makeText(this,"GPS not permission granted",Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
}
