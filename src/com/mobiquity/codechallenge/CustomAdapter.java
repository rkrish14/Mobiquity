package com.mobiquity.codechallenge;

import com.mobiquity.challenge.onrampchallenge.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/*
 * CustomAdapter class to inflate listview
*/
public class CustomAdapter extends ArrayAdapter<String> {
    int size = 1;
    Context context;
    int[] icons;
    String[] name;

    CustomAdapter(Context c, String[] name, int imgs[]) {
        super(c, R.layout.list_row, R.id.textView1, name);
        this.context = c;
        this.icons = imgs;
        this.name = name;
    }
    public View getView(int position, View convertView, ViewGroup parent) {
       
    	LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.list_row, parent, false);
        
        ImageView icon = (ImageView) row.findViewById(R.id.imageView1);
        icon.setImageResource(icons[position]);
        
        TextView animal_name = (TextView) row.findViewById(R.id.textView1);
        animal_name.setText(name[position]);
        
        return row;
    }

}