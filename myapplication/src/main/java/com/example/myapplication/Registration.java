package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ViewAnimator;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


public class Registration extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        EditText idText = (EditText) findViewById(R.id.idText);
        EditText passText = (EditText) findViewById(R.id.passwordText);
        EditText confText = (EditText) findViewById(R.id.confirm_passwordText);
        String id = idText.getText().toString();
        String pass = passText.getText().toString();
        String conf = confText.getText().toString();

        Button btn = (Button) findViewById(R.id.registration_bt);

        final String connectUrl = "http://192.168.0.100:3000";

        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                EditText idText = (EditText) findViewById(R.id.idText);
                EditText passText = (EditText) findViewById(R.id.passwordText);
                EditText confText = (EditText) findViewById(R.id.confirm_passwordText);
                String id = idText.getText().toString();
                String pass = passText.getText().toString();
                String conf = confText.getText().toString();

                if (!pass.equals(conf)){
                    Toast.makeText(getApplicationContext(), "Password and Confirmation are not equal!!", Toast.LENGTH_SHORT).show();
                    return;
                }

                new Register().execute(connectUrl + "/register", id, pass);

            }
        });



    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);*/
    }



    public class Register extends AsyncTask<String, String, String> {

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

                    return buffer.toString();
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
        protected void onPostExecute(String result){
            super.onPostExecute(result);

            if (result.equals("success")){
                Toast.makeText(getApplicationContext(), "Succeeded to register", Toast.LENGTH_SHORT).show();
                finish();
            }
            else{
                Toast.makeText(getApplicationContext(), "Failed to register", Toast.LENGTH_SHORT).show();
            }
            return;

        }


    }




}
