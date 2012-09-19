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
	
	/*�������ƶ��ͼ��(ͼƬ)
	 * */
	static int DrawMultBmpScale(SurfaceHolder holder,int alpha,TDev dev,Bmp src,float sx,float sy) {
		int i,x,y;
		/*
		Bitmap tmpbmp = Bitmap.createBitmap(935, 570, Config.ARGB_8888); //������λͼ
		Canvas canvas = new Canvas(tmpbmp); //��Canvas��λͼ����
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
		
		//��������������䣺���ͼ����Ʋ�ȫ������
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
		
		//��������������䣺���ͼ����Ʋ�ȫ������
		holder.lockCanvas(new Rect(0, 0, 0, 0));
		holder.unlockCanvasAndPost(cnvs);
		dst.recycle();
		return 0;
		
	}
	
	/*��ָ��λ�û���BMPͼ��
	 * ������
	 * SurfaceHolder holder : ��ͼ��������(����)
	 * int alpha : ��ͼ͸���� [ȫ͸��]0--255[ȫ���]
	 * int left,int top,int w,int h �� ͼ����εĴ�С(����������ͼ��������)
	 * Bitmap src : bmpͼ��(���ƶ���)
	 * */
	static int DrawBmp(SurfaceHolder holder,int alpha,int left,int top,int w,int h,Bitmap src)
	{
		//ʹ��int�������ݻ�ͼ��������,����rect����:���Ǵ���ʱRECTֵ��δ֪ԭ�����(left,top����)
		Canvas cnvs = holder.lockCanvas(new Rect(left,top,left+w,top+h));
		Paint pnt=new Paint();
		pnt.setAlpha(alpha);
		cnvs.drawBitmap(src, left, top, pnt);
		holder.unlockCanvasAndPost(cnvs);
		
		//��������������䣺���ͼ����Ʋ�ȫ������
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
	
	/*��ָ��͸������ͼ��
	 * ������
	 * SurfaceHolder holder : ��ͼ��������(����)
	 * Rect rct  �� ͼ����εĴ�С(����������ͼ��������)
	 * boolean isEraseAll : �Ƿ��ȫ�� =True��ȫ��  =False���ֲ�(����rct������)
	 * */
	static int EraseArea(SurfaceHolder holder,Rect rct,boolean isEraseAll)
	{
		Canvas cnvs = new Canvas();
		if (isEraseAll) {
			cnvs = holder.lockCanvas(); //��ȫ��
		} else {
			cnvs = holder.lockCanvas(rct); //���ֲ�
		}
		Paint pnt = new Paint();
		pnt.setAntiAlias(true);
		pnt.setStyle(Paint.Style.STROKE);
		pnt.setXfermode(
				new PorterDuffXfermode(Mode.CLEAR)
				);
		cnvs.drawPaint(pnt);
		holder.unlockCanvasAndPost(cnvs);
		
		//��������������䣺���ͼ����Ʋ�ȫ������
		holder.lockCanvas(new Rect(0, 0, 0, 0));
		holder.unlockCanvasAndPost(cnvs);
		
		return 0;
	}	
	


}
