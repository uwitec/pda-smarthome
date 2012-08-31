package com.prj.smarthome;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import com.prj.smarthome.MultiCameraActivity;

public class SmartHomeActivity extends Activity { 
	
	private View.OnTouchListener Listener_btn_touch;
	final static String Tag = "SmartHomeActivity";
	
    /** Called when the activity is first created. */
	/** yes , I change a little and commit to GC **/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        final ImageButton imgbtn_ledctrl,imgbtn_curtainctrl,imgbtn_moviectrl,imgbtn_musictrl,
                          imgbtn_speechctrl,imgbtn_cameractrl,imgbtn_warmctrl,imgbtn_airconctrl,
                          imgbtn_doorctrl,imgbtn_safectrl;
        imgbtn_ledctrl = (ImageButton)findViewById(R.id.btn_ledcrtl);
        imgbtn_curtainctrl = (ImageButton)findViewById(R.id.btn_curtaincrtl);
        imgbtn_moviectrl = (ImageButton)findViewById(R.id.btn_moviecrtl);
        imgbtn_musictrl = (ImageButton)findViewById(R.id.btn_musiccrtl);
        imgbtn_speechctrl = (ImageButton)findViewById(R.id.btn_speechcrtl);
        imgbtn_cameractrl = (ImageButton)findViewById(R.id.btn_cameractrl);
        imgbtn_warmctrl = (ImageButton)findViewById(R.id.btn_warmctrl);
        imgbtn_airconctrl = (ImageButton)findViewById(R.id.btn_aircondnctrl);
        imgbtn_doorctrl = (ImageButton)findViewById(R.id.btn_doorctrl);
        imgbtn_safectrl = (ImageButton)findViewById(R.id.btn_safectrl);
        
        Listener_btn_touch = new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (v.getId()) {
				case R.id.btn_safectrl :
		    		if(event.getAction() == MotionEvent.ACTION_DOWN){     
	                    //更改为按下时的背景图片     
		    			imgbtn_safectrl.setImageResource(R.drawable.btn_safedn);
		            }else if(event.getAction() == MotionEvent.ACTION_UP){     
	                    //改为抬起时的图片     
		            	imgbtn_safectrl.setImageResource(R.drawable.btn_safeup);
		            }
		    		Log.i(Tag, "Button 1");
					break;
				case R.id.btn_doorctrl :
		    		if(event.getAction() == MotionEvent.ACTION_DOWN){     
	                    //更改为按下时的背景图片     
		    			imgbtn_doorctrl.setImageResource(R.drawable.btn_doordn);
		            }else if(event.getAction() == MotionEvent.ACTION_UP){     
	                    //改为抬起时的图片     
		            	imgbtn_doorctrl.setImageResource(R.drawable.btn_doorup);
		            }				
		    		Log.i(Tag, "Button 2");
					break;
				case R.id.btn_aircondnctrl :
		    		if(event.getAction() == MotionEvent.ACTION_DOWN){     
	                    //更改为按下时的背景图片     
		    			imgbtn_airconctrl.setImageResource(R.drawable.btn_aircondn);
		            }else if(event.getAction() == MotionEvent.ACTION_UP){     
	                    //改为抬起时的图片     
		            	imgbtn_airconctrl.setImageResource(R.drawable.btn_airconup);
		            }				
		    		Log.i(Tag, "Button 3");
					break;	
				case R.id.btn_warmctrl :
		    		if(event.getAction() == MotionEvent.ACTION_DOWN){     
	                    //更改为按下时的背景图片     
		    			imgbtn_warmctrl.setImageResource(R.drawable.btn_warmdn);
		            }else if(event.getAction() == MotionEvent.ACTION_UP){     
	                    //改为抬起时的图片     
		            	imgbtn_warmctrl.setImageResource(R.drawable.btn_warmup);
		            }				
		    		Log.i(Tag, "Button 4");
					break;	
				case R.id.btn_cameractrl :
		    		if(event.getAction() == MotionEvent.ACTION_DOWN){     
	                    //更改为按下时的背景图片     
		    			imgbtn_cameractrl.setImageResource(R.drawable.btn_cameradn);
		            }else if(event.getAction() == MotionEvent.ACTION_UP){     
	                    //改为抬起时的图片     
		            	imgbtn_cameractrl.setImageResource(R.drawable.btn_cameraup);
		            			            	
		            	Intent step1 = new Intent(SmartHomeActivity.this, MultiCameraActivity.class);
		            	startActivity(step1);
		            }				
		    		Log.i(Tag, "Button 5");
					break;	
				case R.id.btn_ledcrtl :
		    		if(event.getAction() == MotionEvent.ACTION_DOWN){     
	                    //更改为按下时的背景图片     
		    			imgbtn_ledctrl.setImageResource(R.drawable.btn_leddn);
		            }else if(event.getAction() == MotionEvent.ACTION_UP){     
	                    //改为抬起时的图片     
		            	imgbtn_ledctrl.setImageResource(R.drawable.btn_ledup);
		            } 					
		    		Log.i(Tag, "Button 6");
					break;
				case R.id.btn_curtaincrtl :
		    		if(event.getAction() == MotionEvent.ACTION_DOWN){     
	                    //更改为按下时的背景图片     
		    			imgbtn_curtainctrl.setImageResource(R.drawable.btn_curtaindn);
		            }else if(event.getAction() == MotionEvent.ACTION_UP){     
	                    //改为抬起时的图片     
		            	imgbtn_curtainctrl.setImageResource(R.drawable.btn_curtainup);
		            }				
		    		Log.i(Tag, "Button 7");
					break;
				case R.id.btn_moviecrtl :
		    		if(event.getAction() == MotionEvent.ACTION_DOWN){     
	                    //更改为按下时的背景图片     
		    			imgbtn_moviectrl.setImageResource(R.drawable.btn_moviedn);
		            }else if(event.getAction() == MotionEvent.ACTION_UP){     
	                    //改为抬起时的图片     
		            	imgbtn_moviectrl.setImageResource(R.drawable.btn_movieup);
		            }				
		    		Log.i(Tag, "Button 8");
					break;	
				case R.id.btn_musiccrtl :
		    		if(event.getAction() == MotionEvent.ACTION_DOWN){     
	                    //更改为按下时的背景图片     
		    			imgbtn_musictrl.setImageResource(R.drawable.btn_musicdn);
		            }else if(event.getAction() == MotionEvent.ACTION_UP){     
	                    //改为抬起时的图片     
		            	imgbtn_musictrl.setImageResource(R.drawable.btn_musicup);
		            }				
		    		Log.i(Tag, "Button 9");
					break;					
				case R.id.btn_speechcrtl :
		    		if(event.getAction() == MotionEvent.ACTION_DOWN){     
	                    //更改为按下时的背景图片     
		    			imgbtn_speechctrl.setImageResource(R.drawable.btn_speechdn);
		            }else if(event.getAction() == MotionEvent.ACTION_UP){     
	                    //改为抬起时的图片     
		            	imgbtn_speechctrl.setImageResource(R.drawable.btn_speechup);
		            }				
		    		Log.i(Tag, "Button 10");
					break;				
				}
				return false;
			}
		};
		
		imgbtn_ledctrl.setOnTouchListener(Listener_btn_touch);
		imgbtn_curtainctrl.setOnTouchListener(Listener_btn_touch);
		imgbtn_moviectrl.setOnTouchListener(Listener_btn_touch);
		imgbtn_musictrl.setOnTouchListener(Listener_btn_touch);
		imgbtn_speechctrl.setOnTouchListener(Listener_btn_touch);
		imgbtn_cameractrl.setOnTouchListener(Listener_btn_touch);
		imgbtn_warmctrl.setOnTouchListener(Listener_btn_touch);
		imgbtn_airconctrl.setOnTouchListener(Listener_btn_touch);
		imgbtn_doorctrl.setOnTouchListener(Listener_btn_touch);
		imgbtn_safectrl.setOnTouchListener(Listener_btn_touch);
    }
}