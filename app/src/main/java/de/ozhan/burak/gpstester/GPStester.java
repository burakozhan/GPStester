package de.ozhan.burak.gpstester;

import android.app.Activity;
import android.location.GpsStatus;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;


public class GPStester extends Activity implements GpsStatus.NmeaListener{

    TextView GPSFIX;
    TextView midt;
    TextView rightt;
    TextView leftb;
    TextView midb;
    TextView count;
    TextView MessageTypes;
    TextView DevInfo;
    TextView GGA;
    TextView GSA;
    TextView GSV;
    TextView RMC;
    TextView VTG;
    TextView GL;
    TextView BD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpstester);

        GPSFIX = (TextView) findViewById(R.id.GPSFIX);
        midt   = (TextView) findViewById(R.id.midt);
        rightt = (TextView) findViewById(R.id.rightt);
        leftb  = (TextView) findViewById(R.id.leftb);
        midb   = (TextView) findViewById(R.id.midb);
        count  = (TextView) findViewById(R.id.count);
        MessageTypes = (TextView) findViewById(R.id.MessageTypes);
        DevInfo = (TextView) findViewById(R.id.DevInfo);
        GGA = (TextView) findViewById(R.id.GGA);
        GSA = (TextView) findViewById(R.id.GSA);
        GSV = (TextView) findViewById(R.id.GSV);
        RMC = (TextView) findViewById(R.id.RMC);
        VTG = (TextView) findViewById(R.id.VTG);
        GL  = (TextView) findViewById(R.id.GL);
        BD  = (TextView) findViewById(R.id.BD);

        APPLocationManager.getInstance().setContext(getApplicationContext(),this);
        APPLocationManager.getInstance().startUpdatingLocation();

        DevInfo.setText("Device Product: " + Build.PRODUCT + " Model: " + Build.MODEL + " Manuf: " + Build.MANUFACTURER + " Version: " + Build.VERSION.RELEASE + " SDK:" + Build.VERSION.SDK_INT);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.gpstester, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private static enum TalkerDevice {GNSS, GPS, GLONASS, GALILEO, BEIDOU, PROPRIETARY, OTHER}
    private static ArrayList<String> messagetypeslist = new ArrayList<String>(1);
    private static boolean hasFix = false;

    private static int nmeactr = 0;
    private static int bestfix = 0;

    @Override
    public void onNmeaReceived(long l, String s) {
        midb.setText("NMEActr:"+ nmeactr++);
        if (!s.startsWith("$")) {
            throw new IllegalArgumentException("Supposed NMEA message does not start with '$'.");
        }
        if (!s.contains("*")) {
            throw new IllegalArgumentException("Supposed NMEA message does not contain '*'.");
        }
        if (s.length() > 80) {
            throw new IllegalArgumentException("Supposed NMEA message exceeds 80 characters.");
        }
        String talkerID = s.substring(1,s.indexOf(","));
        TalkerDevice device = TalkerDevice.OTHER;
        if (talkerID.startsWith("GP")){
            device = TalkerDevice.GPS;
        } else if (talkerID.startsWith("GN")){
            device = TalkerDevice.GNSS;
        } else if (talkerID.startsWith("GL")){
            device = TalkerDevice.GLONASS;
        } else if (talkerID.startsWith("BD")){
            device = TalkerDevice.BEIDOU;
        } else if (talkerID.startsWith("GA")){
            device = TalkerDevice.GALILEO;
        } else if (talkerID.startsWith("P")){
            device = TalkerDevice.PROPRIETARY;
        }

        if (talkerID.length() == 5) {
            if ( !messagetypeslist.contains(talkerID)){
                messagetypeslist.add(talkerID);
                MessageTypes.setText(messagetypeslist.toString());
                count.setText("Count: "+messagetypeslist.size());
            }
        }

        if (talkerID.contains("GPGSA")) {
            String[] messageParts = s.split(",");
            int fixValue = Integer.parseInt(messageParts[2]);
            if ( fixValue == 1 ) {
                hasFix = false;
                GPSFIX.setText("GPSFIX: No Fix");
            }
            if ( fixValue == 2 ) {
                hasFix = true;
                GPSFIX.setText("GPSFIX: 2D Fix");
            }
            if ( fixValue == 3 ) {
                hasFix = true;
                GPSFIX.setText("GPSFIX: 3D Fix");
            }
            if ( fixValue != 1 && fixValue != 2 && fixValue != 3){
                Log.e("Satellite FIX broken returns : ",""+messageParts[2]);
                GPSFIX.setText("GPSFIX: WEIRD("+fixValue+")");
            }
            if ( fixValue > bestfix ){
                bestfix = fixValue;
                midt.setText("BestFix: "+GPSFIX.getText().subSequence(8,GPSFIX.getText().length()));
            }
        }

        if (talkerID.contains("GPGGA")) {
            GGA.setText(s);
        }
        if (talkerID.contains("GPGSA")) {
            GSA.setText(s);
        }
        if (talkerID.contains("GPGSV")) {
            GSV.setText(s);
        }
        if (talkerID.contains("GPRMC")) {
            RMC.setText(s);
        }
        if (talkerID.contains("GPVTG")) {
            VTG.setText(s);
        }
        if (talkerID.startsWith("GL")) {
            GL.setText(s);
        }
        if (talkerID.startsWith("BD")) {
            BD.setText(s);
        }

        Log.v("NMEA Message:",s);

//        if (hasFix) {
//            Log.v("NEMA Message Types", "This device produces "+messagetypeslist.size()+" different NMEA Messages. Location Fix : "+hasFix+". These are as follows"+messagetypeslist.toString());
//        } else {
//            Log.e("NEMA Message Types", "This device produces "+messagetypeslist.size()+" different NMEA Messages. Location Fix : "+hasFix+". These are as follows"+messagetypeslist.toString());
//        }

//        if (talkerID.equals("GPEVT")) {
//            Log.e("GPEVT Message received ","Looks like this:"+s);
//        }


//        if ( s.contains("GSA") ) {}

//        if ( s.startsWith("$GP") ) {
//            Log.wtf("NMEA", "GPS");
//            Log.wtf("NMEA",s);
//        }
//        if ( s.startsWith("$GN") ) Log.wtf("NMEA","GNSS");
//        if ( s.startsWith("$GL") ) {
//            Log.wtf("NMEA","GLONASS");
//            Log.wtf("NMEA",s);
//        }
//        if ( s.startsWith("$GA") ) Log.wtf("NMEA","GALILEO   GALILEO   GALILEO   GALILEO   GALILEO   GALILEO   GALILEO   GALILEO   GALILEO   GALILEO");
//        if ( s.startsWith("$BD") ) {
//            Log.wtf("NMEA", "BEIDOU");
//            Log.wtf("NMEA",s);
//        }
//        Log.wtf("NMEA",s);
    }
}
