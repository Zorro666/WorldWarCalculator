package worldwarcalculator.zorro666.com;

import android.app.Activity;
import android.widget.TabHost;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.EditText;
import android.text.TextWatcher;
import android.text.Editable;
import android.util.Log;

public class WorldWarCalc extends Activity implements TextWatcher
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        final TabHost tabs = (TabHost)findViewById(R.id.tabhost);
        tabs.setup();
        
        TabHost.TabSpec spec;
        
        spec = tabs.newTabSpec("Income");
        spec.setContent(R.id.IncomeView);
        spec.setIndicator("Income");
        tabs.addTab(spec);
       
        spec = tabs.newTabSpec("Defence");
        spec.setContent(R.id.DefenceView);
        spec.setIndicator("Defence");
        tabs.addTab(spec);
        tabs.setCurrentTab(0);
        
        m_value1 = (TextView)findViewById(R.id.IncomeBuilding1Value);
        EditText value = (EditText)findViewById(R.id.IncomeBuilding1Number);
        value.addTextChangedListener(this);
        
    }
    
    public void beforeTextChanged(CharSequence s, int start, int count, int after)
    {
    }
    
    public void onTextChanged(CharSequence s, int start, int count, int after)
    {
    }
    
    public void afterTextChanged(Editable e)
    {
    	if (e.length() > 0)
    	{
    		int number = Integer.parseInt(e.toString());
    		Log.i(TAG, "value="+Integer.toString(number));
    	}
    	
    }
    
    private TextView m_value1;
    private static final String TAG = "WWCALC";
    
}