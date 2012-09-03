package com.prj.smarthome;

public final class PtzCtrl {
	
	public static final class Cmd {
		public static final int PTZ_CAM_STOP = 0;
	
		public static final int PTZ_CAM_UP = 1;
		public static final int PTZ_CAM_DOWN = 2;
		public static final int PTZ_CAM_LEFT = 3;
		public static final int PTZ_CAM_LEFT_UP = 4;
		public static final int PTZ_CAM_LEFT_DOWN = 5;
	
		public static final int PTZ_CAM_RIGHT = 6;
		public static final int PTZ_CAM_RIGHT_UP = 7;
		public static final int PTZ_CAM_RIGHT_DOWN = 8;
	
		public static final int PTZ_CAM_RIGHT_PATROL_H = 10;
		public static final int PTZ_CAM_RIGHT_PATROL_V = 11;
		public static final int PTZ_CAM_RIGHT_PATROL_CIRCLE = 12;
		
		public static final int PTZ_CAM_MAX = 20;
		public static final int CAM_REBOOT = 21;
	}
	
	public static final class ProgramTag {
		public static final String VisionTag = "smart";
	}

}
