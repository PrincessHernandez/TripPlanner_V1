package com.firebaseloginapp1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class SignUpActivity extends AppCompatActivity {

    private EditText txtEmailAddress;
    private EditText txtPassword;
    private FirebaseAuth firebaseAuth;

    EditText editTextName;
    EditText editTextCollegeName;
    Button buttonAdd;

    DatabaseReference databaseNames;
    DatabaseReference databaseCollegeNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        databaseNames = FirebaseDatabase.getInstance().getReference("names");
        databaseCollegeNames = FirebaseDatabase.getInstance().getReference("College Names");

        txtEmailAddress = (EditText) findViewById(R.id.txtEmailSignUp);
        txtPassword = (EditText) findViewById(R.id.txtPasswordSignUp);
        firebaseAuth = FirebaseAuth.getInstance();

        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextCollegeName = (EditText) findViewById(R.id.editTextCollegeName);
        buttonAdd = (Button) findViewById(R.id.btnSignUpUser);

    }
    public void btnSignUpUser_Click(View v){

        final ProgressDialog progressDialog = ProgressDialog.show(SignUpActivity.this, "Please wait...", "Processing...", true);
        (firebaseAuth.createUserWithEmailAndPassword(txtEmailAddress.getText().toString(), txtPassword.getText().toString()))
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();

                if (task.isSuccessful()){
                    Toast.makeText(SignUpActivity.this, "SignUp Successfull", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(SignUpActivity.this, LoginActivity.class);
                    startActivity(i);
                }
                else
                {
                    Log.e("ERROR", task.getException().toString());
                    Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
        addName();
        addCollegeName();

    }

    private void addCollegeName() {
        String collegename = editTextCollegeName.getText().toString().trim();

        if (!TextUtils.isEmpty(collegename)){

            String id = databaseCollegeNames.push().getKey();
            CollegeName collegename1 = new CollegeName(id, collegename);

            databaseCollegeNames.child(id).setValue(collegename1);
        }
    }

    private void addName(){
        String name = editTextName.getText().toString().trim();

        if(!TextUtils.isEmpty(name)) {

           String id = databaseNames.push().getKey();
            Name name1 = new Name(id, name);

            databaseNames.child(id).setValue(name1);
            Toast.makeText(this, "Name added", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this, "You should enter a name", Toast.LENGTH_LONG).show();
        }
    }
}
