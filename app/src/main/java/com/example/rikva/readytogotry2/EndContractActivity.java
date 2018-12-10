package com.example.rikva.readytogotry2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EndContractActivity extends AppCompatActivity implements NfcAdapter.CreateNdefMessageCallback, NfcAdapter.OnNdefPushCompleteCallback {

    public NfcAdapter mNfcAdapter;
    public MessageDigest digest;
    public String username;
    public byte[] ToBeHashed;
    public byte[] Hash1;
    public byte[] DateByte;
    public String Startdate;
    public String CurrentDateMillis;
    public NdefMessage Ndef;
    private byte[] Hash2;
    private String half1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_contract);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        } else {
            // Register callback to set NDEF message
            mNfcAdapter.setNdefPushMessageCallback(this, this);
            // Register callback to listen for message-sent success
            mNfcAdapter.setOnNdefPushCompleteCallback(this, this);
        }
        SharedPreferences prefs = getSharedPreferences("Prefs", MODE_PRIVATE);
        username = prefs.getString("username","");
        Log.d("cw2", username + "HALLO1");


        // HASHING
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }



        // Getting all the data that needs to be hashed
        String Hash1String = prefs.getString("hash1", "");
        Hash1String = Hash1String.toUpperCase();

        Hash1 = Hash1String.getBytes();

        Startdate = prefs.getString("startTime", "");


        CurrentDateMillis = "0";
        DateByte = CurrentDateMillis.getBytes();
        ToBeHashed = new byte[DateByte.length + Hash1.length];
        System.arraycopy(DateByte, 0, ToBeHashed, 0, DateByte.length);
        System.arraycopy(Hash1, 0, ToBeHashed, DateByte.length, Hash1.length);
        Hash2 = digest.digest(ToBeHashed);
        String Hash2String = bin2hex(Hash2);
        half1 = Hash2String.substring(0, Hash2String.length() / 2);
        Log.d("cw2", "##########" + "HASH1 COMPONENTS END CONTRACT" );
        Log.d("cw2", "hash1:   " +  Hash1String);
        Log.d("cw2", "startd:   " +  Startdate);
        Log.d("cw2", "##########" + "HASH2 COMPONENTS" );
        Log.d("cw2", "hash2:   " +  half1);
        Log.d("cw2", "currentdaate:   " +  CurrentDateMillis);
        Log.d("cw2", "##########" + "  " );









    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        NdefRecord Hash2Record = NdefRecord.createMime("Hash2",  half1.getBytes());
        NdefRecord DateInMillisRecord = NdefRecord.createMime("DateInMillis",  DateByte);
        NdefRecord StartdateRecord = NdefRecord.createMime("startdate",  Startdate.getBytes());
        Log.d("cw2", "TEST 1");

        NdefRecord UsernameRecord = NdefRecord.createMime("username",  username.trim().getBytes());
        Log.d("cw2", "TEST 2");

        Log.d("cw2",UsernameRecord.toString()+" "+ StartdateRecord.toString()+ " "+DateInMillisRecord);
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
        startActivity(new Intent(this, HomeActivity.class));
        finish();


    }
    static String bin2hex(byte[] data) {
        return String.format("%0" + (data.length*2) + "X", new BigInteger(1, data));
    }

}

