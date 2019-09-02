package com.example.shoaibbajwa.login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class HomePage extends AppCompatActivity {

    Button btn_LogOut, btn_btn;
    FirebaseAuth mAuth;

@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        btn_btn = findViewById(R.id.button4);
        btn_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomePage.this, Main2Activity.class);
                startActivity(intent);
            }
        });

        mAuth = FirebaseAuth.getInstance();

        btn_LogOut = findViewById(R.id.button);
        btn_LogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                finish();
                Intent in = new Intent(HomePage.this, MainActivity.class);
                startActivity(in);
            }
        });
    }
}
