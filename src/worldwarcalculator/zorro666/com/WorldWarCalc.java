package worldwarcalculator.zorro666.com;

import android.app.Activity;
import android.widget.TabHost;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.EditText;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnKeyListener;
import android.view.KeyEvent;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.text.method.DigitsKeyListener;

public class WorldWarCalc extends Activity implements OnKeyListener
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	m_defenceBuildings = new WWBuilding[NUM_DEFENCE_BUILDINGS];
    	m_incomeBuildings = new WWBuilding[NUM_INCOME_BUILDINGS];
    	m_numDefenceBuildings = 0;
    	m_numIncomeBuildings = 0;
    	
    	m_defenceBuildings[m_numDefenceBuildings++] = new WWBuilding("Bunker", 3000, 3);
    	m_defenceBuildings[m_numDefenceBuildings++] = new WWBuilding("DefBuild2", 6000, 10);
    	m_defenceBuildings[m_numDefenceBuildings++] = new WWBuilding("DefBuild3", 100000, 25);
    	m_defenceBuildings[m_numDefenceBuildings++] = new WWBuilding("DefBuild4", 10000000, 75);
    	
    	m_incomeBuildings[m_numIncomeBuildings++] = new WWBuilding("Sat Dish", 30000, 3000);
    	m_incomeBuildings[m_numIncomeBuildings++] = new WWBuilding("IncBuild2", 56000, 16500);
    	m_incomeBuildings[m_numIncomeBuildings++] = new WWBuilding("IncBuild3", 270000, 56000);
    	m_incomeBuildings[m_numIncomeBuildings++] = new WWBuilding("IncBuild4", 150000000, 700000);
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        ViewGroup defenceView = (LinearLayout)findViewById(R.id.DefenceView);
        for (int i=0; i<m_numDefenceBuildings; i++)
        {
        	addRow( defenceView, m_defenceBuildings[i]);
        }
        ViewGroup incomeView = (LinearLayout)findViewById(R.id.IncomeView);
        for (int i=0; i<m_numIncomeBuildings; i++)
        {
        	addRow( incomeView, m_incomeBuildings[i]);
        }
        
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
    }
    
    public boolean onKey(View v, int key, KeyEvent event)
    {
    	int number = -1;
    	Object tag = v.getTag();
    	if (tag.getClass() == WWBuilding.class)
    	{
    		WWBuilding building = (WWBuilding)tag;
    		EditText numberText = building.GetViewNumOwned();
    		if (v==numberText)
    		{
    			String text = numberText.getText().toString();
    		
    			number = 0;
    			if (text.length() > 0)
    			{
    				number = Integer.decode(text);
    			}
    			building.SetNumOwned(number);
    			float value = building.GetValue();
    			String valueString = Float.toString(value);
    			TextView valueText = building.GetViewValue();
    			valueText.setText(valueString);
    			Log.i(TAG,"onKey Building = " + building.GetName() + " number = " + number + " value = " + value);
    		}
    	}
    	return false;
    }
    // OR could use afterTextChanged() and compare Editable e with m_number1.getText()
    
    private void addRow(ViewGroup parent,WWBuilding building)
    {
       	LayoutParams rowLayoutParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 1.0f);
       	LayoutParams numberLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1.0f);
       	
       	InputFilter[] filters5 = new InputFilter[1];
       	filters5[0] = new InputFilter.LengthFilter(5);
       	
       	InputFilter[] filters8 = new InputFilter[1];
       	filters8[0] = new InputFilter.LengthFilter(8);
       	
    	LinearLayout row = new LinearLayout(parent.getContext());
    	row.setOrientation(LinearLayout.HORIZONTAL);
    	row.setLayoutParams(rowLayoutParams);
    	row.setTag(building);
    	
    	TextView name = new TextView(row.getContext());
    	name.setText(building.GetName());
    	name.setPadding(5,5,5,5);
    	name.setWidth(96);
    	name.setTag(building);
    	row.addView(name);
    	
    	EditText number = new EditText(row.getContext());
    	number.setLayoutParams(numberLayoutParams);
    	number.setInputType(InputType.TYPE_CLASS_NUMBER);
    	number.setKeyListener(new DigitsKeyListener());
    	number.setSingleLine();
    	number.setMinWidth(64);
    	number.setWidth(64);
    	number.setMaxWidth(64);
    	number.setMaxLines(1);
    	number.setFilters(filters5);
    	number.setLines(1);
    	number.setText(Integer.toString(building.GetNumOwned()));
    	number.setPadding(5,5,5,5);
    	number.setTag(building);
    	number.setOnKeyListener(this);
    	building.SetViewNumOwned(number);
    	row.addView(number);
    	
    	TextView value = new TextView(row.getContext());
    	value.setText(Float.toString(building.GetValue()));
    	value.setLayoutParams(numberLayoutParams);
    	value.setKeyListener(new DigitsKeyListener());
    	value.setInputType(InputType.TYPE_CLASS_NUMBER);
    	value.setSingleLine();
    	value.setMinWidth(64);
    	value.setWidth(64);
    	value.setMaxWidth(64);
    	value.setMaxLines(1);
    	value.setFilters(filters8);
    	value.setPadding(5,5,5,5);
    	value.setWidth(96);
    	value.setTag(building);
    	building.SetViewValue(value);
    	row.addView(value);
    	
    	parent.addView(row);
    }
    private static final String TAG = "WWCALC";
    
    private WWBuilding[] m_defenceBuildings;
    private WWBuilding[] m_incomeBuildings;
    private int m_numDefenceBuildings;
    private int m_numIncomeBuildings;
    
    static private int NUM_DEFENCE_BUILDINGS = 6;
    static private int NUM_INCOME_BUILDINGS = 6;
}