package com.prj.smarthome;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;

public class OneCamera extends Activity {
	
	static String video_uri = ""; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.acamera);
		
		showVideo();
	}

	private void showVideo()
    {
        VideoView vd = (VideoView)findViewById(R.id.a_videoview);
        Uri uri = Uri.parse(video_uri);
//        MediaController mc = new MediaController(this);
//        vd.setMediaController(mc);
        vd.setVideoURI(uri);
        vd.start();
    }		
}
