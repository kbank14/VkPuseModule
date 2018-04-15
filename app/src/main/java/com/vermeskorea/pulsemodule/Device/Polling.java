package com.vermeskorea.pulsemodule.Device;

import android.widget.TextView;

import com.vermeskorea.pulsemodule.Data.PulseModuleData;
import com.vermeskorea.pulsemodule.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import at.grabner.circleprogress.CircleProgressView;

/**
 * Created by kbank14 on 2018-01-28.
 */

public class Polling {
    private static Polling _instance = null;
    private CircleProgressView mCPView1 = null;
    private CircleProgressView mCPView2 = null;
    private TextView mText = null;

    public static Polling getInstance() {
        if (_instance == null)
            _instance = new Polling();

        return _instance;
    }

    public Polling() {

    }

    public synchronized void Add(CircleProgressView v1, CircleProgressView v2, TextView v3) {
        mCPView1 = v1;
        mCPView2 = v2;
        mText = v3;

        oldState = "";
    }

    private void setColor(boolean isRun) {
        if( mCPView1 == null || mCPView2 == null)
            return;

        if (isRun) {
            mCPView1.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        mCPView1.setBarColor(mCPView1.getResources().getColor(R.color.red_700));
                        mCPView1.setRimColor(mCPView1.getResources().getColor(R.color.red_100));
                        mCPView1.setSpinBarColor(mCPView1.getResources().getColor(R.color.red_700));
                        mCPView1.setTextColor(mCPView1.getResources().getColor(R.color.red_900));
                        mCPView1.setUnitColor(mCPView1.getResources().getColor(R.color.red_300));

                        mCPView2.setBarColor(mCPView2.getResources().getColor(R.color.red_700));
                        mCPView2.setRimColor(mCPView2.getResources().getColor(R.color.red_100));
                        mCPView2.setSpinBarColor(mCPView2.getResources().getColor(R.color.red_700));
                        mCPView2.setTextColor(mCPView2.getResources().getColor(R.color.red_900));
                        mCPView2.setUnitColor(mCPView2.getResources().getColor(R.color.red_300));
                    } catch (Exception e) {
                        mCPView1 = null;
                        mCPView2 = null;
                    }
                }
            });
        } else {
            mCPView1.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        mCPView1.setBarColor(mCPView1.getResources().getColor(R.color.colorPrimary));
                        mCPView1.setRimColor(mCPView1.getResources().getColor(R.color.colorAccent));
                        mCPView1.setSpinBarColor(mCPView1.getResources().getColor(R.color.colorPrimary));
                        mCPView1.setTextColor(mCPView1.getResources().getColor(R.color.colorPrimaryDark));
                        mCPView1.setUnitColor(mCPView1.getResources().getColor(R.color.colorAccent));

                        mCPView2.setBarColor(mCPView2.getResources().getColor(R.color.colorPrimary));
                        mCPView2.setRimColor(mCPView2.getResources().getColor(R.color.colorAccent));
                        mCPView2.setSpinBarColor(mCPView2.getResources().getColor(R.color.colorPrimary));
                        mCPView2.setTextColor(mCPView2.getResources().getColor(R.color.colorPrimaryDark));
                        mCPView2.setUnitColor(mCPView2.getResources().getColor(R.color.colorAccent));
                    } catch (Exception e) {
                        mCPView1 = null;
                        mCPView2 = null;
                    }
                }
            });
        }
    }

    public synchronized void setValue1(final float v) {
        if (mCPView1 == null)
            return;

        mCPView1.post(new Runnable() {
            @Override
            public void run() {
                try {
                    mCPView1.setValue(v);
                } catch (Exception e) {
                    mCPView1 = null;
                }
            }
        });
    }

    public synchronized void setMaxValue1(final float v) {
        if (mCPView1 == null)
            return;

        mCPView1.post(new Runnable() {
            @Override
            public void run() {
                try {
                    mCPView1.setMaxValue(Float.compare(v, 0) == 0 ? 1 : v);
                } catch (Exception e) {
                    mCPView1 = null;
                }
            }
        });
    }

    public synchronized void setValue2(final String v) {
        if (mCPView2 == null)
            return;

        mCPView2.post(new Runnable() {
            @Override
            public void run() {
                try {
                    mCPView2.setText(v);
                } catch (Exception e) {
                    mCPView2 = null;
                }
            }
        });
    }

    public synchronized void setValue3(final String v) {
        if (mText == null)
            return;

        mText.post(new Runnable() {
            @Override
            public void run() {
                try {
                    mText.setText(v);
                } catch (Exception e) {
                    mText = null;
                }
            }
        });
    }

    String dataFilePath = "/sys/class/vk/vk_file";
    String infoFilePath = "/sys/class/vk/vk_state";

//    int start=1;
//    int mode=0;
//    int cur_pulse=0;
//    int max_pulse=80;
//    int data_load=1;

    public String readDevice(String path) {
//        if( dataFilePath.length() > 5) {
//            cur_pulse++;
//            if( (cur_pulse % max_pulse) == 0 ) {
//                start = start > 0 ? 0 : 1;
//                mode++;
//                mode %= 8;
//                cur_pulse = 0;
//
//                data_load = data_load > 0 ? 0 : 1;
//            }
//            return String.format("start=%d\nmode=%d\ncur pulse=%d\nmax pulse=%d\ndata load=%d\n",
//                    start, mode, cur_pulse, max_pulse, data_load
//            );
//        }
        File f = new File(path);
        if (f == null || f.exists() == false)
            return "";

        StringBuffer output = new StringBuffer();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
            String line = "";
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }

            return output.toString();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }


    private String oldState = "~";

    public boolean setDeviceState(String value) {
        if( mCPView1 == null || mCPView2 == null)
            return false;

        String[] ar = value.split("\n");
        for (int i = 0; i < ar.length; i++) {
            String[] line = ar[i].split("=");
            if (line.length == 2) {
                switch (line[0]) {
                    case "start":
                        setColor(Integer.parseInt(line[1]) != 0);
                        break;

                    case "mode":
                        setValue2(line[1]);
                        break;

                    case "cur pulse":
                        setValue1(Integer.parseInt(line[1]));
                        break;
                    case "max pulse":
                        setMaxValue1(Integer.parseInt(line[1]));
                        break;
                    case "data load":
                        setValue3(Integer.parseInt(line[1]) != 0 ? "Data loaded" : "No data");
                        break;

                    case "Last Pulse":
                        break;
                }
            }
        }

        return true;
    }

    public boolean getDeviceState() {
        if( mCPView1 == null || mCPView2 == null)
            return false;

        String value = readDevice(infoFilePath);
        if (oldState.equals(value) == true)
            return false;

        oldState = value;
        setDeviceState(value);

        return true;
    }
}
