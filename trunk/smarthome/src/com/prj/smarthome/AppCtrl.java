package com.prj.smarthome;

public class AppCtrl {
	public int MapLevel; //视图层次
	public int OpLevel;  //操作层次 - 回退、前进操作
	public int OpDev;    //操作对象
	public int SelectedDevIdx; //点取选中设备的index索引号
	public int FloorID;  //楼层标识
	public int RoomID;   //房间标识
	
	public static int FULL_ERASE = 255;
	public static int NULL = 255;
	
	//MapLevel变量取值
	public static int LEVEL_FULLHOUSE = 0;        //房屋全视图
	public static int LEVEL_FLOOR = 1;            //楼层视图
	public static int LEVEL_ROOM = 2;             //房间视图
	public static int LEVEL_ROOM3D = 3;           //房间局部3D视图
	
	
	//FloorID,RoomID变量取值
	public static int LEVEL_FLOOR_BOTTOM = -1;     //楼层视图：地下1层    -1层
	public static int LEVEL_FLOOR_GARDEN =1;      //楼层视图：花园客厅层      1层
	public static int LEVEL_FLOOR_BEDROOM = 2;    //楼层视图：卧室层               2层
	public static int LEVEL_FLOOR_TOP = 3;        //楼层视图：屋顶阳光层      3层
	public static int LEVEL_ROOM_BOTTOM_01 = -11;  //房间视图：地下1层 1区
	public static int LEVEL_ROOM_BOTTOM_02 = -12;  //房间视图：地下1层2区
	public static int LEVEL_ROOM_BOTTOM_03 = -13;  //房间视图：地下1层3区
	public static int LEVEL_ROOM_GARDEN_01 = 11;  //房间视图：花园客厅1区
	public static int LEVEL_ROOM_GARDEN_02 = 12;  //房间视图：花园客厅2区
	public static int LEVEL_ROOM_GARDEN_03 = 13;  //房间视图：花园客厅3区
	public static int LEVEL_ROOM_BEDROOM_01 = 21; //房间视图：卧室1区
	public static int LEVEL_ROOM_BEDROOM_02 = 22; //房间视图：卧室2区
	public static int LEVEL_ROOM_BEDROOM_03 = 23; //房间视图：卧室3区	
	public static int LEVEL_ROOM_TOP = 31; //房间视图:房屋顶层(1个区域)
	

	public AppCtrl() {
		MapLevel = LEVEL_FULLHOUSE;
		OpLevel = 0;
		RoomID = NULL;
		FloorID = NULL;
		OpDev = NULL;
		SelectedDevIdx = -1;
	}
}
