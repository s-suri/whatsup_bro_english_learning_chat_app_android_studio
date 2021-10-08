package com.some.notes.Adapter;

import android.graphics.Bitmap;
import android.util.Log;

public class ImageResize {

    public static Bitmap reduveBitmapSize(Bitmap bitmap,int MAX_SIZE){
        double ratioSquare;

        int bitmapHeight,bitmapWidth;
        bitmapHeight = bitmap.getHeight();
        bitmapWidth = bitmap.getWidth();
        ratioSquare =(bitmapHeight*bitmapWidth)/MAX_SIZE;
        if (ratioSquare<=1)
            return bitmap;
        double ratio = Math.sqrt(ratioSquare);

        Log.d("mylog","Ratio:" + ratio);
        int requiredHeight =(int)Math.round(bitmapHeight / ratio);
        int requiredWidth = (int)Math.round(bitmapWidth/ratio);
        return  Bitmap.createScaledBitmap(bitmap,requiredWidth,requiredHeight,true);
    }
}
