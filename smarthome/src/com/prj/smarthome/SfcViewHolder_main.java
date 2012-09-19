package com.prj.smarthome;

import com.prj.smarthome.DevDsParam.TDev;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.PorterDuff.Mode;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;

public class SfcViewHolder_main implements Callback { 
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub

	}
	
	/*批量绘制多个图标(图片)
	 * */
	static int DrawMultBmpScale(SurfaceHolder holder,int alpha,TDev dev,Bmp src,float sx,float sy) {
		int i,x,y;
		/*
		Bitmap tmpbmp = Bitmap.createBitmap(935, 570, Config.ARGB_8888); //创建空位图
		Canvas canvas = new Canvas(tmpbmp); //将Canvas与位图关联
		Matrix mx = new Matrix();
		mx.setScale(sx, sy);
		Bitmap dst = null;
		dst = Bitmap.createBitmap(src.img, 0, 0, src.Width, src.Height, mx, true);
		Paint pnt=new Paint();
		pnt.setAlpha(alpha);
		for (i=0;i<dev.Count;i++) {
			x = dev.Coord[i].x;
			y = dev.Coord[i].y;
			x -= Math.round(dst.getWidth()/2);
			y -= Math.round(dst.getHeight()/2);
			canvas.drawBitmap(dst, x, y, pnt);
		}
		
		Canvas cnvs = holder.lockCanvas();
		cnvs.drawBitmap(tmpbmp, 0, 0, pnt);
		holder.unlockCanvasAndPost(cnvs);
		
		//必须加入下列两句：解决图像绘制不全的问题
		holder.lockCanvas(new Rect(0, 0, 0, 0));
		holder.unlockCanvasAndPost(cnvs);		
		dst.recycle();
		tmpbmp.recycle();
		return 0;
		*/
		
		
		Canvas cnvs = holder.lockCanvas();
		Matrix mx = new Matrix();
		mx.setScale(sx, sy);
		Bitmap dst = null;
		dst = Bitmap.createBitmap(src.img, 0, 0, src.Width, src.Height, mx, true);
		Paint pnt=new Paint();
		pnt.setAlpha(alpha);
		
		for (i=0;i<dev.Count;i++) {
			x = dev.Coord[i].x;
			y = dev.Coord[i].y;
			x -= Math.round(dst.getWidth()/2);
			y -= Math.round(dst.getHeight()/2);
			cnvs.drawBitmap(dst, x, y, pnt);
		}
		holder.unlockCanvasAndPost(cnvs);
		
		//必须加入下列两句：解决图像绘制不全的问题
		holder.lockCanvas(new Rect(0, 0, 0, 0));
		holder.unlockCanvasAndPost(cnvs);
		dst.recycle();
		return 0;
		
	}
	
	/*在指定位置绘制BMP图像
	 * 参数：
	 * SurfaceHolder holder : 绘图操作对象(画布)
	 * int alpha : 绘图透明度 [全透明]0--255[全填充]
	 * int left,int top,int w,int h ： 图像矩形的大小(用于锁定绘图操作区域)
	 * Bitmap src : bmp图像(绘制对象)
	 * */
	static int DrawBmp(SurfaceHolder holder,int alpha,int left,int top,int w,int h,Bitmap src)
	{
		//使用int变量传递绘图矩形区域,别用rect类型:老是传递时RECT值被未知原因更改(left,top归零)
		Canvas cnvs = holder.lockCanvas(new Rect(left,top,left+w,top+h));
		Paint pnt=new Paint();
		pnt.setAlpha(alpha);
		cnvs.drawBitmap(src, left, top, pnt);
		holder.unlockCanvasAndPost(cnvs);
		
		//必须加入下列两句：解决图像绘制不全的问题
		holder.lockCanvas(new Rect(0, 0, 0, 0));
		holder.unlockCanvasAndPost(cnvs);
		
		return 0;
	} 

	static int DrawBmpScale(SurfaceHolder holder,int alpha,int left,int top,int w,int h,Bitmap src,float sx,float sy) {
		Matrix mx = new Matrix();
		mx.setScale(sx, sy);
		Bitmap dst = null;
		dst = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), mx, true);
		DrawBmp(holder,alpha,left,top,dst.getWidth(),dst.getHeight(),dst);
		dst.recycle();
		return 0;
	}
	
	/*在指定透明擦除图像
	 * 参数：
	 * SurfaceHolder holder : 绘图操作对象(画布)
	 * Rect rct  ： 图像矩形的大小(用于锁定绘图操作区域)
	 * boolean isEraseAll : 是否擦全屏 =True擦全屏  =False擦局部(参数rct起作用)
	 * */
	static int EraseArea(SurfaceHolder holder,Rect rct,boolean isEraseAll)
	{
		Canvas cnvs = new Canvas();
		if (isEraseAll) {
			cnvs = holder.lockCanvas(); //擦全屏
		} else {
			cnvs = holder.lockCanvas(rct); //擦局部
		}
		Paint pnt = new Paint();
		pnt.setAntiAlias(true);
		pnt.setStyle(Paint.Style.STROKE);
		pnt.setXfermode(
				new PorterDuffXfermode(Mode.CLEAR)
				);
		cnvs.drawPaint(pnt);
		holder.unlockCanvasAndPost(cnvs);
		
		//必须加入下列两句：解决图像绘制不全的问题
		holder.lockCanvas(new Rect(0, 0, 0, 0));
		holder.unlockCanvasAndPost(cnvs);
		
		return 0;
	}	
	


}
