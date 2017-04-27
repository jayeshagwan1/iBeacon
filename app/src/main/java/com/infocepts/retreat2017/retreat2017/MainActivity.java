package com.infocepts.retreat2017.retreat2017;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.BoolRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.BeaconTransmitter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;

public class MainActivity extends AppCompatActivity {
    /**
     * bytesToHex method
     */
    static final char[] hexArray = "0123456789ABCDEF".toCharArray();
    private static final String DEFAULT_SIMPLE_DATE_FORMAT = "dd-MM-yyyy HH:mm:ss";
    private static final String LOG_TAG = "BeaconActivity";
    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();
    // Session Manager Class
    SessionManager session;
    //Button btnSignIn, btnSignUp;
    LoginDataBaseAdapter loginDataBaseAdapter;
    ArrayList<String> uuidList = new ArrayList<String>();
    Hashtable dictInfo = new Hashtable();
    Calendar calDatetime;
    SimpleDateFormat dtFormat;
    String formattedDate = "";
    HashMap<String, String> mapKeyValue = new HashMap<String,String>();
    StringBuilder log = new StringBuilder();
    String infoBeatzDateTime ="22-02-2017 13:00:00";
    String dtConference = "22-02-2017 13:05:00";
    String dtBooth = "22-02-2017 08:00:00";
    private BluetoothAdapter BA;
    private BluetoothManager btManager;
    private BluetoothAdapter btAdapter;
    private Handler scanHandler = new Handler();
    private int scan_interval_ms = 5000;
    private boolean isScanning = false;
    private String uuid ="";
    private String currentDate="";
    private static final int MY_NOTIFICATION_ID=1;
    NotificationManager notificationManager;
    Notification myNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TabHost host = (TabHost)findViewById(R.id.tabhost);
        host.setup();

        //Tab 1
        TabHost.TabSpec spec = host.newTabSpec("SCHEDULE");
        spec.setContent(R.id.tab1);
        spec.setIndicator("SCHEDULE");
        host.addTab(spec);

        //Tab 2
        spec = host.newTabSpec("TRACK");
        spec.setContent(R.id.tab2);
        spec.setIndicator("TRACK");
        host.addTab(spec);


        currentDate = getCurrentDateAsStr();

       /* loginDataBaseAdapter = new LoginDataBaseAdapter(this);
        loginDataBaseAdapter = loginDataBaseAdapter.open();*/


        BA = BluetoothAdapter.getDefaultAdapter();
        if (!BA.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, 0);
            Toast.makeText(getApplicationContext(), "Bluetooth Turned on",Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Enjoy (Re)Treat", Toast.LENGTH_LONG).show();
        }

        // Session class instance
        session = new SessionManager(getApplicationContext());

        //TextView lblEmpName = (TextView) findViewById(R.id.lblEmployeeName);
       // TextView lblEID = (TextView) findViewById(R.id.lblEmployeeId);
       // Toast.makeText(getApplicationContext(), "User Login Status: " + session.isLoggedIn(), Toast.LENGTH_LONG).show();


        /**
         * Call this function whenever you want to check user login
         * This will redirect user to LoginActivity is he is not
         * logged in
         * */
        if(session.checkLogin()) {
            // get user data from session
            HashMap<String, String> user = session.getUserDetails();

            // name
            String name = user.get(SessionManager.KEY_NAME);

            // email
            String eid = user.get(SessionManager.KEY_EID);

            // displaying user data
            //lblEmpName.setText(Html.fromHtml("Name: <b>" + name + "</b>"));
            //lblEID.setText(Html.fromHtml("EID: <b>" + eid + "</b>"));

            //For InfoBeatz let app be Transmitter
            // init BLE
            btManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            btAdapter = btManager.getAdapter();

            TurnBeaconOn();

            //Check date and make device as transmitter
            // To -Do get date using Getcurrenttimestamp() and check with date
//            if (comapreDates(dtConference, currentDate)) {
//                TurnBeaconOn();
//            }  else {
//                Toast.makeText(getApplicationContext(), "Yet to start", Toast.LENGTH_LONG).show();
//            }

//            else if (comapreDates(infoBeatzDateTime, currentDate)) {
//                scanHandler.post(scanRunnable);
//            }
        }}
    private void TurnBeaconOn()
    {
        AsyncTask.execute(new Runnable(){

            @Override
            public void run() {
                try{
                    Thread.sleep(300000);
                }catch (InterruptedException ie){

                }
                Log.e("Thread for beacon ", "called...............................");
                String unique_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                HashMap<String, String> user = session.getUserDetails();


                // Turn beacon into transmitter
                Beacon beacon = new Beacon.Builder()
                        .setId1(unique_id)
                        .setId2("1")
                        .setId3("2")
                        .setManufacturer(0x004C) // Radius Networks.0x0118  Change this for other beacon layouts//0x004C for iPhone//.setManufacturer(0x0118)
                        .setTxPower(-59)
                        .setDataFields(Arrays.asList(new Long[]{0l}))
                        .setBluetoothName(user.get(SessionManager.KEY_NAME))
                        .build();
                BeaconParser beaconParser = new BeaconParser()
                        .setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24");
                //.setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25");
                BeaconTransmitter beaconTransmitter = new BeaconTransmitter(getApplicationContext(), beaconParser);
                beaconTransmitter.startAdvertising(beacon);
            }
        });


    }

    // ------------------------------------------------------------------------
    // public usage
    // ------------------------------------------------------------------------
    private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback()
    {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord)
        {
            int startByte = 2;
            boolean patternFound = false;
            while (startByte <= 5)
            {
                if (    ((int) scanRecord[startByte + 2] & 0xff) == 0x02 && //Identifies an iBeacon
                        ((int) scanRecord[startByte + 3] & 0xff) == 0x15)
                { //Identifies correct data length
                    patternFound = true;
                    break;
                }
                startByte++;
            }

            if (patternFound)
            {
                //Convert to hex String
                byte[] uuidBytes = new byte[16];
                System.arraycopy(scanRecord, startByte + 4, uuidBytes, 0, 16);
                String hexString = bytesToHex(uuidBytes);

                //UUID detection
                uuid =  hexString.substring(0,8) + "-" +
                        hexString.substring(8,12) + "-" +
                        hexString.substring(12,16) + "-" +
                        hexString.substring(16,20) + "-" +
                        hexString.substring(20,32);


                // major
                final int major = (scanRecord[startByte + 20] & 0xff) * 0x100 + (scanRecord[startByte + 21] & 0xff);

                // minor
                final int minor = (scanRecord[startByte + 22] & 0xff) * 0x100 + (scanRecord[startByte + 23] & 0xff);

                Log.i(LOG_TAG,"UUID: " +uuid + "\\nmajor: " +major +"\\nminor" +minor);

                //Add UUID with timestap to dictionary

                if(!dictInfo.containsKey(uuid)) {

                    dictInfo.put(uuid, new Date());
                }

                //Check if uuid already exist else insert  in list
                if(!uuidList.contains(uuid)) {
                    uuidList.add(uuid);

                    /*//Show all UUID's on screen
                    log.append(uuid);
                    final TextView tv = (TextView) findViewById(R.id.uuid);
                    tv.setMovementMethod(new ScrollingMovementMethod());
                    tv.setSingleLine(false);
                    tv.append("UUID: " +uuid + "\n");*/
                    //Push Notification
                    ShowNotification(uuid);

                    // Turn device as Beacon
                    TurnBeaconOn();

                }
            }


        }
    };

    // ------------------------------------------------------------------------
    // Inner classes
    // ------------------------------------------------------------------------
    private Runnable scanRunnable = new Runnable()
    {
        @Override
        public void run() {

            if (isScanning)
            {
                if (btAdapter != null)
                {
                    btAdapter.stopLeScan(leScanCallback);
                }
            }
            else
            {
                if (btAdapter != null)
                {
                    btAdapter.startLeScan(leScanCallback);
                }
            }

            isScanning = !isScanning;

            scanHandler.postDelayed(this, scan_interval_ms);
        }
    };

    private static String bytesToHex(byte[] bytes)
    {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ )
        {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }


    private void ShowNotification( String uuid) {

        Intent myIntent = new Intent(getApplicationContext(), BoothActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, myIntent,0);

        myNotification = new NotificationCompat.Builder(getApplicationContext())
                .setContentTitle("Welcome to InfoBeatz!")
                .setContentText("Hi-Tec Track Welcomes you...")
                .setTicker("Notification!")
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_stat_name)
                .build();

        notificationManager =
                (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(MY_NOTIFICATION_ID, myNotification);


//        NotificationManager notif=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
//        //Define sound URI
//        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//
//        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
//                .setSmallIcon(R.drawable.ic_stat_name)
//                .setContentTitle("Conference Attendance")
//                .setContentText("Your attendance is marked !!!")
//                .setSound(soundUri); //This sets the sound to play
//
////Display notification
//        notif.notify(0, mBuilder.build());


    }

    private String getCurrentDateAsStr(){
        final Date currentDate = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_SIMPLE_DATE_FORMAT);
        return sdf.format(currentDate);
    }

    private boolean comapreDates(String date, String currentDate){
        try{
            final SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_SIMPLE_DATE_FORMAT);
            final long timeDiff = sdf.parse(currentDate).getTime() - sdf.parse(date).getTime();
            return timeDiff/1000 > 1 ;
        }catch (ParseException pe){
            return false;
        }

    }


}
