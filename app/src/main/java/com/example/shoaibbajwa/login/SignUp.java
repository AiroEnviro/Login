package com.example.shoaibbajwa.login;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {

    EditText et_name, et_email, et_pwd, et_rePwd;
    Button btn;
    String name, email, pwd, rePwd;

    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        et_name = (EditText) findViewById(R.id.editText_Name);
        et_email = (EditText) findViewById(R.id.editText);
        et_pwd = (EditText) findViewById(R.id.editText2);
        et_rePwd = (EditText) findViewById(R.id.editText5);

        btn = findViewById(R.id.register);

        //FireBase listener
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if (user != null){
                    Intent intent = new Intent(SignUp.this, Main2Activity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = et_name.getText().toString();
                email = et_email.getText().toString();
                pwd = et_pwd.getText().toString();
                rePwd =et_rePwd.getText().toString();
                if (!validate()){
                    Toast.makeText(SignUp.this, "Signup has been failed", Toast.LENGTH_SHORT).show();
                }
                else {
                    mAuth.createUserWithEmailAndPassword(email, pwd)
                            .addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(SignUp.this, "SignUp Error", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        String user_id = mAuth.getCurrentUser().getUid();
                                        DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child(name);
                                        current_user_db.setValue(true);
                                    }
                                }
                            });
                }
            }
        });
    }

    private boolean validate() {
        boolean valid = true;

        if (name.isEmpty() || name.length() > 16){
            et_name.setError("Please enter correct name");
            valid = false;
        }
        if (email.isEmpty()){
            et_email.setError("Please enter correct email");
            valid = false;
        }
        if (pwd.isEmpty()){
            et_pwd.setError("Please enter password");
            valid = false;
        }
        if (rePwd.isEmpty()){
            et_pwd.setError("Password not matched");
            valid = false;
        }
        else if (!rePwd.equals(pwd)){
            et_rePwd.setError("Not Match");
            valid = false;
        }
        return valid;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(authStateListener);
    }
}
