package com.example.shoaibbajwa.login;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.sdsmdg.tastytoast.TastyToast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class Bluetooth extends AppCompatActivity {

    Handler bluetoothIn;
    float COPercent=0;
    final int handlerState = 0;
    private BluetoothAdapter btAdaper = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder DataStringIn = new StringBuilder();
    private ConnectedThread connectedThread;

    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static String address = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        setupPieChart();
        bluetoothIn = new Handler(){
            public void handleMessage(android.os.Message msg){
                if (msg.what == handlerState)
                {
                    String readMessage = (String) msg.obj;
                    DataStringIn.append(readMessage);
//                    Toast.makeText(getApplicationContext(),Stri,Toast.LENGTH_LONG).show();
//                    int endOFLineIndex = DataStringIn.indexOf("#");
                    if (true){
                        String dataInPrint = DataStringIn.toString();
                        //                        IdBufferIn.setText("Value of CO in Air is"+ dataInPrint);
                        Log.v("Printing","DAta To Print is "+dataInPrint);
                        if(!dataInPrint.contains("#")||!dataInPrint.equals(""))
                        {
                            dataInPrint=dataInPrint.substring(1,dataInPrint.length());

                            Log.v("Printing","new String"+dataInPrint);
                            if(!dataInPrint.isEmpty())
                            {
                                COPercent= (float)Integer.parseInt(dataInPrint);

                                List<PieEntry> pieEnteries=new ArrayList<>();
                                float c= (float)COPercent;

                                pieEnteries.add(new PieEntry(c, "CO%"));
                                pieEnteries.add(new PieEntry(100-c,"Others"));
                                pieEnteries.add(new PieEntry(50-c,"LPG"));
                                setupPieChart(pieEnteries);
                                if (COPercent<8){
                                    TastyToast.makeText(getBaseContext(),"Air Quality is Normal",TastyToast.LENGTH_LONG,TastyToast.SUCCESS);
                                }else if(COPercent<15){
                                    TastyToast.makeText(getBaseContext(),"Air is damaging through LPG ",TastyToast.LENGTH_LONG,TastyToast.CONFUSING);
                                }else if(COPercent<30){
                                    TastyToast.makeText(getBaseContext(),"Danger! Now Air is silent Killer",TastyToast.LENGTH_LONG,TastyToast.WARNING);
                                }else{
                                    TastyToast.makeText(getBaseContext(),"You should go away righ now",TastyToast.LENGTH_LONG,TastyToast.ERROR);

                                }


                            }
                        }
//                            COPercent= (float)Integer.parseInt(dataInPrint);
//
//                        List<PieEntry> pieEnteries=new ArrayList<>();
//                        float c= (float)COPercent;
//                        pieEnteries.add(new PieEntry(c, "CO%"));
//                        pieEnteries.add(new PieEntry(100-c,"Others"));
//                        setupPieChart(pieEnteries);
//                        //                        data=Integer.parseInt(dataInPrint);
//                        Toast.makeText(getApplicationContext(),"Value of CO at instance is "+String.valueOf(data),Toast.LENGTH_SHORT).show();
                        DataStringIn.delete(0,DataStringIn.length());
//                        DataStringIn.setLength(0);

                    }
                }
            }
        };

        btAdaper = BluetoothAdapter.getDefaultAdapter();
        VerificarEstadoBT();

    }
    public void setupPieChart()
    {

        List<PieEntry> pieEnteries=new ArrayList<>();
        float c= (float) 20.2;
        pieEnteries.add(new PieEntry(c, "CO%"));
        c= (float) 70.2;
        pieEnteries.add(new PieEntry(c,"Others"));
        c=(float) 10.2;
        pieEnteries.add(new PieEntry(c,"LPG"));
        PieDataSet dataSet = new PieDataSet(pieEnteries,"Pollution for Daska");
        dataSet.setColors(ColorTemplate.PASTEL_COLORS);
        PieData data = new PieData(dataSet);

        //get the chart
        PieChart chart = (PieChart) findViewById(R.id.barchart);
        chart.setData(data);
        chart.invalidate();




    }

    public void setupPieChart(List<PieEntry> pieEnteries)
    {
        PieDataSet dataSet = new PieDataSet(pieEnteries,"Pollution for Daska");
        dataSet.setColors(ColorTemplate.PASTEL_COLORS);
        PieData data = new PieData(dataSet);

        //get the chart
        PieChart chart = (PieChart) findViewById(R.id.barchart);
        chart.setData(data);

        chart.postInvalidate();



    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }

    @Override
    public void onResume() {

        super.onResume();
        Intent intent= getIntent();
        address = intent.getStringExtra(Connection_BT.EXTRA_DEVICE_ADRESS);
        BluetoothDevice device=btAdaper.getRemoteDevice(address);
        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            Toast.makeText(getBaseContext(),"JA paran ho oye",Toast.LENGTH_SHORT).show();
        }

        try{
            btSocket.connect();
        } catch (IOException e) {

        }
        connectedThread = new ConnectedThread(btSocket);
        connectedThread.start();
        Timer timer = new Timer();


        // Schedule to run after every 3 second(3000 millisecond)
        timer.scheduleAtFixedRate(new Task(),0,1000);
    }

    @Override
    public void onPause() {

        super.onPause();
        try {
            btSocket.close();
        } catch (IOException e) {

        }
    }
    private void VerificarEstadoBT()
    {
        if (btAdaper==null){
            Toast.makeText(getBaseContext(),"Adapter is null",Toast.LENGTH_SHORT).show();
        }else{
            if(btAdaper.isEnabled()){
            }else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent,1);
            }
        }
    }

    class Task extends TimerTask {


        int count = 1;

        // run is a abstract method that defines task performed at scheduled time.
        public void run() {
            System.out.println(count+" : Mahendra Singh");
            connectedThread.write("1");
//            List<PieEntry> pieEnteries=new ArrayList<>();
//            float c= (float)COPercent;
//            pieEnteries.add(new PieEntry(c, "CO%"));
//            pieEnteries.add(new PieEntry(100-c,"Others"));
//            setupPieChart(pieEnteries);
            count++;
        }
    }
    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmoutStream;
        public ConnectedThread(BluetoothSocket btSocket) {
            InputStream tmpIn = null;
            OutputStream tmpout = null;
            try{
                tmpIn  = btSocket.getInputStream();
                tmpout = btSocket.getOutputStream();
                display("DAta Got is "+tmpout.toString());
            }catch (IOException e){

            }
            mmInStream= tmpIn;
            mmoutStream= tmpout;
        }

        public void run(){
            byte[] buffer = new byte[256];
            int bytes;
            while (true){
                try{
                    bytes = mmInStream.read(buffer);
                    String readMessage = new String(buffer,0,bytes);
                    bluetoothIn.obtainMessage(handlerState, bytes,-1, readMessage).sendToTarget();
//                    Toast.makeText(getApplicationContext(),"Value of CO at instance is "+String.valueOf(data),Toast.LENGTH_SHORT).show();
//                                            IdBufferIn.setText("Value of CO in Air is"+ String.valueOf(data));
                } catch (IOException e) {
                    break;
                }
            }
        }
        public void display(String ar)
        {

            Toast.makeText(getApplicationContext(),ar.toString(),Toast.LENGTH_SHORT).show();
        }
        public void write(String input){
            try {
                mmoutStream.write(input.getBytes());
            } catch (IOException e) {
                Toast.makeText(getBaseContext(),"Something went wrong!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
