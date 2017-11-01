package travelers.tripplanner;

//package com.firebaseloginapp1;

        import android.os.Bundle;
        import android.support.v7.app.AppCompatActivity;
        import android.widget.TextView;

public class ProfileActivity2 extends AppCompatActivity {

    private TextView tvEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);a
                tvEmail = (TextView) findViewById(R.id.tvEmailProfile);
        tvEmail.setText(getIntent().getExtras().getString("Email"));
    }
}
