package com.infocepts.retreat2017.retreat2017;

/**
 * Created by jjagwan on 31-01-2017.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.bluetooth.BluetoothAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import com.infocepts.retreat2017.retreat2017.Employee;

import static android.content.ContentValues.TAG;

public class RegisterActivity extends Activity {

    Calendar calDatetime;
    SimpleDateFormat dtFormat;
    String formattedDate ;
    EditText txtUsername, txtPassword;
    String EmployeeName,Datetime;
    String EmployeeID;

    private static String dUUID = "";
    List<String> lstUUID;
    // login button
    Button btnLogin;

    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();

    // Session Manager Class
    SessionManager session;
    Employee employee;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Session Manager
        session = new SessionManager(getApplicationContext());

        // Email, Password input text
        txtUsername = (EditText) findViewById(R.id.txtEmployeename);
        txtPassword = (EditText) findViewById(R.id.txtEmployeeId);

        Toast.makeText(getApplicationContext(), "User Login Status: " + session.isLoggedIn(), Toast.LENGTH_LONG).show();

        //Debug to get bluetooth id to use as UUID

        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        Method getUuidsMethod = null;
        try {
            getUuidsMethod =BluetoothAdapter.class.getDeclaredMethod("getUuids", null);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        ParcelUuid[] uuids = new ParcelUuid[0];
        try {
            uuids = (ParcelUuid[]) getUuidsMethod.invoke(adapter, null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

//        for (ParcelUuid uuid: uuids) {
//           /* Log.d(TAG, "UUID: " + uuid.getUuid().toString());*/
//            dUUID = uuid.getUuid().toString();
//            //lstUUID.add(dUUID);
//        }


        // Login button
        btnLogin = (Button) findViewById(R.id.btnLogin);


        // Login button click event
        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Get username, password from EditText
                String username = txtUsername.getText().toString();
                String eid = txtPassword.getText().toString();

                // Check if username, password is filled
                if (username.trim().length() > 0 && eid.trim().length() > 0) {
                    // For testing puspose username, password is checked with sample data
                    // username = test
                    // password = test


                        // Creating user login session
                        // For testing i am stroing name, email as follow
                        // Use user real data
                        session.createLoginSession(username, eid);
                        txtUsername.setBackgroundColor(Color.WHITE);
                        txtPassword.setBackgroundColor(Color.WHITE);
                        //add service call to post data to DB
                        senddatatoserver();

                        // Staring MainActivity
                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(i);
                        finish();

/*                   if (username.equals("retreat") && eid.equals("2017")) {
                    } else {
                        // username / password doesn't match
                        alert.showAlertDialog(RegisterActivity.this, "Login failed..", "Employee Name/Employee ID is incorrect", false);

                    }*/
                } else {
                    // user didn't entered username or password
                    // Show alert asking him to enter the details
                    if(username.trim().length() == 0){
                        txtUsername.setBackgroundColor(Color.RED);
                    }
                    if(eid.trim().length() == 0){
                        txtPassword.setBackgroundColor(Color.RED);
                    }
                   Toast.makeText(getApplicationContext(),"Please Enter All Mandatory Details", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    public void senddatatoserver() {
        //function in the activity that corresponds to the layout button
        EmployeeName = txtUsername.getText().toString();
        EmployeeID = txtPassword.getText().toString();
        Datetime =  Getcurrenttimestamp();
        JSONObject post_dict = new JSONObject();

        try {
            post_dict.put("EmployeeName", EmployeeName);
            post_dict.put("EmployeeID", EmployeeID);
            post_dict.put("UUID",getUUID());
            post_dict.put("Timestamp", Datetime);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (post_dict.length() > 0) {
            new SendJsonDataToServer().execute(String.valueOf(post_dict));
            //call to async class
        }
    }

    String getUUID(){
        String deviceId = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
       // Toast.makeText(this, deviceId, Toast.LENGTH_SHORT).show();
        return deviceId;
    }

    private String Getcurrenttimestamp(){
        //Get current date time
        calDatetime = Calendar.getInstance();
        dtFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formattedDate =  dtFormat.format(calDatetime.getTime());
        return formattedDate;

    }

    private class SendJsonDataToServer extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... params) {
            String JsonResponse = null;
            String JsonDATA = params[0];
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL("http://115.114.8.198:8080/Service/rest/info/userinfo");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                // is output buffer writter
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
//set headers and method
                Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
                writer.write(JsonDATA);
// json data
                writer.close();
                InputStream inputStream = urlConnection.getInputStream();
//input stream
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String inputLine;
                while ((inputLine = reader.readLine()) != null)
                    buffer.append(inputLine + "\n");
                if (buffer.length() == 0) {
                    // Stream was empty. No point in parsing.
                    return null;
                }
                JsonResponse = buffer.toString();
//response data
                Log.i(TAG,JsonResponse);
                try {
//send to post execute
                    return JsonResponse;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;



            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(TAG, "Error closing stream", e);
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
        }

    }

}
