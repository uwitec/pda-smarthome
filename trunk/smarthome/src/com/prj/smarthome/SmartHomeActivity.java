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
	
	private View.OnTouchListener Listener_btn_touch;  //按钮触摸监听
	private View.OnTouchListener Listener_scrn_touch; //触摸屏触摸监听
	private View.OnClickListener Listener_btn_click;  //按钮单击监听
	private View.OnClickListener Listener_scrn_click; //触摸屏单击监听
	private SurfaceView sfv_main = null; //主触摸屏
	private SurfaceHolder Holder_sfvMian = null; //主触摸屏绘图对象
	private static Boolean isExit = false;//退出标志位--程序关闭退出使用
	public static int scrnX,scrnY = 0;//主触摸屏坐标点变量
	public Bitmap bmp = null;
	public Bmp bmp_bkg = new Bmp(); //背景图片--房屋结构图,楼层,房间图--作为底图用
	public Bmp bmp_dev = new Bmp(); //设备图片--各种电器的图标--作为电器点位显示用
	public AppCtrl AppCtrl = new AppCtrl(); //应用控制变量结构体(类)
	
	private int RETURN_DEV_NAME = 1;
	private int RETURN_DEV_INDEX = 2;
	
	private int tmp_aim = 255;
	private int tmp_left=0,tmp_top=0;
	private float tmp_sx=0.0f,tmp_sy=0.0f;
	private float dotScale_ICON = 0.35f;
	private TextView lbl_info = null;	
	
	public DevDsParam DsParam = new DevDsParam(); //设备显示坐标参数结构体
	public DevCtrlParm CtrlParam = new DevCtrlParm(); //设备控制参数结构体
	
	public IniReader iniF_dsParam = new IniReader(); //设备显示参数ini文件对象
	public IniReader iniF_ctrlParam = new IniReader(); //设备控制参数ini文件对象
	
	public Bitmap bmp_mainSCRN = Bitmap.createBitmap(935, 570, Config.ARGB_8888); //创建空位图
	
	private List<String> List_Items = null;// 适配器:列别名[功能名称] 
	
	
	
	/*测试使用:画矩形--标示出可相应用户点击动作的(图标)区域
	 * */
	private void drawAreaRect(int left,int top,int w,int h,int padding) {
		Canvas cnvs = Holder_sfvMian.lockCanvas();
		Paint pnt=new Paint();
		pnt.setColor(Color.GREEN);
		pnt.setAlpha(150);
		cnvs.drawRect(left-padding, top-padding, left+w+padding, top+h+padding, pnt);
		Holder_sfvMian.unlockCanvasAndPost(cnvs);
		
		//必须加入下列两句：解决图像绘制不全的问题
		Holder_sfvMian.lockCanvas(new Rect(0, 0, 0, 0));
		Holder_sfvMian.unlockCanvasAndPost(cnvs);
	}
	
	/*判断当前触摸点x,y坐标是否属于某设备图标矩形(选中区域)范围内
	 * 返回选中对应的dev[i].ID,若未选中任何设备则返回-1
	 * TDev dev : 设备坐标数组结构体
	 * x,y : 用户屏幕点击的坐标值
	 * w,h : ICON图标(若有缩放则是缩放后图标)的宽度,高度
	 * padding : 托盘(扩展区)的像素边缘尺寸--若图标太小,用户难以点取选中则需要进行托盘扩展以便于用户手指点击更容易选中图标
	 *           单位是像素 padding=10 则代表原ICON的left-10,top-10,bottom+10,right+10 矩形区域的四边向外扩展10像素
	 *                  padding=10 则代表ICON矩形四边向内缩小10像素
	 * 返回:选中元素的index序号
	 * */
	public int whichDevSelected(TDev dev,int x,int y,int w,int h,int padding,int rtntype) {
		int i,side_x,side_y,match_cnt,delta_x,delta_y,min_dlt_x,min_dlt_y,min_idx;
		int[] bufidx = new int[256];
		match_cnt = 0;
		side_x = Math.round(w/2);
		side_y = Math.round(h/2);
		for (i=0;i<dev.Count;i++) {
			//drawAreaRect(dev.Coord[i].x-side_x, dev.Coord[i].y-side_y, w, h, padding); //调试时使用--保留代码！
			if (
				(((dev.Coord[i].x-side_x-padding)<=x)&&(x<=(dev.Coord[i].x+w+padding)))
				&&
				(((dev.Coord[i].y-side_y-padding)<=y)&&(y<=(dev.Coord[i].y+h+padding)))
				)
			{
				bufidx[match_cnt] = i; //暂存命中图标区域对应的索引值index--在dev[n]中的下标
				match_cnt +=1; //命中计数器+1 (应对多个目标命中的情况,图标区域叠加)
			}
		}
		//存在多个区域同时满足条件--区域叠加--同时选中了2个及以上的设备图标
		//须进行趋近判断 -- x,y距离谁的中心点最近则选谁
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
		
		if (rtntype == RETURN_DEV_NAME) { //返回选中设备的NAME编码
			if (min_idx!=-1) {
				return dev.Coord[min_idx].ID; //返回值为  设备ID(ini文件中16进制字符串对应的整数)
			} else {
				return -1;
			}
		}else { //返回选中设备的数组序列号
			return min_idx;
		}
	}
	
	private void DrawDevSelectedMark(int left,int top,int w,int h,int padding) {
		Canvas cnvs = Holder_sfvMian.lockCanvas();
		Paint pnt=new Paint();
		pnt.setColor(Color.GREEN);
		pnt.setAlpha(150);
		pnt.setStyle(Style.STROKE);//空心矩形框 
		pnt.setStrokeWidth(2); //设置线框宽度
		cnvs.drawRect(left-padding, top-padding, left+w+padding, top+h+padding, pnt);
		Holder_sfvMian.unlockCanvasAndPost(cnvs);
		
		//必须加入下列两句：解决图像绘制不全的问题
		Holder_sfvMian.lockCanvas(new Rect(0, 0, 0, 0));
		Holder_sfvMian.unlockCanvasAndPost(cnvs);
	}
	
	/*UI交互动作:在房间内选取设备点
	 * 返回选中设备的ID编号
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
			//Toast.makeText(SmartHomeActivity.this, "选中："+Integer.toString(idx), Toast.LENGTH_SHORT).show();
		} else {
			//Toast.makeText(SmartHomeActivity.this, "未选中设备", Toast.LENGTH_SHORT).show();
		}
		return idx;
	}
	
	
	/*UI交互动作:在某楼层平面图内选择房间(局部区域)
	 * */
	public void UIACT_RoomSelect() {
		switch (AppCtrl.FloorID) {
		case -1 : //地下1层平面图内的 触屏点击
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
		
		SfcViewHolder_main.EraseArea(Holder_sfvMian, null, true); //擦除框线(框线切换前,擦除上次绘制的框线)
		if (AppCtrl.OpLevel==2) {
			AppCtrl.OpLevel = 3;
			AppCtrl.RoomID = tmp_aim;
			SfcViewHolder_main.DrawBmpScale(Holder_sfvMian, 180, tmp_left,tmp_top,
					bmp_bkg.Width, bmp_bkg.Height, bmp_bkg.img, tmp_sx, tmp_sy);//绘制房间局部框线
		} else if (AppCtrl.OpLevel == 3) {
			if (AppCtrl.RoomID != tmp_aim) {
				AppCtrl.RoomID = tmp_aim;
				SfcViewHolder_main.DrawBmpScale(Holder_sfvMian, 180, tmp_left,tmp_top,
						bmp_bkg.Width, bmp_bkg.Height, bmp_bkg.img, tmp_sx, tmp_sy);//绘制房间局部框线
			} else { //绘出框线后第二次点击在框线内(同范围双击)--认为用户选中此房间,进行房间放大图显示
				AppCtrl.MapLevel = com.prj.smarthome.AppCtrl.LEVEL_ROOM; //MapLevel进入房间局部 -- 需绘制房间局部平面图
				AppCtrl.OpLevel = 4; //操作层面=4 -- 操作为：房间局部平面图绘制操作
				SmartHomeActivity.this.onResume(); //绘制动作在onResume (MapLevel=LEVEL_ROOM=2,RoomID=选中楼层号)								
			}
		}
		
	}

	
	/*UI交互动作:在全宅(全楼宇)预览图上选择楼层平面
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
		if (AppCtrl.OpLevel == 0) { // 未选择某楼层
			AppCtrl.OpLevel = 1;    // 操作为：已选择楼层
			AppCtrl.FloorID = tmp_aim; //楼层号
			Draw_FullHouse(Holder_sfvMian,AppCtrl.FloorID); //房屋全局图：高亮被选楼层
		}else if (AppCtrl.OpLevel ==1) {
			if (AppCtrl.FloorID!=tmp_aim) {
				AppCtrl.FloorID = tmp_aim;
				Draw_FullHouse(Holder_sfvMian,AppCtrl.FloorID);							
			} else {
			AppCtrl.MapLevel = com.prj.smarthome.AppCtrl.LEVEL_FLOOR; //MapLevel进入楼层平面 -- 需绘制楼层平面图
			AppCtrl.OpLevel = 2; //操作层面=2 -- 操作为：楼层平面图绘制操作
			SmartHomeActivity.this.onResume(); //绘制动作在onResume (MapLevel=LEVEL_FLOOR=1,FloorID=选中楼层号)
			}
		}		
	}
	
	/*在平面图上绘制设备点ICON图标--作为设备在房间或楼层内的位置
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
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) { //判断SD卡是否存在
			Fpath = Environment.getExternalStorageDirectory().getPath().toString();
			Fpath = Fpath + "/Homever/SmartHome/DevDspParam";			
			
			//根据操作对象指定ini文件名
			switch (DevType) { //ini子目录:不同设备在不同子目录
			case 1 : //LED 照明设备
				Fname = "/led";
				break;
			case 2 : //窗帘设备
				Fname = "/curtain";
				break;
			//...
			}
			switch (app.MapLevel) { //文件名:不同操作层次有不同文件名
			case 0 :  //LEVEL_FULLHOUSE
				break;
			case 1 :  //LEVEL_FLOOR  楼层平面
				Fname = Fname+"/floor.ini"; //楼层文件
				switch (app.FloorID) {
				case -1 : //地下1层
					AreaName = "floor_bottom";
					break;
				case 1 : //花园层
					AreaName = "floor_garden";
					break;
				case 2 : //卧室层
					AreaName = "floor_bedroom";
					break;
				case 3 : //屋顶层
					AreaName = "floor_top";
					break;
				}
				break;
			case 2 :  //LEVEL_ROOM  房间局部
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
			case 3 :  //LEVEL_ROOM3D  房间3D
				break;
			}
			
			//拼接ini文件:形如Fpath="/sdcard/Homever/SmartHome/DevDspParam/led/floor.ini"
			Fpath = Fpath + Fname;
			try {
				
				iniF_dsParam.load(Fpath); //载入并解析ini文件
				//将ini文件里AreaName节中所有键值x,y坐标读入DsParam.Devs[DevType]
				//DsParam.Devs[DevType].Coord[0].x,DsParam.Devs[DevType].Coord[0].y 即为某设备(DevType)类某终端点的屏幕显示坐标
				//此坐标:不同ini文件记录的是不同层面的坐标  全宅图中的某点  楼层图中的某点  房间图中的某点  房间局部3D图中的某点 (分别对应各自的ini文件)
				//若DsParam.Devs[DevType].Count = -1  则说明无此设备(ini文件内没有AreaName的区域名  或者 该区域内(ini节)内无数据)
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
	
	/*设定矩形区域
	 * */
	public void setRect(Rect rct,int left,int top,int width,int height) {
		rct.left = left;
		rct.top = top;
		rct.right = left+width;
		rct.bottom = left+height;
	}
	
	/*判断x,y是否在矩形区域内
	 * */
	public boolean isInRect(int x,int y,int left,int top,int right,int bottom) {
		if (((x>left)&&(x<right))&&((y>top)&&(y<bottom))) {
			return true;
		} else {
			return false;
		}
	}
	
	
	
	
	
	/*绘制楼层全景视图
	 * 参数：
	 * SurfaceHolder holder : 绘图操作对象(画布)
	 * int actLevel : 高亮显示(用户点击的)楼层
	 *     = -1  用户未点击任何楼层
	 *     =  0  用户点击了地下1层
	 *     =  1 用户点击了花园客厅层
	 *     =  2 用户点击了卧室层
	 *     =  3 用户点击了楼顶阳光层
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
        //----调试使用
        lbl_info = (TextView)findViewById(R.id.lbl_info);
        bmp = BitmapFactory.decodeResource(this.getResources(), R.drawable.house_bottom);
        //--------------------绘图控件事件区-------------------------
        //屏幕绘图区 X=[0..934] y=[0..569]实现SurfaceView的全透明效果
        sfv_main = (SurfaceView)findViewById(R.id.sfcView_main);
        //--设置surfaceView为透明风格
        sfv_main.setZOrderOnTop(true);
        Holder_sfvMian=sfv_main.getHolder();
        Holder_sfvMian.setFormat(PixelFormat.TRANSPARENT);
        
        //----主按钮事件控制----
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
        
        //----按钮触碰事件
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
					break;
				case R.id.btn_doorctrl :
		    		if(event.getAction() == MotionEvent.ACTION_DOWN){     
	                    //更改为按下时的背景图片     
		    			imgbtn_doorctrl.setImageResource(R.drawable.btn_doordn);
		            }else if(event.getAction() == MotionEvent.ACTION_UP){     
	                    //改为抬起时的图片     
		            	imgbtn_doorctrl.setImageResource(R.drawable.btn_doorup);
		            }				
					break;
				case R.id.btn_aircondnctrl :
		    		if(event.getAction() == MotionEvent.ACTION_DOWN){     
	                    //更改为按下时的背景图片     
		    			imgbtn_airconctrl.setImageResource(R.drawable.btn_aircondn);
		            }else if(event.getAction() == MotionEvent.ACTION_UP){     
	                    //改为抬起时的图片     
		            	imgbtn_airconctrl.setImageResource(R.drawable.btn_airconup);
		            }				
					break;	
				case R.id.btn_warmctrl :
		    		if(event.getAction() == MotionEvent.ACTION_DOWN){     
	                    //更改为按下时的背景图片     
		    			imgbtn_warmctrl.setImageResource(R.drawable.btn_warmdn);
		            }else if(event.getAction() == MotionEvent.ACTION_UP){     
	                    //改为抬起时的图片     
		            	imgbtn_warmctrl.setImageResource(R.drawable.btn_warmup);
		            }				
					break;	
				case R.id.btn_cameractrl :
		    		if(event.getAction() == MotionEvent.ACTION_DOWN){     
	                    //更改为按下时的背景图片     
		    			imgbtn_cameractrl.setImageResource(R.drawable.btn_cameradn);
		            }else if(event.getAction() == MotionEvent.ACTION_UP){     
	                    //改为抬起时的图片     
		            	imgbtn_cameractrl.setImageResource(R.drawable.btn_cameraup);
		            }				
					break;	
				case R.id.btn_ledcrtl :
		    		if(event.getAction() == MotionEvent.ACTION_DOWN){     
	                    //更改为按下时的背景图片     
		    			imgbtn_ledctrl.setImageResource(R.drawable.btn_leddn);
		            }else if(event.getAction() == MotionEvent.ACTION_UP){     
	                    //改为抬起时的图片     
		            	imgbtn_ledctrl.setImageResource(R.drawable.btn_ledup);
		            } 					
					break;
				case R.id.btn_curtaincrtl :
		    		if(event.getAction() == MotionEvent.ACTION_DOWN){     
	                    //更改为按下时的背景图片     
		    			imgbtn_curtainctrl.setImageResource(R.drawable.btn_curtaindn);
		            }else if(event.getAction() == MotionEvent.ACTION_UP){     
	                    //改为抬起时的图片     
		            	imgbtn_curtainctrl.setImageResource(R.drawable.btn_curtainup);
		            }				
					break;
				case R.id.btn_moviecrtl :
		    		if(event.getAction() == MotionEvent.ACTION_DOWN){     
	                    //更改为按下时的背景图片     
		    			imgbtn_moviectrl.setImageResource(R.drawable.btn_moviedn);
		            }else if(event.getAction() == MotionEvent.ACTION_UP){     
	                    //改为抬起时的图片     
		            	imgbtn_moviectrl.setImageResource(R.drawable.btn_movieup);
		            }				
					break;	
				case R.id.btn_musiccrtl :
		    		if(event.getAction() == MotionEvent.ACTION_DOWN){     
	                    //更改为按下时的背景图片     
		    			imgbtn_musictrl.setImageResource(R.drawable.btn_musicdn);
		            }else if(event.getAction() == MotionEvent.ACTION_UP){     
	                    //改为抬起时的图片     
		            	imgbtn_musictrl.setImageResource(R.drawable.btn_musicup);
		            }				
					break;					
				case R.id.btn_speechcrtl :
		    		if(event.getAction() == MotionEvent.ACTION_DOWN){     
	                    //更改为按下时的背景图片     
		    			imgbtn_speechctrl.setImageResource(R.drawable.btn_speechdn);
		            }else if(event.getAction() == MotionEvent.ACTION_UP){     
	                    //改为抬起时的图片     
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
						//从ini文件读取平面图内的设备点位坐标
						lbl_info.setText(Integer.toString(DsParam.Devs[DevDsParam.DEVNAME_LED].Count));
						DrawDevsDot(Holder_sfvMian,
									DsParam.Devs[DevDsParam.DEVNAME_LED],
									DevDsParam.DEVNAME_LED,
									R.drawable.dot_led,
									dotScale_ICON,dotScale_ICON);
					} else {
						Toast.makeText(SmartHomeActivity.this, "文件不存在或无该房间数据", Toast.LENGTH_SHORT).show();
					}
					
					List_Items = null;
					List_Items = new ArrayList<String>();
					List_Items.add("灯光全开");
					List_Items.add("灯光全关");
					List_Items.add("点控开灯");
					List_Items.add("点控关灯");
					List_Items.add("调光设置");
					//将数据适配器绑定到ListActivity
					setListAdapter(new ListAdapter(SmartHomeActivity.this, List_Items, AppCtrl.OpDev ));					

					break;
				case R.id.btn_curtaincrtl :
					
					LayoutInflater inflater = getLayoutInflater();
					View layout = inflater.inflate(R.layout.dialog,
									(ViewGroup) findViewById(R.id.dialog));

				    new AlertDialog.Builder(SmartHomeActivity.this)
					   .setTitle("自定义布局").setView(layout)
					   .setIcon(android.R.drawable.ic_menu_more)
					   .setPositiveButton("确定",new OnClickListener() {
						   @Override
						   public void onClick(DialogInterface dialog, int which) {
						    dialog.dismiss();
						   }})
					   .setNegativeButton("取消", null)
					   .show();

					
					/*
					Dialog_LedCtrl dialog1 = new Dialog_LedCtrl(SmartHomeActivity.this, R.layout.layout_dialog, R.style.Theme_dialog);//Dialog使用默认大小(160) 
					Dialog_LedCtrl dialog2 = new Dialog_LedCtrl(SmartHomeActivity.this, 500, 300, R.layout.layout_dialog, R.style.Theme_dialog);
			        dialog2.setTitle("测试");
					dialog2.show();//显示Dialog
			        TextView mMessage = (TextView) dialog1.findViewById(R.id.message);
			        mMessage.setText("加载中...");
			        */
					AppCtrl.OpDev = DevDsParam.DEVNAME_CURTAIN;
					if (true==getDevDisplayParam(DevDsParam.DEVNAME_CURTAIN,AppCtrl)) {
						//从ini文件读取平面图内的设备点位坐标
						lbl_info.setText(Integer.toString(DsParam.Devs[DevDsParam.DEVNAME_CURTAIN].Count));
						DrawDevsDot(Holder_sfvMian,
									DsParam.Devs[DevDsParam.DEVNAME_CURTAIN],
									DevDsParam.DEVNAME_CURTAIN,
									R.drawable.dot_led,
									dotScale_ICON,dotScale_ICON);
					} else {
						Toast.makeText(SmartHomeActivity.this, "文件不存在或无该房间数据", Toast.LENGTH_SHORT).show();
					}					
					break;
				case R.id.btn_cameractrl :
					AppCtrl.OpDev = DevDsParam.DEVNAME_CAMERA;
					Toast.makeText(SmartHomeActivity.this, "启动摄像头监控", Toast.LENGTH_SHORT).show();
	            	Intent step1 = new Intent(SmartHomeActivity.this, MultiCameraActivity.class);
	            	startActivity(step1);					
					break;
				}
				//----显示设备点图
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
		//----end of 主按钮事件控制 ----
		
		//----触摸屏控制----
		//----触摸屏触碰事件----
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
		
		//----触摸屏单击事件
		Listener_scrn_click = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				switch (v.getId()) {
				case R.id.sfcView_main :
					lbl_info.setText(Integer.toString(scrnX)+","+Integer.toString(scrnY));
					switch (AppCtrl.MapLevel) {
					//楼宇总体透视图==LEVEL_FULLHOUSE opLevel=1
					case 0 : 
						UIACT_FloorSelect(); //UI交互动作:楼层选取
						break;
					//楼层平面图==LEVEL_FLOOR :opLevel = 2 -->楼层平面图内的点击 -->操作层次深入为房间局部
					case 1 : 
						UIACT_RoomSelect();  //UI交互动作:房间选取
						break;
					//房间局部图==LEVEL_ROOM :opLevel = 4 -->房间平面图内的点击 -->操作具体的设备点ICON
					case 2 :
						//UI交互动作:设备选取,并得到选中设备的索引号Index
						AppCtrl.SelectedDevIdx = UIACT_DevSelect(AppCtrl.OpDev);  
						break;
					}
					break;

				}
			}
		};//end of Listener_scrn_click
		
		sfv_main.setOnTouchListener(Listener_scrn_touch);
		sfv_main.setOnClickListener(Listener_scrn_click);

		
		//------------按钮单击事件
		
    } //end of onCreat
    
   /* 
    * */
    /* 捕获按键按下事件 -- 本次不用
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { //按下的如果是BACK，同时没有重复
           Toast.makeText(SmartHomeActivity.this,"魔力去吧Back键测试",0).show();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
    */
    
    @Override
    public void onBackPressed() {
    	super.onBackPressed();
    	switch (AppCtrl.MapLevel) {
    	case 0 : //楼宇全视图
    		Draw_FullHouse(Holder_sfvMian,com.prj.smarthome.AppCtrl.FULL_ERASE);
    		if (AppCtrl.OpLevel>0) {AppCtrl.OpLevel -= 1;}
    		break;
    		
    	case 1 : //由楼层平面视图向上回退
    		if (AppCtrl.OpLevel == 3) {
    			AppCtrl.OpLevel = 2;
    			AppCtrl.RoomID = com.prj.smarthome.AppCtrl.NULL;
    			SfcViewHolder_main.EraseArea(Holder_sfvMian, null, true);
    		} else {
        		AppCtrl.RoomID = com.prj.smarthome.AppCtrl.NULL;
    			AppCtrl.OpLevel = 1; //保留某层(之前的楼层点选)高亮
    			AppCtrl.MapLevel = 0; //退回楼宇全视图    	
    		}
			this.onResume();
    		break;
    		
    	case 2 : //由房间局部放大图 向上回退
    		AppCtrl.MapLevel = 1;
    		AppCtrl.OpLevel = 3;  //保留之前选择的房间区域框线
    		this.onResume();
    		break;
    	}
    		

    }//end of onBackPressed
    
    //复写程序退出事件：对back键做定时处理
	@Override
	public void finish()
	{
		if (isExit == false)
		{// 第一次按下返回键
			if (AppCtrl.OpLevel==0) {
				isExit = true;//退出标志位设置成true
				Toast.makeText(this, "再按一次后退键退出应用程序", Toast.LENGTH_SHORT).show();//向用户提示
				//一个Timer()对象，如果用户在第一次按返回键两秒后没有再按一次返回键退出，表示用户取消了操作，则重新将标志位设置成false
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
		{//退出程序
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
        			sfv_main.setBackgroundResource(R.drawable.housefull);//背景为全视图
        			SfcViewHolder_main.EraseArea(Holder_sfvMian, null, true);
        			Draw_FullHouse(Holder_sfvMian,AppCtrl.FloorID); //返回后--绘制(上次选中)保留高亮的楼层 
        			break;
        			
        		case 1 : //=LEVEL_FLOOR
        			switch (AppCtrl.FloorID) {
        			case -1 : //地下1层
        				sfv_main.setBackgroundResource(R.drawable.floor_bottom);
        				break;
        			case 1 :  //花园客厅层
        				sfv_main.setBackgroundResource(R.drawable.floor_garden);
        				break;
        			case 2 :  //卧室1层
        				sfv_main.setBackgroundResource(R.drawable.floor_bedroom);
        				break;
        			case 3 :  //屋顶阳光房2层
        				sfv_main.setBackgroundResource(R.drawable.floor_top);
        				break;
        			}
        			SfcViewHolder_main.EraseArea(Holder_sfvMian, null, true);
        			if (AppCtrl.RoomID != com.prj.smarthome.AppCtrl.NULL) {
        				SfcViewHolder_main.DrawBmpScale(Holder_sfvMian, 255, tmp_left,tmp_top,
        						bmp_bkg.Width, bmp_bkg.Height, bmp_bkg.img, tmp_sx, tmp_sy);//绘制房间局部框线
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