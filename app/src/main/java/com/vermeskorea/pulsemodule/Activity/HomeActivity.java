package com.vermeskorea.pulsemodule.Activity;

import android.content.DialogInterface;
import android.content.Intent;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.vermeskorea.pulsemodule.Adapter.SettingPagerAdapter;
import com.vermeskorea.pulsemodule.Data.PulseModuleData;
import com.vermeskorea.pulsemodule.R;
import com.vermeskorea.pulsemodule.SlidingTabLayout;

public class HomeActivity extends AppCompatActivity {
    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;
    private SettingPagerAdapter mSettingPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
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
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setAdapter(mSettingPagerAdapter);

        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    private void alertMessage(String title, String msg)
    {
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
        int mode = mViewPager.getCurrentItem() - 1;
        switch (id)
        {
            case R.id.action_load:
                PulseModuleData.getInstance().loadFile();
                alertMessage("Load Data", "Pulse data has been successfully loaded.");
                updateScreen();
                return true;

            case R.id.action_save:
                PulseModuleData.getInstance().saveFile();
                alertMessage("Save Data", "Pulse data was saved.");
                updateScreen();
                return true;

            case R.id.action_add:
                if( mode < 0)
                    return true;
                PulseModuleData.getInstance().add(mode, new PulseModuleData.PulseParamInfo());
                updateScreen();
                return true;

            case R.id.action_delete:
                if( mode < 0)
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

    private void updateScreen()
    {
        if( mSettingPagerAdapter != null) {
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
}
