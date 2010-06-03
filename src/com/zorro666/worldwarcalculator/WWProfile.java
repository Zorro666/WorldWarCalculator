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
		m_incomeNumToBuy = new int[WorldWarCalc.NUM_INCOME_BUILDINGS];
		m_incomeCheapnessSorted = new int[WorldWarCalc.NUM_INCOME_BUILDINGS];
		m_incomeBuildings=new WWProfileEntry[WorldWarCalc.NUM_INCOME_BUILDINGS];
		for (int i=0; i<WorldWarCalc.NUM_INCOME_BUILDINGS;i++)
		{
			m_incomeBuildings[i]=new WWProfileEntry();
			m_incomeCheapnessSorted[i]=i;
			m_incomeNumToBuy[i]=0;
		}
		
		m_defenceNumToBuy = new int[WorldWarCalc.NUM_DEFENCE_BUILDINGS];
		m_defenceCheapnessSorted = new int[WorldWarCalc.NUM_DEFENCE_BUILDINGS];
		m_defenceBuildings=new WWProfileEntry[WorldWarCalc.NUM_DEFENCE_BUILDINGS];
		for (int i=0; i<WorldWarCalc.NUM_DEFENCE_BUILDINGS;i++)
		{
			m_defenceBuildings[i]=new WWProfileEntry();
			m_defenceCheapnessSorted[i]=i;
			m_defenceNumToBuy[i]=0;
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
		for (int i=0; i<WorldWarCalc.NUM_INCOME_BUILDINGS-1;i++)
		{
			for (int j=i+1; j<WorldWarCalc.NUM_INCOME_BUILDINGS;j++)
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
		for (int i=0; i<WorldWarCalc.NUM_INCOME_BUILDINGS-1;i++)
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
		m_incomeNumToBuy[WorldWarCalc.NUM_INCOME_BUILDINGS-1] = 0;
		for (int i=0; i<WorldWarCalc.NUM_INCOME_BUILDINGS;i++)
		{
			int row = m_incomeCheapnessSorted[i];
			WWProfileEntry entry = m_incomeBuildings[row];
			WWBuilding building = entry.GetBuilding();
			if (building != null)
			{
				int numOwned = entry.GetNumOwned();
				Log.i("WWCALC", i + " " + building.GetName() + " Cheapness " + building.GetCheapness(numOwned) + " NumToBuy " + m_incomeNumToBuy[i]);
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
				int numOwned = entry.GetNumOwned();
				Log.i("WWCALC", i + " " + building.GetName() + " Cheapness " + building.GetCheapness(numOwned) + " NumToBuy " + m_defenceNumToBuy[i]);
			}
		}
	}
	
	private String m_name;
	private WWProfileEntry[] m_incomeBuildings;
	private WWProfileEntry[] m_defenceBuildings;
	private int[] m_incomeCheapnessSorted;
	private int[] m_defenceCheapnessSorted;
	private int[] m_incomeNumToBuy;
	private int[] m_defenceNumToBuy;
}
    	
