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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    EditText et_email, et_pwd;
    Button signIn, signUp;
    String email, pwd;

    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_email = (EditText) findViewById(R.id.editText_email);
        et_pwd = (EditText) findViewById(R.id.editText_pwd);
        signIn = findViewById(R.id.button2);
        signUp = findViewById(R.id.button3);

        mAuth = FirebaseAuth.getInstance();
        //FireBase listener
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if (user != null){
                    Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(MainActivity.this, SignUp.class);
                startActivity(in);
            }
        });

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = et_email.getText().toString();
                pwd = et_pwd.getText().toString();

                if (!validate()){
                    Toast.makeText(MainActivity.this, "Signup has been failed", Toast.LENGTH_SHORT).show();
                }
                else {
                    mAuth.signInWithEmailAndPassword(email, pwd)
                            .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()){
                                        Toast.makeText(MainActivity.this, "SigIn Error", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        Toast.makeText(MainActivity.this, "SigIn Successfully", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

    }
    private boolean validate() {
        boolean valid = true;

        if (email.isEmpty()){
            et_email.setError("Please enter correct email");
            valid = false;
        }
        if (pwd.isEmpty()){
            et_pwd.setError("Please enter password");
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
