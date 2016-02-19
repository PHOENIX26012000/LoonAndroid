package com.maws.loonandroid.adapters;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.maws.loonandroid.LoonAndroid;
import com.maws.loonandroid.R;
import com.maws.loonandroid.activities.MainActivity;
import com.maws.loonandroid.dao.DeviceDao;
import com.maws.loonandroid.dao.DevicePropertyDao;
import com.maws.loonandroid.fragments.ActivationDialogFragment;
import com.maws.loonandroid.models.Device;
import com.maws.loonandroid.models.DeviceProperty;
import com.maws.loonandroid.models.Property;
import com.maws.loonandroid.services.BLEService;
import com.maws.loonandroid.util.Util;
import com.maws.loonandroid.views.CustomToast;
import java.util.List;

/**
 * Created by Andrexxjc on 12/04/2015.
 */
public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.DeviceViewHolder> {

    private static final String TAG = "DeviceListAdapter";
    private  Context context;
    private final List<Device> items;
    private DeviceViewHolderClickListener listener;

    public static interface DeviceViewHolderClickListener {
        void onClick(Device device);
    }

    public static class DeviceViewHolder extends RecyclerView.ViewHolder {
        AppCompatButton connectBtn, activateBtn;
        TextView nameTV, connectingTV, alertTV, sensorToiletTV, sensorIncontinenceTV, sensorChairTV, sensorBedTV, sensorCallTV, sensorPriTV, sensorFallTV;
        ImageView signalIV, batteryIV;
        LinearLayout cardLL;
        View mainView, loadingPB, divider1, divider2, sensorsLL1, sensorsLL2, careComSensorsLL, statusV;

        public DeviceViewHolder(View v) {
            super(v);
            this.mainView = v;
        }

    }

    public DeviceListAdapter(Context context, List<Device> values, DeviceViewHolderClickListener listener) {
        this.context = context;
        this.items = values;
        this.listener = listener;
    }

    @Override
    public DeviceViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View convertView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.sensor_item, viewGroup, false);
        DeviceViewHolder viewHolder = new DeviceViewHolder(convertView);
        viewHolder.cardLL = (LinearLayout) convertView.findViewById(R.id.cardLL);
        viewHolder.nameTV = (TextView) convertView.findViewById(R.id.nameTV);
        viewHolder.signalIV = (ImageView) convertView.findViewById(R.id.signalIV);
        viewHolder.batteryIV = (ImageView) convertView.findViewById(R.id.batteryIV);
        viewHolder.alertTV = (TextView) convertView.findViewById(R.id.alertTV);
        viewHolder.connectBtn = (AppCompatButton) convertView.findViewById(R.id.connectBtn);
        viewHolder.activateBtn = (AppCompatButton) convertView.findViewById(R.id.activateBtn);
        viewHolder.connectingTV = (TextView) convertView.findViewById(R.id.connectingTV);
        viewHolder.loadingPB = convertView.findViewById(R.id.loadingPB);
        viewHolder.divider1 = convertView.findViewById(R.id.divider1);
        viewHolder.divider2 = convertView.findViewById(R.id.divider2);
        viewHolder.sensorsLL1 = convertView.findViewById(R.id.sensorsLL1);
        viewHolder.sensorsLL2 = convertView.findViewById(R.id.sensorsLL2);
        viewHolder.careComSensorsLL = convertView.findViewById(R.id.careComSensorsLL);
        viewHolder.statusV = convertView.findViewById(R.id.statusV);
        viewHolder.sensorToiletTV = (TextView)convertView.findViewById(R.id.sensorToiletTV);
        viewHolder.sensorIncontinenceTV = (TextView)convertView.findViewById(R.id.sensorIncontinenceTV);
        viewHolder.sensorChairTV = (TextView)convertView.findViewById(R.id.sensorChairTV);
        viewHolder.sensorBedTV = (TextView)convertView.findViewById(R.id.sensorBedTV);
        viewHolder.sensorCallTV = (TextView)convertView.findViewById(R.id.sensorCallTV);
        viewHolder.sensorPriTV = (TextView)convertView.findViewById(R.id.sensorPriTV);
        viewHolder.sensorFallTV = (TextView)convertView.findViewById(R.id.sensorFallButtonTV);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null) {
                    int position = (int) v.getTag();
                    if(items.get(position).isActive()) {
                        listener.onClick(items.get(position));
                    }
                }
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(DeviceViewHolder viewHolder, int position) {

        final Device thisDevice = items.get(position);
        viewHolder.mainView.setTag(position);
        int paintMode = getPaintMode(thisDevice);
        viewHolder.nameTV.setText(TextUtils.isEmpty(thisDevice.getDescription()) ? thisDevice.getName() : thisDevice.getDescription());
        viewHolder.cardLL.setBackgroundResource(R.drawable.white_card);
        viewHolder.divider1.setVisibility(View.VISIBLE);
        viewHolder.divider2.setVisibility(View.VISIBLE);
        viewHolder.sensorsLL1.setVisibility(View.VISIBLE);
        viewHolder.sensorsLL2.setVisibility(View.VISIBLE);
        viewHolder.activateBtn.setVisibility(View.GONE);
        viewHolder.connectBtn.setVisibility(View.GONE);
        viewHolder.signalIV.setVisibility(View.GONE);
        viewHolder.batteryIV.setVisibility(View.GONE);
        viewHolder.loadingPB.setVisibility(View.GONE);
        viewHolder.connectingTV.setVisibility(View.GONE);
        viewHolder.statusV.setVisibility(View.VISIBLE);
        viewHolder.alertTV.setVisibility(View.GONE);
        viewHolder.activateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivationDialogFragment newFragment = ActivationDialogFragment.newInstance(thisDevice, context, new ActivationDialogFragment.ActivationDialogFragmentListener() {
                    @Override
                    public void onSensorAdded(Device device) {
                        device.setActive(true);
                        DeviceDao sDao = new DeviceDao(context);
                        sDao.update(device);
                    }
                });
                newFragment.show(((Activity) context).getFragmentManager(), "dialog");
            }
        });

        switch (paintMode){
            case _MODE_IGNORED:
                viewHolder.cardLL.setBackgroundResource(R.drawable.red_card);
                viewHolder.connectBtn.setVisibility(View.GONE);
                viewHolder.divider1.setVisibility(View.GONE);
                viewHolder.sensorsLL1.setVisibility(View.GONE);
                viewHolder.sensorsLL2.setVisibility(View.GONE);
                viewHolder.careComSensorsLL.setVisibility(View.GONE);
                viewHolder.activateBtn.setVisibility(View.VISIBLE);
                viewHolder.statusV.setVisibility(View.GONE);

                break;
            case _MODE_CONNECTED:
                Util.setUpSignalView(context, viewHolder.signalIV, thisDevice);
                Util.setUpBatteryView(context, viewHolder.batteryIV, thisDevice);
                viewHolder.signalIV.setVisibility(View.VISIBLE);
                viewHolder.batteryIV.setVisibility(View.VISIBLE);
                viewHolder.statusV.setBackgroundResource(R.drawable.circle_green);
                setupSensors( thisDevice.getType(), viewHolder );
                break;
            case _MODE_CONNECTING:
                viewHolder.loadingPB.setVisibility(View.VISIBLE);
                viewHolder.connectingTV.setVisibility(View.VISIBLE);
                viewHolder.statusV.setBackgroundResource(R.drawable.circle_orange);
                setupSensors(thisDevice.getType(), viewHolder);
                break;
            case _MODE_NOT_CONNECTED:
                viewHolder.connectBtn.setVisibility(View.VISIBLE);
                viewHolder.statusV.setBackgroundResource(R.drawable.circle_gray);
                viewHolder.connectBtn.setTag(thisDevice.getMacAddress());
                viewHolder.connectBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

                        // Checks if Bluetooth is supported on the device.
                        if (mBluetoothAdapter == null) {
                            CustomToast.showAlert(context, context.getString(R.string.ble_not_supported), CustomToast._TYPE_ERROR);
                            return;
                        }
                        // Checks if Bluetooth is enabled, or asks to make it enabled.
                        if (!mBluetoothAdapter.isEnabled()) {

                            DeviceDao dDao = new DeviceDao(context);
                            Device device = dDao.findByMacAddress(v.getTag().toString());
                            device.setManualDisconnect(false);
                            dDao.update(device);

                            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            ((Activity)context).startActivityForResult(enableBtIntent, MainActivity.REQUEST_ENABLE_BT);
                            return;
                        }

                        BLEService instance = BLEService.getInstance();
                        if (instance != null) {
                            instance.connect(v.getTag().toString());
                        }
                    }
                });
                setupSensors( thisDevice.getType(), viewHolder );
                break;
        }

        paintProperties(paintMode, viewHolder, thisDevice);

        //i need to look for this item's last alert
        DevicePropertyDao aDao = new DevicePropertyDao(context);
        DeviceProperty dProperty = aDao.getLastAlertForDevice(thisDevice.getId());

        if (dProperty != null && dProperty.getDismissedAt() == null) {

            Property alertProperty = Property.getDefaultProperty(dProperty.getPropertyId(), thisDevice.getType());
            String propertyMessage = dProperty.getValue().equalsIgnoreCase("on")?
                    context.getString(alertProperty.getOnTextId()):
                    context.getString(alertProperty.getOffTextId());

            viewHolder.alertTV.setText(Util.sdf.format(dProperty.getCreatedAt()) + " " + propertyMessage);
            viewHolder.alertTV.setVisibility(View.VISIBLE);
            viewHolder.alertTV.setTag(dProperty.getId());
            viewHolder.alertTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    long id = Long.valueOf(v.getTag().toString());

                    DevicePropertyDao aDao = new DevicePropertyDao(context);
                    DeviceProperty deviceProperty = aDao.get(id);

                    if (deviceProperty != null && deviceProperty.getCreatedAt() != null) {
                        aDao.dismiss(deviceProperty);
                    }
                    v.setVisibility(View.GONE);
                }
            });
        }
    }

    private void setupSensors(int deviceType, DeviceViewHolder viewHolder){
        switch(deviceType){
            case Device._TYPE_MONITOR:
                viewHolder.sensorsLL1.setVisibility(View.VISIBLE);
                viewHolder.sensorsLL2.setVisibility(View.VISIBLE);
                viewHolder.careComSensorsLL.setVisibility(View.GONE);
                break;
            case Device._TYPE_CARECOM:
                viewHolder.sensorsLL1.setVisibility(View.GONE);
                viewHolder.sensorsLL2.setVisibility(View.GONE);
                viewHolder.careComSensorsLL.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void paintProperties(int status, DeviceViewHolder viewHolder, Device device){

        if(status == _MODE_CONNECTED && BLEService.switchValues.containsKey(device.getMacAddress()) ){
            String currentValue = BLEService.switchValues.get(device.getMacAddress());

            //now i need to paint the status of the switches
            switch (device.getType()) {
                case Device._TYPE_MONITOR:
                    for (int i = 0; i < Property.defaultProperties.length; i++) {
                        Property property = Property.defaultProperties[i];
                        boolean isOn = false;
                        if (currentValue.charAt(Integer.valueOf(String.valueOf(property.getId()))) == '1') {
                            isOn = true;
                        }
                        if (property.getName().equalsIgnoreCase("Call")) {
                            isOn = !isOn;
                        }
                        int color = isOn ? ContextCompat.getColor(context, R.color.green) : ContextCompat.getColor(context, R.color.dark_orange);
                        switch (i) {
                            case 0:
                                viewHolder.sensorToiletTV.setTextColor(color);
                                break;
                            case 1:
                                viewHolder.sensorIncontinenceTV.setTextColor(color);
                                break;
                            case 2:
                                viewHolder.sensorChairTV.setTextColor(color);
                                break;
                            case 3:
                                viewHolder.sensorBedTV.setTextColor(color);
                                break;
                            case 4:
                                viewHolder.sensorCallTV.setTextColor(color);
                                break;
                            case 5:
                                viewHolder.sensorPriTV.setTextColor(color);
                                break;
                        }
                    }
                    break;
                case Device._TYPE_CARECOM:
                    for (int i = 0; i < Property.carecomProperties.length; i++) {
                        Property property = Property.carecomProperties[i];
                        boolean isOn = false;
                        if (currentValue.charAt(Integer.valueOf(String.valueOf(property.getId()))) == '1') {
                            isOn = true;
                        }
                        int color = isOn ? ContextCompat.getColor(context, R.color.green) : ContextCompat.getColor(context, R.color.dark_orange);
                        switch (i) {
                            case 0:
                                viewHolder.sensorFallTV.setTextColor(color);
                                break;
                        }
                    }
                    break;
            }
        }else{
            viewHolder.sensorToiletTV.setTextColor( ContextCompat.getColor(context, R.color.hint_gray) );
            viewHolder.sensorIncontinenceTV.setTextColor( ContextCompat.getColor(context, R.color.hint_gray) );
            viewHolder.sensorChairTV.setTextColor( ContextCompat.getColor(context, R.color.hint_gray) );
            viewHolder.sensorBedTV.setTextColor( ContextCompat.getColor(context, R.color.hint_gray) );
            viewHolder.sensorCallTV.setTextColor( ContextCompat.getColor(context, R.color.hint_gray) );
            viewHolder.sensorPriTV.setTextColor( ContextCompat.getColor(context, R.color.hint_gray) );
            viewHolder.sensorFallTV.setTextColor( ContextCompat.getColor(context, R.color.hint_gray) );
        }
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).getId();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public Device getItem(int position) {
        return items.get(position);
    }

    public void remove(int position){
        items.remove(position);
        this.notifyDataSetChanged();
    }

    private final static int _MODE_IGNORED = 0;
    private final static int _MODE_CONNECTED = 1;
    private final static int _MODE_CONNECTING = 2;
    private final static int _MODE_NOT_CONNECTED = 3;

    public int getPaintMode(Device device){
        if(!device.isActive()){
            return _MODE_IGNORED;
        }
        if((device.isActive() && device.isConnected()) || LoonAndroid.demoMode){
            return _MODE_CONNECTED;
        }
        if(device.isActive() && device.isConnecting()){
            return _MODE_CONNECTING;
        }
        return _MODE_NOT_CONNECTED;
    }
}
