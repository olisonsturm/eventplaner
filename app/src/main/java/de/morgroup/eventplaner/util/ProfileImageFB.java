package de.morgroup.eventplaner.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseUser;

import java.io.InputStream;

import de.morgroup.eventplaner.R;

public class ProfileImageFB extends AsyncTask<String, Void, Bitmap> {

    ImageView imageView;
    Bitmap bimage;

    public ProfileImageFB(ImageView imageView) {
        this.imageView = imageView;
    }

    protected Bitmap doInBackground(String... urls) {
        String imageURL = urls[0];
        if (TextUtils.isEmpty(imageURL)) {
            return null;
        }
        try {
            InputStream in = new java.net.URL(imageURL).openStream();
            bimage = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bimage;
    }

    protected void onPostExecute(Bitmap result) {
        if (result == null) {
            imageView.setImageResource(R.drawable.img_placeholder);
        } else {
            imageView.setImageBitmap(getRoundedCornerBitmap(result, 20));
        }
    }

    private Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }
}

