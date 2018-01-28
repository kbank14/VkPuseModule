package com.vermeskorea.pulsemodule.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.vermeskorea.pulsemodule.Data.PulseModuleData;
import com.vermeskorea.pulsemodule.Device.Polling;
import com.vermeskorea.pulsemodule.R;

import java.util.List;

import at.grabner.circleprogress.CircleProgressView;
import at.grabner.circleprogress.TextMode;

/**
 * Created by kbank14 on 2018-01-07.
 */

public class SettingPagerAdapter extends PagerAdapter {
    private Activity mActivity;
    private boolean isReload = false;

    public SettingPagerAdapter(Activity ctx)
    {
        mActivity = ctx;
    }

    private boolean isChanged()
    {
        boolean isChange = isReload;

        return true;
    }
    @Override
    public int getCount() {
        return 10;
    }

    @Override
    public int getItemPosition(Object object) {
        if (!isChanged()) {
            return POSITION_UNCHANGED;
        } else {
            // after this, onCreateView() of Fragment is called.
            return POSITION_NONE;   // notifyDataSetChanged
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return object == view;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position)
        {
            case 0 :
                return "HOME";
            case 1 :
                return "COMMON";
            default:
                return "MODE " + (position - 1);
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view;

        switch (position)
        {
            case 0 :
                view = mActivity.getLayoutInflater().inflate(R.layout.pager_home, container, false);
                HomeLoad(view);
                break;
            case 1 :
                view = mActivity.getLayoutInflater().inflate(R.layout.pager_common, container, false);
                CommonLoad(view);
                break;
            default:
                view = mActivity.getLayoutInflater().inflate(R.layout.pager_mode_param, container, false);
                ModeLoad(view, position);
                break;
        }
         container.addView(view);
        return  view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    private void HomeLoad(View v) {
        CircleProgressView pulse = (CircleProgressView) v.findViewById(R.id.pulse_module_pulse);
        CircleProgressView state= (CircleProgressView) v.findViewById(R.id.pulse_module_state);
        TextView text = (TextView) v.findViewById(R.id.data_load_string);

        state.setTextMode(TextMode.TEXT);
        Polling.getInstance().Add(pulse, state, text);
    }
    private void CommonLoad(View v)
    {
        EditText input_pulse_count = (EditText) v.findViewById(R.id.input_pulse_count);
        EditText move_length = (EditText) v.findViewById(R.id.move_length);
        EditText cut_left = (EditText) v.findViewById(R.id.cut_left);
        EditText cut_right = (EditText) v.findViewById(R.id.cut_right);
        Button btn = (Button) v.findViewById(R.id.btn_calibrate);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_RUN);
                i.setClassName("com.android.development", "com.android.development.PointerLocation");
                mActivity.startActivity(i);
            }
        });

        input_pulse_count.setText(String.valueOf(PulseModuleData.getInstance().inPulseCount));
        move_length.setText(String.valueOf(PulseModuleData.getInstance().inPulseLength));
        cut_left.setText(String.valueOf(PulseModuleData.getInstance().cutLeft));
        cut_right.setText(String.valueOf(PulseModuleData.getInstance().cutRight));

        input_pulse_count.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    PulseModuleData.getInstance().inPulseCount = Integer.parseInt(charSequence.toString());
                }
                catch (Exception e)
                {

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        move_length.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    PulseModuleData.getInstance().inPulseLength = Integer.parseInt(charSequence.toString());
                }
                catch (Exception e)
                {

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        cut_left.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    PulseModuleData.getInstance().cutLeft = Integer.parseInt(charSequence.toString());
                }
                catch (Exception e)
                {

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        cut_right.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    PulseModuleData.getInstance().cutRight = Integer.parseInt(charSequence.toString());
                }
                catch (Exception e)
                {

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private RecyclerView value_list;
    private ModeParamAdapter modeAdapter;
    private  void ModeLoad(View v, int pos)
    {
         modeAdapter = new ModeParamAdapter(pos - 1);
        value_list = (RecyclerView) v.findViewById(R.id.value_list);
        value_list.setAdapter(modeAdapter);
    }

    public void updateScreen()
    {
        isReload = true;

        if( value_list != null)
        {
            //modeAdapter.notifyDataSetChanged();
            //value_list.invalidate();
        }
    }
}