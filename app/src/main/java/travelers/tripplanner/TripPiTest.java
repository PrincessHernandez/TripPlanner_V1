package travelers.tripplanner;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class TripPiTest extends AppCompatActivity {
    TextView id_tv, lat_tv, lon_tv, temp_tv, stmp_tv;
    private RequestQueue requestQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_pi_test);
        requestQueue = Volley.newRequestQueue(this);

        id_tv = findViewById(R.id.DataId_TripPi);
        lat_tv = findViewById(R.id.Latitude_TripPi);
        lon_tv = findViewById(R.id.Longitude_TripPi);
        temp_tv = findViewById(R.id.Temperature_TripPi);
        stmp_tv = findViewById(R.id.TimeStmp_TripPi);

        deleteCache(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getData();
    }

    public void getData() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                JsonObjectRequest request = new JsonObjectRequest("http://justlikerav.com/trippie/read_data.php",
                        new Response.Listener<JSONObject>(){

                            @Override
                            public void onResponse(JSONObject response) {

                                try {
                                    JSONObject mObj = response.getJSONArray("TripPi_response").getJSONObject(0);
                                    id_tv.setText(String.format("Data id: %s", mObj.getString("id")));
                                    lat_tv.setText(String.format("Longitude: %s", mObj.getString("lat")));
                                    lon_tv.setText(String.format("Latitude: %s", mObj.getString("lon")));
                                    temp_tv.setText(String.format("Temperature: %s", mObj.getString("temp")));
                                    stmp_tv.setText(String.format("Time: %s", mObj.getString("time_stamp")));
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

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
            Toast.makeText(context, "lol", Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                boolean success = deleteDir(new File(dir, aChildren));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else {
            return dir!= null && dir.isFile() && dir.delete();
        }
    }
}
