package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.net.http.HttpResponseCache;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.SharedPreferences;

public class Login extends Activity {

    LoginButton loginButton;
    private CallbackManager callbackManager;
    String connectUrl = "http://192.168.0.100:3000";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        Button button1=(Button) findViewById(R.id.login_bt);
        Button button2=(Button) findViewById(R.id.registration_bt);
//        EditText edid = (EditText) findViewById(R.id.idText);
//        EditText edps = (EditText) findViewById(R.id.passwordText);
//        final String id = edid.getText().toString();
//        final String pass = edps.getText().toString();

        SharedPreferences pref = getSharedPreferences("login", MODE_PRIVATE);
        String sesId = pref.getString("id", "");
        String sesPass = pref.getString("password", "");
        new LoginTask().execute(connectUrl + "/login", sesId, sesPass);


        button1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                EditText edid = (EditText) findViewById(R.id.idText);
                EditText edps = (EditText) findViewById(R.id.passwordText);
                String id = edid.getText().toString();
                String pass = edps.getText().toString();
                Toast.makeText(getApplicationContext(), "Login", Toast.LENGTH_SHORT).show();
                new LoginTask().execute(connectUrl + "/login", id, pass);
            }
        });


        button2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, Registration.class);

                startActivity(intent);
            }
        });
        callbackManager = CallbackManager.Factory.create();

        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");
        // If using in a fragment

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Intent intent = new Intent(Login.this, MainActivity.class);

                startActivity(intent);
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    /* 여기부터 JSONTask 만들기 */

    public class LoginTask extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... urls){
            try{
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", urls[1]);
                jsonObject.put("password", urls[2]);

                HttpURLConnection con = null;
                BufferedReader reader = null;

                try{
                    URL url = new URL(urls[0]);
                    con = (HttpURLConnection) url.openConnection();

                    con.setRequestMethod("POST");
                    con.setRequestProperty("Cache-Control", "no-cache");
                    con.setRequestProperty("Content-Type", "application/json");

                    con.setRequestProperty("Accept", "text/html");
                    con.setDoOutput(true);
                    con.setDoInput(true);

                    con.connect();

                    OutputStream outStream = con.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter((outStream)));
                    writer.write(jsonObject.toString());
                    writer.flush();
                    writer.close();

                    // Create Input Stream
                    InputStream stream = con.getInputStream();

                    // Create buffer to improve speed and lessen burden
                    reader = new BufferedReader(new InputStreamReader(stream));

                    // Where get data in real
                    StringBuffer buffer = new StringBuffer();

                    // variance for getting lines' strings respectively
                    String line = "";

                    while((line = reader.readLine()) != null){
                        buffer.append(line);
                    }

                    return buffer.toString() + "/" + urls[1] + "/" + urls[2];
                }catch (Exception e){
                    e.printStackTrace();
                }finally{
                    if (con != null){
                        con.disconnect();
                    }try{
                        if (reader != null){
                            reader.close();
                        }
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }//finally part
            }catch (Exception e){
                e.printStackTrace();
            }
            return "failure";
        }

        @Override
        protected void onPostExecute(String result1){
            super.onPostExecute(result1);

            String[] result_list = result1.split("/");
            String result = result_list[0];
            if (result_list.length < 2){
                return;
            }

            if (result.equals("success")){

                String id = result_list[1];
                String pass = result_list[2];
                SharedPreferences pref = getSharedPreferences("login", MODE_PRIVATE);
                SharedPreferences.Editor edit = pref.edit();
                edit.putString("id", id);
                edit.putString("password", pass);
                edit.commit();

                Intent intent = new Intent(Login.this, MainActivity.class);

                startActivity(intent);
                finish();
            }
            else{
                Toast.makeText(getApplicationContext(), "Failed to login", Toast.LENGTH_SHORT).show();
            }
            return;

        }


    }

}



