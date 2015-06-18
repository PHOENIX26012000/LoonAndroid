package com.maws.loonandroid.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.maws.loonandroid.contentproviders.AlertContentProvider;
import com.maws.loonandroid.models.Alert;
import com.maws.loonandroid.util.Util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Andrexxjc on 10/05/2015.
 */
public class AlertDao {

    // Contacts table name
    public static final String TABLE_NAME = "tblAlert";

    // Contacts Table Columns names
    public static final String KEY_ID = "_id";
    public static final String KEY_DEVICE_ID = "deviceId";
    public static final String KEY_DEVICE_SERVICE_ID = "deviceServiceId";
    public static final String KEY_ALERT_DATE = "dateInMillis";
    public static final String KEY_IS_ON = "isOn";
    public static final String KEY_DISMISSED = "dismissed";
    public static final String KEY_DISMISSED_DATE = "dismissedDate";
    public static final String KEY_TOTAL_TIME_ALARM = "totalTimeAlarm";
    public static final String KEY_CUSTOMER_ID = "customerID";
    public static final String KEY_SITE_ID = "siteID";

    private Context context;

    public AlertDao(Context context) {
        this.context = context;
    }

    // Creating Tables
    public void onCreate(SQLiteDatabase db) {

        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
                KEY_ID + " INTEGER PRIMARY KEY," +
                KEY_DEVICE_ID + " INT," +
                KEY_DEVICE_SERVICE_ID + " INT," +
                KEY_ALERT_DATE + " INT," +
                KEY_IS_ON + " TINYINT," +
                KEY_DISMISSED + " TINYINT," +
                KEY_DISMISSED_DATE + " INT," +
                KEY_TOTAL_TIME_ALARM + " INT," +
                KEY_CUSTOMER_ID + " INT," +
                KEY_SITE_ID + " INT" + ")";

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
    public void create(Alert alert) {

        ContentValues values = new ContentValues();
        values.put(KEY_DEVICE_ID, alert.getDeviceId());
        values.put(KEY_DEVICE_SERVICE_ID, alert.getDeviceServiceId());
        values.put(KEY_ALERT_DATE, alert.getAlertDate() == null ? null : alert.getAlertDate().getTime());
        values.put(KEY_DISMISSED, alert.isDismissed() ? 1 : 0);
        values.put(KEY_IS_ON, alert.isOn() ? 1 : 0);
        values.put(KEY_DISMISSED_DATE, alert.getDismissedDate() == null ? null : alert.getAlertDate().getTime());
        values.put(KEY_TOTAL_TIME_ALARM, alert.getTotalTimeAlarm());
        values.put(KEY_CUSTOMER_ID, alert.getCostumerId());
        values.put(KEY_SITE_ID, alert.getId());
        context.getContentResolver().insert(AlertContentProvider.CONTENT_URI, values);
    }

    // Getting single object
    public Alert get(long id) {

        Cursor cursor = context.getContentResolver().query(AlertContentProvider.CONTENT_URI,
                new String[]{
                        KEY_ID,
                        KEY_DEVICE_ID,
                        KEY_DEVICE_SERVICE_ID,
                        KEY_ALERT_DATE,
                        KEY_IS_ON,
                        KEY_DISMISSED,
                        KEY_DISMISSED_DATE,
                        KEY_TOTAL_TIME_ALARM,
                        KEY_CUSTOMER_ID,
                        KEY_SITE_ID
                },
                KEY_ID + "=?",
                new String[]{
                        String.valueOf(id)
                },
                null
        );

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
        }else{
            return null;
        }

        Alert alert = new Alert();
        alert.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
        alert.setDeviceId(cursor.getInt(cursor.getColumnIndex(KEY_DEVICE_ID)));
        alert.setDeviceServiceId(cursor.getInt(cursor.getColumnIndex(KEY_DEVICE_SERVICE_ID)));
        alert.setAlertDate(new Date(cursor.getLong(cursor.getColumnIndex(KEY_ALERT_DATE))));
        alert.setDismissed(cursor.getInt(cursor.getColumnIndex(KEY_DISMISSED)) == 1);
        alert.setIsOn(cursor.getInt(cursor.getColumnIndex(KEY_IS_ON)) == 1);
        alert.setDismissedDate( cursor.isNull(cursor.getColumnIndex(KEY_DISMISSED_DATE))?null: new Date(cursor.getLong(cursor.getColumnIndex(KEY_DISMISSED_DATE))));
        alert.setTotalTimeAlarm(cursor.getLong(cursor.getColumnIndex(KEY_TOTAL_TIME_ALARM)));
        alert.setCostumerId(cursor.getLong(cursor.getColumnIndex(KEY_CUSTOMER_ID)));
        alert.setSiteId(cursor.getLong(cursor.getColumnIndex(KEY_SITE_ID)));

        cursor.close();

        // return object
        return alert;
    }

    public List<Alert> getAllForId(long id) {
        List<Alert> listAlerts = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(AlertContentProvider.CONTENT_URI,
                new String[]{
                        KEY_ID,
                        KEY_DEVICE_ID,
                        KEY_DEVICE_SERVICE_ID,
                        KEY_ALERT_DATE,
                        KEY_IS_ON,
                        KEY_DISMISSED,
                        KEY_DISMISSED_DATE,
                        KEY_TOTAL_TIME_ALARM,
                        KEY_CUSTOMER_ID,
                        KEY_SITE_ID
                },
                KEY_DEVICE_ID + "=?" ,
                new String[]{
                        String.valueOf(id)
                },
                null
        );

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();

            do  {
                Alert alert = new Alert();
                alert.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                alert.setDeviceId(cursor.getInt(cursor.getColumnIndex(KEY_DEVICE_ID)));
                alert.setDeviceServiceId(cursor.getInt(cursor.getColumnIndex(KEY_DEVICE_SERVICE_ID)));
                alert.setAlertDate(new Date(cursor.getLong(cursor.getColumnIndex(KEY_ALERT_DATE))));
                alert.setDismissed(cursor.getInt(cursor.getColumnIndex(KEY_DISMISSED)) == 1);
                alert.setIsOn(cursor.getInt(cursor.getColumnIndex(KEY_IS_ON)) == 1);
                alert.setDismissedDate( cursor.isNull(cursor.getColumnIndex(KEY_DISMISSED_DATE))?null: new Date(cursor.getLong(cursor.getColumnIndex(KEY_DISMISSED_DATE))));
                alert.setTotalTimeAlarm(cursor.getLong(cursor.getColumnIndex(KEY_TOTAL_TIME_ALARM)));
                alert.setCostumerId(cursor.getLong(cursor.getColumnIndex(KEY_CUSTOMER_ID)));
                alert.setSiteId(cursor.getLong(cursor.getColumnIndex(KEY_SITE_ID)));
                listAlerts.add(alert);
            } while(cursor.moveToNext());
        }else{
            return null;
        }
        cursor.close();
        // return list object
        return listAlerts;
    }

    // Getting All objects
    public List<Alert> getAll() {

        List<Alert> toReturnList = new ArrayList<Alert>();
        Cursor cursor = context.getContentResolver().query(AlertContentProvider.CONTENT_URI,
                new String[]{
                        KEY_ID,
                        KEY_DEVICE_ID,
                        KEY_DEVICE_SERVICE_ID,
                        KEY_ALERT_DATE,
                        KEY_IS_ON,
                        KEY_DISMISSED,
                        KEY_DISMISSED_DATE,
                        KEY_TOTAL_TIME_ALARM,
                        KEY_CUSTOMER_ID,
                        KEY_SITE_ID
                },
                null,
                null,
                null
        );

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                Alert alert = new Alert();
                alert.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                alert.setDeviceId(cursor.getInt(cursor.getColumnIndex(KEY_DEVICE_ID)));
                alert.setDeviceServiceId(cursor.getInt(cursor.getColumnIndex(KEY_DEVICE_SERVICE_ID)));
                alert.setAlertDate(new Date(cursor.getLong(cursor.getColumnIndex(KEY_ALERT_DATE))));
                alert.setDismissed(cursor.getInt(cursor.getColumnIndex(KEY_DISMISSED)) == 1);
                alert.setIsOn(cursor.getInt(cursor.getColumnIndex(KEY_IS_ON)) == 1);
                alert.setDismissedDate( cursor.isNull(cursor.getColumnIndex(KEY_DISMISSED_DATE))?null: new Date(cursor.getLong(cursor.getColumnIndex(KEY_DISMISSED_DATE))));
                alert.setTotalTimeAlarm( cursor.getLong( cursor.getColumnIndex(KEY_TOTAL_TIME_ALARM)) );
                alert.setCostumerId(cursor.getLong(cursor.getColumnIndex(KEY_CUSTOMER_ID)));
                alert.setSiteId(cursor.getLong(cursor.getColumnIndex(KEY_SITE_ID)));
                toReturnList.add(alert);

            } while (cursor.moveToNext());
        }
        cursor.close();

        // return contact list
        return toReturnList;
    }

    // Updating single object
    public int update(Alert alert) {

        ContentValues values = new ContentValues();
        values.put(KEY_DEVICE_ID, alert.getDeviceId());
        values.put(KEY_DEVICE_SERVICE_ID, alert.getDeviceServiceId());
        values.put(KEY_ALERT_DATE, alert.getAlertDate().getTime());
        values.put(KEY_DISMISSED, alert.isDismissed() ? 1 : 0);
        values.put(KEY_IS_ON, alert.isOn() ? 1 : 0);
        values.put(KEY_DISMISSED_DATE, alert.getDismissedDate().getTime());
        values.put(KEY_TOTAL_TIME_ALARM, alert.getTotalTimeAlarm());
        values.put(KEY_CUSTOMER_ID, alert.getCostumerId());
        values.put(KEY_SITE_ID, alert.getSiteId());

        int toReturn = context.getContentResolver().update(
                AlertContentProvider.CONTENT_URI,
                values,
                KEY_ID + " = ?",
                new String[] { String.valueOf(alert.getId()) }
        );
        return toReturn;
    }

    // Updating single object
    public void dismiss(Alert alert) {

        ContentValues values = new ContentValues();
        long dismissedDate = new Date().getTime();
        values.put(KEY_DISMISSED, 1);
        values.put(KEY_DISMISSED_DATE, dismissedDate);
        long  totalTimeAlarm = Util.subtract2Dates(alert.getAlertDate(),new Date(dismissedDate));
        values.put(KEY_TOTAL_TIME_ALARM, totalTimeAlarm);

        context.getContentResolver().update(
                AlertContentProvider.CONTENT_URI,
                values,
                KEY_ID + " = ?",
                new String[]{String.valueOf(alert.getId())}
        );
    }

    // Deleting single object
    public void delete(Alert alert) {
        context.getContentResolver().delete(
                AlertContentProvider.CONTENT_URI,
                KEY_ID + " = ?",
                new String[] { String.valueOf(alert.getId()) }
        );
    }

    public void deleteAll(SQLiteDatabase db){
        db.delete(TABLE_NAME, null, null);
    }

    public Cursor getUndismissedAlertInfo(SQLiteDatabase db, long deviceId){

        final SQLiteQueryBuilder todayShiftQueryBuilder = new SQLiteQueryBuilder();
        todayShiftQueryBuilder.setTables(AlertDao.TABLE_NAME
                        + " JOIN " + DeviceDao.TABLE_NAME+ " ON(" + AlertDao.TABLE_NAME + "." + AlertDao.KEY_DEVICE_ID
                        + "=" + DeviceDao.TABLE_NAME + "." + DeviceDao.KEY_ID + ")"
        );
        String[] projection = {
                AlertDao.TABLE_NAME + "." + AlertDao.KEY_ID,
                AlertDao.TABLE_NAME + "." + AlertDao.KEY_ALERT_DATE,
                AlertDao.TABLE_NAME + "." + AlertDao.KEY_DEVICE_SERVICE_ID,
                AlertDao.TABLE_NAME + "." + AlertDao.KEY_DISMISSED,
                AlertDao.TABLE_NAME + "." + AlertDao.KEY_IS_ON,
                AlertDao.TABLE_NAME + "." + AlertDao.KEY_DEVICE_ID,
                AlertDao.TABLE_NAME + "." + AlertDao.KEY_DISMISSED_DATE,
                AlertDao.TABLE_NAME + "." + AlertDao.KEY_TOTAL_TIME_ALARM,
                AlertDao.TABLE_NAME + "." + AlertDao.KEY_CUSTOMER_ID,
                AlertDao.TABLE_NAME + "." + AlertDao.KEY_SITE_ID,
                DeviceDao.TABLE_NAME + "." + DeviceDao.KEY_NAME +  " AS Device",
                DeviceDao.TABLE_NAME + "." + DeviceDao.KEY_CODE +  " AS DeviceCode",
                DeviceDao.TABLE_NAME + "." + DeviceDao.KEY_DESCRIPTION + " AS DeviceDescription"
        };
        String selection = AlertDao.TABLE_NAME + "." + AlertDao.KEY_DISMISSED + "=? AND " +
                DeviceDao.TABLE_NAME + "." + DeviceDao.KEY_ID + "=?";
        String[] selectArgs = {"0", String.valueOf(deviceId)};

        Cursor cursor = todayShiftQueryBuilder.query( db, projection, selection, selectArgs, null, null, null );
        return cursor;
    }

}
