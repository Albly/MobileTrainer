package com.example.mobiletrainer;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Utils {

    /** Чтение файла со структурой нейросети */

    public static String assetFilePath(Context context, String assetName) {
        File file = new File(context.getFilesDir(), assetName);

        try {
            InputStream is = context.getAssets().open(assetName);
            try  {
                OutputStream os = new FileOutputStream(file);
                byte[] buffer = new byte[4 * 1024];
                int read;
                while ((read = is.read(buffer)) != -1) {
                    os.write(buffer, 0, read);
                }
                os.flush();
            }catch (Exception e) {
                Log.e("pytorchandroid", "Error process asset 1 " + assetName + " to file path");
            }
            return file.getAbsolutePath();
        } catch (IOException e) {
            Log.e("pytorchandroid", "Error process asset 2" + assetName + " to file path");
        }
        return null;
    }



}
