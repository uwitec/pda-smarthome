package com.prj.smarthome;

/*
 * 16进制字符串转int
int i = Integer.valueOf("12df",16);
lbl_info.setText(Integer.toString(i));

int转16进制字符串
String ss = String.format("%05x", i);
lbl_info.setText(ss);
*/

public class DevDsParam {
	
	private short MAXDEVTYPE = 128; //最多128种电器 
	private short MAXDEVCOUNT = 256; //每区域最多256个电器设备
	
	public static short DEVNAME_LED = 1;      //照明
	public static short DEVNAME_CURTAIN = 2;  //窗帘
	public static short DEVNAME_MOVIE = 3;    //家庭影院
	public static short DEVNAME_MUSIC = 4;    //背景音乐
	public static short DEVNAME_SPEECH = 5;   //语音对讲
	public static short DEVNAME_CAMERA = 6;   //摄像头
	public static short DEVNAME_WARM = 7;     //地暖
	public static short DEVNAME_AIRCOOL = 8;  //空调
	public static short DEVNAME_DOOR = 9;     //门禁
	public static short DEVNAME_SAFE = 10;    //安防
	
	public int AreaName; //楼层;房间;房间3D
	
	public class TDev {
		int Count; //设备数量
		public class TCoord {
			short x;
			short y;
			int ID; //设备编号
			public TCoord() {
				x = 0;
				y = 0;
				ID = -1;
			}
		}
		public TCoord[] Coord = new TCoord[MAXDEVCOUNT];
		
		public TDev() {
			int i;
			Count = 0;
			for (i=0;i<MAXDEVCOUNT;i++) {
				Coord[i] = new TCoord();
			}
		}
	}
	
	public TDev[] Devs = new TDev[MAXDEVTYPE];
	
	public DevDsParam() {
		int i;
		AreaName = 255; //置为空
		for(i=0;i<MAXDEVTYPE;i++) {
			Devs[i] = new TDev();
		}
	}
	
	/*读取ini文件中AreaID节下所有设备的坐标
	 * iniF : ini文件操作对象(已读取并解析)
	 * Devs[i] : 设备坐标点存储结构,每种设备对应一个Devs[i]元素,每个Devs[i]元素内包含若干个TCoord[]坐标元素
	 *           例如:led灯光(照明)对应为Devs[1],窗帘设备对应为Devs[2]。。。依次类推
	 * AreaName  : 区域名称 --对应为ini文件中的section字符串名 
	 * */
	static int getDevCoordList(IniReader iniF,TDev Dev,String AreaName) {
		int cnt = 0;
		String name,value;
		if (iniF.isExistSection(AreaName)) { //若AreaName存在于ini文件中
			Dev.Count = iniF.getNames(AreaName);
			for (cnt=0;cnt<Dev.Count;cnt++) {
				name = iniF.Names[cnt];
				value = iniF.getValue(AreaName, name);
				Dev.Coord[cnt].ID = Integer.valueOf(name,16); //16进制字符串转整形int
				Dev.Coord[cnt].x = (short) iniF.getX(value);
				Dev.Coord[cnt].y = (short) iniF.getY(value);
			}
		} else { //AreaName不存在于ini文件中,返回-1
			Dev.Count = -1;
		}
		return Dev.Count; 
	}

}
