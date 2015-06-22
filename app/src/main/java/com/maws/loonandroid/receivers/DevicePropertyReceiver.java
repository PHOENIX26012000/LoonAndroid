package com.maws.loonandroid.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import com.maws.loonandroid.R;
import com.maws.loonandroid.dao.DevicePropertyDao;
import com.maws.loonandroid.dao.DeviceDao;
import com.maws.loonandroid.models.Customer;
import com.maws.loonandroid.models.Device;
import com.maws.loonandroid.models.DeviceProperty;
import com.maws.loonandroid.models.Property;
import com.maws.loonandroid.models.Site;
import com.maws.loonandroid.util.Util;
import java.util.Date;

/**
 * Created by Andrexxjc on 19/05/2015.
 */
public class DevicePropertyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        long deviceId = intent.getLongExtra("deviceId", 0);
        long propertyId = intent.getIntExtra("propertyId", -1);
        String value = intent.getStringExtra("value");
        long alarmDateMillis = intent.getLongExtra("dateMillis", 0);

        DeviceDao sDao = new DeviceDao(context);
        Device device = sDao.get(deviceId);

        if( deviceId > 0 && propertyId > -1 && alarmDateMillis >= 0 && device != null ) {

            //when i receive an alarm, i want to do 2 things: Save it to the db and show a notification

            //let's save it to db
            DeviceProperty dProperty = new DeviceProperty();
            dProperty.setDeviceId(deviceId);
            dProperty.setPropertyId(propertyId);
            dProperty.setValue(value);
            dProperty.setCreatedAt(new Date(alarmDateMillis));
            dProperty.setCostumerId(Customer.getCurrent(context).getId());
            dProperty.setSiteId(Site.getCurrent(context).getId());

            DevicePropertyDao aDao = new DevicePropertyDao(context);
            aDao.create(dProperty);

            //let's show the notification
            String title = TextUtils.isEmpty(device.getDescription())? device.getName() : device.getDescription();
            String message = String.format(
                    context.getString(R.string.push_notification_message_alert),
                    context.getString(Property.getDefaultProperty(propertyId).getDisplayId()),
                    value
            );
            Util.generateNotification(context, title, message);
        }
    }

}