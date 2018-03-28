package com.qiyue.jia.kmz;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by jia on 2018/3/28.
 */

public class PictureLoader {
    private ImageView imageView;
    private String imageUrl;
    private HttpURLConnection connection;
    private byte[] picByte;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 0x123) {
                if (picByte != null) {

                    Bitmap bitmap = BitmapFactory.decodeByteArray(picByte,0,picByte.length);
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    };



    public void PictureLoader(ImageView imageView, String imageUrl) {
        this.imageView = imageView;
        this.imageUrl = imageUrl;

        Drawable drawable = imageView.getDrawable();

        if (drawable != null && drawable instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
        new Thread(runnable).start();
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                URL url = new URL(imageUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setReadTimeout(10000);
                if (connection.getResponseCode() == 200) {
                    InputStream inputStream = connection.getInputStream();
                    ByteArrayOutputStream byteOut =  new ByteArrayOutputStream();
                    byte[] bytes = new byte[1024];
                    int length = -1;
                    while ((length = inputStream.read(bytes)) != -1) {
                        byteOut.write(bytes, 0, length);
                    }
                    picByte = byteOut.toByteArray();
                    inputStream.close();
                    byteOut.close();
                    handler.sendEmptyMessage(0x123);

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };
}
