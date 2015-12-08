package com.maws.loonandroid.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.maws.loonandroid.R;
import com.maws.loonandroid.adapters.DevicePropertyHistoryListAdapter;
import com.maws.loonandroid.dao.DevicePropertyDao;
import com.maws.loonandroid.models.DeviceProperty;

import java.util.List;

public class HistorySensorActivity extends ActionBarActivity {
    private ListView listView ;
    private long deviceId;
    private DevicePropertyHistoryListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_sensor);
        Intent i = this.getIntent();
        Bundle extras =  i.getExtras();
        deviceId = extras.getLong(MonitorActivity.MONITOR_ID, -1);
        listView = (ListView) findViewById(R.id.listAlarmHistory);
        LinearLayout emptyMessage = (LinearLayout) findViewById(R.id.messageHistoryEmptyRL);
        DevicePropertyHistoryListAdapter devicePropertyHistoryListAdapter = loadInformation(deviceId);
        listView.setAdapter(devicePropertyHistoryListAdapter);
        if( devicePropertyHistoryListAdapter != null && devicePropertyHistoryListAdapter.getCount() > 0){
            listView.setVisibility(View.VISIBLE);
            emptyMessage.setVisibility(View.GONE);
        }else {
            listView.setVisibility(View.GONE);
            emptyMessage.setVisibility(View.VISIBLE);
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setLogo(R.mipmap.ic_launcher);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_action_back_arrow);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

    }

    @Override
    public void onBackPressed() {
        goBackInfo();
        return;
    }

    private void  goBackInfo(){
        Intent returnIntent = new Intent();
        Intent i = this.getIntent();
        Bundle extras =  i.getExtras();
        returnIntent.putExtras(extras);
        this.setResult(RESULT_OK, returnIntent);
        finish();
    }

    private DevicePropertyHistoryListAdapter loadInformation (long deviceId) {
        DevicePropertyDao devicePropertyDao = new DevicePropertyDao(this);
        List<DeviceProperty> listDeviceProperty = devicePropertyDao.getAllByIndex(deviceId);
        if (listDeviceProperty != null && listDeviceProperty.size() > 0 ) {
            adapter= new DevicePropertyHistoryListAdapter(listDeviceProperty,this);
        }
        return adapter;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            goBackInfo();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
