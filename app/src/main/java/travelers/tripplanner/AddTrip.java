package travelers.tripplanner;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

public class AddTrip extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getActionBar().hide();


    }
}