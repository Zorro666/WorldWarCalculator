package com.zorro666.worldwarcalculator;

import android.app.Activity;
import android.app.AlertDialog;

import android.content.Context;
import android.content.DialogInterface;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;

import android.os.Bundle;
import android.os.Environment;

import android.text.InputFilter;
import android.text.InputType;
import android.text.method.DigitsKeyListener;

import android.util.AttributeSet;
import android.util.Log;

import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;

import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabWidget;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorldWarCalc extends Activity implements OnKeyListener, OnTouchListener, OnClickListener, OnFocusChangeListener, OnItemSelectedListener, OnTabChangeListener, DialogInterface.OnClickListener
{
     //A custom HorizontalScrollView 
    public static class LinkedHorizontalScrollView extends HorizontalScrollView 
    {
        // we need this constructor for LayoutInflater
        public LinkedHorizontalScrollView(Context context, AttributeSet attrs) 
        {
            super(context, attrs);
            m_linkedView = null;
        }
           
        @Override
        protected void onDraw(Canvas canvas) 
        {
        	super.onDraw(canvas);
        	if (m_linkedView != null)
        	{
        		// Keep the linked view in sync
        		int myScrollX = getScrollX();
				int linkedScrollX = m_linkedView.getScrollX();
				if (myScrollX != linkedScrollX)
				{
					int y = m_linkedView.getScrollY();
					m_linkedView.scrollTo(myScrollX,y);
				}
        	}
        }
        
        public void setLinkedView(View linkedView)
        {
        	m_linkedView = linkedView;
        }
        
        private View m_linkedView;
    }
    /** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		Log.i(TAG,"onCreate");
		super.onCreate(savedInstanceState);
		
		m_dialogAction = DIALOG_NONE;
	
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
		m_incomeBuildings[m_numIncomeBuildings++] = new WWIncomeBuilding( "Weapons Factory", 540000, 16500);
		m_incomeBuildings[m_numIncomeBuildings++] = new WWIncomeBuilding( "Power Plant", 2700000, 56000);
		m_incomeBuildings[m_numIncomeBuildings++] = new WWIncomeBuilding( "Oil Rig", 20000000, 270000);
		m_incomeBuildings[m_numIncomeBuildings++] = new WWIncomeBuilding( "Military Research Lab", 60000000, 500000);
		m_incomeBuildings[m_numIncomeBuildings++] = new WWIncomeBuilding( "Nuclear Testing Facility", 100000000, 700000);
		m_incomeBuildings[m_numIncomeBuildings++] = new WWIncomeBuilding( "Solar Satellite Network", 340000000, 1200000);

		m_activeProfile = new WWProfile("default", NUM_INCOME_BUILDINGS, NUM_DEFENCE_BUILDINGS);
		m_activeProfileName = m_activeProfile.GetName();
		m_bestBuildingToBuy = "";
		
		m_profileNames = new ArrayList<String>();
		m_profiles = new HashMap<String, WWProfile>(DEFAULT_NUM_PROFILES);

		m_profilesAdapter =  new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, m_profileNames);
		
		setContentView(R.layout.main);

		Button saveProfile = (Button) findViewById(R.id.profileSaveButton);
		saveProfile.setOnClickListener(this);
		
		Button newProfile = (Button) findViewById(R.id.profileNewButton);
		newProfile.setOnClickListener(this);

		Button deleteProfile = (Button) findViewById(R.id.profileDeleteButton);
		deleteProfile.setOnClickListener(this);

		//EditText profileName = (EditText)findViewById(R.id.profileName);
		//profileName.setOnFocusChangeListener(this);

		Spinner profileNameView = (Spinner)findViewById(R.id.profileSpinner);
		profileNameView.setOnItemSelectedListener(this);
		
		Button copyProfile = (Button) findViewById(R.id.profileCopyButton);
		copyProfile.setOnClickListener(this);
		
		Button renameProfile = (Button) findViewById(R.id.profileRenameButton);
		renameProfile.setOnClickListener(this);
		
		Button backup = (Button) findViewById(R.id.profileBackupButton);
		backup.setOnClickListener(this);
		
		Button restore = (Button) findViewById(R.id.profileRestoreButton);
		restore.setOnClickListener(this);
		
		// An option would be to subclass it and implement getView function to make it work with WWProfile
		m_profilesAdapter.setDropDownViewResource(android.R.layout. simple_spinner_dropdown_item);
		profileNameView.setAdapter(m_profilesAdapter);
		profileNameView.setSelection(0);
		
		// Make the defence view layout
		TableLayout defenceViewHeaderData = (TableLayout) findViewById(R.id.DefenceViewHeaderData);
		addHeaderRow(defenceViewHeaderData);
		
		TableLayout defenceView = (TableLayout) findViewById(R.id.DefenceViewData);
		boolean evenRow;
		evenRow = true;
		for (int i = 0; i < m_numDefenceBuildings; i++) 
		{
			WWBuilding building = m_defenceBuildings[i];
			addRow(defenceView, building);
			
			TableRow row = building.GetViewRow();
			SetDefaultRowColours(row,evenRow);
			evenRow ^= true;
		}
		// Add the final separator row
		TableRow sep = new TableRow(defenceView.getContext());
		sep.setBackgroundColor(Color.WHITE);
		sep.setPadding(0,1,0,1);
		defenceView.addView(sep);

		m_defenceViewHeader = (HorizontalScrollView)findViewById(R.id.DefenceViewHeader);
		m_defenceViewHeader.setOnTouchListener(this);
		m_defenceViewHeader.setSmoothScrollingEnabled(true);

		m_defenceViewScroll = (LinkedHorizontalScrollView)findViewById(R.id.DefenceViewScroll);
		m_defenceViewScroll.setOnTouchListener(this);
		m_defenceViewScroll.setSmoothScrollingEnabled(true);
		m_defenceViewScroll.setLinkedView(m_defenceViewHeader);
		
		// Make the income view layout
		TableLayout incomeViewHeaderData = (TableLayout) findViewById(R.id.IncomeViewHeaderData);
		addHeaderRow(incomeViewHeaderData);
		
		TableLayout incomeView = (TableLayout) findViewById(R.id.IncomeViewData);
		evenRow = true;
		for (int i = 0; i < m_numIncomeBuildings; i++) 
		{
			WWBuilding building = m_incomeBuildings[i];
			addRow(incomeView, building);
			
			TableRow row = building.GetViewRow();
			SetDefaultRowColours(row,evenRow);
			evenRow ^= true;
		}
		// Add the final separator row
		sep = new TableRow(incomeView.getContext());
		sep.setBackgroundColor(Color.WHITE);
		sep.setPadding(0,1,0,1);
		incomeView.addView(sep);

		m_incomeViewHeader = (HorizontalScrollView)findViewById(R.id.IncomeViewHeader);
		m_incomeViewHeader.setOnTouchListener(this);
		m_incomeViewHeader.setSmoothScrollingEnabled(true);

		m_incomeViewScroll = (LinkedHorizontalScrollView)findViewById(R.id.IncomeViewScroll);
		m_incomeViewScroll.setOnTouchListener(this);
		m_incomeViewScroll.setSmoothScrollingEnabled(true);
		m_incomeViewScroll.setLinkedView(m_incomeViewHeader);

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
		spec.setContent(R.id.DefenceViewTab);
		spec.setIndicator("Defence");
		tabs.addTab(spec);
		
		// Create a new default profile to use - do this before the tab listener is set so the tab listener code has a valid profile to work with!
		ProfileNew();
		
		tabs.setOnTabChangedListener(this);
		tabs.setCurrentTab(1);
	}
	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState)
	{
	}
	
	@Override
	public void onStart()
	{
		Log.i(TAG,"onStart");
		super.onStart();
		
		LoadAllProfilesFromPrivateData(true);
	}

	@Override
	// The activity comes to the foreground from being stopped (from onStop state)
	public void onRestart()
	{
		Log.i(TAG,"onRestart");
		super.onRestart();
	}

	// The activity comes to the foreground from being paused (from onPause state)
	@Override
	public void onResume()
	{
		Log.i(TAG,"onResume");
		super.onResume();
		
		Log.i(TAG,"onResume RestoreAppState");
		LoadAppState();
		RestoreAppState();
	}
	
	// Another application comes in front of this view
	@Override
	public void onPause()
	{
		Log.i(TAG,"onPause");
		super.onPause();
		
		// Save state in the onSaveInstanceState() override
		// Restore state in the onRestoreInstanceState() override
		// BAD BAD BAD BAD BAD
		SaveAllProfiles(false);
		SaveAppState();
	}

	// Application is no longer in view
	@Override
	public void onStop()
	{
		Log.i(TAG,"onStop");
		super.onStop();
	}
	
	// Application is being destroyed
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		
		Log.i(TAG,"onDestroy");
		SaveAllProfiles(true);
		SaveAppState();
	}
	
	public boolean onTouch(View v, MotionEvent event) 
	{
		if (v == m_incomeViewScroll) 
		{
			int scrollX0 = v.getScrollX();
			int scrollY0 = v.getScrollY();
			
			boolean result = v.onTouchEvent(event);
			
			int scrollX1= v.getScrollX();
			int scrollY1= v.getScrollY();
			
			// Keep the horizontal motion in sync
			int x = m_incomeViewHeader.getScrollX();
			if (scrollX1 != x)
			{
				int y = m_incomeViewHeader.getScrollY();
				m_incomeViewHeader.scrollTo(scrollX1,y);
			}
			Log.i(TAG,"onTouch BEFORE: "+scrollX0+","+scrollY0+" AFTER: "+scrollX1+","+scrollY1);
			return result;
		}
		if (v == m_incomeViewHeader) 
		{
			return true;
		}
		return v.onTouchEvent(event);
	}

	public void onFocusChange(View v, boolean hasFocus) 
	{ 
	}
	public void onTabChanged(String tabId)
	{
		if (m_activeProfile != null)
		{
			UpdateDisplay();
		}
		//For the profile tab update the "backup" "restore" buttons
		if (tabId.equals("Profile"))
		{
			UpdateProfileTabDisplay();
		}
	}
	
	public void onClick(DialogInterface dialog, int which)
	{
		if (which == DialogInterface.BUTTON_POSITIVE)
		{
			if (m_dialogAction == DIALOG_RESTORE)
			{
				//Wipe all local data
				if (RemoveAllLocalData()==true)
				{
					boolean result = RestoreData();
					if (result==true)
					{
						SaveAllProfiles(true);
						SaveAppState();
						RestoreAppState();
					}
					AlertResultDialog("Restore Data", result);
				}
				else
				{
					AlertErrorDialog("Restore Data", "RemoveAllLocalData failed");
				}
			}
			if (m_dialogAction == DIALOG_BACKUP)
			{
				InternalBackup();
			}
			if (m_dialogAction == DIALOG_PROFILE_DELETE)
			{
				if (ProfileDelete() == false)
				{
					AlertErrorDialog("Delete Profile", "Error deleting profile"+m_activeProfile.GetName());
				}
			}
		}
		else
		{
			Log.i(TAG,"onClick ignore dialog action="+m_dialogAction+" which="+which);
		}
		UpdateDisplay();
		m_dialogAction = DIALOG_NONE;
	}
	
	public void onClick(View v) 
	{
		if (v.getId() == R.id.profileSaveButton) 
		{
			if (ProfileSave(m_activeProfile,true) == false)
			{
				AlertErrorDialog("Save Profile", "Error saving profile: "+m_activeProfile.GetName());
			}
			else
			{
				AlertOkDialog("Save Profile", "Success saving profile: "+m_activeProfile.GetName(), false);
			}
			UpdateDisplay();
		}
		else if (v.getId() == R.id.profileNewButton) 
		{
			ProfileNew();
			UpdateDisplay();
		}
		else if (v.getId() == R.id.profileDeleteButton)
		{
			m_dialogAction = DIALOG_PROFILE_DELETE;
			AlertYesNoDialog( "Delete Profile: "+m_activeProfile.GetName(), "Are you sure?");
		}
		else if (v.getId() == R.id.profileRenameButton)
		{
			EditText profileNameView = (EditText)findViewById(R.id.profileName);
			String newName = profileNameView.getText().toString();
			
			ProfileRename(newName);
			UpdateDisplay();
		}
		else if (v.getId() == R.id.profileCopyButton)
		{
			EditText profileNameView = (EditText)findViewById(R.id.profileName);
			String newName = profileNameView.getText().toString();
		
			ProfileCopy(newName);
			UpdateDisplay();
		}
		else if (v.getId() == R.id.profileBackupButton)
		{
			if (TestForBackupData() == true)
			{
				m_dialogAction = DIALOG_BACKUP;
				AlertYesNoDialog( "Backup Data: Warning", "This will overwrite existing backup data. Are you sure?");
			}
			else
			{
				InternalBackup();
			}
		}
		else if (v.getId() == R.id.profileRestoreButton)
		{
			m_dialogAction = DIALOG_RESTORE;
			AlertYesNoDialog( "Restore Data: Warning", "This will overwrite all local data. Are you sure?");
		}
		else
		{
			Object tag = v.getTag();
			if (tag.getClass() == WWProfileEntry.class)
			{
				WWProfileEntry profileEntry = (WWProfileEntry) tag;
				WWBuilding building = profileEntry.GetBuilding();
				int delta = 0;
				if (v.getId() == 123456)
				{
					delta = -1;
					Log.i(TAG,"Minus Button:"+building.GetName());
				}
				if (v.getId() == 654321)
				{
					delta = +1;
				}
				if (delta != 0)
				{
					int numOwned = profileEntry.GetNumOwned();
					numOwned += delta;
					if ((numOwned >= 0) && (numOwned <= 9999))
					{
						profileEntry.SetNumOwned(numOwned);
						ComputeNumToBuy();
						UpdateBuildingRow(profileEntry);
						UpdateBuildingNumOwned(profileEntry);
						UpdateDisplay();
						Log.i(TAG,"-/+ Button:"+building.GetName()+" Delta:"+delta);
					}
				}
			}
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
				
				ComputeNumToBuy();
				UpdateBuildingRow(profileEntry);
				UpdateDisplay();

				Log.i(TAG, "onKey Building = " + profileEntry.GetBuilding().GetName() + " numOwned = " + numOwned+ " key="+key);
			}
		}
		return false;
	}

	public void onNothingSelected(AdapterView<?> parent)
	{
	}

	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) 
	{
		if (parent.getId() == R.id.profileSpinner)
		{
			TextView item = (TextView)view;
			if (item != null)
			{
				String profileName = item.getText().toString();
				Log.i(TAG,"onItemSelected["+position+"] = "+profileName);
				ProfileSelect();
				UpdateDisplay();
			}
		}
	}
	private void RestoreAppState()
	{
		// Set selected item to match the active profile
		if (m_profilesAdapter.getCount() == 0)
		{
			ProfileNew();
		}

		ProfileSetSelectedProfile(m_activeProfileName);
	}
	private void ProfileSetSelectedProfile(String profileName)
	{
		Spinner profileSpinner = (Spinner)findViewById(R.id.profileSpinner);
		int itemIndex = m_profilesAdapter.getPosition(profileName);
		int numItems = profileSpinner.getCount();
		if ((itemIndex < 0) || (itemIndex >= numItems))
		{
			Log.i(TAG,"ProfileSetSelectedProfile: "+profileName+" NOT FOUND");
			return;
		}
		Log.i(TAG,"ProfileSetSelectedProfile: "+profileName+" "+itemIndex);
		profileSpinner.setSelection(itemIndex);
		ProfileSelect();
	}
	
	private void ProfileSelect()
	{
		Spinner profileSpinner = (Spinner)findViewById(R.id.profileSpinner);
		int itemIndex = (int)profileSpinner.getSelectedItemId();
		int numItems = profileSpinner.getCount();
		if (itemIndex >= numItems)
		{
			itemIndex = 0;
		}
		if (numItems == 0)
		{
			return;
		}
		String profileName = m_profilesAdapter.getItem(itemIndex);
		Log.i(TAG,"ProfileSelect = "+profileName);
		ProfileSelectByName(profileName);
	}
	
	void ProfileSelectByName(String profileName)
	{
		Log.i(TAG,"ProfileSelectByName = "+profileName);
		WWProfile profile = m_profiles.get(profileName);
		// Need to error check this in case it returns NULL
		if (profile == null)
		{
			Log.i(TAG,"ProfileSelectByName: NULL = "+profileName);
			// Make sure we have a profile to use
			if (m_profilesAdapter.getCount() == 0)		
			{
				ProfileNew();
			}
		}
		
		EditText profileNameView = (EditText)findViewById(R.id.profileName);
		profileNameView.setText(profileName);
		
		// Now update the displayed details based on the active profile
		m_activeProfile = profile;
		m_activeProfileName = m_activeProfile.GetName();
		UpdateBuildingsView();
	}
	
	private void UpdateBuildingsView()
	{
		ComputeNumToBuy();
		for (int i = 0; i < m_activeProfile.GetNumDefenceBuildings(); i++) 
		{
			WWProfileEntry profileEntry = m_activeProfile.GetDefenceBuilding(i);
			UpdateBuildingRow(profileEntry);
			UpdateBuildingNumOwned(profileEntry);
		}
		for (int i = 0; i < m_activeProfile.GetNumIncomeBuildings(); i++) 
		{
			WWProfileEntry profileEntry = m_activeProfile.GetIncomeBuilding(i);
			UpdateBuildingRow(profileEntry);
			UpdateBuildingNumOwned(profileEntry);
		}
		UpdateDisplay();
	}
	private void HighlightBestBuildingToBuy(String bestBuildingToBuy)
	{
		final TabHost tabs = (TabHost)findViewById(R.id.tabhost);
		String tabName = tabs.getCurrentTabTag();

		if (tabName.equals("Income"))
		{
			boolean evenRow;
			evenRow = true;
			for (int i = 0; i < m_activeProfile.GetNumIncomeBuildings(); i++) 
			{
				WWProfileEntry profileEntry = m_activeProfile.GetIncomeBuilding(i);
				WWBuilding building = profileEntry.GetBuilding();
				
				if (building != null)
				{
					TableRow row = building.GetViewRow();
					//Reset colours to normal or highlight if this is the best building to buy
					boolean highlight = (building.GetName().equals(bestBuildingToBuy));
					SetRowColours(row,evenRow,highlight);
					
					int numBuy = profileEntry.GetNumBuy();
					TextView numBuyView = building.GetViewNumBuy();
					String numBuyString = Integer.toString(numBuy);
					numBuyView.setText(numBuyString);
				}
				evenRow ^= true;
			}
		}
		else if (tabName.equals("Defence"))
		{
			boolean evenRow;
			evenRow = true;
			for (int i = 0; i < m_activeProfile.GetNumDefenceBuildings(); i++) 
			{
				WWProfileEntry profileEntry = m_activeProfile.GetDefenceBuilding(i);
				WWBuilding building = profileEntry.GetBuilding();
				
				if (building != null)
				{
					TableRow row = building.GetViewRow();
					//Highlight if this is the best building to buy
					//Reset colours to normal or highlight if this is the best building to buy
					boolean highlight = (building.GetName().equals(bestBuildingToBuy));
					SetRowColours(row,evenRow,highlight);
				}
				evenRow ^= true;
			}
		}
	}
	private void UpdateProfileTabDisplay()
	{
		boolean backupFileFound = TestForBackupData();
		Button restoreButton = (Button)findViewById(R.id.profileRestoreButton);
		restoreButton.setEnabled(backupFileFound);
			
		boolean storageWritable = IsExternalStorageWritable();
		Button backupButton = (Button)findViewById(R.id.profileBackupButton);
		backupButton.setEnabled(storageWritable);
	}
	private void UpdateDisplay()
	{
		final TabHost tabs = (TabHost)findViewById(R.id.tabhost);
		String tabName = tabs.getCurrentTabTag();
		//For the profile tab update the "backup" "restore" buttons
		if (tabName.equals("Profile"))
		{
			UpdateProfileTabDisplay();
		}
		
		UpdateTabTitles();
		
		UpdateHintText();
		
		// Now highlight the best buy row in bold & red
		HighlightBestBuildingToBuy(m_bestBuildingToBuy);
	}
	private void UpdateTabTitles()
	{
		final TabHost tabs = (TabHost)findViewById(R.id.tabhost);
		
		long totalDefence = m_activeProfile.GetTotalDefence();
		String totalDefenceString = PrettyPrintNumber(totalDefence,true);
		
		long totalIncome = m_activeProfile.GetTotalIncome();
		String totalIncomeString = PrettyPrintNumber(totalIncome,true);
		
		TabWidget tabWidget = tabs.getTabWidget();
		int numTabs = tabWidget.getChildCount();
		for (int tabIndex=0; tabIndex<numTabs; tabIndex++)
		{
			RelativeLayout tabIndicator = (RelativeLayout)tabWidget.getChildAt(tabIndex);
			int numChild = tabIndicator.getChildCount();
			if (numChild>1)
			{
				//ImageView imageView = (ImageView)tabIndicator.getChildAt(0);
				TextView textView = (TextView)tabIndicator.getChildAt(1);
				textView.setSingleLine(false);
				String textString = textView.getText().toString();
				if (textString.contains("Income"))
				{
					String tabTitle = "Income " + totalIncomeString;
					textView.setText(tabTitle);
				}
				else if (textString.contains("Defence"))
				{
					String tabTitle = "Defence " + totalDefenceString;
					textView.setText(tabTitle);
				}
				else if (textString.contains("Profile"))
				{
					String tabTitle = "Profile";
					if (m_activeProfile.HasChanged())
					{
						tabTitle += " Changed";
					}
					textView.setText(tabTitle);
				}
			}
		}
	}
	private void ComputeNumToBuy()
	{
		final TabHost tabs = (TabHost)findViewById(R.id.tabhost);
		String tabName = tabs.getCurrentTabTag();
		if (tabName.equals("Income"))
		{
			m_activeProfile.SortIncomeCheapness();
		}
		else if (tabName.equals("Defence"))
		{
			m_activeProfile.SortDefenceCheapness();
		}
	}
	private void UpdateHintText()
	{
		final TabHost tabs = (TabHost)findViewById(R.id.tabhost);
		String tabName = tabs.getCurrentTabTag();
		Log.i(TAG,"tabName="+tabName);

		TextView hintView = (TextView)findViewById(R.id.hintText);
		String hintText = "";
		String bestBuildingToBuy = "";
		if (tabName.equals("Income"))
		{
			WWProfileEntry cheapestIncome = m_activeProfile.GetSortedIncomeEntry(0);
			WWBuilding cheapestIncomeBuilding = cheapestIncome.GetBuilding();
			if (cheapestIncomeBuilding != null)
			{
				bestBuildingToBuy = cheapestIncomeBuilding.GetName();
				int numBuy = m_activeProfile.GetIncomeNumBuy(0);
				hintText = "Buy " + numBuy + " " + bestBuildingToBuy;
			}
			cheapestIncome = m_activeProfile.GetSortedIncomeEntry(1);
			cheapestIncomeBuilding = cheapestIncome.GetBuilding();
			if (cheapestIncomeBuilding != null)
			{
				int numBuy = m_activeProfile.GetIncomeNumBuy(1);
				hintText += "\n";
				hintText += "Buy " + numBuy + " " + cheapestIncomeBuilding.GetName();
			}
		}
		else if (tabName.equals("Defence"))
		{
			m_activeProfile.SortDefenceCheapness();
			WWProfileEntry cheapestDefence = m_activeProfile.GetSortedDefenceEntry(0);
			WWBuilding cheapestDefenceBuilding = cheapestDefence.GetBuilding();
			if (cheapestDefenceBuilding != null)
			{
				bestBuildingToBuy = cheapestDefenceBuilding.GetName();
				int numBuy = m_activeProfile.GetDefenceNumBuy(0);
				hintText += "Buy " + numBuy + " " + bestBuildingToBuy;
			}
			cheapestDefence = m_activeProfile.GetSortedDefenceEntry(1);
			cheapestDefenceBuilding = cheapestDefence.GetBuilding();
			if (cheapestDefenceBuilding != null)
			{
				int numBuy = m_activeProfile.GetDefenceNumBuy(1);
				hintText += "\n";
				hintText += "Buy " + numBuy + " " + cheapestDefenceBuilding.GetName();
			}
		}
		else if (tabName.equals("Profile"))
		{
			hintText ="";
			if (m_activeProfile.HasChanged())
			{
				hintText="Changed ";
			}
			hintText += "A:" + m_activeProfile.GetName();
			hintText += "\nN:" + m_activeProfileName;
			Spinner profileSpinner = (Spinner)findViewById(R.id.profileSpinner);
			String itemName = (String)profileSpinner.getSelectedItem();
			if (itemName != null)
			{
				hintText += " S:" + itemName;
			}
		}
		else
		{
			hintText = "Unknown";
		}
		Log.i(TAG,"UpdateHintText:"+hintText);
		
		m_bestBuildingToBuy = bestBuildingToBuy;
		hintView.setText(hintText);
	}
	
	private void UpdateBuildingNumOwned( WWProfileEntry profileEntry )
	{
		WWBuilding building = profileEntry.GetBuilding();
		
		int numOwned =  profileEntry.GetNumOwned();
		EditText numOwnedView = building.GetViewNumOwned();
		String numOwnedString = Integer.toString(numOwned);
		numOwnedView.setText(numOwnedString);
		numOwnedView.setTag(profileEntry);
		
		Button minusButtonView = building.GetViewMinusButton();
		if (minusButtonView != null)
		{
			minusButtonView.setTag(profileEntry);
		}
		
		Button plusButtonView = building.GetViewPlusButton();
		if (plusButtonView != null)
		{
			plusButtonView.setTag(profileEntry);
		}
		
		//Log.i(TAG, "UpdateBuildingNumOwned: "+building.GetName()+" numOwned:"+numOwnedString);
	}
	
	private void UpdateBuildingRow( WWProfileEntry profileEntry )
	{
		WWBuilding building = profileEntry.GetBuilding();
		
		int numOwned =  profileEntry.GetNumOwned();
		float cheapness = building.GetCheapness(numOwned);
		String cheapnessString = Float.toString(cheapness);
		TextView cheapnessView = building.GetViewCheapness();
		cheapnessView.setText(cheapnessString);

		long currentCost = building.GetCurrentCost(numOwned);
		String currentCostString =  PrettyPrintNumber(currentCost,true);
		TextView currentCostText = building.GetViewCurrentCost();
		currentCostText.setText(currentCostString);
		
		long totalReward = building.GetTotalReward(numOwned);
		String totalRewardString =  PrettyPrintNumber(totalReward,true);
		TextView totalRewardText = building.GetViewTotalReward();
		totalRewardText.setText(totalRewardString);
		
		long totalCost = building.GetTotalCost(numOwned);
		String totalCostString =  PrettyPrintNumber(totalCost,true);
		TextView totalCostText = building.GetViewTotalCost();
		totalCostText.setText(totalCostString);
		
		//Log.i(TAG, "UpdateBuildingRow: "+building.GetName()+" cheapness:"+cheapnessString+" currentCost:"+currentCostString);
	}

	private void SetDefaultRowColours(TableRow row,boolean evenRow)
	{
		SetRowColours(row,evenRow,false);
	}
	private void SetRowColours(TableRow row,boolean evenRow,boolean highlight)
	{
		int colour = evenRow ? Color.BLUE : Color.DKGRAY;
		colour = highlight ? Color.RED : colour;
		int numChildren = row.getChildCount();
		for (int i=0; i<numChildren; i++)
		{
			View element = row.getChildAt(i);
			int id = element.getId();
			if ((id == 123456) || (id == 654321))
			{
				// Special case for the minus & plus buttons
				element.setBackgroundColor(0xFFA0A0A0);
			}
			else if (id == 2468)
			{
				if (element.getClass() == EditText.class)
				{
					EditText number = (EditText)element;
					if (highlight == false)
					{
						// Special case for the number text default non-highlight colour
						number.setBackgroundColor(colour);
						number.setTextColor(Color.WHITE);
						number.setHighlightColor(Color.RED);
					}
					else
					{
						number.setBackgroundColor(colour);
						number.setTextColor(Color.WHITE);
						number.setHighlightColor(Color.BLACK);
					}
				}
			}
			else
			{
				element.setBackgroundColor(colour);
			}
		}
	}
	private void addHeaderRow(TableLayout parent)
	{
		final float textSize = 14.0f;
		final int rowHeight = 20;
		
		final int padTop = 0;
		final int padBottom = 0;
		final int padLeft = 1;
		final int padRight = 1;
		
		final int colour = Color.DKGRAY;
		TableRow row = new TableRow(parent.getContext());
		row.setPadding(0,0,0,0);

		TextView name = new TextView(row.getContext());
		name.setTypeface(Typeface.DEFAULT_BOLD, Typeface.BOLD);
		name.setTextSize(textSize);
		name.setMinWidth(130);
		name.setWidth(130);
		name.setMaxWidth(130);
		name.setLines(1);
		name.setMinLines(1);
		name.setMaxLines(1);
		name.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL);
		name.setPadding(padLeft,padTop,padRight,padBottom);
		name.setShadowLayer(1.0f, 2.0f, 2.0f, Color.BLACK);
		name.setMinHeight(rowHeight);
		name.setHeight(rowHeight);
		name.setMaxHeight(rowHeight);
		name.setBackgroundColor(colour);
		name.setText("Building");
		row.addView(name);

		TextView number = new TextView(row.getContext());
		number.setTypeface(Typeface.DEFAULT_BOLD, Typeface.BOLD);
		number.setTextSize(textSize);
		number.setMinWidth(96);
		number.setWidth(96);
		number.setMaxWidth(96);
		number.setLines(1);
		number.setMinLines(1);
		number.setMaxLines(1);
		number.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL);
		number.setPadding(padLeft,padTop,padRight,padBottom);
		number.setShadowLayer(1.0f, 2.0f, 2.0f, Color.BLACK);
		number.setBackgroundColor(colour);
		number.setText("Number");
		row.addView(number);

		TextView buy = new TextView(row.getContext());
		buy.setTypeface(Typeface.DEFAULT_BOLD, Typeface.BOLD);
		buy.setTextSize(textSize);
		buy.setMinWidth((int)(3.0f*textSize));
		buy.setWidth((int)(3.0f*textSize));
		buy.setMaxWidth((int)(3.0f*textSize));
		buy.setLines(1);
		buy.setMinLines(1);
		buy.setMaxLines(1);
		buy.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL);
		buy.setPadding(padLeft,padTop,padRight,padBottom);
		buy.setShadowLayer(1.0f, 2.0f, 2.0f, Color.BLACK);
		buy.setBackgroundColor(colour);
		buy.setText("Buy");
		row.addView(buy);

		TextView cheapness = new TextView(row.getContext());
		cheapness.setTypeface(Typeface.DEFAULT_BOLD, Typeface.BOLD);
		cheapness.setTextSize(textSize);
		cheapness.setMinWidth((int)(textSize*5.5f));
		cheapness.setWidth((int)(textSize*5.5f));
		cheapness.setMaxWidth((int)(textSize*5.5f));
		cheapness.setLines(1);
		cheapness.setMinLines(1);
		cheapness.setMaxLines(1);
		cheapness.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL);
		cheapness.setPadding(padLeft,padTop,padRight,padBottom);
		cheapness.setShadowLayer(1.0f, 2.0f, 2.0f, Color.BLACK);
		cheapness.setBackgroundColor(colour);
		cheapness.setText("Cheapness");
		row.addView(cheapness);

		TextView reward = new TextView(row.getContext());
		reward.setTypeface(Typeface.DEFAULT_BOLD, Typeface.BOLD);
		reward.setTextSize(textSize);
		reward.setMinWidth((int)(textSize*4.5f));
		reward.setWidth((int)(textSize*4.5f));
		reward.setMaxWidth((int)(textSize*4.5f));
		reward.setMinLines(1);
		reward.setMaxLines(1);
		reward.setLines(1);
		reward.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL);
		reward.setPadding(padLeft,padTop,padRight,padBottom);
		reward.setShadowLayer(1.0f, 2.0f, 2.0f, Color.BLACK);
		reward.setBackgroundColor(colour);
		reward.setText("Reward");
		row.addView(reward);

		TextView currentCost = new TextView(row.getContext());
		currentCost.setTypeface(Typeface.DEFAULT_BOLD, Typeface.BOLD);
		currentCost.setTextSize(textSize);
		currentCost.setMinWidth((int)(textSize*7.5f));
		currentCost.setWidth((int)(textSize*7.5f));
		currentCost.setMaxWidth((int)(textSize*7.5f));
		currentCost.setMinLines(1);
		currentCost.setMaxLines(1);
		currentCost.setLines(1);
		currentCost.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL);
		currentCost.setPadding(padLeft,padTop,padRight,padBottom);
		currentCost.setShadowLayer(1.0f, 2.0f, 2.0f, Color.BLACK);
		currentCost.setBackgroundColor(colour);
		currentCost.setText("Current Cost");
		row.addView(currentCost);

		TextView baseCost = new TextView(row.getContext());
		baseCost.setTypeface(Typeface.DEFAULT_BOLD, Typeface.BOLD);
		baseCost.setTextSize(textSize);
		baseCost.setMinWidth((int)(textSize*5.5f));
		baseCost.setWidth((int)(textSize*5.5f));
		baseCost.setMaxWidth((int)(textSize*5.5f));
		baseCost.setMinLines(1);
		baseCost.setMaxLines(1);
		baseCost.setLines(1);
		baseCost.setPadding(padLeft,padTop,padRight,padBottom);
		baseCost.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL);
		baseCost.setPadding(padLeft,padTop,padRight,padBottom);
		baseCost.setShadowLayer(1.0f, 2.0f, 2.0f, Color.BLACK);
		baseCost.setBackgroundColor(colour);
		baseCost.setText("Base Cost");
		row.addView(baseCost);
		
		TextView totalReward = new TextView(row.getContext());
		totalReward.setTypeface(Typeface.DEFAULT_BOLD, Typeface.BOLD);
		totalReward.setTextSize(textSize);
		totalReward.setMinWidth((int)(textSize*8.5f));
		totalReward.setWidth((int)(textSize*8.5f));
		totalReward.setMaxWidth((int)(textSize*8.5f));
		totalReward.setMinLines(1);
		totalReward.setMaxLines(1);
		totalReward.setLines(1);
		totalReward.setPadding(padLeft,padTop,padRight,padBottom);
		totalReward.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL);
		totalReward.setPadding(padLeft,padTop,padRight,padBottom);
		totalReward.setShadowLayer(1.0f, 2.0f, 2.0f, Color.BLACK);
		totalReward.setBackgroundColor(colour);
		totalReward.setText("Total Reward");
		row.addView(totalReward);
		
		TextView totalCost = new TextView(row.getContext());
		totalCost.setTypeface(Typeface.DEFAULT_BOLD, Typeface.BOLD);
		totalCost.setTextSize(textSize);
		totalCost.setMinWidth((int)(textSize*5.5f));
		totalCost.setWidth((int)(textSize*5.5f));
		totalCost.setMaxWidth((int)(textSize*5.5f));
		totalCost.setMinLines(1);
		totalCost.setMaxLines(1);
		totalCost.setLines(1);
		totalCost.setPadding(padLeft,padTop,padRight,padBottom);
		totalCost.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL);
		totalCost.setPadding(padLeft,padTop,padRight,padBottom);
		totalCost.setShadowLayer(1.0f, 2.0f, 2.0f, Color.BLACK);
		totalCost.setBackgroundColor(colour);
		totalCost.setText("Total Cost");
		row.addView(totalCost);
		
		parent.addView(row);
	}
	
	private void addRow(TableLayout parent, WWBuilding building) 
	{
		final float textSize = 14.0f;
		final int rowHeight = 40;
		
		final int padTop = 0;
		final int padBottom = 0;
		final int padLeft = 1;
		final int padRight = 1;
		
		final int numOwned = 0;
		
		int colour = Color.BLUE;
		
		InputFilter[] filters3 = new InputFilter[1];
		filters3[0] = new InputFilter.LengthFilter(3);

		InputFilter[] filters4 = new InputFilter[1];
		filters4[0] = new InputFilter.LengthFilter(4);

		InputFilter[] filters8 = new InputFilter[1];
		filters8[0] = new InputFilter.LengthFilter(8);
		
		InputFilter[] filters10 = new InputFilter[1];
		filters10[0] = new InputFilter.LengthFilter(10);

		InputFilter[] filters11 = new InputFilter[1];
		filters11[0] = new InputFilter.LengthFilter(11);

		InputFilter[] filters13 = new InputFilter[1];
		filters13[0] = new InputFilter.LengthFilter(13);
		
		TableRow sep = new TableRow(parent.getContext());
		sep.setBackgroundColor(Color.WHITE);
		sep.setPadding(0,1,0,1);
		parent.addView(sep);
		
		TableRow row = new TableRow(parent.getContext());
		row.setPadding(0,0,0,0);
		building.SetViewRow(row);

		TextView name = new TextView(row.getContext());
		name.setTypeface(Typeface.DEFAULT_BOLD, Typeface.BOLD);
		name.setTextSize(textSize);
		name.setMinWidth(116);
		name.setWidth(116);
		name.setMaxWidth(116);
		name.setLines(2);
		name.setMinLines(2);
		name.setMaxLines(2);
		name.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL);
		name.setPadding(padLeft,padTop,padRight,padBottom);
		name.setShadowLayer(1.0f, 2.0f, 2.0f, Color.BLACK);
		name.setMinHeight(rowHeight);
		name.setHeight(rowHeight);
		name.setMaxHeight(rowHeight);
		name.setBackgroundColor(colour);
		name.setText(building.GetName());
		row.addView(name);

		Button minus = new Button(row.getContext());
		minus.setTextSize(textSize);
		minus.setMinWidth((int)(textSize*2.0f));
		minus.setWidth((int)(textSize*2.0f));
		minus.setMaxWidth((int)(textSize*2.0f));
		minus.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL);
		minus.setPadding(padLeft,padTop,padRight,padBottom);
		minus.setMinHeight(rowHeight);
		minus.setHeight(rowHeight);
		minus.setMaxHeight(rowHeight);
		minus.setBackgroundDrawable(null);
		minus.setBackgroundColor(0xFFA0A0A0);
		minus.setText("-");
		minus.setOnClickListener(this);
		minus.setId(123456);
		building.SetViewMinusButton(minus);
		row.addView(minus);
		
		EditText number = new EditText(row.getContext());
		number.setInputType(InputType.TYPE_CLASS_NUMBER);
		number.setTypeface(Typeface.DEFAULT_BOLD, Typeface.BOLD);
		number.setKeyListener(new DigitsKeyListener());
		number.setMinWidth((int)(textSize*3.8f));
		number.setWidth((int)(textSize*3.8f));
		number.setMaxWidth((int)(textSize*3.8f));
		number.setMinLines(1);
		number.setMaxLines(1);
		number.setFilters(filters4);
		number.setLines(1);
		number.setGravity(Gravity.TOP|Gravity.RIGHT);
		number.setText(Integer.toString(numOwned));
		number.setPadding(10,padTop,10,padBottom);
		number.setOnKeyListener(this);
		number.setSelectAllOnFocus(true);
		number.setMinHeight(rowHeight);
		number.setHeight(rowHeight);
		number.setMaxHeight(rowHeight);
		number.setTextSize(textSize);
		number.setTextColor(0xFFFFFFFF);
		number.setBackgroundDrawable(null);
		number.setBackgroundColor(0xFF444444);
		number.setId(2468);
		building.SetViewNumOwned(number);
		row.addView(number);

/*
		ImageButton plusImage = new ImageButton(row.getContext());
		plusImage.setPadding(0,0,0,0);
		plusImage.setMaxHeight(rowHeight);
		//plusImage.setBackgroundColor(0xFFA0A0A0);
		plusImage.setImageResource(android.R.drawable.btn_plus);
		plusImage.setOnClickListener(this);
		plusImage.setId(654321);
		row.addView(plusImage);
*/
		
		Button plus = new Button(row.getContext());
		plus.setTextSize(textSize);
		plus.setMinWidth((int)(textSize*2.0f));
		plus.setWidth((int)(textSize*2.0f));
		plus.setMaxWidth((int)(textSize*2.0f));
		plus.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL);
		plus.setPadding(padLeft,padTop,padRight,padBottom);
		plus.setMinHeight(rowHeight);
		plus.setHeight(rowHeight);
		plus.setMaxHeight(rowHeight);
		//plus.setBackgroundDrawable(null);
		//plus.setBackgroundResource(android.R.drawable.btn_default_small);
		//plus.setBackgroundColor(0xFFA0A0A0);
		plus.setText("+");
		plus.setOnClickListener(this);
		plus.setId(654321);
		building.SetViewPlusButton(plus);
		row.addView(plus);
		
		TextView buy = new TextView(row.getContext());
		buy.setMinWidth((int)(3.0f*textSize));
		buy.setWidth((int)(3.0f*textSize));
		buy.setMaxWidth((int)(3.0f*textSize));
		buy.setLines(1);
		buy.setMinLines(1);
		buy.setMaxLines(1);
		buy.setFilters(filters3);
		buy.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL);
		buy.setPadding(padLeft,padTop,padRight,padBottom);
		buy.setText("");
		buy.setTextSize(textSize);
		buy.setMinHeight(rowHeight);
		buy.setHeight(rowHeight);
		buy.setMaxHeight(rowHeight);
		buy.setBackgroundColor(colour);
		building.SetViewNumBuy(buy);
		row.addView(buy);
		
		TextView cheapness = new TextView(row.getContext());
		cheapness.setMinWidth((int)(textSize*5));
		cheapness.setWidth((int)(textSize*5));
		cheapness.setMaxWidth((int)(textSize*5));
		cheapness.setLines(1);
		cheapness.setMinLines(1);
		cheapness.setMaxLines(1);
		cheapness.setFilters(filters10);
		cheapness.setGravity(Gravity.TOP|Gravity.RIGHT);
		cheapness.setPadding(padLeft,padTop,padRight,padBottom);
		cheapness.setText(Float.toString(building.GetCheapness(numOwned)));
		cheapness.setTextSize(textSize);
		cheapness.setMinHeight(rowHeight);
		cheapness.setHeight(rowHeight);
		cheapness.setMaxHeight(rowHeight);
		cheapness.setBackgroundColor(colour);
		building.SetViewCheapness(cheapness);
		row.addView(cheapness);

		TextView reward = new TextView(row.getContext());
		reward.setMinWidth((int)(textSize*4.5f));
		reward.setWidth((int)(textSize*4.5f));
		reward.setMaxWidth((int)(textSize*4.5f));
		reward.setMinLines(1);
		reward.setMaxLines(1);
		reward.setLines(1);
		reward.setFilters(filters10);
		reward.setPadding(padLeft,padTop,padRight,padBottom);
		reward.setGravity(Gravity.TOP|Gravity.RIGHT);
		reward.setText(String.format("%,d",building.GetReward()));
		reward.setTextSize(textSize);
		reward.setMinHeight(rowHeight);
		reward.setHeight(rowHeight);
		reward.setMaxHeight(rowHeight);
		reward.setBackgroundColor(colour);
		row.addView(reward);

		TextView currentCost = new TextView(row.getContext());
		currentCost.setMinWidth((int)(textSize*8.0f));
		currentCost.setWidth((int)(textSize*8.0f));
		currentCost.setMaxWidth((int)(textSize*8.0f));
		currentCost.setMinLines(1);
		currentCost.setMaxLines(1);
		currentCost.setLines(1);
		currentCost.setFilters(filters13);
		currentCost.setPadding(padLeft,padTop,padRight,padBottom);
		currentCost.setGravity(Gravity.TOP|Gravity.RIGHT);
		currentCost.setText(PrettyPrintNumber(building.GetCurrentCost(numOwned),true));
		currentCost.setTextSize(textSize);
		currentCost.setMinHeight(rowHeight);
		currentCost.setHeight(rowHeight);
		currentCost.setMaxHeight(rowHeight);
		currentCost.setBackgroundColor(colour);
		building.SetViewCurrentCost(currentCost);
		row.addView(currentCost);

		TextView baseCost = new TextView(row.getContext());
		baseCost.setMinWidth((int)(textSize*5.5f));
		baseCost.setWidth((int)(textSize*5.5f));
		baseCost.setMaxWidth((int)(textSize*5.5f));
		baseCost.setMinLines(1);
		baseCost.setMaxLines(1);
		baseCost.setLines(1);
		baseCost.setFilters(filters8);
		baseCost.setPadding(padLeft,padTop,padRight,padBottom);
		baseCost.setGravity(Gravity.TOP|Gravity.RIGHT);
		baseCost.setText(PrettyPrintNumber(building.GetBaseCost(),true));
		baseCost.setTextSize(textSize);
		baseCost.setMinHeight(rowHeight);
		baseCost.setHeight(rowHeight);
		baseCost.setMaxHeight(rowHeight);
		baseCost.setBackgroundColor(colour);
		row.addView(baseCost);
		
		TextView totalReward = new TextView(row.getContext());
		totalReward.setMinWidth((int)(textSize*8.5f));
		totalReward.setWidth((int)(textSize*8.5f));
		totalReward.setMaxWidth((int)(textSize*8.5f));
		totalReward.setMinLines(1);
		totalReward.setMaxLines(1);
		totalReward.setLines(1);
		totalReward.setFilters(filters11);
		totalReward.setPadding(padLeft,padTop,padRight,padBottom);
		totalReward.setGravity(Gravity.TOP|Gravity.RIGHT);
		building.SetViewTotalReward(totalReward);
		totalReward.setTextSize(textSize);
		totalReward.setMinHeight(rowHeight);
		totalReward.setHeight(rowHeight);
		totalReward.setMaxHeight(rowHeight);
		totalReward.setBackgroundColor(colour);
		row.addView(totalReward);
		
		TextView totalCost = new TextView(row.getContext());
		totalCost.setMinWidth((int)(textSize*9.5f));
		totalCost.setWidth((int)(textSize*9.5f));
		totalCost.setMaxWidth((int)(textSize*9.5f));
		totalCost.setMinLines(1);
		totalCost.setMaxLines(1);
		totalCost.setLines(1);
		totalCost.setFilters(filters11);
		totalCost.setPadding(padLeft,padTop,padRight,padBottom);
		totalCost.setGravity(Gravity.TOP|Gravity.RIGHT);
		building.SetViewTotalCost(totalCost);
		totalCost.setTextSize(textSize);
		totalCost.setMinHeight(rowHeight);
		totalCost.setHeight(rowHeight);
		totalCost.setMaxHeight(rowHeight);
		totalCost.setBackgroundColor(colour);
		row.addView(totalCost);
		
		parent.addView(row);
	}

	private String MakeProfileFileName(String profileName)
	{
		String profileFileName = PROFILE_HEADER + "_" + PROFILE_VERSION + "_" + profileName;
		return profileFileName;
	}
	
	private boolean SaveAppState() 
	{
		String fileName = APPSTATE_FILENAME;
		try 
		{
			FileOutputStream outStream = openFileOutput(fileName, MODE_PRIVATE);
			boolean result = SaveAppStateToStream(outStream);
			Log.i(TAG,"SaveAppState DONE file: "+fileName);
			return result;
		}
		catch (FileNotFoundException e) 
		{
			Log.i(TAG,"SaveAppState FileNotFound:"+fileName);
			return false;
		}
	}
	
	private boolean SaveAppStateToStream(FileOutputStream outStream)
	{ 
		TextFileOutput outFile = new TextFileOutput(outStream);
		try 
		{
			// Active profile
			String activeProfile = m_activeProfileName;
			outFile.WriteString(activeProfile);
			Log.i(TAG,"SaveAppStateToStream activeProfile: "+activeProfile);
			
			outFile.Close();
		} 
		catch (IOException e) 
		{
			try 
			{
				outFile.Close();
				return false;
			}
			catch (IOException e2) 
			{
				return false;
			}
		}
		return true;
	}
		
	private boolean LoadAppState() 
	{
		String fileName = APPSTATE_FILENAME;
		try 
		{
			FileInputStream inStream = openFileInput(fileName);
			boolean result = LoadAppStateFromStream(inStream);
			Log.i(TAG,"LoadAppState DONE file: "+fileName);
			return result;
		} 
		catch (FileNotFoundException e) 
		{
			Log.i(TAG,"LoadAppState FileNotFound:"+fileName);
			return false;
		}
	}
			
	private boolean LoadAppStateFromStream(FileInputStream inStream)
	{
		TextFileInput inFile = new TextFileInput(inStream);
		try 
		{
			// Active profile
			String activeProfile = inFile.ReadString();
			Log.i(TAG,"LoadAppStateFromStream activeProfile: "+activeProfile);
			
			inFile.Close();
				
			m_activeProfileName = activeProfile;
		} 
		catch (IOException e) 
		{
			try
			{
				inFile.Close();
				return false;
			}
			catch (IOException e2)
			{
				return false;
			}
		}
		return true;
	}
	private void SaveAllProfiles(boolean force) 
	{
		for (Map.Entry<String,WWProfile> entry: m_profiles.entrySet())
		{
			WWProfile profile = entry.getValue();
			ProfileSave(profile,force);
		}
	}
	private int LoadAllProfilesFromPrivateData(boolean force) 
	{
		String[] fileNames = fileList();
		return LoadAllProfiles(force,fileNames);
	}
	private int LoadAllProfiles(boolean force, String[] fileNames) 
	{
		Log.i(TAG,"LoadAllProfiles");
		int numProfiles = 0;
		int numFiles = fileNames.length;
		if (numFiles > 0) 
		{
			for (int file = 0; file < numFiles; file++) 
			{
				String profileFileName = fileNames[file];
				String baseProfileFileName = MakeProfileFileName("");
				if (profileFileName.startsWith(baseProfileFileName))
				{
					try
					{
						FileInputStream inStream = openFileInput(profileFileName);
						if (LoadProfile(profileFileName,force,inStream) == true) 
						{
							numProfiles++;
						}
					}
					catch (FileNotFoundException e) 
					{
					}
				}
			}
		}
		return numProfiles;
	}

	private boolean LoadProfile(String profileFileName,boolean force,FileInputStream inStream)
	{
		TextFileInput inFile = new TextFileInput(inStream);
		try 
		{
			// Profile name
			String name = inFile.ReadString();
			if (force==false)
			{
				if (m_profiles.containsKey(name))
				{
					Log.i(TAG,"LoadProfile: "+name+" not loading because it is already loaded");
					inFile.Close();
					return true;
				}
			}
			else
			{
				if (m_profiles.containsKey(name))
				{
					Log.i(TAG,"LoadProfile: "+name+" loading but it is already loaded");
				}
			}
			
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
			inFile.Close();
			tempProfile.SetChanged(false);
			ProfileAdd(tempProfile);
			Log.i(TAG,"LoadProfile DONE: "+name+" file: "+profileFileName);
		} 
		catch (IOException e) 
		{
			try
			{
				inFile.Close();
			}
			catch (IOException e2)
			{
			}
			return false;
		}
		return true;
	}

	private boolean SaveProfile(String profileFileName,WWProfile profile,FileOutputStream outStream) 
	{
		TextFileOutput outFile = new TextFileOutput(outStream);
		String name = profile.GetName();
		try 
		{
			// Profile name
			outFile.WriteString(name);

			// Number of each defence building
			int numDefenceBuildings = profile.GetNumDefenceBuildings();
			outFile.WriteInt(numDefenceBuildings);
			for (int i = 0; i < numDefenceBuildings; i++) 
			{
				int number = profile.GetNumDefenceBuilding(i);
				outFile.WriteInt(number);
			}
			// Number of each income building
			int numIncomeBuildings = profile .GetNumIncomeBuildings();
			outFile.WriteInt(numIncomeBuildings);
			for (int i = 0; i < numIncomeBuildings; i++) 
			{
				int number = profile.GetNumIncomeBuilding(i);
				outFile.WriteInt(number);
			}
			outFile.Close();
			Log.i(TAG,"SaveProfile DONE:"+name+" file: "+profileFileName);
		} 
		catch (IOException e) 
		{
			try
			{
				outFile.Close();
				return false;
			}
			catch (IOException e2)
			{
				return false;
			}
		}
		profile.SetChanged(false);
		return true;
	}

	private void ProfileNew() 
	{
		String name = "default";
		WWProfile tempProfile = CreateNewProfile(name);
		ProfileAdd(tempProfile);
		
		Log.i(TAG,"ProfileNew");
	}
	private boolean ProfileDelete() 
	{
		boolean result = true;
		
		if (m_profilesAdapter.getCount() == 0)
		{
			return true;
		}
		String name = m_activeProfile.GetName();
		int index = m_profilesAdapter.getPosition(name);
		
		m_profiles.remove(name);
		m_profilesAdapter.remove(name);
		
		String profileFileName = MakeProfileFileName(name);
		if (deleteFile(profileFileName) == false)
		{
			Log.i(TAG, "ProfileDelete FAILED:"+profileFileName);
			result = false;
		}
		else
		{
			Log.i(TAG, "ProfileDelete SUCCESS:"+profileFileName);
		}
		
		// If deleting the last profile then create a default one
		if (m_profilesAdapter.getCount() == 0)
		{
			ProfileNew();
		}
		//Catch deleting the last item in the list
		if (index >= m_profilesAdapter.getCount())
		{
			index = m_profilesAdapter.getCount()-1;
		}
		if (index >=0)
		{
			String newProfileName = m_profilesAdapter.getItem(index);
			ProfileSetSelectedProfile(newProfileName);
		}
		return result;
	}
	private void ProfileCopy(String newName)
	{
		//make a new profile from the existing profile 
		WWProfile tempProfile = CreateNewProfile(newName);
		tempProfile.Copy(m_activeProfile);
		tempProfile.SetName(newName);
		ProfileAdd(tempProfile);
	}

	private WWProfile CreateNewProfile(String name) 
	{
		WWProfile profile = m_profiles.get(name);
		if (profile == null) 
		{
			profile = new WWProfile(name, NUM_INCOME_BUILDINGS, NUM_DEFENCE_BUILDINGS);
			Log.i(TAG, "CreateNewProfile: " + name);
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

	private void ProfileRename(String newName)
	{
		// Get the new profile name and compare against the active profile name 
		// Update active profile name if it has changed 
		// Update m_profileNames, m_profiles, profile drop down list 
		String oldName = m_activeProfile.GetName();
		if (m_profiles.containsKey(oldName) == true) 
		{
			if (oldName.equals(newName) == false)
			{
				Log.i(TAG, "ProfileRename: "+oldName+"->"+newName);
				m_profilesAdapter.remove(oldName);
				m_profiles.remove(oldName);
				m_activeProfile.SetName(newName);
				m_activeProfileName = m_activeProfile.GetName();
				ProfileAdd(m_activeProfile);
			}
		}
	}
	
	private void ProfileAdd(WWProfile profile) 
	{
		String name = profile.GetName();
		if (m_profiles.containsKey(name) == false) 
		{
			Log.i(TAG,"ProfileAdd:"+name);
			m_profiles.put(name, profile);
			m_profilesAdapter.add(name);
			m_activeProfile = profile;
			m_activeProfileName = m_activeProfile.GetName();
		}
		
		// Update the selected profile in the drop down list
		int profileIndex = m_profilesAdapter.getPosition(name);
		Spinner profileSpinner = (Spinner)findViewById(R.id.profileSpinner);
		profileSpinner.setSelection(profileIndex);
		
		ProfileSelect();
	}

	private boolean ProfileSave(WWProfile profile,boolean force) 
	{
		if (force == false)
		{
			if (profile.HasChanged()==false)
			{
				Log.i(TAG,"ProfileSave: "+profile.GetName()+" not saving because it hasn't changed");
				return true;
			}
		}
		else
		{
			if (profile.HasChanged()==false)
			{
				Log.i(TAG,"ProfileSave: "+profile.GetName()+" saving but it hasn't changed");
			}
		}
		String profileName = profile.GetName();
		String profileFileName = MakeProfileFileName(profileName);
		try
		{
			FileOutputStream outStream = openFileOutput( profileFileName, MODE_PRIVATE);
			boolean result = SaveProfile(profileFileName,profile,outStream);
			return result;
		}
		catch (FileNotFoundException e)
		{
			return false;
		}
	}
	void ResetAllInternalData()
	{
		m_profiles.clear();
		m_profilesAdapter.clear();
		m_activeProfile = null;
		m_activeProfileName = null;
	}
	boolean RemoveAllLocalData()
	{
		String[] fileNames = fileList();
		for (int i=0; i<fileNames.length; i++)
		{
			String fileName = fileNames[i];
			boolean result = deleteFile(fileName);
			if (result == false)
			{
				return false;
			}
		}
		ResetAllInternalData();
		return true;
	}
	private String PrettyPrintNumber(long number,boolean commas)
	{
		String numberString = "0";
		String unitsString = "";
		long newNumber = number;
		if (((number/1000000)*1000000) == number)
		{
			newNumber = (long)(number/1000000);
			unitsString = "M";
		}
		else if (((number/1000)*1000) == number)
		{
			newNumber = (long)(number/1000);
			unitsString = "K";
		}
		else 
		{
			newNumber = number;
			unitsString ="";
		}
		//Catch trivial case
		if (number == 0)
		{
			newNumber = number;
			unitsString ="";
		}
		if (commas)
		{
			numberString = String.format("%,d", newNumber);
		}
		else
		{
			numberString = Long.toString(newNumber);
		}
		String result = numberString + unitsString;
		return result;
	}
	private boolean IsExternalStorageWritable() 
	{
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state)) 
	    {
	    	return true;
	    } 
	    return false;
	}
	private boolean IsExternalStorageReadable() 
	{
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state)) 
	    {
	    	return true;
	    }
	    else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) 
	    {
	    	return true;
	    } 
	    return false;
	}
	private File GetExternalStoragePrivateDirectory()
	{
		if (IsExternalStorageReadable() == false)
		{
			return null;
		}
		File baseExternalDir = Environment.getExternalStorageDirectory();
	    if (baseExternalDir == null) 
	    {
	    	Log.i(TAG,"baseExternalDir is NULL");
	    	return null;
	    }
		//sdcard usage: save files to /Android/data/<package_name>/files/
		String appExternalDirName = "/Android/data/"+getPackageName()+"/files/";
	    File appExternalDir = new File(baseExternalDir, appExternalDirName);
	    if (appExternalDir == null)
	    {
	    	return null;
	    }
	    Log.i(TAG,"appExternalDir="+appExternalDir.getAbsolutePath());
	    return appExternalDir;
	}
	private File GetExternalStoragePrivateFile(boolean writable, String fileName) 
	{
		if (IsExternalStorageReadable() == false)
		{
			return null;
		}
		if (writable && (IsExternalStorageWritable() == false))
		{
			return null;
		}
		File appExternalDir = GetExternalStoragePrivateDirectory();
	    File appExternalFile = new File(appExternalDir, fileName);
	    if (appExternalFile != null)
	    {
	    	Log.i(TAG,"appExternalFile="+appExternalFile.getAbsolutePath());
	    }
	    return appExternalFile;
	}
	private boolean TestForExternalStoragePrivateFile(String fileName) 
	{
		File appExternalFile = GetExternalStoragePrivateFile(false,fileName);
		if (appExternalFile == null)
		{
			return false;
		}
		if (appExternalFile.exists() && appExternalFile.isFile())
		{
			return true;
		}
		return false;
	}
	private File CreateExternalStoragePrivateFile(String fileName) 
	{
		File appExternalFile = GetExternalStoragePrivateFile(true,fileName);
		if (appExternalFile == null)
		{
			Log.i(TAG,"CreateExternalStoragePrivateFile: appExternalFile is NULL");
			return null;
		}
		//mkdirs returns true if all the dirs exist, false = error or the dirs already exist!
		File dirFile = appExternalFile.getParentFile();
		if (dirFile != null)
		{
			if (!dirFile.exists())
			{
				if (dirFile.mkdirs() == false)
				{
					Log.i(TAG,"CreateExternalStoragePrivateFile: mkdirs failed "+dirFile.getName());
					return null;
				}
			}
			try
			{
				if (appExternalFile.exists())
				{
					if (!appExternalFile.isFile())
					{
						appExternalFile.delete();
					}
				}
				if (!appExternalFile.exists())
				{
					if (appExternalFile.createNewFile() == false)
					{
						Log.i(TAG,"CreateExternalStoragePrivateFile: createNewFile failed "+appExternalFile.getName());
						return null;
					}
				}
				return appExternalFile;
			}
			catch (IOException e) 
			{
				Log.i(TAG,"CreateExternalStoragePrivateFile: createNewFile IOException "+appExternalFile.getName());
				return null;
			}
		}
		return null;
	}
	private boolean TestForBackupData() 
	{
		String[] fileNames = GetFilelistFromExternalStorage();
		if (fileNames.length == 0)
		{
			return false;
		}
		String baseProfileFileName = MakeProfileFileName("");
		String appStateFileName = APPSTATE_FILENAME;
		for (int i=0; i<fileNames.length; i++)
		{
			String fileName = fileNames[i];
			if (fileName.startsWith(baseProfileFileName))
			{
				Log.i(TAG,"TestForBackupData: found profile data:"+fileName);
				return true;
			}
			if (fileName.equals(appStateFileName))
			{
				Log.i(TAG,"TestForBackupData: found AppState data:"+fileName);
				return true;
			}
			if (fileName.startsWith(baseProfileFileName))
			{
				return true;
			}
		}
		return false;
	}
	private String[] GetFilelistFromExternalStorage() 
	{
		File appExternalDir = GetExternalStoragePrivateDirectory();
		String[] fileNames = appExternalDir.list();
		for (int i=0; i<fileNames.length; i++)
		{
			Log.i(TAG,"externalFile["+i+"] = "+fileNames[i]);
		}
		return fileNames;
	}
	private void InternalBackup()
	{
		boolean result = BackupData();
		AlertResultDialog("Backup Data", result);
		UpdateDisplay();
	}
	private boolean BackupData()
	{
		Log.i(TAG,"BackupData: AppState");
		
		boolean result = true;
		
		if (IsExternalStorageWritable() == false)
		{
			Log.i(TAG,"BackupData external storage is not writeable");
			return false;
		}
		File appExternalDir = GetExternalStoragePrivateDirectory();
		if (appExternalDir == null)
		{
			Log.i(TAG,"BackupData appExternalDir is null");
			return false;
		}
		// Delete any existing files
		String[] fileNames = GetFilelistFromExternalStorage();
		for (int i=0; i<fileNames.length; i++)
		{
			String fileName = fileNames[i];
			File externalFile = new File(appExternalDir,fileName);
			if (externalFile.isFile())
			{
				externalFile.delete();
			}
		}
		
		// Save the AppState data
		File appStateFile = CreateExternalStoragePrivateFile(APPSTATE_FILENAME);
		if (appStateFile == null)
		{
			Log.i(TAG,"BackupData appStateFile is NULL");
			return false;
		}
		try
		{
			FileOutputStream outStream = new FileOutputStream(appStateFile);
			result = SaveAppStateToStream(outStream);
			Log.i(TAG,"BackupData DONE result="+result);
			if (result == false)
			{
				return false;
			}
		}
		catch (FileNotFoundException e) 
		{
			Log.i(TAG,"BackupData FileNotFound:"+appStateFile.getName());
			return false;
		}
		//Save the profiles
		for (Map.Entry<String,WWProfile> entry: m_profiles.entrySet())
		{
			WWProfile profile = entry.getValue();
			String profileName = profile.GetName();
			String profileFileName = MakeProfileFileName(profileName);
			File externalProfileFile = new File(appExternalDir,profileFileName);
			try
			{
				FileOutputStream outStream = new FileOutputStream(externalProfileFile);
				boolean wasChanged = profile.HasChanged();
				boolean saveResult =  SaveProfile(profileFileName,profile,outStream);
				profile.SetChanged(wasChanged);
				if (saveResult == false)
				{
					result = false;
				}
			}
			catch (FileNotFoundException e)
			{
				return false;
			}
		}
		return result;
	}
	private boolean RestoreData()
	{
		Log.i(TAG,"RestoreData: AppState");
		
		File appExternalDir = GetExternalStoragePrivateDirectory();
		if (appExternalDir == null)
		{
			return false;
		}
		
		boolean result = false;
		
		// Get the file names that could be loaded
		String[] fileNames = GetFilelistFromExternalStorage();
		String baseProfileFileName = MakeProfileFileName("");
		// Loop over loading the profiles first
		for (int i=0; i<fileNames.length; i++)
		{
			String fileName = fileNames[i];
			Log.i(TAG,"RestoreData:externalFile["+i+"] = "+fileName);
			if (fileName.startsWith(baseProfileFileName))
			{
				Log.i(TAG,"RestoreData: found profile data to load:"+fileName);
				File externalProfileFile = new File(appExternalDir,fileName);
				try
				{
					FileInputStream inStream = new FileInputStream(externalProfileFile);
					if (LoadProfile(fileName,true,inStream) == true)
					{
						result = true;
					}
				}
				catch (FileNotFoundException e)
				{
					Log.i(TAG,"RestoreData:externalFile not found exception:"+fileName);
					return false;
				}
			}
		}
		//Mark all the profiles as changed
		for (Map.Entry<String,WWProfile> entry: m_profiles.entrySet())
		{
			WWProfile profile = entry.getValue();
			profile.SetChanged(true);
		}
		
		// Load the AppState data if it exists
		if (TestForExternalStoragePrivateFile(APPSTATE_FILENAME)==false)
		{
			Log.i(TAG,"RestoreData: TestForExternalStoragePrivateFile failed");
			return result;
		}
		File appStateFile = GetExternalStoragePrivateFile(false, APPSTATE_FILENAME);
		if (appStateFile == null)
		{
			Log.i(TAG,"RestoreData appStateFile is NULL");
			return false;
		}
		try
		{
			FileInputStream inStream = new FileInputStream(appStateFile);
			result = LoadAppStateFromStream(inStream);
			Log.i(TAG,"Restore DONE result="+result);
			if (result==false)
			{
				return false;
			}
		}
		catch (FileNotFoundException e) 
		{
			Log.i(TAG,"Restore FileNotFound:"+appStateFile.getName());
			return false;
		}
		return true;
	}
	private void AlertYesNoDialog(String title, String message)
	{
		AlertDialog.Builder alertBox = new AlertDialog.Builder(this);
		alertBox.create();
		alertBox.setMessage(message);
		alertBox.setCancelable(false);
		alertBox.setTitle(title);
		alertBox.setIcon(android.R.drawable.ic_dialog_alert);
		alertBox.setPositiveButton("Yes", this);
		alertBox.setNegativeButton("No", this);
		alertBox.show();
	}
	private void AlertOkDialog(String title, String message, boolean error)
	{
		AlertDialog.Builder alertBox = new AlertDialog.Builder(this);
		alertBox.create();
		alertBox.setMessage(message);
		alertBox.setCancelable(false);
		alertBox.setTitle(title);
		if (error)
		{
			alertBox.setIcon(android.R.drawable.ic_dialog_alert);
		}
		else
		{
			alertBox.setIcon(android.R.drawable.star_on);
		}
		alertBox.setPositiveButton("OK", null);
		alertBox.show();
	}
	private void AlertResultDialog(String title, boolean result)
	{
		String resultString = result ? "Successful" : "Failed";
		AlertOkDialog(title, resultString, !result);
	}
	private void AlertErrorDialog(String title, String message)
	{
		AlertOkDialog(title, message, true);
	}

	private static final String TAG = "WWCALC";
	private static final String PROFILE_HEADER = "WWProfile";
	private static final String PROFILE_VERSION = "1";
	private static final String APPSTATE_FILENAME = "AppState";

	public static final int NUM_DEFENCE_BUILDINGS = 6;
	public static final int NUM_INCOME_BUILDINGS = 8;
	public static final int DEFAULT_NUM_PROFILES = 8;

	private static final int DIALOG_NONE = 0;
	private static final int DIALOG_RESTORE = 1;
	private static final int DIALOG_BACKUP = 2;
	private static final int DIALOG_PROFILE_DELETE = 3;
	private int m_dialogAction;
	
	private int m_numDefenceBuildings;
	private int m_numIncomeBuildings;
	private WWBuilding[] m_defenceBuildings;
	private WWBuilding[] m_incomeBuildings;
	private ArrayAdapter<String> m_profilesAdapter;

	private Map<String, WWProfile> m_profiles;
	private List<String> m_profileNames;
	private WWProfile m_activeProfile;
	private String m_activeProfileName;
	private String m_bestBuildingToBuy;

	HorizontalScrollView m_defenceViewHeader;
	LinkedHorizontalScrollView m_defenceViewScroll;
	
	HorizontalScrollView m_incomeViewHeader;
	LinkedHorizontalScrollView m_incomeViewScroll;
}