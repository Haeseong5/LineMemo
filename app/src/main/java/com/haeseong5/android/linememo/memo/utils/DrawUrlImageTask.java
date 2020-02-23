package com.haeseong5.android.linememo.memo.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.haeseong5.android.linememo.memo.views.ImageAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class DrawUrlImageTask extends AsyncTask<String, Void, Bitmap> {
    private ArrayList<byte[]> images;
    private ImageAdapter adapter;
    public DrawUrlImageTask(ArrayList<byte[]> images, ImageAdapter adapter) {
        this.images = images;
        this.adapter = adapter;
    }

    protected Bitmap doInBackground(String... urls) {
        String url = urls[0];
        Bitmap bitmap = null;
        InputStream in = null;

        try {
            in = new java.net.URL(url).openStream();
            bitmap = BitmapFactory.decodeStream(in);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                in.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        return bitmap;
    }

    protected void onPostExecute(Bitmap bitmap) {
        images.add(DbBitmapUtility.getBytes(bitmap));
        adapter.notifyDataSetChanged();
    }
}


