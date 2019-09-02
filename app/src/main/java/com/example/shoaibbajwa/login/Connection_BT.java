package com.example.shoaibbajwa.login;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

public class Connection_BT extends AppCompatActivity {

    //1)
    private static final String TAG = "DispositivosBT";
    //2)
    ListView IdLista;
    //3)
    public static String EXTRA_DEVICE_ADRESS = "device_adress";
    //4)
    private BluetoothAdapter bluetoothAdapter;
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection__bt);
    }
    @Override
    public void onResume() {

        super.onResume();
        VerificarEstadoBT();
        mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this,R.layout.nu_connect);
        IdLista= (ListView) findViewById(R.id.IdLista);
        IdLista.setAdapter(mPairedDevicesArrayAdapter);
        IdLista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String info = ((TextView) view).getText().toString();
                String address = info.substring(info.length() -17);

                Intent i = new Intent(Connection_BT.this, Bluetooth.class);
                i.putExtra(EXTRA_DEVICE_ADRESS, address);
                startActivity(i);
            }
        });
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        Set<BluetoothDevice> pairedDevice = bluetoothAdapter.getBondedDevices();
        if (pairedDevice.size()>0){
            for (BluetoothDevice device : pairedDevice){
                mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        }
    }
    private void VerificarEstadoBT(){
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null){
            Toast.makeText(getBaseContext(),"El dispositivo no soportos Bluetooth", Toast.LENGTH_SHORT).show();
        }else {
            if (bluetoothAdapter.isEnabled()){
                Log.d(TAG, "Bluetooth Activate");
            }else{
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }
}
