package com.example.beacontest;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.ScanResult;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {
    public static BeaconService beaconService;

    BluetoothAdapter mBluetoothAdapter;

    boolean isScan_ = false;
    ListView listView;
    beaconAdapter adapter;

    private static final int SINGLE_PERMISSION = 1004; //권한 변수
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //권한이 있는지 확인
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {//권한없음
             ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.BLUETOOTH_ADMIN,Manifest.permission.ACCESS_FINE_LOCATION}, SINGLE_PERMISSION);


        }
        listView = findViewById(R.id.beaconlist);
        adapter = new beaconAdapter(getLayoutInflater());
        listView.setAdapter(adapter);

    }

    public void stop_scan(View v){
        beaconService.stopScanning();
    }


    public void start_scan(View v){
        beaconService = new BeaconService(this, fHandler);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        boolean checkEnable;
        checkEnable = checkEnableBluetooth();     //블루투스 상태 확인 함수
        if (checkEnable == true) {       //블루투스가 켜져있으면
            Log.d("비콘 시작", "시작");
            beaconService.startScanning();
            isScan_ = true;
        }


        if (mBluetoothAdapter == null) {
            //Toast.makeText(this, getString(R.string.this_android_smartphone_does_not_support_bluetooth_function), Toast.LENGTH_SHORT).show();
            Log.d("여기서안", "됨안");
            finish();
            return;
        }
    }


    public boolean checkEnableBluetooth() {
        //블루투스 활성화 코드
        if (mBluetoothAdapter.isEnabled()) {
            Log.d("BlueTooth TAG", "bluetooth is enabled");
            return true;
        } else {
            Log.d("BlueTooth TAG", "bluetooth is disabled");
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, 2);
            return false;
        }
    }


    //권한 요청에 대한 결과 처리
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case SINGLE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    /*권한이 있는경우 실행할 코드....*/
                } else {
                    // 하나라도 거부한다면.
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                    alertDialog.setTitle("앱 권한");
                    alertDialog.setMessage("해당 앱의 원할한 기능을 이용하시려면 애플리케이션 정보>권한> 에서 모든 권한을 허용해 주십시오");
                    // 권한설정 클릭시 이벤트 발생
                    alertDialog.setPositiveButton("권한설정",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                                    startActivity(intent);
                                    dialog.cancel();
                                }
                            });
                    //취소
                    alertDialog.setNegativeButton("취소",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    alertDialog.show();
                }
                return;
        }

    }

    @SuppressLint("HandlerLeak")
    Handler fHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1009:
                    ArrayList<DataBeacon> newData = (ArrayList<DataBeacon>) msg.obj;
                    Log.d("beaconnum", " " + newData.size());
                    adapter.updateItem(newData);
                    adapter.notifyDataSetChanged();

//                    DataBeacon newData = (DataBeacon) msg.obj;
//                    adapter.addItem(newData);
//                    adapter.notifyDataSetChanged();
                    break;

            }

        }
    };


}