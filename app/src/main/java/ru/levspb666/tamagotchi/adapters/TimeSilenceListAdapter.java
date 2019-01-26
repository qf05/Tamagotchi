package ru.levspb666.tamagotchi.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ru.levspb666.tamagotchi.R;

public class TimeSilenceListAdapter extends BaseAdapter {

    private List<Map<String, Integer>> data = new ArrayList<>();
    private Context context;

    public TimeSilenceListAdapter(Context context, List<Map<String, Integer>> arr) {
        if (arr != null) {
            data = arr;
        }
        this.context = context;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int item) {
        return data.get(item);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int i, View view, ViewGroup arg2) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if (view == null) {
            view = inflater.inflate(R.layout.time_silence_adapter, arg2, false);
        }
        TextView startTime = view.findViewById(R.id.timeStart);
        TextView stopTime = view.findViewById(R.id.timeStop);
        String sth = String.valueOf(data.get(i).get("startH"));
        String stm = String.valueOf(data.get(i).get("startM"));
        String sph = String.valueOf(data.get(i).get("stopH"));
        String spm = String.valueOf(data.get(i).get("stopM"));
        if (data.get(i).get("startH") < 10) {
            sth = "0" + sth;
        }
        if (data.get(i).get("startM") < 10) {
            stm = "0" + stm;
        }
        if (data.get(i).get("stopH") < 10) {
            sph = "0" + sph;
        }
        if (data.get(i).get("stopM") < 10) {
            spm = "0" + spm;
        }
        startTime.setText(sth + " : " + stm);
        stopTime.setText(sph + " : " + spm);
        return view;
    }
}