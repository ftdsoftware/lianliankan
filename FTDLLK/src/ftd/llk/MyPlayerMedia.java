package ftd.llk;

import java.io.IOException;

import android.media.MediaPlayer;

public class MyPlayerMedia 
{
	MediaPlayer media;
	String fileNameString;
	public MyPlayerMedia(String fileName)
	{
		fileNameString=fileName;
		media=MyTools.loadMediaPlayer(fileName);
	}
	
	public void start() throws IllegalStateException, IOException{
		media.start();
	}
	public void stop(){
		
		media.stop();
		media.reset();
		media.release();
		media=MyTools.loadMediaPlayer(fileNameString);
	}

}
