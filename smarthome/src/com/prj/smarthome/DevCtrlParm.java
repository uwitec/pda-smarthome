package com.prj.smarthome;

import com.prj.smarthome.DevDsParam.TDev;

public class DevCtrlParm {
	
	private short MAXDEVTYPE = 128; //最多128种电器 
	private short MAXDEVCOUNT = 1024; //每区域最多1024个电器设备
	
	public short DEVNAME_LED = 1;      //照明
	public short DEVNAME_CURTAIN = 2;  //窗帘
	public short DEVNAME_MOVIE = 3;    //家庭影院
	public short DEVNAME_MUSIC = 4;    //背景音乐
	public short DEVNAME_SPEECH = 5;   //语音对讲
	public short DEVNAME_CAMERA = 6;   //摄像头
	public short DEVNAME_WARM = 7;     //地暖
	public short DEVNAME_AIRCOOL = 8;  //空调
	public short DEVNAME_DOOR = 9;     //门禁
	public short DEVNAME_SAFE = 10;    //安防
	
	public class TDev {
		int Count; //设备数量
		public class TCtrl {
			int time; //设备控制时间点(段)
			int op;   //操作分量(照明调光度,窗帘开度,电机步进,地暖温度etc。。。)
		}
		public TCtrl[] Ctrl = new TCtrl[MAXDEVCOUNT];
		
		public TDev() {
			int i;
			Count = 0;
			for (i=0;i<MAXDEVCOUNT;i++) {
				Ctrl[i] = new TCtrl();
			}
		}
	}
	
	public TDev[] Devs = new TDev[MAXDEVTYPE];
	
	public DevCtrlParm() {
		int i;
		for(i=0;i<MAXDEVTYPE;i++) {
			Devs[i] = new TDev();
		}
	}
	
	/*读取ini文件中某模式mode下所有设备的控制参数
	 * iniF : ini文件操作对象(已读取并解析)
	 * Devs[i] : 设备坐标点存储结构,每种设备对应一个Devs[i]元素,每个Devs[i]元素内包含若干个TCoord[]坐标元素
	 *           例如:led灯光(照明)对应为Devs[1],窗帘设备对应为Devs[2]。。。依次类推
	 * ModeFileName  : 模式ini文件名称 
	 * */
	/*
	static int getDevCtrlList(IniReader iniF,TDev Dev,String ModeFileName) {
		
		int cnt = 0;
		String key,value;
		if (iniF.isExistSection(AreaName)) { //若AreaName存在于ini文件中
			Dev.Count = iniF.getKeys(AreaName);
			for (cnt=0;cnt<Dev.Count;cnt++) {
				key = iniF.Keys[cnt];
				value = iniF.getValue(AreaName, key);
				Dev.Coord[cnt].x = (short) iniF.getX(value);
				Dev.Coord[cnt].y = (short) iniF.getY(value);
			}
		} else { //AreaName不存在于ini文件中,返回-1
			return -1;
		}
		
		return Dev.Count;
	}
	*/
	
}
