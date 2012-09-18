package com.prj.smarthome;

public class AppCtrl {
	public int MapLevel; //��ͼ���
	public int OpLevel;  //������� - ���ˡ�ǰ������
	public int OpDev;    //��������
	public int SelectedDevIdx; //��ȡѡ���豸��index������
	public int FloorID;  //¥���ʶ
	public int RoomID;   //�����ʶ
	
	public static int FULL_ERASE = 255;
	public static int NULL = 255;
	
	//MapLevel����ȡֵ
	public static int LEVEL_FULLHOUSE = 0;        //����ȫ��ͼ
	public static int LEVEL_FLOOR = 1;            //¥����ͼ
	public static int LEVEL_ROOM = 2;             //������ͼ
	public static int LEVEL_ROOM3D = 3;           //����ֲ�3D��ͼ
	
	
	//FloorID,RoomID����ȡֵ
	public static int LEVEL_FLOOR_BOTTOM = -1;     //¥����ͼ������1��    -1��
	public static int LEVEL_FLOOR_GARDEN =1;      //¥����ͼ����԰������      1��
	public static int LEVEL_FLOOR_BEDROOM = 2;    //¥����ͼ�����Ҳ�               2��
	public static int LEVEL_FLOOR_TOP = 3;        //¥����ͼ���ݶ������      3��
	public static int LEVEL_ROOM_BOTTOM_01 = -11;  //������ͼ������1�� 1��
	public static int LEVEL_ROOM_BOTTOM_02 = -12;  //������ͼ������1��2��
	public static int LEVEL_ROOM_BOTTOM_03 = -13;  //������ͼ������1��3��
	public static int LEVEL_ROOM_GARDEN_01 = 11;  //������ͼ����԰����1��
	public static int LEVEL_ROOM_GARDEN_02 = 12;  //������ͼ����԰����2��
	public static int LEVEL_ROOM_GARDEN_03 = 13;  //������ͼ����԰����3��
	public static int LEVEL_ROOM_BEDROOM_01 = 21; //������ͼ������1��
	public static int LEVEL_ROOM_BEDROOM_02 = 22; //������ͼ������2��
	public static int LEVEL_ROOM_BEDROOM_03 = 23; //������ͼ������3��	
	public static int LEVEL_ROOM_TOP = 31; //������ͼ:���ݶ���(1������)
	

	public AppCtrl() {
		MapLevel = LEVEL_FULLHOUSE;
		OpLevel = 0;
		RoomID = NULL;
		FloorID = NULL;
		OpDev = NULL;
		SelectedDevIdx = -1;
	}
}
