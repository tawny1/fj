package com.fj.gnss.nmea.msg;

public class BestPos2Msg extends BaseNMEAMessage {

    private static final String HEAD = "#BESTPOS2A";

    double lat;
    double lon;
    double altitude;
    double height;
    int gpsState;

    //#BESTPOS2A,COM2,0,60.0,UNKNOWN,1356,000223.650,00000000,0000,1114;COLD_START,NONE,-0.00000000000,0.00000000000,-6378154.1620,17.1620,WGS84,0.0000,0.0000,0.0000,"AAAA",0
    // .000,2085.000,0,0,0,0,0,0,0,0*e7f1f34c
    //0           1   2  3    4    5    6          7       8  9              10      11             12              13         14
    //#BESTPOS2A,COM2,0,20.0,FINE,2140,111186.300,1922750,38,18;SOL_COMPUTED,PSRDIFF,30.49579406410,114.17709324375,27.6718,-14.5309,WGS84,3.0830,2.9006,7.0053,"1812",157.300,0
    // .000,24,9,9,0,0,08,00,51*56056997
    //30.49569584416667,114.1771658185,
    @Override
    public boolean parse(String data) {
        try {
            if (data.startsWith(HEAD)) {
                String[] datas = data.substring(0, data.lastIndexOf("*")).split(",");
                lat = Double.parseDouble(datas[11]);
                lon = Double.parseDouble(datas[12]);
                altitude = Double.parseDouble(datas[13]);
                height = Double.parseDouble(datas[14]) + altitude;
                hasParsed = true;

                if (datas[10] != null) {
                    if (datas[10].contains("INT")) {
                        gpsState = 4;
                    } else if (datas[10].contains("FLOAT")) {
                        gpsState = 5;
                    } else if (datas[10].contains("PSRDIFF") || datas[10].contains("WAAS")) {
                        gpsState = 2;
                    } else if (datas[10].contains("SINGLE")) {
                        gpsState = 1;
                    } else {
                        gpsState = 0;
                    }
                } else {
                    gpsState = 0;
                }

                return true;
            }
        } catch (Exception e) {

        }
        hasParsed = false;
        return false;
    }

    public static boolean toBeBestPos2(String data) {
        return data.startsWith(HEAD);
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public int getGpsState() {
        return gpsState;
    }
}
