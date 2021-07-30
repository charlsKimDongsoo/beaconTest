package com.example.beacontest;

import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.provider.ContactsContract;
import android.service.controls.templates.ControlTemplate;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class beaconAdapter extends BaseAdapter {
    ArrayList<DataBeacon> items = new ArrayList<DataBeacon>();
    Context context;
    LayoutInflater beaconInflater;


    public beaconAdapter(LayoutInflater layoutInflater){
        this.beaconInflater = layoutInflater;
    }

    @Override
    public int getCount() {
        if(items == null) {
            return 0;
        }else{
            return items.size();
        }
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return items.indexOf(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();
        this.context = context;
        DataBeacon item = items.get(position);


        if(convertView == null){
            beaconInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = beaconInflater.inflate(R.layout.list_beacon,parent,false);
        }

        TextView addr = (TextView) convertView.findViewById(R.id.address);
        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView now = (TextView) convertView.findViewById(R.id.now);
        TextView rssi = (TextView) convertView.findViewById(R.id.rssi);
        TextView uuid = (TextView) convertView.findViewById(R.id.uuid);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss.SSS", Locale.KOREAN);

        addr.setText(item.address);
        name.setText(item.name);
        now.setText(simpleDateFormat.format(new Date(item.now)));
        rssi.setText(Integer.toString(item.rssi));
        //uuid.setText(item.uuid);
        //this.notifyDataSetChanged();

        return convertView;
    }


    public void updateItem(ArrayList<DataBeacon> itemset){
        items.clear();
        if(itemset != null) {
            items.addAll(itemset);
        }
        notifyDataSetChanged();
    }

    public void addItem(DataBeacon item){
        if(!items.isEmpty()) {
            for(int i = 0 ; i < items.size() ; i++){
                if(items.get(i).address.equals(item.address)){
                    items.set(i,item);
                    return;
                }
            }
            items.add(item);
        }else{
            items.add(item);
        }

    }
}
