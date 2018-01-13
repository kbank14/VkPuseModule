package com.vermeskorea.pulsemodule.Adapter;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vermeskorea.pulsemodule.Data.PulseModuleData;
import com.vermeskorea.pulsemodule.R;

import java.util.List;

/**
 * Created by kbank14 on 2018-01-08.
 */

public class ModeParamAdapter extends RecyclerView.Adapter<ModeParamAdapter.ViewHolder> {
    private final int mMode;

    public ModeParamAdapter(int mode) {
        mMode = mode;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mode_param,
                parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        PulseModuleData.PulseParamInfo info = PulseModuleData.getInstance().get(mMode, position);
        holder.lockChange = true;
        holder.position = position;

        holder.mode_distance.setTag(holder);
        holder.mode_action.setTag(holder);

        holder.mode_index.setText(String.valueOf(position));
        holder.mode_distance.setText(String.valueOf(info.Distance));
        if (info.Action == 'L') {
            //holder.mode_action.setText("LOW");
            holder.mode_action.setChecked(false);
        } else {
            //holder.mode_action.setText("HIGH");
            holder.mode_action.setChecked(true);
        }
        holder.mode_pulse_count.setText(String.valueOf(info.PulseCount));

        holder.lockChange = false;
    }

    @Override
    public int getItemCount() {
        return PulseModuleData.getInstance().get(mMode).size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mode_index;
        public final EditText mode_distance;
        public final SwitchCompat mode_action;
        public final TextView mode_pulse_count;
        public int position;
        public boolean lockChange;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mode_index = (TextView) view.findViewById(R.id.mode_index);
            mode_distance = (EditText) view.findViewById(R.id.mode_distance);
            mode_action = (SwitchCompat) view.findViewById(R.id.mode_action);
            mode_pulse_count = (TextView) view.findViewById(R.id.mode_pulse_count);
            position = 0;
            lockChange = false;

            mode_distance.setOnFocusChangeListener(new View.OnFocusChangeListener()
            {

                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    //if(hasFocus)
                    //    return;

                    try {
                        EditText et = (EditText) view;
                        ViewHolder vh = (ViewHolder) et.getTag();
                        PulseModuleData.PulseParamInfo info = PulseModuleData.getInstance().get(mMode, vh.position);

                        int distance = Integer.parseInt(et.getText().toString());
                        int pulse = PulseModuleData.getInstance().calcTargetPulse(distance);
                        info.Distance = distance;
                        info.PulseCount = pulse;
                        vh.mode_pulse_count.setText(String.valueOf(pulse));
                    }
                    catch (Exception e)
                    {

                    }
                }
            });

            mode_distance.addTextChangedListener(new TextWatcher() {

                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                     try {
                        int distance = Integer.parseInt(charSequence.toString());
                        int pulse = PulseModuleData.getInstance().calcTargetPulse(distance);
                        mode_pulse_count.setText(String.valueOf(pulse));
                    } catch (Exception e) {

                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            mode_action.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    SwitchCompat sc = (SwitchCompat) view;
                    ViewHolder vh = (ViewHolder) sc.getTag();

                    if (!sc.isChecked()) {
                        sc.setText("LOW");
                        PulseModuleData.getInstance().get(mMode, vh.position).Action = 'L';
                    } else {
                        sc.setText("HIGH");
                        PulseModuleData.getInstance().get(mMode, vh.position).Action = 'H';
                    }
                }
            });
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mode_index.getText() + "'";
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}
