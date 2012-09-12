package com.prj.smarthome;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityGroup;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

/*
 * 该播放器不支持需要用户授权的rtsp视频流
 */
public class MultiCameraActivity extends ActivityGroup {
	
	static int onclickettime = 0;
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		onclickettime = 0;
	}
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.multicamera);
        
        Button addaview = (Button) findViewById(R.id.playerbutton);
        addaview.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v){			
	        	Intent intent = new Intent(MultiCameraActivity.this, OneCamera.class);	
	        	add_activitygroup(MultiCameraActivity.this, R.id.CameraslinearLayout, intent);
	        }
    	});
	}
	
	@SuppressLint("NewApi")
	String getCameraIP()
	{
		EditText ipin = (EditText) findViewById(R.id.camera_ip);    	
		String ip = ipin.getText().toString();
		if(ip.isEmpty())
			/* rtsp H264 stream (default)*/
			ip = "172.16.55.47";

		/*	成都交通摄像头 ok */ 
//			video_uri = "rtsp://218.205.231.149:554/live/1/35AB46A109903442/0037f42790b7cb14.sdp?id=guest&t=1305311828&en=a4d5d7adaf0eca0d907875009a901a85&rs=wap";

    	return ip;
    }

	String getRtspPort()
	{
		EditText ipin = (EditText) findViewById(R.id.camera_rtsp_port);    	
		String pt = ipin.getText().toString();
		if(pt.isEmpty())
			/* rtsp H264 stream (default)*/
			pt = "554";
    	return pt;
    }
	
	String getPtzPort()
	{
		EditText ipin = (EditText) findViewById(R.id.camera_ptz_port);    	
		String pt = ipin.getText().toString();
		if(pt.isEmpty())
			/* rtsp H264 stream (default)*/
			pt = "81";
    	return pt;
    }

	String getLoginUser()
	{
		EditText ipin = (EditText) findViewById(R.id.camera_login_user);    	
		String pt = ipin.getText().toString();
		if(pt.isEmpty())
			/* rtsp H264 stream (default)*/
			pt = "admin";
    	return pt;
    }

	String getLoginPwd()
	{
		EditText ipin = (EditText) findViewById(R.id.camera_login_pwd);    	
		String pt = ipin.getText().toString();
		if(pt.isEmpty())
			/* rtsp H264 stream (default)*/
			pt = "nimda";
    	return pt;
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

	    OneCamera one = (OneCamera)actgp.getLocalActivityManager().getActivity(unq);
		one.setPtzCameraIpPort(getCameraIP(), getRtspPort(), getPtzPort());		
		one.setPtzLoginInfo(getLoginUser(), getLoginPwd());
		
	    one.showVideo();
	}

	public static class OneCamera extends Activity implements OnClickListener{

		String ptz_url = "";
		String ptz_format = "http://%s:%s/cgi-bin/decoder_control.cgi?command=%d&onestep=0&loginuse=%s&loginpas=%s&next_url=";
		String rtsp_format = "rtsp://%s:%s@%s:%s/H264";
		String reboot_format = "http://%s:%s/reboot.cgi?loginuse=%s&loginpas=%s&next_url=";		
		String domain = "";
		String rtsp_port = "";
		String ptz_port = "";
		String loguse = "";
		String logpwd = "";
	
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.Button1:
				goPtzRun(PtzCtrl.Cmd.PTZ_CAM_LEFT_UP);
				break;
			case R.id.Button2:
				goPtzRun(PtzCtrl.Cmd.PTZ_CAM_UP);
				break;
			case R.id.Button3:
				goPtzRun(PtzCtrl.Cmd.PTZ_CAM_RIGHT_UP);
				break;
			case R.id.Button4:
				goPtzRun(PtzCtrl.Cmd.PTZ_CAM_LEFT);
				break;
			case R.id.Button5:
				goPtzRun(PtzCtrl.Cmd.PTZ_CAM_STOP);
				break;
			case R.id.Button6:
				goPtzRun(PtzCtrl.Cmd.PTZ_CAM_RIGHT);
				break;
			case R.id.Button7:
				goPtzRun(PtzCtrl.Cmd.PTZ_CAM_LEFT_DOWN);
				break;
			case R.id.Button8:
				goPtzRun(PtzCtrl.Cmd.PTZ_CAM_DOWN);
				break;
			case R.id.Button9:
				goPtzRun(PtzCtrl.Cmd.PTZ_CAM_RIGHT_DOWN);
				break;
			case R.id.Button10:
				goPtzRun(PtzCtrl.Cmd.PTZ_CAM_RIGHT_PATROL_H);
				break;
			case R.id.Button11:
				goPtzRun(PtzCtrl.Cmd.PTZ_CAM_RIGHT_PATROL_V);
				break;
			case R.id.Button12:
				goPtzRun(PtzCtrl.Cmd.PTZ_CAM_RIGHT_PATROL_CIRCLE);
				break;
			case R.id.Button21:
				goPtzRun(PtzCtrl.Cmd.CAM_REBOOT);
				break;
			}
		}
		
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);
			this.setContentView(R.layout.acamera);
			VideoView vd = (VideoView)findViewById(R.id.a_videoview);
			
			Button bt1 = (Button)findViewById(R.id.Button1);		
			bt1.setOnClickListener(this);
			Button bt2 = (Button)findViewById(R.id.Button2);
			bt2.setOnClickListener(this);
			Button bt3 = (Button)findViewById(R.id.Button3);
			bt3.setOnClickListener(this);
			Button bt4 = (Button)findViewById(R.id.Button4);
			bt4.setOnClickListener(this);
			Button bt5 = (Button)findViewById(R.id.Button5);
			bt5.setOnClickListener(this);
			Button bt6 = (Button)findViewById(R.id.Button6);
			bt6.setOnClickListener(this);
			Button bt7 = (Button)findViewById(R.id.Button7);
			bt7.setOnClickListener(this);
			Button bt8 = (Button)findViewById(R.id.Button8);
			bt8.setOnClickListener(this);
			Button bt9 = (Button)findViewById(R.id.Button9);
			bt9.setOnClickListener(this);
			Button bt10 = (Button)findViewById(R.id.Button10);
			bt10.setOnClickListener(this);
			Button bt11 = (Button)findViewById(R.id.Button11);
			bt11.setOnClickListener(this);
			Button bt12 = (Button)findViewById(R.id.Button12);
			bt12.setOnClickListener(this);
			Button bt21 = (Button)findViewById(R.id.Button21);
			bt21.setOnClickListener(this);
		}
		
		public void setPtzCameraIpPort(String ip, String rtsppt, String ptzpt)
		{
			domain = ip;
			rtsp_port = rtsppt;
			ptz_port = ptzpt;
		}
		
		public void setPtzLoginInfo(String use, String pas)
		{
			loguse = use;
			logpwd = pas;
		}
	
		public boolean getRtspAuthoritySupported()
		{
			return false;
		}
		
		public void showVideo()
	    {
			String video_uri = String.format(rtsp_format, loguse, logpwd, domain, rtsp_port);
			VideoView vd = (VideoView)findViewById(R.id.a_videoview);
	        Log.i(PtzCtrl.ProgramTag.VisionTag, video_uri);
	        Uri uri = Uri.parse(video_uri);
	//        MediaController mc = new MediaController(this);
	//        vd.setMediaController(mc);
	        vd.setVideoURI(uri);
	        vd.start();
	
	        TextView tv = (TextView)findViewById(R.id.rtspinfo);
	        tv.setText("Video Info: "+ "rtsp://" + domain + ":" + rtsp_port + "/H264, loginuser=" + loguse + ", loginpwd=" + logpwd);
	
	        TextView tvv = (TextView)findViewById(R.id.ptzinfo);
	        tvv.setText("PTZ Info: " + "http://" + domain + ":" + ptz_port + ", loginuser=" + loguse + ", loginpwd=" + logpwd);
	    }
	
		public void goPtzRun(int cmd)
		{
			if(cmd>=0 && cmd<PtzCtrl.Cmd.PTZ_CAM_MAX)
				ptz_url = String.format(ptz_format, domain, ptz_port, cmd, loguse, logpwd);
	
			if(cmd == PtzCtrl.Cmd.CAM_REBOOT)
				ptz_url = String.format(reboot_format, domain, ptz_port, loguse, logpwd);
				
			new Thread(new Runnable() 
			{  
	            @Override  
	            public synchronized void run() 
	            {
	            	try  
	    	        {
	    				Log.i(PtzCtrl.ProgramTag.VisionTag, ptz_url);
	    				// 新建HttpGet对象  
	    				HttpGet httpGet = new HttpGet(ptz_url);
	        		    // 获取HttpClient对象  
	        		    HttpClient httpClient = new DefaultHttpClient();  
	        		    // 获取HttpResponse实例  
	        		    HttpResponse httpResp = httpClient.execute(httpGet);
	    	        }
	    	        catch (Exception e)  
	    	        {  
	    	            Log.e(PtzCtrl.ProgramTag.VisionTag,  ptz_url);
	//    	            e.printStackTrace();
	    	        }
	            }
	        }).start();  
		}
	}

}
