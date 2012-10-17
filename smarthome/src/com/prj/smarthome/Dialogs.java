package com.prj.smarthome;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class Dialogs {

	public static Result_LED rslt_led ;
	public static Result_Curtain rslt_curtain ;
//	private static int tmp_rslt = 0;
	
	//灯控对话框--返回接口
	public class Result_LED {
		int light; //调光度 0-100
		public void Result_LED() {
			light = 0;
		}
	}
	
	//窗帘控制对话框--返回接口
	public class Result_Curtain {
		int opening; //窗帘开度
		int rolling; //旋转度
		public void Result_Curtain() {
			opening = 0; //0..100
			rolling = 0; //0..100
		}
	}
	
	//构建设备控制(对话框)返回接口
	public void Dialogs() {
		rslt_led = new Result_LED();
		rslt_curtain = new Result_Curtain();
	//	SeekBar skb = (SeekBar)dialog.findViewById(R.id.seekbar);
	}
	
	//调光对话框
	static int Dialog_Adj_Light(final Context context,View DialogView,int cur_light) {
		
        //内部类：一个监听器，该监听器负责监听进度条状态 的改变  
        class SeekBar_lightADJ_Listener implements OnSeekBarChangeListener {  
              //当进度条的进度发生 变化 时，会调用 该 方法 
        	
        	  //滑动进行中:滑块按下不放并左右滑动
     	   	  @Override
     	   	  public void onProgressChanged(SeekBar seekBar,
						int progress, boolean fromUser) {  
                      //System.out.println(progress);  //滑动的时候输出当前位置
               }  
     	   	  
     	   	  //滑动开始
     	   	  @Override
               public void onStartTrackingTouch(SeekBar seekBar) {  
                      System.out.println("start->"+ seekBar.getProgress());   
               } 
     	   	  
     	   	  //滑动停止
     	   	  @Override
               public void onStopTrackingTouch(SeekBar seekBar) {  
                      System.out.println("stop->"+ seekBar.getProgress());  
               }
        }
		
		final SeekBar skb = (SeekBar) DialogView.findViewById(R.id.seekbar);
		skb.setOnSeekBarChangeListener(new SeekBar_lightADJ_Listener());  //设置seekbar滑块滑动/改变的响应监听
		skb.setProgress(cur_light); //设置光度条当前的进度值 -- 从数据库或下位设备读取到的实际亮度值
	    new AlertDialog.Builder(context)
		   .setTitle("调光设置").setView(DialogView)
		   .setIcon(android.R.drawable.ic_menu_more)
		   .setPositiveButton("确定",new OnClickListener() {
			   @Override
			   public void onClick(DialogInterface dialog, int which) {
				   //rslt.light = skb.getProgress();
				   Toast.makeText(context,"light="+Integer.toString(skb.getProgress()),Toast.LENGTH_SHORT).show();
			   }})
		   .setNegativeButton("取消", null)
		   .show();
		return 0;
	}
	
}
