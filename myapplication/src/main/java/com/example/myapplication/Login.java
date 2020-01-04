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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Login extends Activity {

    LoginButton loginButton;
    private CallbackManager callbackManager;
    String connectUrl = "http://192.168.0.100/3000";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        Button button1=(Button) findViewById(R.id.login_bt);
        Button button2=(Button) findViewById(R.id.registration_bt);
        EditText edid = (EditText) findViewById(R.id.idText);
        EditText edps = (EditText) findViewById(R.id.passwordText);
        final String id = edid.getText().toString();
        final String pass = edps.getText().toString();

        button1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                AsyncTask<String, Void, String> asyncTask = new AsyncTask<String, Void, String>() {
                    @Override
                    protected String doInBackground(String... strings) {
                        try {
                            URL url = new URL(connectUrl);
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setDoOutput(true);
                            conn.setRequestMethod("GET");
                            OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());

                            String params=String.format("/login/%s/%s", id, pass);
                            osw.write(params);
                            osw.flush();

                            BufferedReader br = null;
                            br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

                            String line = null;
                            line = br.readLine();
                            if (line == "success"){
                                return line;
                            }else{
                                Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_SHORT).show();
                                return null;
                            }

                        }catch(MalformedURLException e){
                            Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_SHORT).show();
                        }catch(IOException e){
                            Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_SHORT).show();
                        }

                        return null;
                    }
                };
                String result = "failure";
                try {
                    result = asyncTask.execute(connectUrl, null, connectUrl).get();

                }catch(Exception e){
                    Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_SHORT).show();
                }
                if (result == "success"){
                    Intent intent = new Intent(Login.this, MainActivity.class);

                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_SHORT).show();
                }
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
        // If using in a fragmen

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
}
