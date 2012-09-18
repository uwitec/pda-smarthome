package com.prj.smarthome;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

public class Bmp {
	public Bitmap img;
	public int Width;
	public int Height;
	public Bmp() {
		img = null;
		Width = 0;
		Height = 0;	
		}
	
	public void setBmp(Resources res,int id) {
		img = BitmapFactory.decodeResource(res,id);
		Width = img.getWidth();
		Height = img.getHeight();
	}
	
	public Bitmap getBmpScale(Bitmap srcbmp,float sx,float sy) {
		Bitmap dest = null;
		Matrix mx = new Matrix();
		mx.setScale(sx, sy);
		dest = Bitmap.createBitmap(srcbmp, 0, 0, srcbmp.getWidth(), srcbmp.getHeight(), mx, true);
		return dest;
	}
}
