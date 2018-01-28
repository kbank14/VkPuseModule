package com.vermeskorea.pulsemodule.Activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.vermeskorea.pulsemodule.Adapter.SettingPagerAdapter;
import com.vermeskorea.pulsemodule.Data.PulseModuleData;
import com.vermeskorea.pulsemodule.Device.Polling;
import com.vermeskorea.pulsemodule.Pager.SwipeViewPager;
import com.vermeskorea.pulsemodule.R;
import com.vermeskorea.pulsemodule.SlidingTabLayout;

import static android.content.ContentValues.TAG;

public class HomeActivity extends AppCompatActivity {
    private SlidingTabLayout mSlidingTabLayout;
    private SwipeViewPager mViewPager;
    private SettingPagerAdapter mSettingPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        fab.setVisibility(View.GONE);

        mSettingPagerAdapter = new SettingPagerAdapter(this);
        mViewPager = (SwipeViewPager) findViewById(R.id.viewpager);
        mViewPager.setAdapter(mSettingPagerAdapter);

        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);

        checkPermission();
        if (PulseModuleData.getInstance().loadFile() == true)
            PulseModuleData.getInstance().setFile();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    private void alertMessage(String title, String msg) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle(title)
                .setMessage(msg)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        int mode = mViewPager.getCurrentItem() - 2;
        switch (id) {
            case R.id.action_load:
                checkPermission();

                if (PulseModuleData.getInstance().loadFile())
                    alertMessage("Load Data", "Pulse data has been successfully loaded.");
                else
                    alertMessage("Load Data", "Pulse data can't loaded.");
                updateScreen();
                return true;

            case R.id.action_save:
                checkPermission();

                if (PulseModuleData.getInstance().saveFile())
                    alertMessage("Save Data", "Pulse data was saved.");
                else
                    alertMessage("Save Data", "Pulse data can't saved.");
                updateScreen();
                return true;

            case R.id.action_add:
                if (mode < 0)
                    return true;
                PulseModuleData.getInstance().add(mode, new PulseModuleData.PulseParamInfo());
                updateScreen();
                return true;

            case R.id.action_delete:
                if (mode < 0)
                    return true;

                PulseModuleData.getInstance().delete(mode);
                updateScreen();
                return true;

            case R.id.action_settings:
                goApps();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    private void updateScreen() {
        if (mSettingPagerAdapter != null) {
            mSettingPagerAdapter.notifyDataSetChanged();
            mSettingPagerAdapter.updateScreen();
        }

        mViewPager.invalidate();
        mSlidingTabLayout.invalidate();
    }

    public void goApps() {
        Intent i = new Intent(this, AppListActivity.class);

        startActivity(i);
    }

    private final int MY_PERMISSION_REQUEST_STORAGE = 100;

    /**
     * Permission check.
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermission() {
        Log.i(TAG, "CheckPermission : " + checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE));
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Explain to the user why we need to write the permission.
                Toast.makeText(this, "Read/Write external storage", Toast.LENGTH_SHORT).show();
            }

            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSION_REQUEST_STORAGE);

            // MY_PERMISSION_REQUEST_STORAGE is an
            // app-defined int constant

        } else {
            Log.e(TAG, "permission deny");
            //writeFile();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    //writeFile();

                    // permission was granted, yay! do the
                    // calendar task you need to do.

                } else {

                    Log.d(TAG, "Permission always deny");

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                break;
        }
    }

    private boolean _isPolling = false;
    @Override
    protected void onResume() {
        super.onResume();

        _isPolling = true;
        Polling();
    }

    @Override
    protected void onPause() {
        super.onPause();
        _isPolling = false;
    }

    private void Polling()
    {
        if( _isPolling == false)
            return;

        if(Polling.getInstance().getDeviceState())
        {
            if( mViewPager.getCurrentItem() != 0 )
                 mViewPager.setCurrentItem(0);
        }
        mViewPager.postDelayed(new Runnable() {
            @Override
            public void run() {
                Polling();
            }
        }, 100);
    }
}
