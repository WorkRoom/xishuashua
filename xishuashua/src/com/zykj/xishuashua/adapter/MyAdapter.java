package com.zykj.xishuashua.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zykj.xishuashua.R;

public class MyAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<String> list;//Area
	TextView textView = null;
	public MyAdapter(Context context,ArrayList<String> list){//Area
		this.context = context;
		this.list=list;
	}
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			textView = new TextView(context);
		} else {
			textView = (TextView) convertView;
		}
		String current = list.get(position);
		textView.setText("ab".contains(current.substring(current.length()-1))?current.substring(0, 4):current);//.getName()
		textView.setTextSize(18);
		textView.setGravity(Gravity.CENTER);
		textView.setPadding(0, 20, 0, 20);
		textView.setTextColor(Color.BLACK);
		if("ab".contains(current.substring(current.length()-1))){
			textView.setBackgroundResource(current.substring(4).equals("b")?R.drawable.choose_item_right:0);
		}
		return textView;
	}
}
