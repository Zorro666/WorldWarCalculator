package com.zorro666.worldwarcalculator;

public class WWProfileEntry
{
	public WWProfileEntry()
	{
		m_numOwned = 0;
	}
	public WWBuilding GetBuilding()
	{
		return m_building;
	}
	public int GetNumOwned()
	{
		return m_numOwned;
	}
	public void SetBuilding(WWBuilding building)
	{
		m_building=building;
	}
	public void SetNumOwned(int numOwned)
	{
		if (numOwned < 0)
		{
			return;
		}
		if (numOwned > 9999)
		{
			return;
		}
		m_numOwned=numOwned;
	}
	private WWBuilding m_building;
	private int m_numOwned;
}
