package com.prj.smarthome;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityGroup;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.VideoView;
import com.prj.smarthome.OneCamera;

/*
 * 该播放器不支持rtsp需要用户授权的视频流
 */
public class MultiCameraActivity extends ActivityGroup {	
	static int onclickettime = 0;
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		onclickettime = 0;
		Log.i("ZT", "onclickettime=0");
	}
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.multicamera);
        
        Button addaview = (Button) findViewById(R.id.playerbutton);
        addaview.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v){
				String ip = InitRtspIP();
				String video_uri = "rtsp://" + ip + ":554/H264";
				OneCamera.video_uri = video_uri;
				
				Log.i("ZT", video_uri);
	        	Intent intent = new Intent(MultiCameraActivity.this, OneCamera.class);	
	        	add_activitygroup(MultiCameraActivity.this, R.id.CameraslinearLayout, intent);
	        }
    	});
	}
	
	@SuppressLint("NewApi")
	String InitRtspIP()
	{
		EditText ipin = (EditText) findViewById(R.id.camera_ip);    	
		String ip = ipin.getText().toString();
		if(ip.isEmpty())
			/* rtsp H264 stream (default)*/
			ip = "admin:nimda@172.16.55.47";
	
			/*	成都交通摄像头 ok */ 
	//		video_uri = "rtsp://218.205.231.149:554/live/1/35AB46A109903442/0037f42790b7cb14.sdp?id=guest&t=1305311828&en=a4d5d7adaf0eca0d907875009a901a85&rs=wap";
	 	    	
	    	return ip;
	    }
	
		void add_activitygroup(ActivityGroup actgp, int resid, Intent intent) {
			onclickettime++;
			if(onclickettime>=5)
			{
				String st = "最多创建4个视频，性能考虑";
				Toast.makeText(MultiCameraActivity.this, st, Toast.LENGTH_SHORT).show();
				return ;
			}
			String unq = String.format("AB%d", onclickettime);	
			
			LinearLayout layout = (LinearLayout)actgp.findViewById(resid);
			Window subActivity = actgp.getLocalActivityManager().startActivity(unq, intent);				
			View view = subActivity.getDecorView();  
		    layout.addView(view);
		}
}
