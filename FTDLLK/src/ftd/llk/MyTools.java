package ftd.llk;


import java.io.InputStream;
import java.util.Random;

import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.util.Log;


public class MyTools {
	
	public  static  Resources res = LLKActivity.APP.getResources();
	
	public static AssetFileDescriptor  getAssetFileDescriptor (String fileName)
	{
		try
		{
			return res.getAssets().openFd(fileName);
		}catch(Exception e)
		{
			Log.d("AssetFileDescriptor"," Erro!");
			return null;
		}
	}
	/**
	 * ��AssetsĿ¼�¶�ȡ�ļ�
	 * @param fileName
	 * @return ������
	 */
	public  static InputStream getFromAsset(String fileName)
	{
		try//AssetFileDescriptor 
		{
			return res.getAssets().open(fileName);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * ��ȡ ͼƬ�ļ�
	 * @param fileName
	 * @return
	 */
	public  static  Bitmap loadFromAsset(String fileName)
	{
		InputStream is = getFromAsset(fileName);
		if(is == null)
		{
			Log.d("MyTools" , "����ͼƬ��<" +fileName+"> ʧ��!");
			return null;
		}
		Log.d("MyTools" , "����ͼƬ��<" +fileName+">!�ɹ�");
		Bitmap bitmap = BitmapFactory.decodeStream(is);
		
		try
		{
			is.close();
			//Dhq.res.getAssets().close();//����Ҫclose������getAssets()�õ���AssetManager ��Ҫclose��ӦΪ���AssetManager���ᱻϵͳʹ�á�
		}catch(Exception e)
		{
			Log.d("MyTools" , "����ͼƬ��<" +fileName+"> ʧ��!");
			e.printStackTrace();
		}
		return bitmap;
	}
	/**
	 * ��ȡ������Դ�ļ�
	 */
	public static MediaPlayer loadMediaPlayer(String fileName)
	{	
		MediaPlayer mediaPlayer = new MediaPlayer();
		AssetFileDescriptor fileDescriptor=getAssetFileDescriptor(fileName);
		try {
			mediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(),
					fileDescriptor.getStartOffset(),				                            
					fileDescriptor.getLength());
			mediaPlayer.prepare();//׼����Ƶ�ļ�
		} catch (Exception e) {
			Log.d("MyTools" , "�������֣�<" +fileName+"> ʧ��!");
		}
		
		
		return mediaPlayer;
		
	}
	/**
	 * ����Ӧ�õ�ԭʼ��Դ�ļ�(assets)1) ͨ��Context.getAssets()�������AssetManager����
		2) ͨ��AssetManager�����openFd(String name)������ָ����ԭ����Դ�ļ��У�����һ��AssetFileDescriptor����
		3) ͨ��AssetFileDescriptor��getFileDescriptor()�õ�һ��FileDescriptor����
		4) ͨ��public void setDataSource (FileDescriptor fd, long offset, long length)������MediaPlayer����
		5) ����MediaPlayer.prepare()����׼����Ƶ
		6) ����MediaPlayer��start()��pause()��stop()�ȷ�������
		
		AssetFileDescriptor fileDescriptor = assetManager.openFd("a2.mp3");
		mediaPlayer = new MediaPlayer();
		   
		mediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(),
		                           
		
		fileDescriptor.getStartOffset(),
		                            
		fileDescriptor.getLength());
		   
		mediaPlayer.prepare();
		mediaPlayer.start();
	 */
	static Random r = new Random();
	public static int getRan(int num)
	{
		return Math.abs(r.nextInt()%num);
	}
	public static int getRan(int n,int m)
	{
		return getRan(m-n)+n;
	}
	public static int getRanx(int num)
	{
		return r.nextInt()%num;
	}
	public static int getRanx(int n,int m)
	{
		return getRanx(m-n)+n;
	}
	public static void luanArray(int frame[])
	{
		int b = 0;
		for (int i = 0; i < frame.length; i++) 
		{
			int ran = Math.abs(r.nextInt()) % (frame.length - i);
			b = frame[frame.length - 1 - i];
			frame[frame.length - 1 - i] = frame[ran];
			frame[ran] = b;
		}
	}
	
	static Rect src = new Rect();
	static Rect dst = new Rect();
	/**
	 * �ڴ�ͼ���и�СͼƬ
	 * @param c 
	 * @param im  ��ͼƬ
	 * @param x   �ڵ�ͼ����ϵ�� ������
	 * @param y   �ڵ�ͼ����ϵ��������
	 * @param sx  Сͼ�ڴ�ͼ�еĺ�����  ���Ͻ�
	 * @param sy  Сͼ�����ͼ��������  ���Ͻ�
	 * @param sw  Сͼ��
	 * @param sh  Сͼ��
	 */
	public static void drawClip(Canvas c,Bitmap im,int x,int y,
			int sx,int sy,int sw,int sh)
	{
		src.set(sx, sy , sx + sw, sy + sh);
		dst.set(x , y, x + sw , y + sh);
		c.drawBitmap(im, src, dst, null);
	}
}
