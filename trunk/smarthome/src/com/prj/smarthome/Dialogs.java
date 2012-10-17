package com.prj.smarthome;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class Dialogs {

	public static Result_LED rslt_led ;
	public static Result_Curtain rslt_curtain ;
//	private static int tmp_rslt = 0;
	
	//�ƿضԻ���--���ؽӿ�
	public class Result_LED {
		int light; //����� 0-100
		public void Result_LED() {
			light = 0;
		}
	}
	
	//�������ƶԻ���--���ؽӿ�
	public class Result_Curtain {
		int opening; //��������
		int rolling; //��ת��
		public void Result_Curtain() {
			opening = 0; //0..100
			rolling = 0; //0..100
		}
	}
	
	//�����豸����(�Ի���)���ؽӿ�
	public void Dialogs() {
		rslt_led = new Result_LED();
		rslt_curtain = new Result_Curtain();
	//	SeekBar skb = (SeekBar)dialog.findViewById(R.id.seekbar);
	}
	
	//����Ի���
	static int Dialog_Adj_Light(final Context context,View DialogView,int cur_light) {
		
        //�ڲ��ࣺһ�����������ü������������������״̬ �ĸı�  
        class SeekBar_lightADJ_Listener implements OnSeekBarChangeListener {  
              //���������Ľ��ȷ��� �仯 ʱ������� �� ���� 
        	
        	  //����������:���鰴�²��Ų����һ���
     	   	  @Override
     	   	  public void onProgressChanged(SeekBar seekBar,
						int progress, boolean fromUser) {  
                      //System.out.println(progress);  //������ʱ�������ǰλ��
               }  
     	   	  
     	   	  //������ʼ
     	   	  @Override
               public void onStartTrackingTouch(SeekBar seekBar) {  
                      System.out.println("start->"+ seekBar.getProgress());   
               } 
     	   	  
     	   	  //����ֹͣ
     	   	  @Override
               public void onStopTrackingTouch(SeekBar seekBar) {  
                      System.out.println("stop->"+ seekBar.getProgress());  
               }
        }
		
		final SeekBar skb = (SeekBar) DialogView.findViewById(R.id.seekbar);
		skb.setOnSeekBarChangeListener(new SeekBar_lightADJ_Listener());  //����seekbar���黬��/�ı����Ӧ����
		skb.setProgress(cur_light); //���ù������ǰ�Ľ���ֵ -- �����ݿ����λ�豸��ȡ����ʵ������ֵ
	    new AlertDialog.Builder(context)
		   .setTitle("��������").setView(DialogView)
		   .setIcon(android.R.drawable.ic_menu_more)
		   .setPositiveButton("ȷ��",new OnClickListener() {
			   @Override
			   public void onClick(DialogInterface dialog, int which) {
				   //rslt.light = skb.getProgress();
				   Toast.makeText(context,"light="+Integer.toString(skb.getProgress()),Toast.LENGTH_SHORT).show();
			   }})
		   .setNegativeButton("ȡ��", null)
		   .show();
		return 0;
	}
	
}
