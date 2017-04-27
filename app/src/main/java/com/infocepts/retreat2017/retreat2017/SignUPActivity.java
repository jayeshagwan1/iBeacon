package com.infocepts.retreat2017.retreat2017;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
/**
 * Created by jjagwan on 30-01-2017.
 */

public class SignUPActivity extends Activity {
    EditText editEmployeeName, editEmployeeID;
    Button btnCreateAccount;
    Context context = this;
    LoginDataBaseAdapter loginDataBaseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        loginDataBaseAdapter = new LoginDataBaseAdapter(this);
        loginDataBaseAdapter = loginDataBaseAdapter.open();
        editEmployeeName = (EditText) findViewById(R.id.editEmployeeName);
        editEmployeeID = (EditText) findViewById(R.id.editEmployeeID);

        btnCreateAccount = (Button) findViewById(R.id.buttonCreateAccount);
        btnCreateAccount.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                String userName = editEmployeeName.getText().toString();
                String userId = editEmployeeID.getText().toString();

                if (userName.trim().equals("") || userId.trim().equals(""))
                {

                    Toast.makeText(getApplicationContext(), "Field Vacant",
                            Toast.LENGTH_LONG).show();
                    return;
                }
               else {

                    loginDataBaseAdapter.insertEntry(userName, userId);
                    Toast.makeText(getApplicationContext(),
                            userName + " successfully registered ", Toast.LENGTH_LONG)
                            .show();
                    Intent i = new Intent(SignUPActivity.this,
                            MainActivity.class);
                    startActivity(i);
                    finish();

                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

        loginDataBaseAdapter.close();
    }
}