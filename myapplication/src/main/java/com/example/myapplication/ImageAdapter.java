package com.example.myapplication;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.myapplication.fragments.PageTwoFragment;

import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    ArrayList<Album> albums= new ArrayList<>();

    public ImageAdapter(Context c) {
        mContext = c;
    }

    public ImageAdapter(Context c, ArrayList<Album> list) {
        mContext = c;
        albums=list;
    }

    public int getCount() {
        return albums.size();
    }

    public Object getItem(int position) {
        return mThumbIds[position];
    }

    public long getItemId(int position) {
        return 0;
    }

    Integer[] mThumbIds = {R.drawable.a1, R.drawable.a2, R.drawable.a3, R.drawable.a4, R.drawable.a5,
            R.drawable.b1, R.drawable.b2, R.drawable.b3, R.drawable.b4, R.drawable.b5,
            R.drawable.c1, R.drawable.c2, R.drawable.c3, R.drawable.c4, R.drawable.c5,
            R.drawable.d1, R.drawable.d2, R.drawable.d3, R.drawable.d4, R.drawable.d5,
            R.drawable.mx, R.drawable.fr3,
            R.drawable.pl, R.drawable.sa3,
            R.drawable.cn, R.drawable.kr
    };

    // create a new ImageView for each item referenced by the Adapter
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.pictures, parent, false);
        ImageView imageView = (ImageView)v.findViewById(R.id.imageView1);
        //ImageAdapter imageAdapter = new ImageAdapter(getContext());
        //gridView = (GridView)rootview.findViewById(R.id.gridView1);
        /*ImageView imageView = new ImageView(mContext);
        imageView.setLayoutParams(new GridView.LayoutParams(300, 400));
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setPadding(5, 5, 5, 5);*/
        //imageView.setImageResource(mThumbIds[position]);
        Glide.with(mContext).load(albums.get(position).getPath()).into(imageView);
        //final int pos = position;
        /*imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View dialogView = (View) View.inflate(mContext, R.layout.dialog, null);
                final Dialog dlg = new Dialog(mContext, android.R.style.Theme_Light);
                dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
                ImageView ivPoster = (ImageView) dialogView.findViewById(R.id.ivPoster);
                ivPoster.setImageResource(mThumbIds[pos]);
                dlg.setContentView(dialogView);
                dlg.show();
                ivPoster.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        dlg.dismiss();
                    }
                });
            }

        });*/
        return imageView;
    }
}
