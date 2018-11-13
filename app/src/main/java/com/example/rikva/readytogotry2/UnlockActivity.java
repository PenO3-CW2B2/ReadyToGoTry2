package com.example.rikva.readytogotry2;

import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

public class UnlockActivity extends AppCompatActivity implements NfcAdapter.CreateNdefMessageCallback, NfcAdapter.OnNdefPushCompleteCallback {
    public NfcAdapter mNfcAdapter;
    public MessageDigest digest;
    public String username;
    public byte[] ToBeHashed;
    public byte[] Hash1;
    public byte[] DateByte;
    public String Test = "test";
    public String Startdate = "test";
    public String CurrentDateMillis;
    public NdefMessage Ndef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unlock);
        Log.d("cw2b2ndef", "HALLO");
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        } else {
            // Register callback to set NDEF message
            mNfcAdapter.setNdefPushMessageCallback(this, this);
            // Register callback to listen for message-sent success
            mNfcAdapter.setOnNdefPushCompleteCallback((NfcAdapter.OnNdefPushCompleteCallback) this, this);
        }
        SharedPreferences prefs = getSharedPreferences("Prefs", MODE_PRIVATE);
        String username = prefs.getString("username","");


        // HASHING
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }



        // Getting all the data that needs to be hashed
        Hash1 = Test.getBytes();


        // TODO start date (boeit nie in welk format) en hash (in byte array format) ophalen van de server

        CurrentDateMillis = Long.toString(System.currentTimeMillis());
        DateByte = CurrentDateMillis.getBytes();
        ToBeHashed = new byte[DateByte.length + Hash1.length];
        System.arraycopy(DateByte, 0, ToBeHashed, 0, DateByte.length);
        System.arraycopy(Hash1, 0, ToBeHashed, DateByte.length, Hash1.length);
        digest.digest(ToBeHashed);







    }


    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        NdefRecord Hash2Record = NdefRecord.createMime("Hash2",  digest.digest(ToBeHashed));
        NdefRecord DateInMillisRecord = NdefRecord.createMime("DateInMillis",  DateByte);
        NdefRecord StartdateRecord = NdefRecord.createMime("startdate",  Startdate.getBytes());
        NdefRecord UsernameRecord = NdefRecord.createMime("username",  username.getBytes());
        NdefMessage msg = new NdefMessage(new NdefRecord[]{Hash2Record,DateInMillisRecord,StartdateRecord,UsernameRecord});
        Ndef = msg;
        Log.d("cw2b2ndef", Ndef.toString());


        return msg;

    }

    @Override
    public void onNdefPushComplete(NfcEvent event){
//        processIntent(Intent intent);
        Log.d("cw2b2ndef", Ndef.toString());

        Log.d("cw2b2", bin2hex(digest.digest(ToBeHashed))+"  "+CurrentDateMillis);



    }
    static String bin2hex(byte[] data) {
        StringBuilder hex = new StringBuilder(data.length * 2);
        for (byte b : data)
            hex.append(String.format("%02x", b & 0xFF));
        return hex.toString();
    }

}
