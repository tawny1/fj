package com.fj.gnss.nmea.msg;

//#BESTPOSA,COM2,0,78.0,FINE,2140,107391.250,29784,29791,18;INSUFFICIENT_OBS,NONE,0.00000000000,0.00000000000,0.0000,17.2300,WGS84,0.0000,0.0000,0.0000,"",0.000,7.450,12,0,2,1,0,00,00,00*8fb862b9
public class BestPosMsg extends BaseNMEAMessage {

    @Override
    protected boolean parse(String data) {
        return false;
    }

}
