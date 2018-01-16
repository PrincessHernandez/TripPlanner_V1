package travelers.tripplanner.register;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import travelers.tripplanner.MainActivity;
import travelers.tripplanner.R;

public class signUp extends AppCompatActivity implements View.OnClickListener {
    private EditText email, password, confirmPassword, name;
    private FirebaseAuth mFirebaseAuth;
    private ProgressDialog dialog;
    private Button mButton;
    private TextView mTextView;
    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mUserIdRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mFirebaseAuth = FirebaseAuth.getInstance();

        if(mFirebaseAuth.getCurrentUser() != null){
            finish();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }

        dialog = new ProgressDialog(this);

        mButton = (Button) findViewById(R.id.btn);
        mTextView = (TextView) findViewById(R.id.tv);
        name = (EditText) findViewById(R.id.ET0);
        email = (EditText) findViewById(R.id.ET1);
        password = (EditText) findViewById(R.id.ET2);
        confirmPassword = (EditText) findViewById(R.id.ET3);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mButton.setOnClickListener(this);
        mTextView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id){
            case R.id.btn:

                if(!checkEmpty(name, email, password, confirmPassword)){
                    if(valid(this.password)){
                        if(!validEmail(this.email)){
                            AlertBox(getString(R.string.invalid_email), getString(R.string.message1), view);
                            break;
                        }

                        if(!validPassword(this.password.getText().toString())){
                            AlertBox(getString(R.string.invalid_Password), getString(R.string.message2), view);
                            break;
                        }
                        SignUp();
                    } else{
                        AlertBox(getString(R.string.wrong_password), getString(R.string.message3), view);
                    }
                } else{
                    AlertBox(getString(R.string.empty_field), getString(R.string.message4), view);
                }
                break;
            case R.id.tv:
                Intent i = new Intent(signUp.this, signIn.class);
                startActivity(i);
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
        }
    }

    protected boolean validPassword(String tempPassword) {
        return 6<=tempPassword.length();
    }

    private void SignUp() {
        dialog = ProgressDialog.show(this, getString(R.string.please_wait), getString(R.string.registering_user));
        mFirebaseAuth.createUserWithEmailAndPassword(email.getText().toString().trim(), password.getText().toString().trim())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            mUserIdRef = mRootRef.child(mFirebaseAuth.getCurrentUser().getUid());
                            DatabaseReference mUserNameRef = mUserIdRef.child("Name");
                            mUserNameRef.setValue(name.getText().toString());
                            Intent i = new Intent(signUp.this, MainActivity.class);
                            finish();
                            startActivity(i);
                        }else{
                            Toast.makeText(signUp.this, R.string.text1, Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                });
    }

    private void AlertBox(String title, String msg, View view){
        AlertDialog.Builder a_builder = new AlertDialog.Builder(view.getContext());
        a_builder.setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        AlertDialog alert = a_builder.create();
        alert.setTitle(title);
        alert.show();
    }

    protected boolean checkEmpty(EditText tempname, EditText tempemail, EditText temppassword, EditText tempconfirmPassword) {
        if(tempname.getText().toString().equals("")) return true;
        if(tempemail.getText().toString().equals("")) return true;
        if(temppassword.getText().toString().equals("")) return true;
        if(tempconfirmPassword.getText().toString().equals("")) return true;
        return false;
    }

    protected boolean valid(EditText temppassword) {
        return temppassword.getText().toString().equals(confirmPassword.getText().toString());
    }

    protected boolean validEmail(EditText tempemail){
        CharSequence target = tempemail.getText();
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

}
