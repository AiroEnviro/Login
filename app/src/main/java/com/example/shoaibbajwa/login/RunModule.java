package com.example.shoaibbajwa.login;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class RunModule extends AppCompatActivity {
    Button bluetooth;
    Button btn_location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_module);

        btn_location = findViewById(R.id.btnLocation);
        bluetooth = (Button) findViewById(R.id.btnBluetooth);

        bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bluetooth.setClickable(true);
                Intent i = new Intent(RunModule.this,Connection_BT.class);
                startActivity(i);
            }
        });

        btn_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                Main3Activity obj = new Main3Activity();
                obj.showLocation();
                */
                Intent intent = new Intent(RunModule.this, Main3Activity.class);
                startActivity(intent);
            }
        });
    }
}
