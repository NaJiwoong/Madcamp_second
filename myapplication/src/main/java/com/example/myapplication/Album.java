package com.example.myapplication;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

public class Album {

    private String folder = null;
    private String filepath = null;
    private int imgCount = 0;

    void setPath(String path){
        this.filepath = path;
    }

    public String getPath(){
        return this.filepath;
    }

    public static ArrayList<Album> getAlbums(Context context){
        ArrayList<Album> photos = new ArrayList<Album>();
        ContentResolver resolver = context.getContentResolver();

        int column_index_data;
        int column_index_folder_name;

        String BUCKET_GROUP_BY = "1) GROUP BY 1,(2";
        String BUCKET_ORDER_BY = "MAX(datataken) DESC";

        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor;
        Cursor cursorBucket;

        ArrayList<String> images;

        String absolutePath;

        String[] projection = new String[] {MediaStore.Images.ImageColumns.BUCKET_ID,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Images.ImageColumns.DATE_TAKEN,
                MediaStore.Images.ImageColumns.DATA};
        cursor = resolver.query(uri, projection, null, null, null);
        if (cursor != null){
            column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

            while (cursor.moveToNext()){
                absolutePath = cursor.getString(column_index_data);

                if (absolutePath != "" && absolutePath != null){
                    Album album = new Album();
                    album.setPath(absolutePath);
                    photos.add(album);
                }
            }
        }
        return photos;
    }
}
