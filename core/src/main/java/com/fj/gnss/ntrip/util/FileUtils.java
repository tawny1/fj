package com.fj.gnss.ntrip.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {

    public FileUtils() {
    }

    public static File mkFile(String filePath, String fileName) {
        File fileDir = new File(filePath);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        File file = new File(filePath, fileName);
        return file;
    }

    public static void writeMsgToFile(String filePath, String fileName, byte[] data) {
        File file = mkFile(filePath, fileName);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file, true);
            fileOutputStream.write(data);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException var6) {
            var6.printStackTrace();
        }
    }
}

