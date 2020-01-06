package com.example.myapplication.fragments;


import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.Album;
import com.example.myapplication.ImageAdapter;
import com.example.myapplication.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class PageTwoFragment extends Fragment {

    GridView gridView;
    String connectUrl = "http://192.168.0.100:3000";

    public PageTwoFragment() {
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,@Nullable Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.page_two_fragment,container,false);
        //ImageAdapter imageAdapter = new ImageAdapter(getContext());
        gridView = (GridView)rootview.findViewById(R.id.gridView1);

        ArrayList<Album> albums=Album.getAlbums(getContext());
        ImageAdapter imageAdapter = new ImageAdapter(getContext(),albums);
        gridView.setAdapter(imageAdapter);


        ArrayList<String> sendParam=new ArrayList<>();
        sendParam.add(connectUrl + "/gallery");
        SharedPreferences pref = this.getActivity().getSharedPreferences("login", MODE_PRIVATE);
        String sesId = pref.getString("id", "");
        sendParam.add(sesId);

        String temp="[";
        for(int i=0;i<albums.size()-1;i++)
        {
            Bitmap _bit= BitmapFactory.decodeFile(albums.get(i).getPath());
            String bts=BitmapToString(_bit);
            temp+=(bts+",");
        }
        temp+=(BitmapToString(BitmapFactory.decodeFile(albums.get(albums.size()-1).getPath()))+"]");

        sendParam.add(temp);
        new SendContacts().execute(sendParam);

        return rootview;
    }

    public class SendContacts extends AsyncTask<ArrayList<String>, String, String> {

        @Override
        protected String doInBackground(ArrayList<String>... urls) {
            try {
                JSONObject jsonObject = new JSONObject();
                //JSONObject jsonObject2 = new JSONObject();
                jsonObject.put("id", urls[0].get(1));

                //jsonObject.put("contacts", urls[2]);

                jsonObject.put("gallery", urls[0].get(2));

                HttpURLConnection con = null;
                BufferedReader reader = null;

                try {
                    URL url = new URL(urls[0].get(0));
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

                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }

                    return buffer.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (con != null) {
                        con.disconnect();
                    }
                    try {
                        if (reader != null) {
                            reader.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }//finally part
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "failure";
        }
    }

    public static String BitmapToString(Bitmap bitmapPicture) {
        String encodedImage;
        bitmapPicture = Bitmap.createScaledBitmap(bitmapPicture, 300, 400, true);
        ByteArrayOutputStream byteArrayBitmapStream = new ByteArrayOutputStream();
        bitmapPicture.compress(Bitmap.CompressFormat.PNG, 100, byteArrayBitmapStream);
        byte[] b = byteArrayBitmapStream.toByteArray();
        encodedImage = Base64.encodeToString(b,Base64.DEFAULT);
        encodedImage = encodedImage.replace(System.getProperty("line.separator"), "");

        return encodedImage;
    }
}
