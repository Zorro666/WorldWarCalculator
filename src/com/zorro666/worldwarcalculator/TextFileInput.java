package worldwarcalculator.zorro666.com;

import android.util.Log;

import java.io.IOException;
import java.io.FileInputStream;
import java.io.BufferedInputStream;

public class TextFileInput
{
	public TextFileInput(FileInputStream inStream)
	{
		m_inputStream = new BufferedInputStream(inStream);
	}
	
	public void Close() throws IOException
	{
		m_inputStream.close();
	}

    public int ReadInt() throws IOException
    {
    	String tempStr = ReadString();
    	int value = 0;
    	if (tempStr.length()>0)
    	{
    		value = Integer.parseInt(tempStr);
    	}
   		Log.i(TAG, "value = " + value);
    	
    	return value;
    }
    public String ReadString() throws IOException
    {
   		int length = m_inputStream.read();
    	Log.i(TAG, "length = " + length);
    	if ( length>0)
    	{
    		byte[] byteBuffer = new byte[length];
    		int result = m_inputStream.read(byteBuffer, 0, length);
    		if (result==length)
    		{
    			String str = new String(byteBuffer);
    			Log.i(TAG, "str = " + str);
    		
    			return str;
    		}
    	}
		return "";
    }
    
    private static final String TAG = "IN";
    
    private BufferedInputStream m_inputStream;
}
