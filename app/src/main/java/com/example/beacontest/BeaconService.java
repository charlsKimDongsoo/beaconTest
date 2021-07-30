package com.example.beacontest;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.Vector;

import android.os.Handler;

public class BeaconService {

    BluetoothAdapter mBluetoothAdapter;
    BluetoothLeScanner mBluetoothLeScanner;
    BluetoothLeAdvertiser mBluetoothLeAdvertiser;
    ScanSettings.Builder mScanSettings;
    List<ScanFilter> scanFilters;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss.SSS", Locale.KOREAN);
    ArrayList<DataBeacon> batch = new ArrayList<DataBeacon>();
    ArrayList<DataBeacon> batch_results = new ArrayList<DataBeacon>();
    private Thread thread;
    int scanInterval = 1000;

//    private final String MANUFACTUREID = "5900";
//    private final String TYPE = "4D";
//    private final String LENTGH = "14";
//    private final String PID = "020106";

    private boolean isScan = false;


   // public static UUID UUID_TDCS_SERVICE = UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E");

    private final Handler fHandler;

    public BeaconService(Context context, Handler handler) {
        fHandler = handler;
    }

    public void stopScanning() {
        mBluetoothLeScanner.stopScan(mScanCallback);
        mBluetoothAdapter.cancelDiscovery();
        isScan = false;
    }

    public void startScanning() {
        Log.d("스캔 시" , "작");
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        mBluetoothLeAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
        mScanSettings = new ScanSettings.Builder();
        mScanSettings.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY);
        //mScanSettings.setReportDelay(1000);
        ScanSettings scanSettings = mScanSettings.build();
        scanFilters = new Vector<>();
        ScanFilter.Builder scanFilter = new ScanFilter.Builder();
        scanFilter.setDeviceAddress("DC:0D:D6:F9:2D:44");
       // scanFilter.setDeviceName("");
        ScanFilter scan = scanFilter.build();
        scanFilters.add(scan);
        mBluetoothLeScanner.startScan(scanFilters, scanSettings, mScanCallback);
        isScan = true;
        sendScanResultTread();

    }

    public boolean scanStatus(){
        return isScan;
    }



    ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            ArrayList<Integer> rm = new ArrayList<Integer>();
            boolean op = true;
            long nw = System.currentTimeMillis();
            Log.d("rawbeacon", result.getDevice().getAddress());

            if(batch.size() == 0){
                    batch.add(new DataBeacon(result.getDevice().getAddress(), result.getDevice().getName(),nw,result.getRssi(),null));
                    return;
            }

            for(int i = 0 ; i < batch.size() ; i++){
                    if(batch.get(i).getAddress().equals(result.getDevice().getAddress())){
                        batch.set(i, new DataBeacon(result.getDevice().getAddress(), result.getDevice().getName(),nw,result.getRssi(),null));
                        op = false;
                    }

                    if(nw - batch.get(i).getNow() > scanInterval){
                        batch.remove(i);
                        i--;
                    }
            }

            if(op) {
                batch.add(new DataBeacon(result.getDevice().getAddress(), result.getDevice().getName(),nw,result.getRssi(),null));
            }

        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            List<ScanResult> temp = new ArrayList<ScanResult>();

            for (int i = 0; i < results.size(); i++) {
                if (results.get(i).getDevice().getName() != null) {
                    temp.add(results.get(i));
                }
            }
            if(temp.size() > 0) {
                fHandler.obtainMessage(1009, temp).sendToTarget();
                Log.d("onBatchScanResults", temp.size() + "");
            }


        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.d("onScanFailed()", errorCode + "");
        }
    };


    private void getDataFromPacket(byte[] scanRecord) {
        List<Byte> BeaconBuffer = new ArrayList<Byte>();
        BeaconBuffer.clear();
        if (scanRecord.length > 0) {
            for (int i = 0; i < scanRecord.length; i++) {
                BeaconBuffer.add(scanRecord[i]);
            }
        }

        String rawPacket = null;
        rawPacket = ByteArrayToString(scanRecord);
        Log.d("rawPacket", rawPacket);

    }



    public static String ByteArrayToString(byte[] ba) {
        StringBuilder hex = new StringBuilder(ba.length * 2);
        for (byte b : ba)
            //hex.append(b + " ");
            hex.append(String.format("%02X", b & 0xff));
        return hex.toString();
    }



    private void sendScanResultTread() {

        if (thread != null) thread.interrupt();
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isScan) {

                    // Log.d("batch개수",batch_results.size()+"");
                    if(batch.size() != 0) {
                        fHandler.obtainMessage(1009, copyList(batch)).sendToTarget();
                    }
                    //batch_results.clear();
                    try {
                        Thread.sleep(scanInterval);
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                    }
                }
            }
        });

        thread.start();


    }

    public ArrayList<DataBeacon> copyList(ArrayList<DataBeacon> list){
        ArrayList<DataBeacon> temp = new ArrayList<DataBeacon>();
        for(int i = 0 ; i < list.size(); i++){
            temp.add(list.get(i));
        }
        return temp;
    }






}