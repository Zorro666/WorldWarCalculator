package com.zorro666.worldwarcalculator;

import android.util.Log;

public class WWProfile
{
	WWProfile()
	{
		Log.i("BAD", "Oh noes");
	}
	WWProfile(String name, int numIncomeBuildings, int numDefenceBuildings)
	{
		m_incomeNumToBuy = new int[numIncomeBuildings];
		m_incomeCheapnessSorted = new int[numIncomeBuildings];
		m_incomeBuildings=new WWProfileEntry[numIncomeBuildings];
		for (int i=0; i<m_incomeBuildings.length;i++)
		{
			m_incomeBuildings[i]=new WWProfileEntry();
			m_incomeCheapnessSorted[i]=i;
			m_incomeNumToBuy[i]=0;
		}
		
		m_defenceNumToBuy = new int[numDefenceBuildings];
		m_defenceCheapnessSorted = new int[numDefenceBuildings];
		m_defenceBuildings=new WWProfileEntry[numDefenceBuildings];
		for (int i=0; i<m_defenceBuildings.length;i++)
		{
			m_defenceBuildings[i]=new WWProfileEntry();
			m_defenceCheapnessSorted[i]=i;
			m_defenceNumToBuy[i]=0;
		}
		m_name=name;
		m_changed=false;
	}
	public void SetChanged(boolean changed)
	{
		m_changed = changed;
		if (changed == false)
		{
			for (int i=0; i<m_incomeBuildings.length;i++)
			{
				m_incomeBuildings[i].SetChanged(false);
			}
		}
		for (int i=0; i<m_defenceBuildings.length;i++)
		{
			m_defenceBuildings[i].SetChanged(false);
		}
	}
	public void SetName(String name)
	{
		m_name = name;
		m_changed=true;
	}
	public void SetNumIncomeBuilding(int index,int number)
	{
		if ((index>=0) && (index<m_incomeBuildings.length))
		{
			if (m_incomeBuildings[index].GetNumOwned() != number)
			{
				m_incomeBuildings[index].SetNumOwned(number);
				m_changed=true;
			}
		}
	}
	public void SetNumDefenceBuilding(int index,int number)
	{
		if ((index>=0) && (index<m_defenceBuildings.length))
		{
			if (m_defenceBuildings[index].GetNumOwned() != number)
			{
				m_defenceBuildings[index].SetNumOwned(number);
				m_changed=true;
			}
		}
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
	
	public int GetIncomeNumToBuy(int index)
	{
		return m_incomeNumToBuy[index];
	}
	public int GetDefenceNumToBuy(int index)
	{
		return m_defenceNumToBuy[index];
	}
	public WWProfileEntry GetSortedIncomeEntry(int index)
	{
		int row = m_incomeCheapnessSorted[index];
		WWProfileEntry entry = m_incomeBuildings[row];
		return entry;
	}
	public WWProfileEntry GetSortedDefenceEntry(int index)
	{
		int row = m_defenceCheapnessSorted[index];
		WWProfileEntry entry = m_defenceBuildings[row];
		return entry;
	}
	public void SortIncomeCheapness()
	{
		// Very simple bubble sort - only 6 or 7 elements so be fine
		int numIncomeBuildings = m_incomeBuildings.length;
		for (int i=0; i<numIncomeBuildings-1;i++)
		{
			for (int j=i+1; j<numIncomeBuildings;j++)
			{
				int iEntry = m_incomeCheapnessSorted[i];
				int iNumOwned = m_incomeBuildings[iEntry].GetNumOwned();
				WWBuilding iBuilding = m_incomeBuildings[iEntry].GetBuilding();
				float iCheapness = 0.0f;
				if (iBuilding != null)
				{
					iCheapness = iBuilding.GetCheapness(iNumOwned);
				}
				
				int jEntry = m_incomeCheapnessSorted[j];
				int jNumOwned = m_incomeBuildings[jEntry].GetNumOwned();
				WWBuilding jBuilding = m_incomeBuildings[jEntry].GetBuilding();
				float jCheapness = 0.0f;
				if (jBuilding != null)
				{
					jCheapness = jBuilding.GetCheapness(jNumOwned);
				}
				if (jCheapness < iCheapness)
				{
					m_incomeCheapnessSorted[i] = jEntry;
					m_incomeCheapnessSorted[j] = iEntry;
				}
			}
		}
		// Compute how many to buy of each building
		for (int i=0; i<numIncomeBuildings-1;i++)
		{
			int thisRow = m_incomeCheapnessSorted[i];
			WWProfileEntry thisEntry = m_incomeBuildings[thisRow];
			int thisNumOwned = thisEntry.GetNumOwned();
			WWBuilding thisBuilding = thisEntry.GetBuilding();
			if (thisBuilding != null)
			{
				int nextRow = m_incomeCheapnessSorted[i+1];
				WWProfileEntry nextEntry = m_incomeBuildings[nextRow];
				int nextNumOwned = nextEntry.GetNumOwned();
				WWBuilding nextBuilding = nextEntry.GetBuilding();
				if (nextBuilding != null)
				{
					float thisCheapness = thisBuilding.GetCheapness(thisNumOwned);
					float nextCheapness = nextBuilding.GetCheapness(nextNumOwned);
					float deltaCheapness = nextCheapness - thisCheapness;
					float cheapnessPerBuy = thisBuilding.GetCheapnessPerBuy();
					float floatNumToBuy = deltaCheapness/cheapnessPerBuy;
					floatNumToBuy = (float)Math.ceil((double)floatNumToBuy);
					int numToBuy = (int)floatNumToBuy;
					if (numToBuy == 0)
					{
						numToBuy = 1;
					}
					m_incomeNumToBuy[i] = numToBuy;
				}
			}
		}
		m_incomeNumToBuy[numIncomeBuildings-1] = 0;
		for (int i=0; i<numIncomeBuildings;i++)
		{
			int row = m_incomeCheapnessSorted[i];
			WWProfileEntry entry = m_incomeBuildings[row];
			WWBuilding building = entry.GetBuilding();
			if (building != null)
			{
				//int numOwned = entry.GetNumOwned();
				//Log.i("WWCALC", i + " " + building.GetName() + " Cheapness " + building.GetCheapness(numOwned) + " NumToBuy " + m_incomeNumToBuy[i]);
			}
		}
	}
	public void SortDefenceCheapness()
	{
		// Very simple bubble sort - only 6 or 7 elements so be fine
		for (int i=0; i<WorldWarCalc.NUM_DEFENCE_BUILDINGS-1;i++)
		{
			for (int j=i+1; j<WorldWarCalc.NUM_DEFENCE_BUILDINGS;j++)
			{
				int iEntry = m_defenceCheapnessSorted[i];
				int iNumOwned = m_defenceBuildings[iEntry].GetNumOwned();
				float iCheapness = 0.0f;
				WWBuilding iBuilding = m_defenceBuildings[iEntry].GetBuilding();
				if (iBuilding != null)
				{
					iCheapness = iBuilding.GetCheapness(iNumOwned);
				}
				
				int jEntry = m_defenceCheapnessSorted[j];
				int jNumOwned = m_defenceBuildings[jEntry].GetNumOwned();
				float jCheapness = 0.0f;
				WWBuilding jBuilding = m_defenceBuildings[jEntry].GetBuilding();
				if (jBuilding != null)
				{
					jCheapness = jBuilding.GetCheapness(jNumOwned);
				}
				if (jCheapness < iCheapness)
				{
					m_defenceCheapnessSorted[i] = jEntry;
					m_defenceCheapnessSorted[j] = iEntry;
				}
			}
		}
		// Compute how many to buy of each building
		for (int i=0; i<WorldWarCalc.NUM_DEFENCE_BUILDINGS-1;i++)
		{
			int thisRow = m_defenceCheapnessSorted[i];
			WWProfileEntry thisEntry = m_defenceBuildings[thisRow];
			int thisNumOwned = thisEntry.GetNumOwned();
			WWBuilding thisBuilding = thisEntry.GetBuilding();
			if (thisBuilding != null)
			{
				int nextRow = m_defenceCheapnessSorted[i+1];
				WWProfileEntry nextEntry = m_defenceBuildings[nextRow];
				int nextNumOwned = nextEntry.GetNumOwned();
				WWBuilding nextBuilding = nextEntry.GetBuilding();
				if (nextBuilding != null)
				{
					float thisCheapness = thisBuilding.GetCheapness(thisNumOwned);
					float nextCheapness = nextBuilding.GetCheapness(nextNumOwned);
					float deltaCheapness = nextCheapness - thisCheapness;
					float cheapnessPerBuy = thisBuilding.GetCheapnessPerBuy();
					float floatNumToBuy = deltaCheapness/cheapnessPerBuy;
					floatNumToBuy = (float)Math.ceil((double)floatNumToBuy);
					int numToBuy = (int)floatNumToBuy;
					if (numToBuy == 0)
					{
						numToBuy = 1;
					}
					m_defenceNumToBuy[i] = numToBuy;
				}
			}
		}
		m_defenceNumToBuy[WorldWarCalc.NUM_DEFENCE_BUILDINGS-1] = 0;
		for (int i=0; i<WorldWarCalc.NUM_DEFENCE_BUILDINGS;i++)
		{
			int row = m_defenceCheapnessSorted[i];
			WWProfileEntry entry = m_defenceBuildings[row];
			WWBuilding building = entry.GetBuilding();
			if (building != null)
			{
				//int numOwned = entry.GetNumOwned();
				//Log.i("WWCALC", i + " " + building.GetName() + " Cheapness " + building.GetCheapness(numOwned) + " NumToBuy " + m_defenceNumToBuy[i]);
			}
		}
	}
	public boolean HasChanged()
	{
		boolean changed = m_changed;
		if (changed == true)
		{
			Log.i(TAG,"Changed m_changed");
			return changed;
		}
		for (int i=0; i<m_incomeBuildings.length;i++)
		{
			changed = m_incomeBuildings[i].HasChanged();
			if (changed == true)
			{
				Log.i(TAG,"Changed Building "+m_incomeBuildings[i].GetBuilding().GetName());
				return true;
			}
		}
		for (int i=0; i<m_defenceBuildings.length;i++)
		{
			changed = m_defenceBuildings[i].HasChanged();
			if (changed == true)
			{
				Log.i(TAG,"Changed Building "+m_defenceBuildings[i].GetBuilding().GetName());
				return true;
			}
		}
		return false;
	}
	public void Copy(WWProfile other)
	{
		m_name=other.m_name;
		for (int i=0; i<m_defenceBuildings.length;i++)
		{
			int numOwned = other.m_defenceBuildings[i].GetNumOwned();
			m_defenceBuildings[i].SetNumOwned(numOwned);
		}
		for (int i=0; i<m_incomeBuildings.length;i++)
		{
			int numOwned = other.m_incomeBuildings[i].GetNumOwned();
			m_incomeBuildings[i].SetNumOwned(numOwned);
		}
		m_changed=true;
	}
	public long GetTotalIncome()
	{
		long totalIncome = 0;
		for (int i=0; i<m_incomeBuildings.length;i++)
		{
			int numOwned = m_incomeBuildings[i].GetNumOwned();
			int reward = m_incomeBuildings[i].GetBuilding().GetReward();
			long income = numOwned * reward;
			totalIncome += income;
		}
		return totalIncome;
	}
	public long GetTotalDefence()
	{
		long totalDefence = 0;
		for (int i=0; i<m_defenceBuildings.length;i++)
		{
			int numOwned = m_defenceBuildings[i].GetNumOwned();
			int reward = m_defenceBuildings[i].GetBuilding().GetReward();
			long defence = numOwned * reward;
			totalDefence += defence;
		}
		return totalDefence;
	}
	
	private static final String TAG = "PROFILE";
	private String m_name;
	private WWProfileEntry[] m_incomeBuildings;
	private WWProfileEntry[] m_defenceBuildings;
	private int[] m_incomeCheapnessSorted;
	private int[] m_defenceCheapnessSorted;
	private int[] m_incomeNumToBuy;
	private int[] m_defenceNumToBuy;
	private boolean m_changed;
}
    	
