package com.prj.smarthome;

/*
 * 16�����ַ���תint
int i = Integer.valueOf("12df",16);
lbl_info.setText(Integer.toString(i));

intת16�����ַ���
String ss = String.format("%05x", i);
lbl_info.setText(ss);
*/

public class DevDsParam {
	
	private short MAXDEVTYPE = 128; //���128�ֵ��� 
	private short MAXDEVCOUNT = 256; //ÿ�������256�������豸
	
	public static short DEVNAME_LED = 1;      //����
	public static short DEVNAME_CURTAIN = 2;  //����
	public static short DEVNAME_MOVIE = 3;    //��ͥӰԺ
	public static short DEVNAME_MUSIC = 4;    //��������
	public static short DEVNAME_SPEECH = 5;   //�����Խ�
	public static short DEVNAME_CAMERA = 6;   //����ͷ
	public static short DEVNAME_WARM = 7;     //��ů
	public static short DEVNAME_AIRCOOL = 8;  //�յ�
	public static short DEVNAME_DOOR = 9;     //�Ž�
	public static short DEVNAME_SAFE = 10;    //����
	
	public int AreaName; //¥��;����;����3D
	
	public class TDev {
		int Count; //�豸����
		public class TCoord {
			short x;
			short y;
			int ID; //�豸���
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
		AreaName = 255; //��Ϊ��
		for(i=0;i<MAXDEVTYPE;i++) {
			Devs[i] = new TDev();
		}
	}
	
	/*��ȡini�ļ���AreaID���������豸������
	 * iniF : ini�ļ���������(�Ѷ�ȡ������)
	 * Devs[i] : �豸�����洢�ṹ,ÿ���豸��Ӧһ��Devs[i]Ԫ��,ÿ��Devs[i]Ԫ���ڰ������ɸ�TCoord[]����Ԫ��
	 *           ����:led�ƹ�(����)��ӦΪDevs[1],�����豸��ӦΪDevs[2]��������������
	 * AreaName  : �������� --��ӦΪini�ļ��е�section�ַ����� 
	 * */
	static int getDevCoordList(IniReader iniF,TDev Dev,String AreaName) {
		int cnt = 0;
		String name,value;
		if (iniF.isExistSection(AreaName)) { //��AreaName������ini�ļ���
			Dev.Count = iniF.getNames(AreaName);
			for (cnt=0;cnt<Dev.Count;cnt++) {
				name = iniF.Names[cnt];
				value = iniF.getValue(AreaName, name);
				Dev.Coord[cnt].ID = Integer.valueOf(name,16); //16�����ַ���ת����int
				Dev.Coord[cnt].x = (short) iniF.getX(value);
				Dev.Coord[cnt].y = (short) iniF.getY(value);
			}
		} else { //AreaName��������ini�ļ���,����-1
			Dev.Count = -1;
		}
		return Dev.Count; 
	}

}
