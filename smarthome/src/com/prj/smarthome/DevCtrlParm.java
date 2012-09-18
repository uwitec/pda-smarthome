package com.prj.smarthome;

import com.prj.smarthome.DevDsParam.TDev;

public class DevCtrlParm {
	
	private short MAXDEVTYPE = 128; //���128�ֵ��� 
	private short MAXDEVCOUNT = 1024; //ÿ�������1024�������豸
	
	public short DEVNAME_LED = 1;      //����
	public short DEVNAME_CURTAIN = 2;  //����
	public short DEVNAME_MOVIE = 3;    //��ͥӰԺ
	public short DEVNAME_MUSIC = 4;    //��������
	public short DEVNAME_SPEECH = 5;   //�����Խ�
	public short DEVNAME_CAMERA = 6;   //����ͷ
	public short DEVNAME_WARM = 7;     //��ů
	public short DEVNAME_AIRCOOL = 8;  //�յ�
	public short DEVNAME_DOOR = 9;     //�Ž�
	public short DEVNAME_SAFE = 10;    //����
	
	public class TDev {
		int Count; //�豸����
		public class TCtrl {
			int time; //�豸����ʱ���(��)
			int op;   //��������(���������,��������,�������,��ů�¶�etc������)
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
	
	/*��ȡini�ļ���ĳģʽmode�������豸�Ŀ��Ʋ���
	 * iniF : ini�ļ���������(�Ѷ�ȡ������)
	 * Devs[i] : �豸�����洢�ṹ,ÿ���豸��Ӧһ��Devs[i]Ԫ��,ÿ��Devs[i]Ԫ���ڰ������ɸ�TCoord[]����Ԫ��
	 *           ����:led�ƹ�(����)��ӦΪDevs[1],�����豸��ӦΪDevs[2]��������������
	 * ModeFileName  : ģʽini�ļ����� 
	 * */
	/*
	static int getDevCtrlList(IniReader iniF,TDev Dev,String ModeFileName) {
		
		int cnt = 0;
		String key,value;
		if (iniF.isExistSection(AreaName)) { //��AreaName������ini�ļ���
			Dev.Count = iniF.getKeys(AreaName);
			for (cnt=0;cnt<Dev.Count;cnt++) {
				key = iniF.Keys[cnt];
				value = iniF.getValue(AreaName, key);
				Dev.Coord[cnt].x = (short) iniF.getX(value);
				Dev.Coord[cnt].y = (short) iniF.getY(value);
			}
		} else { //AreaName��������ini�ļ���,����-1
			return -1;
		}
		
		return Dev.Count;
	}
	*/
	
}
