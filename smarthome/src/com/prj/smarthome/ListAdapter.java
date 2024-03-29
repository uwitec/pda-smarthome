package com.prj.smarthome;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListAdapter extends BaseAdapter {

	private LayoutInflater myInflater;

	private Bitmap[] IconBuf = new Bitmap[20];
	private int __FuncID; //本类内部使用的变量 功能ID

	private List<String> items;
	
	private void SetIcons(ViewHolder hld,int FuncID,int position) {
		switch (FuncID) {
		case 1 : //LED灯光控制
			hld.funcName.setText(items.get(position));
			switch (position) {
			case 0 : //灯光全开
				hld.iconL.setImageBitmap(IconBuf[2]);
				hld.iconR.setVisibility(View.INVISIBLE);				
				break;
			case 1 : //灯光全关
				hld.iconL.setImageBitmap(IconBuf[3]);
				hld.iconR.setVisibility(View.INVISIBLE);
				break;
			case 2 : //点控开灯
				hld.iconL.setImageBitmap(IconBuf[2]);
				hld.iconR.setVisibility(View.INVISIBLE);
				break;
			case 3 : //点控关灯
				hld.iconL.setImageBitmap(IconBuf[3]);
				hld.iconR.setVisibility(View.INVISIBLE);
				break;
			case 4 : //调光设置
				hld.iconL.setImageBitmap(IconBuf[1]);
				hld.iconR.setImageBitmap(IconBuf[0]);
				hld.iconR.setVisibility(View.VISIBLE);
				break;				
			}
			break;
		}
	}
	
	private void LoadIcons(Context context,int FuncID) {
		switch (FuncID) {
		//LED灯光控制
		case 1 :
			IconBuf[0] = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.ctrl_func_expend);
			IconBuf[1] = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.ctrl_dev_adj);
			IconBuf[2] = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.ctrl_led_on);
			IconBuf[3] = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.ctrl_led_off);

			break;
			
		}
	}

	public ListAdapter(Context context, List<String> items, int FuncID) {
		myInflater = LayoutInflater.from(context);

		this.items = items;
		__FuncID = FuncID;
		
		LoadIcons(context,FuncID);
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView == null){
			convertView = myInflater.inflate(R.layout.funclist_row, null);
			holder = new ViewHolder();
			holder.funcName =(TextView)convertView.findViewById(R.id.funcName);
			holder.iconL = (ImageView)convertView.findViewById(R.id.iconL);
			holder.iconR = (ImageView)convertView.findViewById(R.id.iconR);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		SetIcons(holder,__FuncID,position);
		return convertView;
	}
	
	private class ViewHolder{
		TextView funcName;
		ImageView iconL;
		ImageView iconR;
	}

}
