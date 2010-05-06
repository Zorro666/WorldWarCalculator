package worldwarcalculator.zorro666.com;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.widget.TabHost;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.EditText;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.widget.HorizontalScrollView;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.text.method.DigitsKeyListener;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;

public class WorldWarCalc extends Activity implements OnKeyListener, OnTouchListener
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	m_defenceBuildings = new WWBuilding[NUM_DEFENCE_BUILDINGS];
    	m_incomeBuildings = new WWBuilding[NUM_INCOME_BUILDINGS];
    	m_numDefenceBuildings = 0;
    	m_numIncomeBuildings = 0;
    	
    	m_defenceBuildings[m_numDefenceBuildings++] = new WWBuilding("Bunker", 					30000, 		3,		0.0001f);
    	m_defenceBuildings[m_numDefenceBuildings++] = new WWBuilding("Guard Tower", 			200000, 	10,		0.0001f);
    	m_defenceBuildings[m_numDefenceBuildings++] = new WWBuilding("Anti-Aircraft Launcher", 	560000, 	15,		0.0001f);
    	m_defenceBuildings[m_numDefenceBuildings++] = new WWBuilding("Turret", 					2800000, 	32,		0.0001f);
    	m_defenceBuildings[m_numDefenceBuildings++] = new WWBuilding("Landmine Field", 			10000000, 	50,		0.0001f);
    	m_defenceBuildings[m_numDefenceBuildings++] = new WWBuilding("Automated Sentry Gun", 	24000000, 	75,		0.0001f);
    	
    	m_incomeBuildings[m_numIncomeBuildings++] = new WWBuilding("Supply Depot", 				18000, 		1000,	0.1f);
    	m_incomeBuildings[m_numIncomeBuildings++] = new WWBuilding("Refinery", 					150000, 	6500,	0.1f);
    	m_incomeBuildings[m_numIncomeBuildings++] = new WWBuilding("Weapons Factor", 			540000, 	16500,	0.1f);
    	m_incomeBuildings[m_numIncomeBuildings++] = new WWBuilding("Power Plant", 				2700000, 	56000,	0.1f);
    	m_incomeBuildings[m_numIncomeBuildings++] = new WWBuilding("Oil Rig", 					20000000, 	270000,	0.1f);
    	m_incomeBuildings[m_numIncomeBuildings++] = new WWBuilding("Military Research Lab", 	60000000, 	500000,	0.1f);
    	m_incomeBuildings[m_numIncomeBuildings++] = new WWBuilding("Nuclear Testing Facility", 	100000000, 	700000,	0.1f);
    	
    	// Load the profile files in
    	SaveProfile("JakeProfile");
    	LoadProfiles();
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        TextView profileNameView = (TextView)findViewById(R.id.profileName);
        profileNameView.setText(m_profileName);
        Log.i(TAG,"m_profileName="+m_profileName);
        
        TableLayout defenceView = (TableLayout)findViewById(R.id.DefenceView);
        for (int i=0; i<m_numDefenceBuildings; i++)
        {
        	addRow( defenceView, m_defenceBuildings[i]);
        }
        
        //m_incomeViewHeader = (HorizontalScrollView)findViewById(R.id.IncomeViewHeader);
        //m_incomeViewHeader.setOnTouchListener(this);
        
        //HorizontalScrollView m_incomeViewScroll = (HorizontalScrollView)findViewById(R.id.IncomeViewScroll);
        //m_incomeViewScroll.setOnTouchListener(this);
        //m_incomeViewScroll.setSmoothScrollingEnabled(true);
        
        TableLayout incomeView = (TableLayout)findViewById(R.id.IncomeViewData);
        for (int i=0; i<m_numIncomeBuildings; i++)
        {
        	addRow( incomeView, m_incomeBuildings[i]);
        }
        
        final TabHost tabs = (TabHost)findViewById(R.id.tabhost);
        tabs.setup();
        
        TabHost.TabSpec spec;
        
        spec = tabs.newTabSpec("Income");
        spec.setContent(R.id.IncomeViewTab);
        spec.setIndicator("Income");
        tabs.addTab(spec);
       
        spec = tabs.newTabSpec("Defence");
        spec.setContent(R.id.DefenceView);
        spec.setIndicator("Defence");
        tabs.addTab(spec);
        tabs.setCurrentTab(0);
    }
    
    public boolean onTouch(View v, MotionEvent event)
    {
    	if (v==m_incomeViewScroll)
    	{
    		m_incomeViewHeader.onTouchEvent(event);
    	}
    	if (v==m_incomeViewHeader)
    	{
    		m_incomeViewScroll.onTouchEvent(event);
    	}
        return v.onTouchEvent(event);
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
    				number = Integer.parseInt(text);
    			}
    			building.SetNumOwned(number);
    			
    			float value = building.GetValue();
    			String valueString = Float.toString(value);
    			TextView valueText = building.GetViewValue();
    			valueText.setText(valueString);
    			
    			long currentCost = building.GetCurrentCost();
    			String currentCostString = Long.toString(currentCost);
    			TextView currentCostText = building.GetViewCurrentCost();
    			currentCostText.setText(currentCostString);
    			
    			Log.i(TAG,"onKey Building = " + building.GetName() + " number = " + number + " value = " + value);
    		}
    	}
    	return false;
    }
    // OR could use afterTextChanged() and compare Editable e with m_number1.getText()
    
    private void addRow(TableLayout parent,WWBuilding building)
    {
       	InputFilter[] filters5 = new InputFilter[1];
       	filters5[0] = new InputFilter.LengthFilter(5);
       	
       	InputFilter[] filters8 = new InputFilter[1];
       	filters8[0] = new InputFilter.LengthFilter(8);
       	
    	TableRow row = new TableRow(parent.getContext());
    	row.setTag(building);
    	
    	TextView name = new TextView(row.getContext());
    	name.setText(building.GetName());
    	name.setPadding(1,1,1,1);
    	name.setWidth(116);
    	name.setTag(building);
    	name.setTypeface(Typeface.DEFAULT_BOLD, Typeface.BOLD);
    	name.setGravity(Gravity.CENTER);
    	name.setShadowLayer(1.0f, 2.0f, 2.0f, Color.BLACK);
    	row.addView(name);
    	
    	EditText number = new EditText(row.getContext());
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
    	number.setSelectAllOnFocus(true);
    	building.SetViewNumOwned(number);
    	row.addView(number);
    	
    	TextView value = new TextView(row.getContext());
    	value.setText(Float.toString(building.GetValue()));
    	value.setKeyListener(new DigitsKeyListener());
    	value.setInputType(InputType.TYPE_CLASS_NUMBER);
    	value.setSingleLine();
    	value.setMinWidth(72);
    	value.setWidth(72);
    	value.setMaxWidth(72);
    	value.setMaxLines(1);
    	value.setFilters(filters8);
    	value.setPadding(5,5,5,5);
    	value.setWidth(72);
    	value.setTag(building);
    	building.SetViewValue(value);
    	row.addView(value);
    	
    	TextView reward = new TextView(row.getContext());
    	reward.setText(Float.toString(building.GetValue()));
    	reward.setKeyListener(new DigitsKeyListener());
    	reward.setInputType(InputType.TYPE_CLASS_NUMBER);
    	reward.setSingleLine();
    	reward.setMinWidth(64);
    	reward.setWidth(64);
    	reward.setMaxWidth(64);
    	reward.setMaxLines(1);
    	reward.setFilters(filters8);
    	reward.setPadding(5,5,5,5);
    	reward.setWidth(96);
    	reward.setTag(building);
    	reward.setText(Integer.toString(building.GetReward()));
    	row.addView(reward);
    	
    	TextView currentCost = new TextView(row.getContext());
    	currentCost.setText(Float.toString(building.GetValue()));
    	currentCost.setKeyListener(new DigitsKeyListener());
    	currentCost.setInputType(InputType.TYPE_CLASS_NUMBER);
    	currentCost.setSingleLine();
    	currentCost.setMinWidth(64);
    	currentCost.setWidth(64);
    	currentCost.setMaxWidth(64);
    	currentCost.setMaxLines(1);
    	currentCost.setFilters(filters8);
    	currentCost.setPadding(5,5,5,5);
    	currentCost.setWidth(96);
    	currentCost.setTag(building);
    	currentCost.setText(Long.toString(building.GetCurrentCost()));
    	building.SetViewCurrentCost(currentCost);
    	row.addView(currentCost);
    	
    	TextView baseCost = new TextView(row.getContext());
    	baseCost.setText(Float.toString(building.GetValue()));
    	baseCost.setKeyListener(new DigitsKeyListener());
    	baseCost.setInputType(InputType.TYPE_CLASS_NUMBER);
    	baseCost.setSingleLine();
    	baseCost.setMinWidth(64);
    	baseCost.setWidth(64);
    	baseCost.setMaxWidth(64);
    	baseCost.setMaxLines(1);
    	baseCost.setFilters(filters8);
    	baseCost.setPadding(5,5,5,5);
    	baseCost.setWidth(96);
    	baseCost.setTag(building);
    	baseCost.setText(Integer.toString(building.GetBaseCost()));
    	row.addView(baseCost);
    	
    	parent.addView(row);
    }
    private int LoadProfiles()
    {
    	int numProfiles = 0;
    	String[] fileNames = fileList();
    	int numFiles = fileNames.length;
    	if (numFiles>0)
    	{
    		String profileFileName = fileNames[0];
    		if ( LoadProfile(profileFileName)==true)
    		{
    			numProfiles++;
    		}
    	}
    	return numProfiles;
    }
    
    private boolean LoadProfile(String profileFileName)
    {
    	try
    	{
    		BufferedInputStream bufferedInput = new BufferedInputStream(openFileInput(profileFileName));
    		try
    		{
    			int result;
    			result = bufferedInput.read();
    			if (result>0)
    			{
    				int length = result;
   					Log.i(TAG, "IN: length = " + length);
    				byte[] byteBuffer = new byte[length];
    				result = bufferedInput.read(byteBuffer, 0, length);
    				if (result==length)
    				{
    					String name = new String(byteBuffer);
    					m_profileName = name;
    					Log.i(TAG, "IN: name = " + m_profileName);
    				}
    			}
    		}
    		catch (IOException e)
    		{
    			try
    			{
    				bufferedInput.close();
    			}
    			catch (IOException ex)
    			{
    			}
    			return false;
    		}
    	} 
    	catch (FileNotFoundException e)
    	{
    		return false;
    	}
    	return true;
    }
    	
    private void writeString(String str)
    {
    	int length = str.length();
    	Log.i(TAG, "OUT: length = " + length);
    	outputStream.(length);
    	Log.i(TAG, "OUT: name = " + name);
    	outputStream.write(str.getBytes(),0,str.length());
    }
    
    private boolean SaveProfile(String profileName)
    {
    	try
    	{
    		String profileFileName = "Profile_" + profileName;
    		BufferedOutputStream outputStream = new BufferedOutputStream(openFileOutput(profileFileName, MODE_PRIVATE));
    		
    		String name = profileName;
    		try
    		{
    			// Profile name
    			writeString(name);
    			
    			// Number of each income building
    			// Number of each defence building
    		}
    		catch (IOException e)
    		{
    			try 
    			{ 
    				outputStream.close(); 
    			}
    			catch (IOException ex) { }
    			return false;
    		}
    		try 
    		{ 
    			outputStream.close(); 
   			} 
    		catch (IOException e) 
    		{ 
    			return false;
    		}
    	} 
    	catch (FileNotFoundException e)
    	{
    		return false;
    	}
    	return true;
    }
    	
    private static final String TAG = "WWCALC";
    
    private static final int NUM_DEFENCE_BUILDINGS = 7;
    private static final int NUM_INCOME_BUILDINGS = 7;

    private WWBuilding[] m_defenceBuildings;
    private WWBuilding[] m_incomeBuildings;
    private int m_numDefenceBuildings;
    private int m_numIncomeBuildings;
    private String m_profileName;
    
    HorizontalScrollView m_incomeViewHeader;
    HorizontalScrollView m_incomeViewScroll;
}