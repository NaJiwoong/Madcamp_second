package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


public class Registration extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);


    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);*/
    }
}
