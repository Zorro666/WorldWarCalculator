package worldwarcalculator.zorro666.com;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.widget.TabHost;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.HorizontalScrollView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class WorldWarCalc extends Activity implements OnKeyListener, OnTouchListener, OnClickListener /* , OnFocusChangeListener */

{
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		m_defenceBuildings = new WWBuilding[NUM_DEFENCE_BUILDINGS];
		m_incomeBuildings = new WWBuilding[NUM_INCOME_BUILDINGS];
		m_numDefenceBuildings = 0;
		m_numIncomeBuildings = 0;

		m_defenceBuildings[m_numDefenceBuildings++] = new WWDefenceBuilding( "Bunker", 30000, 3);
		m_defenceBuildings[m_numDefenceBuildings++] = new WWDefenceBuilding( "Guard Tower", 200000, 10);
		m_defenceBuildings[m_numDefenceBuildings++] = new WWDefenceBuilding( "Anti-Aircraft Launcher", 560000, 15);
		m_defenceBuildings[m_numDefenceBuildings++] = new WWDefenceBuilding( "Turret", 2800000, 32);
		m_defenceBuildings[m_numDefenceBuildings++] = new WWDefenceBuilding( "Landmine Field", 10000000, 50);
		m_defenceBuildings[m_numDefenceBuildings++] = new WWDefenceBuilding( "Automated Sentry Gun", 24000000, 75);

		m_incomeBuildings[m_numIncomeBuildings++] = new WWIncomeBuilding( "Supply Depot", 18000, 1000);
		m_incomeBuildings[m_numIncomeBuildings++] = new WWIncomeBuilding( "Refinery", 150000, 6500);
		m_incomeBuildings[m_numIncomeBuildings++] = new WWIncomeBuilding( "Weapons Factor", 540000, 16500);
		m_incomeBuildings[m_numIncomeBuildings++] = new WWIncomeBuilding( "Power Plant", 2700000, 56000);
		m_incomeBuildings[m_numIncomeBuildings++] = new WWIncomeBuilding( "Oil Rig", 20000000, 270000);
		m_incomeBuildings[m_numIncomeBuildings++] = new WWIncomeBuilding( "Military Research Lab", 60000000, 500000);
		m_incomeBuildings[m_numIncomeBuildings++] = new WWIncomeBuilding( "Nuclear Testing Facility", 100000000, 700000);
		m_incomeBuildings[m_numIncomeBuildings++] = new WWIncomeBuilding( "Solar Satellite Network", 340000000, 1200000);

		m_profileNames = new ArrayList<String>();
		m_profiles = new HashMap<String, WWProfile>(DEFAULT_NUM_PROFILES);

		// Temp save a fake profile to test the loading profile save & load code
		// ProfileSave();

		// Load the profile files in
		// LoadProfiles();

		if (m_profileNames.size() == 0) 
		{
			ProfileNew();
		}

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Button saveProfile = (Button) findViewById(R.id.profileSaveButton);
		saveProfile.setOnClickListener(this);

		// EditText profileName = (EditText)findViewById(R.id.profileName);
		// profileName.setOnFocusChangeListener(this);

		 Spinner profileNameView = (Spinner)findViewById(R.id.profileSpinner);
		 // Subclass it and implement getView function to make it work with WWProfile
		 ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, m_profileNames);
		 adapter.setDropDownViewResource(android.R.layout. simple_spinner_dropdown_item);
		 profileNameView.setAdapter(adapter);
		 profileNameView.setSelection(0);
		 
		 m_activeProfile = m_profiles.get(m_profileNames.get(0));
		 Log.i(TAG, "m_activeProfile.Name=" + m_activeProfile.GetName());

		TableLayout defenceView = (TableLayout) findViewById(R.id.DefenceView);
		for (int i = 0; i < m_numDefenceBuildings; i++) 
		{
			WWProfileEntry profileEntry = m_activeProfile.GetDefenceBuilding(i);
			WWBuilding building = m_defenceBuildings[i];
			addRow(defenceView, building, profileEntry);
		}

		// m_incomeViewHeader =
		// (HorizontalScrollView)findViewById(R.id.IncomeViewHeader);
		// m_incomeViewHeader.setOnTouchListener(this);

		// HorizontalScrollView m_incomeViewScroll =
		// (HorizontalScrollView)findViewById(R.id.IncomeViewScroll);
		// m_incomeViewScroll.setOnTouchListener(this);
		// m_incomeViewScroll.setSmoothScrollingEnabled(true);

		TableLayout incomeView = (TableLayout) findViewById(R.id.IncomeViewData);
		for (int i = 0; i < m_numIncomeBuildings; i++) 
		{
			WWProfileEntry profileEntry = m_activeProfile.GetIncomeBuilding(i);
			WWBuilding building = m_incomeBuildings[i];
			addRow(incomeView, building, profileEntry);
		}

		final TabHost tabs = (TabHost) findViewById(R.id.tabhost);
		tabs.setup();

		TabHost.TabSpec spec;

		spec = tabs.newTabSpec("Profile");
		spec.setContent(R.id.ProfileViewTab);
		spec.setIndicator("Profile");
		tabs.addTab(spec);

		spec = tabs.newTabSpec("Income");
		spec.setContent(R.id.IncomeViewTab);
		spec.setIndicator("Income");
		tabs.addTab(spec);

		spec = tabs.newTabSpec("Defence");
		spec.setContent(R.id.DefenceView);
		spec.setIndicator("Defence");
		tabs.addTab(spec);
		tabs.setCurrentTab(1);
	}

	public boolean onTouch(View v, MotionEvent event) 
	{
		if (v == m_incomeViewScroll) 
		{
			m_incomeViewHeader.onTouchEvent(event);
		}
		if (v == m_incomeViewHeader) 
		{
			m_incomeViewScroll.onTouchEvent(event);
		}
		return v.onTouchEvent(event);
	}

	/*
	 * public void onFocusChange(View v, boolean hasFocus) { if
	 * (v.getId()==R.id.profileName) { if (hasFocus == false) { // Get the new
	 * profile name and compare against the active profile name // Update active
	 * profile name if it has changed // Update m_profileNames, m_profiles,
	 * profile drop down list EditText profileNameView = (EditText)v; Log.i(TAG,
	 * "Profile name has changed:" + (EditText)profileNameView.getText()); } } }
	 */
	public void onClick(View v) 
	{
		if (v.getId() == R.id.profileSaveButton) 
		{
			ProfileSave();
		}
		if (v.getId() == R.id.profileNewButton) 
		{
			ProfileNew();
		}
	}

	public boolean onKey(View v, int key, KeyEvent event) 
	{
		Object tag = v.getTag();
		if (tag.getClass() == WWProfileEntry.class) 
		{
			WWProfileEntry profileEntry = (WWProfileEntry) tag;
			WWBuilding building = profileEntry.GetBuilding();
			EditText numberText = building.GetViewNumOwned();
			if (v == numberText) 
			{
				int numOwned = 0;
				String text = numberText.getText().toString();

				if (text.length() > 0) 
				{
					numOwned = Integer.parseInt(text);
				}
				profileEntry.SetNumOwned(numOwned);

				float value = building.GetValue(numOwned);
				String valueString = Float.toString(value);
				TextView valueText = building.GetViewValue();
				valueText.setText(valueString);

				long currentCost = building.GetCurrentCost(numOwned);
				String currentCostString = Long.toString(currentCost);
				TextView currentCostText = building.GetViewCurrentCost();
				currentCostText.setText(currentCostString);

				Log.i(TAG, "onKey Building = " + building.GetName() + " numOwned = " + numOwned + " value = " + value);
			}
		}
		return false;
	}

	public void OnItemSelected(AdapterView<?> parent, View view, int position, long id) 
	{
	}

	private void addRow(TableLayout parent, WWBuilding building, WWProfileEntry profileEntry) 
	{
		int numOwned = profileEntry.GetNumOwned();
		InputFilter[] filters5 = new InputFilter[1];
		filters5[0] = new InputFilter.LengthFilter(5);

		InputFilter[] filters8 = new InputFilter[1];
		filters8[0] = new InputFilter.LengthFilter(8);

		TableRow row = new TableRow(parent.getContext());
		row.setTag(profileEntry);

		TextView name = new TextView(row.getContext());
		name.setText(building.GetName());
		name.setPadding(1, 1, 1, 1);
		name.setWidth(116);
		name.setTag(profileEntry);
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
		number.setText(Integer.toString(numOwned));
		number.setPadding(5, 0, 5, 0);
		number.setTag(profileEntry);
		number.setOnKeyListener(this);
		number.setSelectAllOnFocus(true);
		number.setSingleLine(true);
		number.setHeight(8);
		building.SetViewNumOwned(number);
		row.addView(number);

		TextView value = new TextView(row.getContext());
		value.setText(Float.toString(building.GetValue(numOwned)));
		value.setKeyListener(new DigitsKeyListener());
		value.setInputType(InputType.TYPE_CLASS_NUMBER);
		value.setSingleLine();
		value.setMinWidth(72);
		value.setWidth(72);
		value.setMaxWidth(72);
		value.setMaxLines(1);
		value.setFilters(filters8);
		value.setPadding(5, 5, 5, 5);
		value.setWidth(72);
		value.setTag(profileEntry);
		building.SetViewValue(value);
		row.addView(value);

		TextView reward = new TextView(row.getContext());
		reward.setKeyListener(new DigitsKeyListener());
		reward.setInputType(InputType.TYPE_CLASS_NUMBER);
		reward.setSingleLine();
		reward.setMinWidth(64);
		reward.setWidth(64);
		reward.setMaxWidth(64);
		reward.setMaxLines(1);
		reward.setFilters(filters8);
		reward.setPadding(5, 5, 5, 5);
		reward.setWidth(96);
		reward.setTag(profileEntry);
		reward.setText(Integer.toString(building.GetReward()));
		row.addView(reward);

		TextView currentCost = new TextView(row.getContext());
		currentCost.setText(Float.toString(building.GetValue(numOwned)));
		currentCost.setKeyListener(new DigitsKeyListener());
		currentCost.setInputType(InputType.TYPE_CLASS_NUMBER);
		currentCost.setSingleLine();
		currentCost.setMinWidth(64);
		currentCost.setWidth(64);
		currentCost.setMaxWidth(64);
		currentCost.setMaxLines(1);
		currentCost.setFilters(filters8);
		currentCost.setPadding(5, 5, 5, 5);
		currentCost.setWidth(96);
		currentCost.setTag(profileEntry);
		currentCost.setText(Long.toString(building.GetCurrentCost(numOwned)));
		building.SetViewCurrentCost(currentCost);
		row.addView(currentCost);

		TextView baseCost = new TextView(row.getContext());
		baseCost.setText(Float.toString(building.GetValue(numOwned)));
		baseCost.setKeyListener(new DigitsKeyListener());
		baseCost.setInputType(InputType.TYPE_CLASS_NUMBER);
		baseCost.setSingleLine();
		baseCost.setMinWidth(64);
		baseCost.setWidth(64);
		baseCost.setMaxWidth(64);
		baseCost.setMaxLines(1);
		baseCost.setFilters(filters8);
		baseCost.setPadding(5, 5, 5, 5);
		baseCost.setWidth(96);
		baseCost.setTag(profileEntry);
		baseCost.setText(Integer.toString(building.GetBaseCost()));
		row.addView(baseCost);

		parent.addView(row);
	}

	private int LoadProfiles() 
	{
		int numProfiles = 0;
		String[] fileNames = fileList();
		int numFiles = fileNames.length;
		if (numFiles > 0) 
		{
			for (int file = 0; file < numFiles; file++) 
			{
				String profileFileName = fileNames[file];
				if (profileFileName.startsWith("Profile3_")) 
				{
					try 
					{
						if (LoadProfile(profileFileName) == true) 
						{
							numProfiles++;
						}
					} 
					catch (IOException e) 
					{
					}
				}
			}
		}
		return numProfiles;
	}

	private boolean LoadProfile(String profileFileName) throws IOException 
	{
		try 
		{
			TextFileInput inFile = new TextFileInput( openFileInput(profileFileName));
			try 
			{
				// Profile name
				String name = inFile.ReadString();
				WWProfile tempProfile = CreateNewProfile(name);

				// Number of each defence building
				int numDefenceBuildings = inFile.ReadInt();
				for (int i = 0; i < numDefenceBuildings; i++) 
				{
					int number = inFile.ReadInt();
					tempProfile.SetNumDefenceBuilding(i, number);
				}
				// Number of each income building
				int numIncomeBuildings = inFile.ReadInt();
				for (int i = 0; i < numIncomeBuildings; i++) 
				{
					int number = inFile.ReadInt();
					tempProfile.SetNumIncomeBuilding(i, number);

				}
				AddProfile(name, tempProfile);
			} 
			catch (IOException e) 
			{
				inFile.Close();
				return false;
			}
			return true;
		} 
		catch (FileNotFoundException e) 
		{
			return false;
		}
	}

	private boolean SaveProfile(String profileName) throws IOException 
	{
		try 
		{
			String profileFileName = "Profile3_" + profileName;
			TextFileOutput outFile = new TextFileOutput(openFileOutput( profileFileName, MODE_PRIVATE));

			try 
			{
				String name = m_activeProfile.GetName();

				// Profile name
				outFile.WriteString(name);

				// Number of each defence building
				int numDefenceBuildings = m_activeProfile.GetNumDefenceBuildings();
				outFile.WriteInt(numDefenceBuildings);
				for (int i = 0; i < numDefenceBuildings; i++) 
				{
					int number = m_activeProfile.GetNumDefenceBuilding(i);
					outFile.WriteInt(number);
				}
				// Number of each income building
				int numIncomeBuildings = m_activeProfile .GetNumIncomeBuildings();
				outFile.WriteInt(numIncomeBuildings);
				for (int i = 0; i < numIncomeBuildings; i++) 
				{
					int number = m_activeProfile.GetNumIncomeBuilding(i);
					outFile.WriteInt(number);
				}
			} 
			catch (IOException e) 
			{
				outFile.Close();
				return false;
			}
			outFile.Close();
			return true;
		} 
		catch (FileNotFoundException e) 
		{
			return false;
		}
	}

	private void ProfileNew() 
	{
		String name = "default";
		WWProfile tempProfile = CreateNewProfile(name);
		AddProfile(name, tempProfile);
	}

	private WWProfile CreateNewProfile(String name) 
	{
		WWProfile profile = m_profiles.get(name);
		if (profile == null) 
		{
			profile = new WWProfile(name);
			Log.i(TAG, "CreateNewProfile:" + name);
		}
		for (int i = 0; i < m_numDefenceBuildings; i++) 
		{
			WWProfileEntry profileEntry = profile.GetDefenceBuilding(i);
			WWBuilding building = m_defenceBuildings[i];
			profileEntry.SetBuilding(building);
		}
		for (int i = 0; i < m_numIncomeBuildings; i++) 
		{
			WWProfileEntry profileEntry = profile.GetIncomeBuilding(i);
			WWBuilding building = m_incomeBuildings[i];
			profileEntry.SetBuilding(building);
		}
		return profile;
	}

	private void AddProfile(String name, WWProfile profile) 
	{
		if (m_profiles.containsKey(name) == false) 
		{
			m_profileNames.add(name);
			m_profiles.put(name, profile);

		}
	}

	private void ProfileSave() 
	{
		try 
		{
			String profileFileName = m_activeProfile.GetName() + "Profile";
			SaveProfile(profileFileName);
		} 
		catch (IOException e) 
		{
		}
	}

	private static final String TAG = "WWCALC";

	public static final int NUM_DEFENCE_BUILDINGS = 7;
	public static final int NUM_INCOME_BUILDINGS = 8;
	public static final int DEFAULT_NUM_PROFILES = 8;

	private int m_numDefenceBuildings;
	private int m_numIncomeBuildings;
	private WWBuilding[] m_defenceBuildings;
	private WWBuilding[] m_incomeBuildings;

	private Map<String, WWProfile> m_profiles;
	private List<String> m_profileNames;
	private WWProfile m_activeProfile;

	HorizontalScrollView m_incomeViewHeader;
	HorizontalScrollView m_incomeViewScroll;
}