package com.gzw.kd.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

public class CopyFileUtils {


    public static boolean copyFile(String oldPath, String newPath) throws Exception {
        boolean copyStatus = false;
        int byteRead;
        File oldFile = new File(oldPath);
        if (oldFile.exists()) {
            InputStream inStream = new FileInputStream(oldPath);
            FileOutputStream fs = new FileOutputStream(newPath);
            byte[] buffer = new byte[inStream.available()];
            while ((byteRead = inStream.read(buffer)) != -1) {
                fs.write(buffer, 0, byteRead);
            }
            fs.close();
            inStream.close();
            copyStatus = true;
        }
        return copyStatus;
    }

}
