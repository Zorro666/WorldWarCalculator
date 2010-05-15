package com.zorro666.worldwarcalculator;

import android.util.Log;

public class WWProfile
{
	WWProfile()
	{
		Log.i("BAD", "Oh noes");
	}
	WWProfile(String name)
	{
		m_incomeBuildings=new WWProfileEntry[WorldWarCalc.NUM_INCOME_BUILDINGS];
		for (int i=0; i<WorldWarCalc.NUM_INCOME_BUILDINGS;i++)
		{
			m_incomeBuildings[i]=new WWProfileEntry();
		}
		m_defenceBuildings=new WWProfileEntry[WorldWarCalc.NUM_DEFENCE_BUILDINGS];
		for (int i=0; i<WorldWarCalc.NUM_DEFENCE_BUILDINGS;i++)
		{
			m_defenceBuildings[i]=new WWProfileEntry();
		}
		m_name=name;
	}
	public String GetName()
	{
		return m_name;
	}
	public int GetNumIncomeBuildings()
	{
		return m_incomeBuildings.length;
	}
	public int GetNumDefenceBuildings()
	{
		return m_defenceBuildings.length;
	}
	public int GetNumIncomeBuilding(int index)
	{
		return m_incomeBuildings[index].GetNumOwned();
	}
	public int GetNumDefenceBuilding(int index)
	{
		return m_defenceBuildings[index].GetNumOwned();
	}
	public WWProfileEntry GetIncomeBuilding(int index)
	{
		return m_incomeBuildings[index];
	}
	public WWProfileEntry GetDefenceBuilding(int index)
	{
		return m_defenceBuildings[index];
	}
	
	public void SetName(String name)
	{
		m_name = name;
	}
	public void SetNumIncomeBuilding(int index,int number)
	{
		if ((index>=0) && (index<WorldWarCalc.NUM_INCOME_BUILDINGS))
		{
			m_incomeBuildings[index].SetNumOwned(number);
		}
	}
	public void SetNumDefenceBuilding(int index,int number)
	{
		if ((index>=0) && (index<WorldWarCalc.NUM_DEFENCE_BUILDINGS))
		{
			m_defenceBuildings[index].SetNumOwned(number);
		}
	}
	
	private String m_name;
	private WWProfileEntry[] m_incomeBuildings;
	private WWProfileEntry[] m_defenceBuildings;
}
    	
