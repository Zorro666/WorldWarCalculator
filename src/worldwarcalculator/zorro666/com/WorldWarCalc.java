package worldwarcalculator.zorro666.com;

import android.app.Activity;
import android.widget.TabHost;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.EditText;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.KeyEvent;
import android.util.Log;

public class WorldWarCalc extends Activity implements OnKeyListener
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	m_defenceBuildings = new WWBuilding[NUM_DEFENCE_BUILDINGS];
    	m_incomeBuildings = new WWBuilding[NUM_INCOME_BUILDINGS];
    	
    	m_defenceBuildings[0] = new WWBuilding("Bunker", 3000, 3);
    	m_defenceBuildings[1] = new WWBuilding("DefBuild2", 6000, 10);
    	m_defenceBuildings[2] = new WWBuilding("DefBuild3", 100000, 25);
    	m_defenceBuildings[3] = new WWBuilding("DefBuild4", 10000000, 75);
    	
    	m_incomeBuildings[0] = new WWBuilding("Sat Dish", 30000, 3000);
    	m_incomeBuildings[1] = new WWBuilding("IncBuild2", 56000, 16500);
    	m_incomeBuildings[2] = new WWBuilding("IncBuild3", 270000, 56000);
    	m_incomeBuildings[3] = new WWBuilding("IncBuild4", 150000000, 700000);
    	
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
        m_number1 = (EditText)findViewById(R.id.IncomeBuilding1Number);
        m_number1.setOnKeyListener(this);
        m_number1.setTag(m_value1);
    }
    
    public boolean onKey(View v, int key, KeyEvent event)
    {
    	int number = -1;
    	if (v==m_number1)
    	{
    		EditText numberText = (EditText)v;
    		String text = numberText.getText().toString();
    		
    		number = 0;
    		if (text.length() > 0)
    		{
    			number = Integer.decode(text);
    			int cost = 10 + number/10;
    			int income = 3;
    			float value = cost/(income+0.0f);
    			value = Math.round(value * 100.0f)/100.0f;
    			String valueString = Float.toString(value);
    			TextView valueText = (TextView)v.getTag();
    			valueText.setText(valueString);
    			Log.i(TAG,"onKey number = " + number + " value = " + value);
    		}
    	}
    	return false;
    }
    // OR could use afterTextChanged() and compare Editable e with m_number1.getText()
    
    private TextView m_value1;
    private static final String TAG = "WWCALC";
    private EditText m_number1;
    
    private WWBuilding[] m_defenceBuildings;
    private WWBuilding[] m_incomeBuildings;
    
    static private int NUM_DEFENCE_BUILDINGS = 6;
    static private int NUM_INCOME_BUILDINGS = 6;
}