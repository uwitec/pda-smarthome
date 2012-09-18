package com.prj.smarthome;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.prj.smarthome.DevDsParam.TDev;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class SmartHomeActivity extends ListActivity { 
	
	private View.OnTouchListener Listener_btn_touch;  //��ť��������
	private View.OnTouchListener Listener_scrn_touch; //��������������
	private View.OnClickListener Listener_btn_click;  //��ť��������
	private View.OnClickListener Listener_scrn_click; //��������������
	private SurfaceView sfv_main = null; //��������
	private SurfaceHolder Holder_sfvMian = null; //����������ͼ����
	private static Boolean isExit = false;//�˳���־λ--����ر��˳�ʹ��
	public static int scrnX,scrnY = 0;//����������������
	public Bitmap bmp = null;
	public Bmp bmp_bkg = new Bmp(); //����ͼƬ--���ݽṹͼ,¥��,����ͼ--��Ϊ��ͼ��
	public Bmp bmp_dev = new Bmp(); //�豸ͼƬ--���ֵ�����ͼ��--��Ϊ������λ��ʾ��
	public AppCtrl AppCtrl = new AppCtrl(); //Ӧ�ÿ��Ʊ����ṹ��(��)
	
	private int RETURN_DEV_NAME = 1;
	private int RETURN_DEV_INDEX = 2;
	
	private int tmp_aim = 255;
	private int tmp_left=0,tmp_top=0;
	private float tmp_sx=0.0f,tmp_sy=0.0f;
	private float dotScale_ICON = 0.35f;
	private TextView lbl_info = null;	
	
	public DevDsParam DsParam = new DevDsParam(); //�豸��ʾ��������ṹ��
	public DevCtrlParm CtrlParam = new DevCtrlParm(); //�豸���Ʋ����ṹ��
	
	public IniReader iniF_dsParam = new IniReader(); //�豸��ʾ����ini�ļ�����
	public IniReader iniF_ctrlParam = new IniReader(); //�豸���Ʋ���ini�ļ�����
	
	public Bitmap bmp_mainSCRN = Bitmap.createBitmap(935, 570, Config.ARGB_8888); //������λͼ
	
	private List<String> List_Items = null;// ������:�б���[��������] 
	
	
	
	/*����ʹ��:������--��ʾ������Ӧ�û����������(ͼ��)����
	 * */
	private void drawAreaRect(int left,int top,int w,int h,int padding) {
		Canvas cnvs = Holder_sfvMian.lockCanvas();
		Paint pnt=new Paint();
		pnt.setColor(Color.GREEN);
		pnt.setAlpha(150);
		cnvs.drawRect(left-padding, top-padding, left+w+padding, top+h+padding, pnt);
		Holder_sfvMian.unlockCanvasAndPost(cnvs);
		
		//��������������䣺���ͼ����Ʋ�ȫ������
		Holder_sfvMian.lockCanvas(new Rect(0, 0, 0, 0));
		Holder_sfvMian.unlockCanvasAndPost(cnvs);
	}
	
	/*�жϵ�ǰ������x,y�����Ƿ�����ĳ�豸ͼ�����(ѡ������)��Χ��
	 * ����ѡ�ж�Ӧ��dev[i].ID,��δѡ���κ��豸�򷵻�-1
	 * TDev dev : �豸��������ṹ��
	 * x,y : �û���Ļ���������ֵ
	 * w,h : ICONͼ��(���������������ź�ͼ��)�Ŀ��,�߶�
	 * padding : ����(��չ��)�����ر�Ե�ߴ�--��ͼ��̫С,�û����Ե�ȡѡ������Ҫ����������չ�Ա����û���ָ���������ѡ��ͼ��
	 *           ��λ������ padding=10 �����ԭICON��left-10,top-10,bottom+10,right+10 ����������ı�������չ10����
	 *                  padding=10 �����ICON�����ı�������С10����
	 * ����:ѡ��Ԫ�ص�index���
	 * */
	public int whichDevSelected(TDev dev,int x,int y,int w,int h,int padding,int rtntype) {
		int i,side_x,side_y,match_cnt,delta_x,delta_y,min_dlt_x,min_dlt_y,min_idx;
		int[] bufidx = new int[256];
		match_cnt = 0;
		side_x = Math.round(w/2);
		side_y = Math.round(h/2);
		for (i=0;i<dev.Count;i++) {
			//drawAreaRect(dev.Coord[i].x-side_x, dev.Coord[i].y-side_y, w, h, padding); //����ʱʹ��--�������룡
			if (
				(((dev.Coord[i].x-side_x-padding)<=x)&&(x<=(dev.Coord[i].x+w+padding)))
				&&
				(((dev.Coord[i].y-side_y-padding)<=y)&&(y<=(dev.Coord[i].y+h+padding)))
				)
			{
				bufidx[match_cnt] = i; //�ݴ�����ͼ�������Ӧ������ֵindex--��dev[n]�е��±�
				match_cnt +=1; //���м�����+1 (Ӧ�Զ��Ŀ�����е����,ͼ���������)
			}
		}
		//���ڶ������ͬʱ��������--�������--ͬʱѡ����2�������ϵ��豸ͼ��
		//����������ж� -- x,y����˭�����ĵ������ѡ˭
		min_idx = -1;
		if (match_cnt>1) {
			min_dlt_x = Math.abs(dev.Coord[bufidx[0]].x-scrnX);
			min_dlt_y = Math.abs(dev.Coord[bufidx[0]].y-scrnY);
			min_idx = bufidx[0];
			for (i=1;i<match_cnt;i++) {
				delta_x = Math.abs(dev.Coord[bufidx[i]].x-scrnX);
				delta_y = Math.abs(dev.Coord[bufidx[i]].y-scrnY);
				if ((delta_x<=min_dlt_x) && (delta_y<min_dlt_y)) {
					min_dlt_x = delta_x;
					min_dlt_y = delta_y;
					min_idx = bufidx[i];
				}
			}
		} else if (match_cnt==1) {
			min_idx = bufidx[0];
		} else {
			min_idx = -1;
		}
		
		if (rtntype == RETURN_DEV_NAME) { //����ѡ���豸��NAME����
			if (min_idx!=-1) {
				return dev.Coord[min_idx].ID; //����ֵΪ  �豸ID(ini�ļ���16�����ַ�����Ӧ������)
			} else {
				return -1;
			}
		}else { //����ѡ���豸���������к�
			return min_idx;
		}
	}
	
	private void DrawDevSelectedMark(int left,int top,int w,int h,int padding) {
		Canvas cnvs = Holder_sfvMian.lockCanvas();
		Paint pnt=new Paint();
		pnt.setColor(Color.GREEN);
		pnt.setAlpha(150);
		pnt.setStyle(Style.STROKE);//���ľ��ο� 
		pnt.setStrokeWidth(2); //�����߿���
		cnvs.drawRect(left-padding, top-padding, left+w+padding, top+h+padding, pnt);
		Holder_sfvMian.unlockCanvasAndPost(cnvs);
		
		//��������������䣺���ͼ����Ʋ�ȫ������
		Holder_sfvMian.lockCanvas(new Rect(0, 0, 0, 0));
		Holder_sfvMian.unlockCanvasAndPost(cnvs);
	}
	
	/*UI��������:�ڷ�����ѡȡ�豸��
	 * ����ѡ���豸��ID���
	 * */
	public int UIACT_DevSelect(int opDev) {
		int idx,w,h,side_x,side_y = 0;
		w = Math.round(bmp_dev.Width * dotScale_ICON);
		h = Math.round(bmp_dev.Height * dotScale_ICON);
		idx = whichDevSelected(DsParam.Devs[opDev],scrnX,scrnY,w,h,0,RETURN_DEV_INDEX);
		if (idx!=-1) { 
			side_x = Math.round(w/2);
			side_y = Math.round(h/2);
			DrawDevsDot(Holder_sfvMian,
					DsParam.Devs[DevDsParam.DEVNAME_LED],
					DevDsParam.DEVNAME_LED,
					R.drawable.dot_led,
					dotScale_ICON,dotScale_ICON);
			DrawDevSelectedMark(DsParam.Devs[opDev].Coord[idx].x-side_x, 
								DsParam.Devs[opDev].Coord[idx].y-side_y, 
								w, h, 2);
			//Toast.makeText(SmartHomeActivity.this, "ѡ�У�"+Integer.toString(idx), Toast.LENGTH_SHORT).show();
		} else {
			//Toast.makeText(SmartHomeActivity.this, "δѡ���豸", Toast.LENGTH_SHORT).show();
		}
		return idx;
	}
	
	
	/*UI��������:��ĳ¥��ƽ��ͼ��ѡ�񷿼�(�ֲ�����)
	 * */
	public void UIACT_RoomSelect() {
		switch (AppCtrl.FloorID) {
		case -1 : //����1��ƽ��ͼ�ڵ� �������
			if (isInRect(scrnX, scrnY, 0, 0, 470, 250)) {
				tmp_aim = com.prj.smarthome.AppCtrl.LEVEL_ROOM_BOTTOM_01;
				tmp_left = 223;
				tmp_top = 1;
				tmp_sx = 1.0f;
				tmp_sy = 1.0f;
				bmp_bkg.setBmp(SmartHomeActivity.this.getResources(), R.drawable.splt_bottom01);
			} else if (isInRect(scrnX,scrnY,0,250,470,570)) {
				tmp_aim = com.prj.smarthome.AppCtrl.LEVEL_ROOM_BOTTOM_02;
				tmp_left = 23;
				tmp_top = 238;
				tmp_sx = 1.03f;
				tmp_sy = 1.0f;
				bmp_bkg.setBmp(SmartHomeActivity.this.getResources(), R.drawable.splt_bottom02);
			} else if (isInRect(scrnX,scrnY,470,0,935,570)) {
				tmp_aim = com.prj.smarthome.AppCtrl.LEVEL_ROOM_BOTTOM_03;
				tmp_left = 462;
				tmp_top = 205;
				tmp_sx = 1.04f;
				tmp_sy = 1.0f;	
				bmp_bkg.setBmp(SmartHomeActivity.this.getResources(), R.drawable.splt_bottom03);
			}
			break;
		case 1 :
			if (isInRect(scrnX, scrnY, 0, 0, 475, 330)) {
				tmp_aim = com.prj.smarthome.AppCtrl.LEVEL_ROOM_GARDEN_01;
				tmp_left = 10;
				tmp_top = 23;
				tmp_sx = 1.067f;
				tmp_sy = 1.0f;
				bmp_bkg.setBmp(SmartHomeActivity.this.getResources(), R.drawable.splt_garden01);
			} else if (isInRect(scrnX,scrnY,0,330,475,570)) {
				tmp_aim = com.prj.smarthome.AppCtrl.LEVEL_ROOM_GARDEN_02;
				tmp_left = 10;
				tmp_top = 320;
				tmp_sx = 1.065f;
				tmp_sy = 1.0f;
				bmp_bkg.setBmp(SmartHomeActivity.this.getResources(), R.drawable.splt_garden02);
			} else if (isInRect(scrnX,scrnY,475,0,935,570)) {
				tmp_aim = com.prj.smarthome.AppCtrl.LEVEL_ROOM_GARDEN_03;
				tmp_left = 463;
				tmp_top = 220;
				tmp_sx = 1.04f;
				tmp_sy = 1.0f;	
				bmp_bkg.setBmp(SmartHomeActivity.this.getResources(), R.drawable.splt_garden03);
			}							
			break;
		case 2 :
			if (isInRect(scrnX, scrnY, 0, 0, 465, 230)) {
				tmp_aim = com.prj.smarthome.AppCtrl.LEVEL_ROOM_BEDROOM_01;
				tmp_left = 130;
				tmp_top = 23;
				tmp_sx = 1.06f;
				tmp_sy = 1.0f;
				bmp_bkg.setBmp(SmartHomeActivity.this.getResources(), R.drawable.splt_bedroom01);
			} else if (isInRect(scrnX,scrnY,0,230,465,570)) {
				tmp_aim = com.prj.smarthome.AppCtrl.LEVEL_ROOM_BEDROOM_02;
				tmp_left = 20;
				tmp_top = 210;
				tmp_sx = 1.035f;
				tmp_sy = 1.0f;
				bmp_bkg.setBmp(SmartHomeActivity.this.getResources(), R.drawable.splt_bedroom02);
			} else if (isInRect(scrnX,scrnY,465,0,935,570)) {
				tmp_aim = com.prj.smarthome.AppCtrl.LEVEL_ROOM_BEDROOM_03;
				tmp_left = 445;
				tmp_top = 205;
				tmp_sx = 1.03f;
				tmp_sy = 1.0f;	
				bmp_bkg.setBmp(SmartHomeActivity.this.getResources(), R.drawable.splt_bedroom03);
			}							
			break;							
		case 3 :
			tmp_aim = com.prj.smarthome.AppCtrl.LEVEL_ROOM_TOP;
			tmp_left = 10;
			tmp_top = 14;
			tmp_sx = 1.06f;
			tmp_sy = 1.14f;	
			bmp_bkg.setBmp(SmartHomeActivity.this.getResources(), R.drawable.splt_top);							
			break;
		}
		
		SfcViewHolder_main.EraseArea(Holder_sfvMian, null, true); //��������(�����л�ǰ,�����ϴλ��ƵĿ���)
		if (AppCtrl.OpLevel==2) {
			AppCtrl.OpLevel = 3;
			AppCtrl.RoomID = tmp_aim;
			SfcViewHolder_main.DrawBmpScale(Holder_sfvMian, 180, tmp_left,tmp_top,
					bmp_bkg.Width, bmp_bkg.Height, bmp_bkg.img, tmp_sx, tmp_sy);//���Ʒ���ֲ�����
		} else if (AppCtrl.OpLevel == 3) {
			if (AppCtrl.RoomID != tmp_aim) {
				AppCtrl.RoomID = tmp_aim;
				SfcViewHolder_main.DrawBmpScale(Holder_sfvMian, 180, tmp_left,tmp_top,
						bmp_bkg.Width, bmp_bkg.Height, bmp_bkg.img, tmp_sx, tmp_sy);//���Ʒ���ֲ�����
			} else { //������ߺ�ڶ��ε���ڿ�����(ͬ��Χ˫��)--��Ϊ�û�ѡ�д˷���,���з���Ŵ�ͼ��ʾ
				AppCtrl.MapLevel = com.prj.smarthome.AppCtrl.LEVEL_ROOM; //MapLevel���뷿��ֲ� -- ����Ʒ���ֲ�ƽ��ͼ
				AppCtrl.OpLevel = 4; //��������=4 -- ����Ϊ������ֲ�ƽ��ͼ���Ʋ���
				SmartHomeActivity.this.onResume(); //���ƶ�����onResume (MapLevel=LEVEL_ROOM=2,RoomID=ѡ��¥���)								
			}
		}
		
	}

	
	/*UI��������:��ȫլ(ȫ¥��)Ԥ��ͼ��ѡ��¥��ƽ��
	 * */
	public void UIACT_FloorSelect() {
		if (scrnY<100) {
			tmp_aim = com.prj.smarthome.AppCtrl.LEVEL_FLOOR_TOP;
		} else if ((scrnY>100)&&(scrnY<215)) {
			tmp_aim = com.prj.smarthome.AppCtrl.LEVEL_FLOOR_BEDROOM;
		} else if ((scrnY>215)&&(scrnY<415)) {
			tmp_aim = com.prj.smarthome.AppCtrl.LEVEL_FLOOR_GARDEN;
		} else if ((scrnY>415)) {
			tmp_aim = com.prj.smarthome.AppCtrl.LEVEL_FLOOR_BOTTOM;
		}
		Draw_FullHouse(Holder_sfvMian,com.prj.smarthome.AppCtrl.FULL_ERASE);
		if (AppCtrl.OpLevel == 0) { // δѡ��ĳ¥��
			AppCtrl.OpLevel = 1;    // ����Ϊ����ѡ��¥��
			AppCtrl.FloorID = tmp_aim; //¥���
			Draw_FullHouse(Holder_sfvMian,AppCtrl.FloorID); //����ȫ��ͼ��������ѡ¥��
		}else if (AppCtrl.OpLevel ==1) {
			if (AppCtrl.FloorID!=tmp_aim) {
				AppCtrl.FloorID = tmp_aim;
				Draw_FullHouse(Holder_sfvMian,AppCtrl.FloorID);							
			} else {
			AppCtrl.MapLevel = com.prj.smarthome.AppCtrl.LEVEL_FLOOR; //MapLevel����¥��ƽ�� -- �����¥��ƽ��ͼ
			AppCtrl.OpLevel = 2; //��������=2 -- ����Ϊ��¥��ƽ��ͼ���Ʋ���
			SmartHomeActivity.this.onResume(); //���ƶ�����onResume (MapLevel=LEVEL_FLOOR=1,FloorID=ѡ��¥���)
			}
		}		
	}
	
	/*��ƽ��ͼ�ϻ����豸��ICONͼ��--��Ϊ�豸�ڷ����¥���ڵ�λ��
	 * */
	public void DrawDevsDot(SurfaceHolder holder,TDev dev,int opdev,int iconID,float sx,float sy) {
		bmp_dev.setBmp(this.getResources(), iconID);
	
		if ((AppCtrl.OpLevel != 3)&&(iconID != AppCtrl.OpDev)) {
			AppCtrl.OpDev = opdev;
			SfcViewHolder_main.EraseArea(Holder_sfvMian, null, true);
		} 
		
		SfcViewHolder_main.DrawMultBmpScale(holder, 255, dev, bmp_dev, sx, sy);
	}
	
	
	public boolean getDevDisplayParam(int DevType,AppCtrl app) {
		String Fpath= File.separator,Fname = "",AreaName = "";
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) { //�ж�SD���Ƿ����
			Fpath = Environment.getExternalStorageDirectory().getPath().toString();
			Fpath = Fpath + "/Homever/SmartHome/DevDspParam";			
			
			//���ݲ�������ָ��ini�ļ���
			switch (DevType) { //ini��Ŀ¼:��ͬ�豸�ڲ�ͬ��Ŀ¼
			case 1 : //LED �����豸
				Fname = "/led";
				break;
			case 2 : //�����豸
				Fname = "/curtain";
				break;
			//...
			}
			switch (app.MapLevel) { //�ļ���:��ͬ��������в�ͬ�ļ���
			case 0 :  //LEVEL_FULLHOUSE
				break;
			case 1 :  //LEVEL_FLOOR  ¥��ƽ��
				Fname = Fname+"/floor.ini"; //¥���ļ�
				switch (app.FloorID) {
				case -1 : //����1��
					AreaName = "floor_bottom";
					break;
				case 1 : //��԰��
					AreaName = "floor_garden";
					break;
				case 2 : //���Ҳ�
					AreaName = "floor_bedroom";
					break;
				case 3 : //�ݶ���
					AreaName = "floor_top";
					break;
				}
				break;
			case 2 :  //LEVEL_ROOM  ����ֲ�
				Fname += "/room.ini";
				switch (app.RoomID) {
				case -11 :
					AreaName = "room_bottom01";
					break;
				case -12 :
					AreaName = "room_bottom02";
					break;
				case -13 :
					AreaName = "room_bottom03";
					break;
				case 11 :
					AreaName = "room_garden01";
					break;
				case 12 :
					AreaName = "room_garden02";
					break;
				case 13 :
					AreaName = "room_garden03";
					break;
				case 21 :
					AreaName = "room_bedroom01";
					break;
				case 22 :
					AreaName = "room_bedroom02";
					break;
				case 23 :
					AreaName = "room_bedroom03";
					break;
				case 31 :
					AreaName = "room_top";
					break;
				}
				break;
			case 3 :  //LEVEL_ROOM3D  ����3D
				break;
			}
			
			//ƴ��ini�ļ�:����Fpath="/sdcard/Homever/SmartHome/DevDspParam/led/floor.ini"
			Fpath = Fpath + Fname;
			try {
				
				iniF_dsParam.load(Fpath); //���벢����ini�ļ�
				//��ini�ļ���AreaName�������м�ֵx,y�������DsParam.Devs[DevType]
				//DsParam.Devs[DevType].Coord[0].x,DsParam.Devs[DevType].Coord[0].y ��Ϊĳ�豸(DevType)��ĳ�ն˵����Ļ��ʾ����
				//������:��ͬini�ļ���¼���ǲ�ͬ���������  ȫլͼ�е�ĳ��  ¥��ͼ�е�ĳ��  ����ͼ�е�ĳ��  ����ֲ�3Dͼ�е�ĳ�� (�ֱ��Ӧ���Ե�ini�ļ�)
				//��DsParam.Devs[DevType].Count = -1  ��˵���޴��豸(ini�ļ���û��AreaName��������  ���� ��������(ini��)��������)
				DevDsParam.getDevCoordList(iniF_dsParam, DsParam.Devs[DevType], AreaName); 
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		} else {
			return false;
		}
		
		return true;
	}
	
	/*�趨��������
	 * */
	public void setRect(Rect rct,int left,int top,int width,int height) {
		rct.left = left;
		rct.top = top;
		rct.right = left+width;
		rct.bottom = left+height;
	}
	
	/*�ж�x,y�Ƿ��ھ���������
	 * */
	public boolean isInRect(int x,int y,int left,int top,int right,int bottom) {
		if (((x>left)&&(x<right))&&((y>top)&&(y<bottom))) {
			return true;
		} else {
			return false;
		}
	}
	
	
	
	
	
	/*����¥��ȫ����ͼ
	 * ������
	 * SurfaceHolder holder : ��ͼ��������(����)
	 * int actLevel : ������ʾ(�û������)¥��
	 *     = -1  �û�δ����κ�¥��
	 *     =  0  �û�����˵���1��
	 *     =  1 �û�����˻�԰������
	 *     =  2 �û���������Ҳ�
	 *     =  3 �û������¥�������
	 */
	public int Draw_FullHouse(SurfaceHolder holder,int actLevel) {
		switch (actLevel) {
		case -1 :
			bmp_bkg.setBmp(this.getResources(), R.drawable.house_bottom);
			SfcViewHolder_main.DrawBmp(holder, 255, 210,413,
					bmp_bkg.Width,bmp_bkg.Height, bmp_bkg.img);
			break;
		case 1 :
			bmp_bkg.setBmp(this.getResources(), R.drawable.house_garden);
			SfcViewHolder_main.DrawBmp(holder, 255, 59,231,
					bmp_bkg.Width,bmp_bkg.Height, bmp_bkg.img);			
			break;
		case 2 :
			bmp_bkg.setBmp(this.getResources(), R.drawable.house_bedroom);
			SfcViewHolder_main.DrawBmp(holder, 255, 290,82,
					bmp_bkg.Width,bmp_bkg.Height, bmp_bkg.img);	
			break;
		case 3 :
			bmp_bkg.setBmp(this.getResources(), R.drawable.house_top);
			SfcViewHolder_main.DrawBmp(holder, 255, 345,19,
					bmp_bkg.Width,bmp_bkg.Height, bmp_bkg.img);	
			break;
		case 255 :
			Rect area = null;
			SfcViewHolder_main.EraseArea(Holder_sfvMian, area, true);
			break;
		}
		return actLevel;
	}
	
    /** Called when the activity is first created. */
	/** yes , I change a little and commit to GC **/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //----����ʹ��
        lbl_info = (TextView)findViewById(R.id.lbl_info);
        bmp = BitmapFactory.decodeResource(this.getResources(), R.drawable.house_bottom);
        //--------------------��ͼ�ؼ��¼���-------------------------
        //��Ļ��ͼ�� X=[0..934] y=[0..569]ʵ��SurfaceView��ȫ͸��Ч��
        sfv_main = (SurfaceView)findViewById(R.id.sfcView_main);
        //--����surfaceViewΪ͸�����
        sfv_main.setZOrderOnTop(true);
        Holder_sfvMian=sfv_main.getHolder();
        Holder_sfvMian.setFormat(PixelFormat.TRANSPARENT);
        
        //----����ť�¼�����----
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
        
        //----��ť�����¼�
        Listener_btn_touch = new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (v.getId()) {
				case R.id.btn_safectrl :
		    		if(event.getAction() == MotionEvent.ACTION_DOWN){     
	                    //����Ϊ����ʱ�ı���ͼƬ     
		    			imgbtn_safectrl.setImageResource(R.drawable.btn_safedn);
		            }else if(event.getAction() == MotionEvent.ACTION_UP){     
	                    //��Ϊ̧��ʱ��ͼƬ     
		            	imgbtn_safectrl.setImageResource(R.drawable.btn_safeup);
		            }				
					break;
				case R.id.btn_doorctrl :
		    		if(event.getAction() == MotionEvent.ACTION_DOWN){     
	                    //����Ϊ����ʱ�ı���ͼƬ     
		    			imgbtn_doorctrl.setImageResource(R.drawable.btn_doordn);
		            }else if(event.getAction() == MotionEvent.ACTION_UP){     
	                    //��Ϊ̧��ʱ��ͼƬ     
		            	imgbtn_doorctrl.setImageResource(R.drawable.btn_doorup);
		            }				
					break;
				case R.id.btn_aircondnctrl :
		    		if(event.getAction() == MotionEvent.ACTION_DOWN){     
	                    //����Ϊ����ʱ�ı���ͼƬ     
		    			imgbtn_airconctrl.setImageResource(R.drawable.btn_aircondn);
		            }else if(event.getAction() == MotionEvent.ACTION_UP){     
	                    //��Ϊ̧��ʱ��ͼƬ     
		            	imgbtn_airconctrl.setImageResource(R.drawable.btn_airconup);
		            }				
					break;	
				case R.id.btn_warmctrl :
		    		if(event.getAction() == MotionEvent.ACTION_DOWN){     
	                    //����Ϊ����ʱ�ı���ͼƬ     
		    			imgbtn_warmctrl.setImageResource(R.drawable.btn_warmdn);
		            }else if(event.getAction() == MotionEvent.ACTION_UP){     
	                    //��Ϊ̧��ʱ��ͼƬ     
		            	imgbtn_warmctrl.setImageResource(R.drawable.btn_warmup);
		            }				
					break;	
				case R.id.btn_cameractrl :
		    		if(event.getAction() == MotionEvent.ACTION_DOWN){     
	                    //����Ϊ����ʱ�ı���ͼƬ     
		    			imgbtn_cameractrl.setImageResource(R.drawable.btn_cameradn);
		            }else if(event.getAction() == MotionEvent.ACTION_UP){     
	                    //��Ϊ̧��ʱ��ͼƬ     
		            	imgbtn_cameractrl.setImageResource(R.drawable.btn_cameraup);
		            }				
					break;	
				case R.id.btn_ledcrtl :
		    		if(event.getAction() == MotionEvent.ACTION_DOWN){     
	                    //����Ϊ����ʱ�ı���ͼƬ     
		    			imgbtn_ledctrl.setImageResource(R.drawable.btn_leddn);
		            }else if(event.getAction() == MotionEvent.ACTION_UP){     
	                    //��Ϊ̧��ʱ��ͼƬ     
		            	imgbtn_ledctrl.setImageResource(R.drawable.btn_ledup);
		            } 					
					break;
				case R.id.btn_curtaincrtl :
		    		if(event.getAction() == MotionEvent.ACTION_DOWN){     
	                    //����Ϊ����ʱ�ı���ͼƬ     
		    			imgbtn_curtainctrl.setImageResource(R.drawable.btn_curtaindn);
		            }else if(event.getAction() == MotionEvent.ACTION_UP){     
	                    //��Ϊ̧��ʱ��ͼƬ     
		            	imgbtn_curtainctrl.setImageResource(R.drawable.btn_curtainup);
		            }				
					break;
				case R.id.btn_moviecrtl :
		    		if(event.getAction() == MotionEvent.ACTION_DOWN){     
	                    //����Ϊ����ʱ�ı���ͼƬ     
		    			imgbtn_moviectrl.setImageResource(R.drawable.btn_moviedn);
		            }else if(event.getAction() == MotionEvent.ACTION_UP){     
	                    //��Ϊ̧��ʱ��ͼƬ     
		            	imgbtn_moviectrl.setImageResource(R.drawable.btn_movieup);
		            }				
					break;	
				case R.id.btn_musiccrtl :
		    		if(event.getAction() == MotionEvent.ACTION_DOWN){     
	                    //����Ϊ����ʱ�ı���ͼƬ     
		    			imgbtn_musictrl.setImageResource(R.drawable.btn_musicdn);
		            }else if(event.getAction() == MotionEvent.ACTION_UP){     
	                    //��Ϊ̧��ʱ��ͼƬ     
		            	imgbtn_musictrl.setImageResource(R.drawable.btn_musicup);
		            }				
					break;					
				case R.id.btn_speechcrtl :
		    		if(event.getAction() == MotionEvent.ACTION_DOWN){     
	                    //����Ϊ����ʱ�ı���ͼƬ     
		    			imgbtn_speechctrl.setImageResource(R.drawable.btn_speechdn);
		            }else if(event.getAction() == MotionEvent.ACTION_UP){     
	                    //��Ϊ̧��ʱ��ͼƬ     
		            	imgbtn_speechctrl.setImageResource(R.drawable.btn_speechup);
		            }				
					break;				
				}
				return false;
			}
		};
		
		Listener_btn_click = new View.OnClickListener() {
			
			//@SuppressLint("SdCardPath")
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				switch (v.getId()) {
				case R.id.btn_ledcrtl :
					AppCtrl.OpDev = DevDsParam.DEVNAME_LED;
					if (true==getDevDisplayParam(DevDsParam.DEVNAME_LED,AppCtrl)) {
						//��ini�ļ���ȡƽ��ͼ�ڵ��豸��λ����
						lbl_info.setText(Integer.toString(DsParam.Devs[DevDsParam.DEVNAME_LED].Count));
						DrawDevsDot(Holder_sfvMian,
									DsParam.Devs[DevDsParam.DEVNAME_LED],
									DevDsParam.DEVNAME_LED,
									R.drawable.dot_led,
									dotScale_ICON,dotScale_ICON);
					} else {
						Toast.makeText(SmartHomeActivity.this, "�ļ������ڻ��޸÷�������", Toast.LENGTH_SHORT).show();
					}
					
					List_Items = null;
					List_Items = new ArrayList<String>();
					List_Items.add("�ƹ�ȫ��");
					List_Items.add("�ƹ�ȫ��");
					List_Items.add("��ؿ���");
					List_Items.add("��عص�");
					List_Items.add("��������");
					//�������������󶨵�ListActivity
					setListAdapter(new ListAdapter(SmartHomeActivity.this, List_Items, AppCtrl.OpDev ));					

					break;
				case R.id.btn_curtaincrtl :
					
					LayoutInflater inflater = getLayoutInflater();
					View layout = inflater.inflate(R.layout.dialog,
									(ViewGroup) findViewById(R.id.dialog));

				    new AlertDialog.Builder(SmartHomeActivity.this)
					   .setTitle("�Զ��岼��").setView(layout)
					   .setIcon(android.R.drawable.ic_menu_more)
					   .setPositiveButton("ȷ��",new OnClickListener() {
						   @Override
						   public void onClick(DialogInterface dialog, int which) {
						    dialog.dismiss();
						   }})
					   .setNegativeButton("ȡ��", null)
					   .show();

					
					/*
					Dialog_LedCtrl dialog1 = new Dialog_LedCtrl(SmartHomeActivity.this, R.layout.layout_dialog, R.style.Theme_dialog);//Dialogʹ��Ĭ�ϴ�С(160) 
					Dialog_LedCtrl dialog2 = new Dialog_LedCtrl(SmartHomeActivity.this, 500, 300, R.layout.layout_dialog, R.style.Theme_dialog);
			        dialog2.setTitle("����");
					dialog2.show();//��ʾDialog
			        TextView mMessage = (TextView) dialog1.findViewById(R.id.message);
			        mMessage.setText("������...");
			        */
					AppCtrl.OpDev = DevDsParam.DEVNAME_CURTAIN;
					if (true==getDevDisplayParam(DevDsParam.DEVNAME_CURTAIN,AppCtrl)) {
						//��ini�ļ���ȡƽ��ͼ�ڵ��豸��λ����
						lbl_info.setText(Integer.toString(DsParam.Devs[DevDsParam.DEVNAME_CURTAIN].Count));
						DrawDevsDot(Holder_sfvMian,
									DsParam.Devs[DevDsParam.DEVNAME_CURTAIN],
									DevDsParam.DEVNAME_CURTAIN,
									R.drawable.dot_led,
									dotScale_ICON,dotScale_ICON);
					} else {
						Toast.makeText(SmartHomeActivity.this, "�ļ������ڻ��޸÷�������", Toast.LENGTH_SHORT).show();
					}					
					break;
				case R.id.btn_cameractrl :
					AppCtrl.OpDev = DevDsParam.DEVNAME_CAMERA;
					Toast.makeText(SmartHomeActivity.this, "��������ͷ���", Toast.LENGTH_SHORT).show();
	            	Intent step1 = new Intent(SmartHomeActivity.this, MultiCameraActivity.class);
	            	startActivity(step1);					
					break;
				}
				//----��ʾ�豸��ͼ
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
		
		imgbtn_ledctrl.setOnClickListener(Listener_btn_click);
		imgbtn_curtainctrl.setOnClickListener(Listener_btn_click);
		imgbtn_cameractrl.setOnClickListener(Listener_btn_click);
		//----end of ����ť�¼����� ----
		
		//----����������----
		//----�����������¼�----
		Listener_scrn_touch = new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (v.getId()) {
				case R.id.sfcView_main :
	        		if (event.getAction() == MotionEvent.ACTION_DOWN)
	        		{
	        			scrnX = (int) event.getX();
	                    scrnY = (int) event.getY();
	        		}			
					break;
				}
				return false;
			}
		};//end of Listener_scrn_touch
		
		//----�����������¼�
		Listener_scrn_click = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				switch (v.getId()) {
				case R.id.sfcView_main :
					lbl_info.setText(Integer.toString(scrnX)+","+Integer.toString(scrnY));
					switch (AppCtrl.MapLevel) {
					//¥������͸��ͼ==LEVEL_FULLHOUSE opLevel=1
					case 0 : 
						UIACT_FloorSelect(); //UI��������:¥��ѡȡ
						break;
					//¥��ƽ��ͼ==LEVEL_FLOOR :opLevel = 2 -->¥��ƽ��ͼ�ڵĵ�� -->�����������Ϊ����ֲ�
					case 1 : 
						UIACT_RoomSelect();  //UI��������:����ѡȡ
						break;
					//����ֲ�ͼ==LEVEL_ROOM :opLevel = 4 -->����ƽ��ͼ�ڵĵ�� -->����������豸��ICON
					case 2 :
						//UI��������:�豸ѡȡ,���õ�ѡ���豸��������Index
						AppCtrl.SelectedDevIdx = UIACT_DevSelect(AppCtrl.OpDev);  
						break;
					}
					break;

				}
			}
		};//end of Listener_scrn_click
		
		sfv_main.setOnTouchListener(Listener_scrn_touch);
		sfv_main.setOnClickListener(Listener_scrn_click);

		
		//------------��ť�����¼�
		
    } //end of onCreat
    
   /* 
    * */
    /* ���񰴼������¼� -- ���β���
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { //���µ������BACK��ͬʱû���ظ�
           Toast.makeText(SmartHomeActivity.this,"ħ��ȥ��Back������",0).show();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
    */
    
    @Override
    public void onBackPressed() {
    	super.onBackPressed();
    	switch (AppCtrl.MapLevel) {
    	case 0 : //¥��ȫ��ͼ
    		Draw_FullHouse(Holder_sfvMian,com.prj.smarthome.AppCtrl.FULL_ERASE);
    		if (AppCtrl.OpLevel>0) {AppCtrl.OpLevel -= 1;}
    		break;
    		
    	case 1 : //��¥��ƽ����ͼ���ϻ���
    		if (AppCtrl.OpLevel == 3) {
    			AppCtrl.OpLevel = 2;
    			AppCtrl.RoomID = com.prj.smarthome.AppCtrl.NULL;
    			SfcViewHolder_main.EraseArea(Holder_sfvMian, null, true);
    		} else {
        		AppCtrl.RoomID = com.prj.smarthome.AppCtrl.NULL;
    			AppCtrl.OpLevel = 1; //����ĳ��(֮ǰ��¥���ѡ)����
    			AppCtrl.MapLevel = 0; //�˻�¥��ȫ��ͼ    	
    		}
			this.onResume();
    		break;
    		
    	case 2 : //�ɷ���ֲ��Ŵ�ͼ ���ϻ���
    		AppCtrl.MapLevel = 1;
    		AppCtrl.OpLevel = 3;  //����֮ǰѡ��ķ����������
    		this.onResume();
    		break;
    	}
    		

    }//end of onBackPressed
    
    //��д�����˳��¼�����back������ʱ����
	@Override
	public void finish()
	{
		if (isExit == false)
		{// ��һ�ΰ��·��ؼ�
			if (AppCtrl.OpLevel==0) {
				isExit = true;//�˳���־λ���ó�true
				Toast.makeText(this, "�ٰ�һ�κ��˼��˳�Ӧ�ó���", Toast.LENGTH_SHORT).show();//���û���ʾ
				//һ��Timer()��������û��ڵ�һ�ΰ����ؼ������û���ٰ�һ�η��ؼ��˳�����ʾ�û�ȡ���˲����������½���־λ���ó�false
				new Timer().schedule(new TimerTask()
				{
					@Override
					public void run()
					{
						isExit = false;
					}
				}, 2000);
			}
		} else
		{//�˳�����
			SmartHomeActivity.super.finish();
		}
	}//end of finish    
    
    @Override    
    public void onResume() {
    	super.onResume();  
    	sfv_main.post(new Runnable() {
        	@Override
            public void run() { 
        		switch (AppCtrl.MapLevel) {
        		
        		case 0 : //=LEVEL_FULLHOUSE
        			sfv_main.setBackgroundResource(R.drawable.housefull);//����Ϊȫ��ͼ
        			SfcViewHolder_main.EraseArea(Holder_sfvMian, null, true);
        			Draw_FullHouse(Holder_sfvMian,AppCtrl.FloorID); //���غ�--����(�ϴ�ѡ��)����������¥�� 
        			break;
        			
        		case 1 : //=LEVEL_FLOOR
        			switch (AppCtrl.FloorID) {
        			case -1 : //����1��
        				sfv_main.setBackgroundResource(R.drawable.floor_bottom);
        				break;
        			case 1 :  //��԰������
        				sfv_main.setBackgroundResource(R.drawable.floor_garden);
        				break;
        			case 2 :  //����1��
        				sfv_main.setBackgroundResource(R.drawable.floor_bedroom);
        				break;
        			case 3 :  //�ݶ����ⷿ2��
        				sfv_main.setBackgroundResource(R.drawable.floor_top);
        				break;
        			}
        			SfcViewHolder_main.EraseArea(Holder_sfvMian, null, true);
        			if (AppCtrl.RoomID != com.prj.smarthome.AppCtrl.NULL) {
        				SfcViewHolder_main.DrawBmpScale(Holder_sfvMian, 255, tmp_left,tmp_top,
        						bmp_bkg.Width, bmp_bkg.Height, bmp_bkg.img, tmp_sx, tmp_sy);//���Ʒ���ֲ�����
        			} 
        			break;
        			
        		case 2 : //=LEVEL_ROOM
        			switch (AppCtrl.RoomID) {
        			case -11 :
        				sfv_main.setBackgroundResource(R.drawable.zoom_bottom01);
        				break;
        			case -12 :
        				sfv_main.setBackgroundResource(R.drawable.zoom_bottom02);
        				break;
        			case -13 :
        				sfv_main.setBackgroundResource(R.drawable.zoom_bottom03);
        				break;
        			case 11 :
        				sfv_main.setBackgroundResource(R.drawable.zoom_garden01);
        				break;
        			case 12 :
        				sfv_main.setBackgroundResource(R.drawable.zoom_garden02);
        				break;
        			case 13 :
        				sfv_main.setBackgroundResource(R.drawable.zoom_garden03);
        				break;
        			case 21 :
        				sfv_main.setBackgroundResource(R.drawable.zoom_bedroom01);
        				break;
        			case 22 :
        				sfv_main.setBackgroundResource(R.drawable.zoom_bedroom02);
        				break;
        			case 23 :
        				sfv_main.setBackgroundResource(R.drawable.zoom_bedroom03);
        				break;  
        			case 31 :
        				sfv_main.setBackgroundResource(R.drawable.floor_top); 
        				break;
        			}
        			break;
        		}
        	}
        });
    }//end of onResume
    
} //end of Activity