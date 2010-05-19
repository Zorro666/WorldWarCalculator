package com.zorro666.worldwarcalculator;

import android.widget.TextView;
import android.widget.EditText;

public class WWBuilding 
{
	public WWBuilding( final String name, final int baseCost, final int reward, final float valueMultiplier )
	{
		m_name = name;
		m_baseCost = baseCost/BASE_COST_DIVISOR;
		m_reward = reward;
		m_valueMultiplier = valueMultiplier;
	}
	public String GetName()
	{
		return m_name;
	}
	
	public long GetBaseCost()
	{
		return m_baseCost*100;
	}
	
	public long GetCurrentCost(int numOwned)
	{
		long cost = m_baseCost + (long)numOwned * ( m_baseCost / (long)10 );
		cost *= BASE_COST_DIVISOR;
		return cost;
	}
	
	public int GetReward()
	{
		return m_reward;
	}
	
	public float GetValue(int numOwned)
	{
		float rawValue = GetRawValue(numOwned);
		float value = rawValue * m_valueMultiplier;
    	value = Math.round(value * 100.0f)/100.0f;
		return value;
	}
	public float GetRawValue(int numOwned)
	{
		long cost = GetCurrentCost(numOwned);
		int reward = GetReward();
		float value = cost/(reward+0.0f);
		value *= BASE_COST_DIVISOR;
		return value;
	}
	
	public EditText GetViewNumOwned()
	{
		return m_viewNumOwned;
	}
	public TextView GetViewValue()
	{
		return m_viewValue;
	}
	public TextView GetViewCurrentCost()
	{
		return m_viewCurrentCost;
	}
	
	public void SetViewNumOwned(EditText viewNumOwned)
	{
		m_viewNumOwned = viewNumOwned;
	}
	public void SetViewValue(TextView viewValue)
	{
		m_viewValue = viewValue;
	}
	public void SetViewCurrentCost(TextView viewCurrentCost)
	{
		m_viewCurrentCost = viewCurrentCost;
	}
	
	private final int BASE_COST_DIVISOR=100;
	private String m_name;
	private long m_baseCost;
	private int m_reward;
	private float m_valueMultiplier;;
	private EditText m_viewNumOwned;
	private TextView m_viewValue;
	private TextView m_viewCurrentCost;
}