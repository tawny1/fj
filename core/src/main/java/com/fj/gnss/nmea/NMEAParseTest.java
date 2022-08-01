package com.fj.gnss.nmea;

import com.fj.gnss.nmea.msg.GGAMessage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * @ClassName NMEAParseTest
 * @Description TODO
 * @Author Jarven.Ding
 * @Date 2021/2/25 11:24
 */
public class NMEAParseTest {

    public static void main(String[] args) {
        parse("C:\\Users\\Jarven.Ding\\Desktop\\GPGGA\\GPGGA-482.TXT");
    }

    public static void parse(String path) {
        Double min = null;
        Double max = null;
        File file = new File(path);
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (GGAMessage.toBeGGA(line)) {
                    GGAMessage ggaMessage = new GGAMessage();
                    ggaMessage.parse(line);
                    System.out.println(ggaMessage.getTimeUTC() + "#" + ggaMessage.getLatitude() + "#" + ggaMessage.getLongitude() + "#" + ggaMessage.getAltitude());
                    if (min == null || ggaMessage.getAltitude() < min) {
                        min = ggaMessage.getAltitude();
                    }
                    if (max == null || ggaMessage.getAltitude() > max) {
                        max = ggaMessage.getAltitude();
                    }
                }
            }
            System.out.println("Max-Min:" + max + "#" + min + "#" + (max - min));
            bufferedReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
