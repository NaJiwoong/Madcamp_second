package com.example.myapplication.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.myapplication.CustomDialog;
import com.example.myapplication.GpsTracker;
import com.example.myapplication.ListAdapter;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.Registration;
import com.example.myapplication.Room;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.MODE_PRIVATE;

public class PageThreeFragment extends Fragment {

    Context mContext;
    ListView mListView;
    String connectUrl = "http://192.168.0.100:3001";
    SharedPreferences pref;
    ListAdapter mMyAdapter;

    ArrayList<String> room_list=new ArrayList<>();

    Socket mSocket;
    String username="";
    public PageThreeFragment() {
    }
    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.page_three_fragment,container,false);
        mContext=getContext();
        mMyAdapter = new ListAdapter(getActivity());
        mListView = (ListView)rootview.findViewById(R.id.room_list);
        mListView.setAdapter(mMyAdapter);
        mListView.setOnItemClickListener(itemClickListener);



        pref = mContext.getSharedPreferences("login", MODE_PRIVATE);
        new refresh().execute(connectUrl + "/getinfo/"+pref.getString("id",""));
        new getRoom().execute(connectUrl + "/getRoom");
        /*for(int i=0;i<room_list.size();i++)
            mMyAdapter.addItem(ContextCompat.getDrawable(getContext(), R.drawable.vfdvdfdfv),room_list.get(i), room_list.get(i));*/

        Button refreshButton = (Button) rootview.findViewById(R.id.refresh_button);

        refreshButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                pref = mContext.getSharedPreferences("login", MODE_PRIVATE);
                new refresh().execute(connectUrl + "/getinfo/"+pref.getString("id",""));
                new getRoom().execute(connectUrl + "/getRoom");
            }
        });

        Button enterButton = (Button) rootview.findViewById(R.id.enter_button);

        enterButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Room.class);

                startActivity(intent);
            }
        });

        Button createRoomButton = (Button) rootview.findViewById(R.id.creatRoom_button);

        createRoomButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                CustomDialog customDialog = new CustomDialog(getContext());

                // 커스텀 다이얼로그를 호출한다.
                // 커스텀 다이얼로그의 결과를 출력할 TextView를 매개변수로 같이 넘겨준다.
                customDialog.callFunction();
/*
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                builder.setTitle("인사말").setMessage("반갑습니다");

                AlertDialog alertDialog = builder.create();

                alertDialog.show();
*/

                /*Intent intent = new Intent(getActivity(), Room.class);

                startActivity(intent);*/
            }
        });



        return rootview;
    }

    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener()
    {
        public void onItemClick(AdapterView<?> parent, View view, int position, long l_position)
        {
            String r=room_list.get(position);
            pref = mContext.getSharedPreferences("login", MODE_PRIVATE);
            SharedPreferences.Editor edit = pref.edit();
            edit.putString("myRoom", r);
            edit.commit();
            Intent intent = new Intent(mContext, Room.class);

            mContext.startActivity(intent);
            new getRoom().execute(connectUrl + "/getRoom");     //Temp
        }
    };


    public class refresh extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... urls) {
            try {

                HttpURLConnection con = null;
                BufferedReader reader = null;

                try{
                    //URL url = new URL("http://192.168.25.16:3000/users");
                    URL url = new URL(urls[0]);
                    con = (HttpURLConnection) url.openConnection();
                    con.connect();

                    InputStream stream = con.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(stream));

                    StringBuffer buffer = new StringBuffer();

                    String line = "";
                    while((line = reader.readLine()) != null){
                        buffer.append(line);
                    }

                    return buffer.toString();

                } catch (MalformedURLException e){
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if(con != null){
                        con.disconnect();
                    }
                    try {
                        if(reader != null){
                            reader.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pref = mContext.getSharedPreferences("login", MODE_PRIVATE);
            SharedPreferences.Editor edit = pref.edit();

            if(result.equals("empty")) {
                edit.putString("victory", "0");
                edit.putString("defeat", "0");
                edit.putString("highscore", "0");
                edit.commit();
            }
            else
            {
                JsonParser jsonParser=new JsonParser();
                JsonObject object = (JsonObject) jsonParser.parse(result);

                edit.putString("victory", object.get("victory").getAsString());
                edit.putString("defeat", object.get("defeat").getAsString());
                edit.putString("highscore", object.get("highscore").getAsString());
                edit.commit();

            }
            String text="ID : "+pref.getString("id","")+
                    "\nVictory : "+pref.getString("victory","")
                    +"\nDefeat : "+pref.getString("defeat","")
                    +"\nHighScore : "+pref.getString("highscore","");
            Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
        }
    }

    public class getRoom extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... urls) {
            try {

                HttpURLConnection con = null;
                BufferedReader reader = null;

                try{
                    //URL url = new URL("http://192.168.25.16:3000/users");
                    URL url = new URL(urls[0]);
                    con = (HttpURLConnection) url.openConnection();
                    con.connect();

                    InputStream stream = con.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(stream));

                    StringBuffer buffer = new StringBuffer();

                    String line = "";
                    while((line = reader.readLine()) != null){
                        buffer.append(line);
                    }

                    return buffer.toString();

                } catch (MalformedURLException e){
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if(con != null){
                        con.disconnect();
                    }
                    try {
                        if(reader != null){
                            reader.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result.equals("empty")){
                mMyAdapter.cleanmItems();
                mMyAdapter.notifyDataSetChanged();
                return ;
            }
            JsonParser jsonParser=new JsonParser();
            JsonArray jsonArray = (JsonArray) jsonParser.parse(result);

            room_list.clear();
            mMyAdapter.cleanmItems();
            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject object = (JsonObject) jsonArray.get(i);
                room_list.add(object.get("name").getAsString());
            }
            for(int i=0;i<room_list.size();i++)
                mMyAdapter.addItem(ContextCompat.getDrawable(mContext, R.drawable.vfdvdfdfv),room_list.get(i), room_list.get(i));
            mMyAdapter.notifyDataSetChanged();
        }
    }

}