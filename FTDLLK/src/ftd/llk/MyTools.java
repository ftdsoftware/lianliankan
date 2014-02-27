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
	 * 从Assets目录下读取文件
	 * @param fileName
	 * @return 输入流
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
	 * 读取 图片文件
	 * @param fileName
	 * @return
	 */
	public  static  Bitmap loadFromAsset(String fileName)
	{
		InputStream is = getFromAsset(fileName);
		if(is == null)
		{
			Log.d("MyTools" , "加载图片：<" +fileName+"> 失败!");
			return null;
		}
		Log.d("MyTools" , "加载图片：<" +fileName+">!成功");
		Bitmap bitmap = BitmapFactory.decodeStream(is);
		
		try
		{
			is.close();
			//Dhq.res.getAssets().close();//不需要close（）；getAssets()得到的AssetManager 不要close，应为这个AssetManager还会被系统使用。
		}catch(Exception e)
		{
			Log.d("MyTools" , "加载图片：<" +fileName+"> 失败!");
			e.printStackTrace();
		}
		return bitmap;
	}
	/**
	 * 读取音乐资源文件
	 */
	public static MediaPlayer loadMediaPlayer(String fileName)
	{	
		MediaPlayer mediaPlayer = new MediaPlayer();
		AssetFileDescriptor fileDescriptor=getAssetFileDescriptor(fileName);
		try {
			mediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(),
					fileDescriptor.getStartOffset(),				                            
					fileDescriptor.getLength());
			mediaPlayer.prepare();//准备音频文件
		} catch (Exception e) {
			Log.d("MyTools" , "加载音乐：<" +fileName+"> 失败!");
		}
		
		
		return mediaPlayer;
		
	}
	/**
	 * 播放应用的原始资源文件(assets)1) 通过Context.getAssets()方法获得AssetManager对象
		2) 通过AssetManager对象的openFd(String name)方法打开指定的原生资源文件夹，返回一个AssetFileDescriptor对象
		3) 通过AssetFileDescriptor的getFileDescriptor()得到一个FileDescriptor对象
		4) 通过public void setDataSource (FileDescriptor fd, long offset, long length)来创建MediaPlayer对象
		5) 调用MediaPlayer.prepare()方法准备音频
		6) 调用MediaPlayer的start()、pause()、stop()等方法控制
		
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
	 * 在大图中切割小图片
	 * @param c 
	 * @param im  大图片
	 * @param x   在地图坐标系中 横坐标
	 * @param y   在地图坐标系中纵坐标
	 * @param sx  小图在大图中的横坐标  左上角
	 * @param sy  小图在你大图中纵坐标  左上角
	 * @param sw  小图宽
	 * @param sh  小图高
	 */
	public static void drawClip(Canvas c,Bitmap im,int x,int y,
			int sx,int sy,int sw,int sh)
	{
		src.set(sx, sy , sx + sw, sy + sh);
		dst.set(x , y, x + sw , y + sh);
		c.drawBitmap(im, src, dst, null);
	}
}
