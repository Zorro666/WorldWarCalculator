package com.zorro666.worldwarcalculator;

public class WWProfileEntry
{
	public WWProfileEntry()
	{
		m_numOwned = 0;
		m_changed = false;
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
		if (numOwned != m_numOwned)
		{
			m_numOwned=numOwned;
			m_changed=true;
		}
	}
	public void SetChanged(boolean changed)
	{
		m_changed=changed;
		if (changed == false)
		{
			m_building.SetChanged(false);
		}
	}
	
	public WWBuilding GetBuilding()
	{
		return m_building;
	}
	public int GetNumOwned()
	{
		return m_numOwned;
	}
	public boolean HasChanged()
	{
		boolean changed = m_changed;
		if (changed == true)
		{
			return true;
		}
		//Strictly speaking the building can never change but that might change in the future ;)
		changed = m_building.HasChanged();
		if (changed == true)
		{
			return true;
		}
		return false;
	}
	private WWBuilding m_building;
	private int m_numOwned;
	private boolean m_changed;
}
