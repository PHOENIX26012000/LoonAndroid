package com.maws.loonandroid.dao;

import java.util.ArrayList;
import java.util.List;

import com.maws.loonandroid.contentproviders.DeviceContentProvider;
import com.maws.loonandroid.models.Device;
import com.maws.loonandroid.services.BLEService;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Created by Andrexxjc on 07/05/2015.
 */
public class DeviceDao {

    // Contacts table name
    public static final String TABLE_NAME = "tblDevice";

    // Contacts Table Columns names
    public static final String KEY_ID = "_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_CODE = "code";
    public static final String KEY_FIRMWARE_VERSION = "firmwareVersion";
    public static final String KEY_HARDWARE_VERSION = "hardwareVersion";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_MAC_ADDRESS = "mac";
    public static final String KEY_ACTIVE = "active";
    public static final String KEY_CONNECTED = "connected";
    public static final String KEY_HARDWARE_ID = "hw_id";
    public static final String KEY_BATTERY_STATUS = "battery";
    public static final String KEY_TEMPERATURE = "temperature";
    public static final String KEY_SIGNAL = "rssi";
    public static final String KEY_MANUAL_DISCONNECT = "manualDisconnect";
    public static final String KEY_CONNECTING = "connecting";


    private Context context;

    public DeviceDao(Context context) {
        this.context = context;
    }

    // Creating Tables
    public void onCreate(SQLiteDatabase db) {

        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
                KEY_ID + " INTEGER PRIMARY KEY," +
                KEY_NAME + " TEXT," +
                KEY_CODE + " TEXT," +
                KEY_FIRMWARE_VERSION + " TEXT," +
                KEY_HARDWARE_VERSION + " TEXT," +
                KEY_DESCRIPTION + " TEXT," +
                KEY_ACTIVE + " TINYINT," +
                KEY_MAC_ADDRESS + " TEXT," +
                KEY_CONNECTED + " TINYINT," +
                KEY_CONNECTING + " TINYINT," +
                KEY_MANUAL_DISCONNECT + " TINYINT," +
                KEY_BATTERY_STATUS + " REAL," +
                KEY_TEMPERATURE + " REAL," +
                KEY_SIGNAL + " INTEGER," +
                KEY_HARDWARE_ID + " TEXT" + ")";
        db.execSQL(CREATE_TABLE);

    }

    // Upgrading database
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        // Create tables again
        onCreate(db);
    }

    // Adding new object
    public void create(Device device) {

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, device.getName());
        values.put(KEY_CODE, device.getCode());
        values.put(KEY_FIRMWARE_VERSION, device.getFirmwareVersion());
        values.put(KEY_HARDWARE_VERSION, device.getHardwareVersion());
        values.put(KEY_DESCRIPTION, device.getDescription());
        values.put(KEY_MAC_ADDRESS, device.getMacAddress());
        values.put(KEY_ACTIVE, device.isActive()?1:0);
        values.put(KEY_CONNECTED, device.isConnected() ? 1 : 0);
        values.put(KEY_CONNECTING, device.isConnecting() ? 1 : 0);
        values.put(KEY_MANUAL_DISCONNECT, device.isManualDisconnect() ? 1 : 0);
        values.put(KEY_HARDWARE_ID, device.getHardwareId());

        Uri uri = context.getContentResolver().insert(DeviceContentProvider.CONTENT_URI, values);
        long deviceId = Long.valueOf(uri.getLastPathSegment());
        device.setId(deviceId);

        try {
            BLEService.getInstance().connect(device.getMacAddress());
        }catch(Exception ex){
            //ignore this. If we can't connect, we'll do it manually.
        }

    }

    // Getting single object
    public Device get(long id) {

        Cursor cursor = context.getContentResolver().query(DeviceContentProvider.CONTENT_URI,
                new String[]{
                        KEY_ID,
                        KEY_NAME,
                        KEY_CODE,
                        KEY_FIRMWARE_VERSION,
                        KEY_HARDWARE_VERSION,
                        KEY_DESCRIPTION,
                        KEY_MAC_ADDRESS,
                        KEY_ACTIVE,
                        KEY_CONNECTED,
                        KEY_HARDWARE_ID,
                        KEY_TEMPERATURE,
                        KEY_BATTERY_STATUS,
                        KEY_SIGNAL,
                        KEY_CONNECTING,
                        KEY_MANUAL_DISCONNECT
                },
                KEY_ID + "=?",
                new String[]{
                        String.valueOf(id)
                },
                null
        );

        if (cursor != null  && cursor.getCount() > 0) {
            cursor.moveToFirst();
        }else{
            return null;
        }

        Device device = new Device();
        device.setId(cursor.getLong(cursor.getColumnIndex(KEY_ID)));
        device.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
        device.setCode(cursor.getString(cursor.getColumnIndex(KEY_CODE)));
        device.setFirmwareVersion(cursor.getString(cursor.getColumnIndex(KEY_FIRMWARE_VERSION)));
        device.setHardwareVersion(cursor.getString(cursor.getColumnIndex(KEY_HARDWARE_VERSION)));
        device.setDescription(cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)));
        device.setActive(cursor.getInt(cursor.getColumnIndex(KEY_ACTIVE)) == 1);
        device.setMacAddress(cursor.getString(cursor.getColumnIndex(KEY_MAC_ADDRESS)));
        device.setConnected(cursor.getInt(cursor.getColumnIndex(KEY_CONNECTED)) == 1);
        device.setHardwareId(cursor.getString(cursor.getColumnIndex(KEY_HARDWARE_ID)));
        device.setTemperature(cursor.getDouble(cursor.getColumnIndex(KEY_TEMPERATURE)));
        device.setBatteryStatus(cursor.getInt(cursor.getColumnIndex(KEY_BATTERY_STATUS)));
        device.setSignalStrength(cursor.getInt(cursor.getColumnIndex(KEY_SIGNAL)));
        device.setConnecting(cursor.getInt(cursor.getColumnIndex(KEY_CONNECTING)) == 1);
        device.setManualDisconnect(cursor.getInt(cursor.getColumnIndex(KEY_MANUAL_DISCONNECT)) == 1);
        cursor.close();

        // return object
        return device;
    }

    // Getting single object
    public Device findByMacAddress(String macAddress) {

        Cursor cursor = context.getContentResolver().query(DeviceContentProvider.CONTENT_URI,
            new String[]{
                KEY_ID,
                KEY_NAME,
                KEY_CODE,
                KEY_FIRMWARE_VERSION,
                KEY_HARDWARE_VERSION,
                KEY_DESCRIPTION,
                KEY_MAC_ADDRESS,
                KEY_ACTIVE,
                KEY_CONNECTED,
                KEY_HARDWARE_ID,
                KEY_TEMPERATURE,
                KEY_BATTERY_STATUS,
                KEY_SIGNAL,
                KEY_CONNECTING,
                KEY_MANUAL_DISCONNECT
            },
            KEY_MAC_ADDRESS + "=?",
                new String[]{
                        macAddress
            },
            null
        );

        if (cursor != null && cursor.getCount() > 0 ) {
            cursor.moveToFirst();
        }else{
            return null;
        }

        Device device = new Device();
        device.setId(cursor.getLong(cursor.getColumnIndex(KEY_ID)));
        device.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
        device.setCode(cursor.getString(cursor.getColumnIndex(KEY_CODE)));
        device.setFirmwareVersion(cursor.getString(cursor.getColumnIndex(KEY_FIRMWARE_VERSION)));
        device.setHardwareVersion(cursor.getString(cursor.getColumnIndex(KEY_HARDWARE_VERSION)));
        device.setDescription(cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)));
        device.setActive(cursor.getInt(cursor.getColumnIndex(KEY_ACTIVE)) == 1);
        device.setMacAddress(cursor.getString(cursor.getColumnIndex(KEY_MAC_ADDRESS)));
        device.setConnected(cursor.getInt(cursor.getColumnIndex(KEY_CONNECTED)) == 1);
        device.setHardwareId(cursor.getString(cursor.getColumnIndex(KEY_HARDWARE_ID)));
        device.setTemperature(cursor.getDouble(cursor.getColumnIndex(KEY_TEMPERATURE)));
        device.setBatteryStatus(cursor.getInt(cursor.getColumnIndex(KEY_BATTERY_STATUS)));
        device.setSignalStrength(cursor.getInt(cursor.getColumnIndex(KEY_SIGNAL)));
        device.setConnecting(cursor.getInt(cursor.getColumnIndex(KEY_CONNECTING)) == 1);
        device.setManualDisconnect(cursor.getInt(cursor.getColumnIndex(KEY_MANUAL_DISCONNECT)) == 1);
        cursor.close();

        // return object
        return device;
    }

    // Getting All objects
    public List<Device> getAll() {

        List<Device> toReturnList = new ArrayList<Device>();
        // Select All Query
        Cursor cursor = context.getContentResolver().query(DeviceContentProvider.CONTENT_URI,
                new String[]{
                        KEY_ID,
                        KEY_NAME,
                        KEY_CODE,
                        KEY_FIRMWARE_VERSION,
                        KEY_HARDWARE_VERSION,
                        KEY_DESCRIPTION,
                        KEY_MAC_ADDRESS,
                        KEY_ACTIVE,
                        KEY_CONNECTED,
                        KEY_HARDWARE_ID,
                        KEY_TEMPERATURE,
                        KEY_BATTERY_STATUS,
                        KEY_SIGNAL,
                        KEY_CONNECTING,
                        KEY_MANUAL_DISCONNECT
                },
                null,
                null,
                KEY_ACTIVE + " DESC, " + KEY_ID + " DESC"
        );

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                Device device = new Device();
                device.setId(cursor.getLong(cursor.getColumnIndex(KEY_ID)));
                device.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
                device.setCode(cursor.getString(cursor.getColumnIndex(KEY_CODE)));
                device.setFirmwareVersion(cursor.getString(cursor.getColumnIndex(KEY_FIRMWARE_VERSION)));
                device.setHardwareVersion(cursor.getString(cursor.getColumnIndex(KEY_HARDWARE_VERSION)));
                device.setDescription(cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)));
                device.setActive(cursor.getInt(cursor.getColumnIndex(KEY_ACTIVE)) == 1);
                device.setMacAddress(cursor.getString(cursor.getColumnIndex(KEY_MAC_ADDRESS)));
                device.setConnected(cursor.getInt(cursor.getColumnIndex(KEY_CONNECTED)) == 1);
                device.setHardwareId(cursor.getString(cursor.getColumnIndex(KEY_HARDWARE_ID)));
                device.setTemperature(cursor.getDouble(cursor.getColumnIndex(KEY_TEMPERATURE)));
                device.setBatteryStatus(cursor.getInt(cursor.getColumnIndex(KEY_BATTERY_STATUS)));
                device.setSignalStrength(cursor.getInt(cursor.getColumnIndex(KEY_SIGNAL)));
                device.setConnecting(cursor.getInt(cursor.getColumnIndex(KEY_CONNECTING)) == 1);
                device.setManualDisconnect(cursor.getInt(cursor.getColumnIndex(KEY_MANUAL_DISCONNECT)) == 1);
                toReturnList.add(device);

            } while (cursor.moveToNext());
        }
        cursor.close();

        // return contact list
        return toReturnList;
    }

    // Getting All objects
    public List<Device> getAllActive() {

        List<Device> toReturnList = new ArrayList<Device>();
        // Select All Query
        Cursor cursor = context.getContentResolver().query(DeviceContentProvider.CONTENT_URI,
                new String[]{
                        KEY_ID,
                        KEY_NAME,
                        KEY_CODE,
                        KEY_FIRMWARE_VERSION,
                        KEY_HARDWARE_VERSION,
                        KEY_DESCRIPTION,
                        KEY_MAC_ADDRESS,
                        KEY_ACTIVE,
                        KEY_CONNECTED,
                        KEY_HARDWARE_ID,
                        KEY_TEMPERATURE,
                        KEY_BATTERY_STATUS,
                        KEY_SIGNAL,
                        KEY_CONNECTING,
                        KEY_MANUAL_DISCONNECT
                },
                KEY_ACTIVE + "=?",
                new String[]{
                        "1"
                },
                KEY_ID + " ASC"
        );

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                Device device = new Device();
                device.setId(cursor.getLong(cursor.getColumnIndex(KEY_ID)));
                device.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
                device.setCode(cursor.getString(cursor.getColumnIndex(KEY_CODE)));
                device.setFirmwareVersion(cursor.getString(cursor.getColumnIndex(KEY_FIRMWARE_VERSION)));
                device.setHardwareVersion(cursor.getString(cursor.getColumnIndex(KEY_HARDWARE_VERSION)));
                device.setDescription(cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)));
                device.setActive(cursor.getInt(cursor.getColumnIndex(KEY_ACTIVE)) == 1);
                device.setMacAddress(cursor.getString(cursor.getColumnIndex(KEY_MAC_ADDRESS)));
                device.setConnected(cursor.getInt(cursor.getColumnIndex(KEY_CONNECTED)) == 1);
                device.setHardwareId(cursor.getString(cursor.getColumnIndex(KEY_HARDWARE_ID)));
                device.setTemperature(cursor.getDouble(cursor.getColumnIndex(KEY_TEMPERATURE)));
                device.setBatteryStatus(cursor.getInt(cursor.getColumnIndex(KEY_BATTERY_STATUS)));
                device.setSignalStrength(cursor.getInt(cursor.getColumnIndex(KEY_SIGNAL)));
                device.setConnecting(cursor.getInt(cursor.getColumnIndex(KEY_CONNECTING)) == 1);
                device.setManualDisconnect(cursor.getInt(cursor.getColumnIndex(KEY_MANUAL_DISCONNECT)) == 1);
                toReturnList.add(device);

            } while (cursor.moveToNext());
        }
        cursor.close();

        // return contact list
        return toReturnList;
    }

    // Getting All objects
    public List<Device> getAllInactive() {

        List<Device> toReturnList = new ArrayList<Device>();
        // Select All Query
        Cursor cursor = context.getContentResolver().query(DeviceContentProvider.CONTENT_URI,
                new String[]{
                        KEY_ID,
                        KEY_NAME,
                        KEY_CODE,
                        KEY_FIRMWARE_VERSION,
                        KEY_HARDWARE_VERSION,
                        KEY_DESCRIPTION,
                        KEY_MAC_ADDRESS,
                        KEY_ACTIVE,
                        KEY_CONNECTED,
                        KEY_HARDWARE_ID,
                        KEY_TEMPERATURE,
                        KEY_BATTERY_STATUS,
                        KEY_SIGNAL,
                        KEY_CONNECTING,
                        KEY_MANUAL_DISCONNECT
                },
                KEY_ACTIVE + "=?",
                new String[]{
                        "0"
                },
                KEY_ID + " ASC"
        );

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                Device device = new Device();
                device.setId(cursor.getLong(cursor.getColumnIndex(KEY_ID)));
                device.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
                device.setCode(cursor.getString(cursor.getColumnIndex(KEY_CODE)));
                device.setFirmwareVersion(cursor.getString(cursor.getColumnIndex(KEY_FIRMWARE_VERSION)));
                device.setHardwareVersion(cursor.getString(cursor.getColumnIndex(KEY_HARDWARE_VERSION)));
                device.setDescription(cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)));
                device.setActive(cursor.getInt(cursor.getColumnIndex(KEY_ACTIVE)) == 1);
                device.setMacAddress(cursor.getString(cursor.getColumnIndex(KEY_MAC_ADDRESS)));
                device.setConnected(cursor.getInt(cursor.getColumnIndex(KEY_CONNECTED)) == 1);
                device.setHardwareId(cursor.getString(cursor.getColumnIndex(KEY_HARDWARE_ID)));
                device.setTemperature(cursor.getDouble(cursor.getColumnIndex(KEY_TEMPERATURE)));
                device.setBatteryStatus(cursor.getInt(cursor.getColumnIndex(KEY_BATTERY_STATUS)));
                device.setSignalStrength(cursor.getInt(cursor.getColumnIndex(KEY_SIGNAL)));
                device.setConnecting(cursor.getInt(cursor.getColumnIndex(KEY_CONNECTING)) == 1);
                device.setManualDisconnect(cursor.getInt(cursor.getColumnIndex(KEY_MANUAL_DISCONNECT)) == 1);
                toReturnList.add(device);

            } while (cursor.moveToNext());
        }
        cursor.close();

        // return contact list
        return toReturnList;
    }

    // Updating single object
    public long update(Device device) {

        //Don't update firmwareVersion, hardwareVersion or hardwareId because they're updated async and we may delete them
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, device.getName());
        values.put(KEY_CODE, device.getCode());
        values.put(KEY_DESCRIPTION, device.getDescription());
        values.put(KEY_MAC_ADDRESS, device.getMacAddress());
        values.put(KEY_ACTIVE, device.isActive() ? 1 : 0);
        values.put(KEY_CONNECTED, device.isConnected() ? 1 : 0);
        values.put(KEY_CONNECTING, device.isConnecting() ? 1 : 0);
        values.put(KEY_MANUAL_DISCONNECT, device.isManualDisconnect() ? 1 : 0);

        context.getContentResolver().update(DeviceContentProvider.CONTENT_URI,
                values,
                KEY_ID + "=?",
                new String[]{
                        String.valueOf(device.getId())
                }
        );
        return device.getId();
    }

    // Updating single property so we don't delete it with the async calls.
    public long updateFirmwareVersion(Device device) {
        ContentValues values = new ContentValues();
        values.put(KEY_FIRMWARE_VERSION, device.getFirmwareVersion());
        context.getContentResolver().update(DeviceContentProvider.CONTENT_URI,
                values,
                KEY_ID + "=?",
                new String[]{
                        String.valueOf(device.getId())
                }
        );
        return device.getId();
    }

    // Updating single property so we don't delete it with the async calls.
    public long updateHardwareVersion(Device device) {
        ContentValues values = new ContentValues();
        values.put(KEY_HARDWARE_VERSION, device.getHardwareVersion());
        context.getContentResolver().update(DeviceContentProvider.CONTENT_URI,
                values,
                KEY_ID + "=?",
                new String[]{
                        String.valueOf(device.getId())
                }
        );
        return device.getId();
    }

    // Updating single property so we don't delete it with the async calls.
    public long updateHardwareId(Device device) {
        ContentValues values = new ContentValues();
        values.put(KEY_HARDWARE_ID, device.getHardwareId());
        context.getContentResolver().update(DeviceContentProvider.CONTENT_URI,
                values,
                KEY_ID + "=?",
                new String[]{
                        String.valueOf(device.getId())
                }
        );
        return device.getId();
    }

    // Updating single property so we don't delete it with the async calls.
    public long updateBatteryStatus(Device device) {
        ContentValues values = new ContentValues();
        values.put(KEY_BATTERY_STATUS, device.getBatteryStatus());
        context.getContentResolver().update(DeviceContentProvider.CONTENT_URI,
                values,
                KEY_ID + "=?",
                new String[]{
                        String.valueOf(device.getId())
                }
        );
        return device.getId();
    }

    // Updating single property so we don't delete it with the async calls.
    public long updateTemperature(Device device) {
        ContentValues values = new ContentValues();
        values.put(KEY_TEMPERATURE, device.getTemperature());
        context.getContentResolver().update(DeviceContentProvider.CONTENT_URI,
                values,
                KEY_ID + "=?",
                new String[]{
                        String.valueOf(device.getId())
                }
        );
        return device.getId();
    }

    // Updating single property so we don't delete it with the async calls.
    public long updateSignal(Device device) {
        ContentValues values = new ContentValues();
        values.put(KEY_SIGNAL, device.getSignalStrength());
        context.getContentResolver().update(DeviceContentProvider.CONTENT_URI,
                values,
                KEY_ID + "=?",
                new String[]{
                        String.valueOf(device.getId())
                }
        );
        return device.getId();
    }

    // Updating single object
    public void disconnectAllDevices() {

        ContentValues values = new ContentValues();
        values.put(KEY_CONNECTED, 0);
        values.put(KEY_CONNECTING, 0);
        context.getContentResolver().update(DeviceContentProvider.CONTENT_URI,
                values,
                null,
                null
        );
    }



    // Deleting single object
    public void delete(Device device) {
        context.getContentResolver().delete(DeviceContentProvider.CONTENT_URI,
                KEY_ID + "=?",
                new String[]{
                        String.valueOf(device.getId())
                }
        );
    }

    public void deleteAll(SQLiteDatabase db){
        context.getContentResolver().delete(DeviceContentProvider.CONTENT_URI,
                null,
                null
        );
        db.delete(DeviceCharacteristicDao.TABLE_NAME, null, null);
        db.delete(DevicePropertyDao.TABLE_NAME, null, null);
        db.delete(DeviceEnabledPropertyDao.TABLE_NAME, null, null);

    }
}

