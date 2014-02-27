package ftd.llk;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

public class MyPlayerSound 
{
	
	SoundPool sp;   //  ∫œ“Ù–ß≤•∑≈
	
	int m_soundID;
	String fileName;
	int maxCount;
	 
	public MyPlayerSound(String fileName,int maxCount)
	{
		this.fileName = fileName;
		this.maxCount = maxCount;
		init();
		
	}
	
	public void init()
	{
		sp = new SoundPool(maxCount,AudioManager.STREAM_MUSIC,100);
		m_soundID = sp.load(MyTools.getAssetFileDescriptor(fileName), 1);
	}
	
	public void playSoundPool()
	{
		sp.play(m_soundID, 1, 1, 1, 0, 1);
	}
	
	public void stopSoundPool()
	{
		
	}
	
	public void closeSoundPool()
	{
		sp.release();
		sp = null;
	}
}
