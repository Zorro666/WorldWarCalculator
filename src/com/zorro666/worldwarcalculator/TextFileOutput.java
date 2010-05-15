package com.zorro666.worldwarcalculator;

import android.util.Log;

import java.io.IOException;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;

public class TextFileOutput 
{
	public TextFileOutput(FileOutputStream outStream)
	{
		m_outputStream = new BufferedOutputStream(outStream);
	}
	public void Close() throws IOException
	{
		m_outputStream.close();
	}
	
    public void WriteInt(int value) throws IOException
    {
    	String tempStr = Integer.toString(value);
    	Log.i(TAG, "value = " + value);
    	WriteString(tempStr);
    }
    
    public void WriteString(String str) throws IOException
    {
    	int length = str.length();
    	Log.i(TAG, "length = " + length);
    	m_outputStream.write(length);
    	Log.i(TAG, "str = " + str);
    	m_outputStream.write(str.getBytes(),0,length);
    }
    
    private static final String TAG = "OUT";
    
    private BufferedOutputStream m_outputStream;
}
