package travelers.tripplanner.register;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import travelers.tripplanner.R;

public class signIn extends AppCompatActivity implements View.OnClickListener{
    private TextView mTextView;
    private Button mButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mButton = findViewById(R.id.btn);
        mTextView = findViewById(R.id.tv);

        mButton.setOnClickListener(this);
        mTextView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id){
            case R.id.btn:
                break;
            case R.id.tv:
                Intent i = new Intent(signIn.this, signUp.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_righ);
                break;
        }
    }
}
