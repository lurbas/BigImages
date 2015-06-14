package com.lucasurbas.bigimages.model;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.lucasurbas.bigimages.App;

import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;

/**
 * Created by l.urbas on 2015-06-14.
 */
public class ImageLoader {

    @Inject
    Context context;

    public ImageLoader() {
        App.getObjectGraph().inject(this);
    }

    public Bitmap getBitmapFromAsset(String filePath, int reqWidth, int reqHeight) {
        AssetManager assetManager = context.getAssets();

        InputStream istr;
        Bitmap bitmap = null;

        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;

            istr = assetManager.open(filePath);
            BitmapFactory.decodeStream(istr, null, options);
            istr.reset();

            int sampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            BitmapFactory.Options optionsSampleSize = new BitmapFactory.Options();
            optionsSampleSize.inSampleSize = sampleSize;

            bitmap = BitmapFactory.decodeStream(istr, null, optionsSampleSize);
            istr.reset();
        } catch (IOException e) {
            // handle exception
        }

        return bitmap;
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }
}
