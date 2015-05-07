package papersize.chreez.com.papersize.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Environment;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import papersize.chreez.com.papersize.R;

/**
 * Created by chris on 07.05.15.
 */
public class Utils {

    /**
     * Create a bitmap from a view
     *
     * @param view
     * @param width
     * @param height
     * @return
     */
    private static Bitmap getBitmapFromView(Context context, View view, int width, int height) {
        view.measure(View.MeasureSpec.makeMeasureSpec(
                width, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.RGB_565);

        Canvas canvas = new Canvas(bitmap);
        int color = context.getResources().getColor(R.color.material_blue_grey_900);
        canvas.drawARGB(color >> 24, color >> 16, color >> 8, color);
        view.draw(canvas);
        return bitmap;
    }

    public static Uri saveViewToFile(Context context, View view) {
        Bitmap bitmap = getBitmapFromView(context, view, view.getWidth(), view.getHeight());

        if(bitmap == null) {
            return null;
        }

        File directory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File bitmapFile = new File(directory, "screenshot" + UUID.randomUUID().getMostSignificantBits() + ".png");

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 75, stream);
        byte[] byteArray = stream.toByteArray();

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(bitmapFile);
            fos.write(byteArray);
            fos.flush();
            fos.close();

            return Uri.fromFile(bitmapFile);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
