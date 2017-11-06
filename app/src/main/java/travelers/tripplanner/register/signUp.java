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

import travelers.tripplanner.MainActivity;
import travelers.tripplanner.R;

public class signUp extends AppCompatActivity implements View.OnClickListener {
    private TextView mTextView;
    private Button mButton;
    private EditText username, password, confirmPassword;
    private FirebaseAuth mFirebaseAuth;
    private ProgressDialog dialog;
    private AlertDialog.Builder a_builder;
    private AlertDialog alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mFirebaseAuth = FirebaseAuth.getInstance();

        dialog = new ProgressDialog(this);

        mButton = findViewById(R.id.btn);
        mTextView = findViewById(R.id.tv);
        username =  findViewById(R.id.ET1);
        password =  findViewById(R.id.ET2);
        confirmPassword =  findViewById(R.id.ET3);

        mButton.setOnClickListener(this);
        mTextView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id){
            case R.id.btn:
                if(!checkEmpty()){
                    if(valid()){
                        SignUp();
                    } else{
                        AlertBox("Wrong Password", "Password did not match, try again!", view);
                    }
                } else{
                    AlertBox("Empty Field", "All fields must be filled", view);
                }
                break;
            case R.id.tv:
                Intent i = new Intent(signUp.this, signIn.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
        }
    }

    private void SignUp() {
        dialog = ProgressDialog.show(this, "Please wait!", "Registering user...");
        mFirebaseAuth.createUserWithEmailAndPassword(username.getText().toString().trim(), password.getText().toString().trim())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(signUp.this, "User created", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(signUp.this, MainActivity.class);
                            startActivity(i);
                            dialog.dismiss();
                        }else{
                            Toast.makeText(signUp.this, "fuck off!", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                });
    }

    private void AlertBox(String title, String msg, View view){
        a_builder = new AlertDialog.Builder(view.getContext());
        a_builder.setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        alert = a_builder.create();
        alert.setTitle(title);
        alert.show();
    }

    private boolean checkEmpty() {
        if(username.getText().toString().equals("")) return true;
        if(password.getText().toString().equals("")) return true;
        if(confirmPassword.getText().toString().equals("")) return true;
        return false;
    }

    private boolean valid() {
        return password.getText().toString().equals(confirmPassword.getText().toString());
    }
}
