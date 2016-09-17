package org.citas.basicdemo;

import android.content.Context;
import android.support.annotation.RequiresPermission;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by francis on 9/13/16.
 */
public class ReadingArrayAdapter extends ArrayAdapter<Reading> {
    private Context _context = null;
    private List<Reading> _readings = null;

    public ReadingArrayAdapter(Context context, List<Reading> readings, int resource) {
        super(context, resource);
        this._context = context;
        this._readings = readings;
        return;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater =
                    (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_readings_custom, parent, false);
        }

        Reading r = _readings.get(position);

        TextView txvSource = (TextView) convertView.findViewById(R.id.txv_source);
        txvSource.setText(r.getSource());

        TextView txvType = (TextView) convertView.findViewById(R.id.txv_type);
        txvType.setText(r.getType());

        TextView txvValue = (TextView) convertView.findViewById(R.id.txv_val);
        txvValue.setText(Double.toString(r.getValue()));

        return convertView;
    }
}
